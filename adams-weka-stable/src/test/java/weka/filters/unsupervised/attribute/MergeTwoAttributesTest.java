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

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.filters.AbstractAdamsFilterTest;
import weka.filters.Filter;
import weka.test.AdamsTestHelper;

/**
 * Tests MergeTwoAttributes. Run from the command line with: <br><br>
 * java weka.filters.unsupervised.attribute.MergeTwoAttributesTest
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MergeTwoAttributesTest
  extends AbstractAdamsFilterTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public MergeTwoAttributesTest(String name) {
    super(name);
  }

  /**
   * The default filter setup to test.
   * 
   * @return		the filter
   */
  @Override
  public Filter getFilter() {
    MergeTwoAttributes	result;
    
    result = new MergeTwoAttributes();
    result.setFirstAttribute("NominalAtt1");
    result.setSecondAttribute("NominalAtt2");
    
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
   * Returns a test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(MergeTwoAttributesTest.class);
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
