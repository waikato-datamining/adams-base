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
import weka.filters.AbstractAdamsFilterTest;
import weka.filters.AllFilter;
import weka.filters.Filter;
import weka.test.AdamsTestHelper;
import adams.core.base.BaseRegExp;

/**
 * Tests MetaPartitionedMultiFilter. Run from the command line with: <br><br>
 * java weka.filters.unsupervised.attribute.MetaPartitionedMultiFilterTest
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MetaPartitionedMultiFilterTest
  extends AbstractAdamsFilterTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public MetaPartitionedMultiFilterTest(String name) {
    super(name);
  }

  /**
   * The default filter setup to test.
   * 
   * @return		the filter
   */
  @Override
  public Filter getFilter() {
    MetaPartitionedMultiFilter	result;
    
    result = new MetaPartitionedMultiFilter();
    result.setFilters(new Filter[]{
	new AllFilter(),
	new AllFilter(),
    });
    result.setRegExp(new BaseRegExp[]{
	new BaseRegExp("Nominal.*"),
	new BaseRegExp("Numeric.*"),
    });
    result.setRemoveUnused(true);
    
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
    
    result = new Instances(m_Instances);
    result.setClassIndex(0);
    
    return result;
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();

    m_Instances.deleteAttributeType(Attribute.STRING);
    m_Instances.deleteAttributeType(Attribute.RELATIONAL);
  }

  /**
   * Returns a test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(MetaPartitionedMultiFilterTest.class);
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
