/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package weka.filters.unsupervised.attribute;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.filters.AbstractAdamsFilterTest;
import weka.filters.Filter;
import weka.test.AdamsTestHelper;
import adams.core.Utils;
import adams.core.base.BaseString;

/**
 * Tests MergeManyAttributes. Run from the command line with: <br><br>
 * java weka.filters.unsupervised.attribute.MergeManyAttributesTest
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MergeManyAttributesTest
  extends AbstractAdamsFilterTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public MergeManyAttributesTest(String name) {
    super(name);
  }

  /**
   * The default filter setup to test.
   * 
   * @return		the filter
   */
  @Override
  public Filter getFilter() {
    MergeManyAttributes	result;
    
    result = new MergeManyAttributes();
    result.setAttributeNames(new BaseString[]{
	new BaseString("NominalAtt1"),
	new BaseString("NominalAtt2"),
    });
    
    return result;
  }
  
  /**
   * returns data generated for the FilteredClassifier test
   * 
   * @return		the dataset for the FilteredClassifier
   * @throws Exception	if generation of data fails
   */
  @Override
  protected Instances getFilteredClassifierData() throws Exception {
    Instances	result;
    RemoveType	remove;
    
    result = new Instances(m_Instances);
    remove = new RemoveType();
    remove.setAttributeType(new SelectedTag(Attribute.STRING, RemoveType.TAGS_ATTRIBUTETYPE));
    remove.setInputFormat(result);
    result = Filter.useFilter(result, remove);
    result.setClassIndex(0);
    
    return result;
  }
  
  /**
   * Tests the merging of three attributes.
   */
  protected void performThreeAttributesMerge(MergeManyAttributes filter, double[] expected) {
    Instances data = null;
    String filename = "weka/filters/data/MergeManyAttributes.arff";
    try {
      data = new Instances(new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(filename))));
      data.setClassIndex(data.numAttributes() - 1);
    }
    catch (Exception e) {
      fail("Failed to load dataset '" + filename + "':\n" + Utils.throwableToString(e));
    }

    filter.setAttributeNames(new BaseString[]{
	new BaseString("att1"),
	new BaseString("att2"),
	new BaseString("att3"),
    });
    
    try {
      filter.setInputFormat(data);
    }
    catch (Exception e) {
      fail("Failed to call setInputFormat:\n" + Utils.throwableToString(e));
    }
    
    Instances output = null;
    try {
      output = Filter.useFilter(data, filter);
    }
    catch (Exception e) {
      fail("Failed to generate output:\n" + Utils.throwableToString(e));
    }
    
    assertEquals("# attribute", 2, output.numAttributes());
    assertEquals("class attribute", 1, output.classIndex());
    assertEquals("number of instances", data.numInstances(), output.numInstances());
    
    double[] values = output.attributeToDoubleArray(0);
    for (int i = 0; i < values.length; i++)
      assertEquals("value #" + (i+1) + " differs", expected[i], values[i]);
  }
  
  /**
   * Tests the merging of three attributes with various filter setups.
   */
  public void testMergeThreeAttributes() {
    MergeManyAttributes filter;
    
    filter = new MergeManyAttributes();
    performThreeAttributesMerge(
	filter, 
	new double[]{
	    1.0,
	    weka.core.Utils.missingValue(),
	    weka.core.Utils.missingValue(),
	    weka.core.Utils.missingValue(),
	    weka.core.Utils.missingValue(),
	});
    
    filter = new MergeManyAttributes();
    filter.setDiffer(new SelectedTag(MergeManyAttributes.VALUESDIFFER_AVERAGE, MergeManyAttributes.TAGS_VALUESDIFFER));
    performThreeAttributesMerge(
	filter, 
	new double[]{
	    1.0,
	    2.0,
	    weka.core.Utils.missingValue(),
	    weka.core.Utils.missingValue(),
	    weka.core.Utils.missingValue(),
	});
    
    filter = new MergeManyAttributes();
    filter.setOneMissing(new SelectedTag(MergeManyAttributes.ONEMISSING_USE_FIRST_PRESENT, MergeManyAttributes.TAGS_ONEMISSING));
    performThreeAttributesMerge(
	filter, 
	new double[]{
	    1.0,
	    weka.core.Utils.missingValue(),
	    1.0,
	    2.0,
	    weka.core.Utils.missingValue(),
	});
  }

  /**
   * Returns a test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(MergeManyAttributesTest.class);
  }

  /**
   * Runs the test from the commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    AdamsTestHelper.setRegressionRoot();
    TestRunner.run(suite());
  }
}
