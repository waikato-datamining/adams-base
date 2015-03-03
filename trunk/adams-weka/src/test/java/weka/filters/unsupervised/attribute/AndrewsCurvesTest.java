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
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package weka.filters.unsupervised.attribute;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.TestInstances;
import weka.filters.AbstractAdamsFilterTest;
import weka.filters.Filter;
import weka.test.AdamsTestHelper;

/**
 * Tests AndrewsCurves. Run from the command line with: <p/>
 * java weka.filters.unsupervised.attribute.AndrewsCurvesTest
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 4748 $
 */
public class AndrewsCurvesTest
  extends AbstractAdamsFilterTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public AndrewsCurvesTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();

    m_Instances = getFilteredClassifierData();
  }

  /**
   * returns data generated for the FilteredClassifier test.
   *
   * @return		the dataset for the FilteredClassifier
   * @throws Exception	if generation of data fails
   */
  @Override
  protected Instances getFilteredClassifierData() throws Exception {
    TestInstances	testinst;

    testinst = new TestInstances();
    testinst.setNumNominal(0);
    testinst.setNumNumeric(20);
    testinst.setClassType(Attribute.NOMINAL);
    testinst.setNumInstances(50);

    return testinst.generate();
  }

  /**
   * Creates a default AndrewsCurves.
   *
   * @return		the default filter
   */
  @Override
  public Filter getFilter() {
    return new AndrewsCurves();
  }

  /**
   * Creates a specialized AndrewsCurves.
   *
   * @param num		the number of data points
   * @return		the filter
   */
  public Filter getFilter(int num) {
    AndrewsCurves 	result;

    result = new AndrewsCurves();
    result.setNumPoints(num);

    return result;
  }

  /**
   * performs the actual test.
   */
  protected void performTest() {
    Instances icopy = new Instances(m_Instances);
    Instances result = null;
    try {
      m_Filter.setInputFormat(icopy);
    }
    catch (Exception ex) {
      ex.printStackTrace();
      fail("Exception thrown on setInputFormat(): \n" + ex.getMessage());
    }
    try {
      result = Filter.useFilter(icopy, m_Filter);
      assertNotNull(result);
    }
    catch (Exception ex) {
      ex.printStackTrace();
      fail("Exception thrown on useFilter(): \n" + ex.getMessage());
    }

    AndrewsCurves filter = (AndrewsCurves) m_Filter;
    int	numAtts = filter.getNumPoints() + (icopy.classIndex() > -1 ? 1 : 0);
    assertEquals(numAtts, result.numAttributes());
    assertEquals(icopy.numInstances(), m_Instances.numInstances());
  }

  /**
   * Test default.
   */
  public void testDefault() {
    m_Filter = getFilter();
    testBuffered();
    performTest();
  }

  /**
   * Test default with few data points.
   */
  public void testSmall() {
    m_Filter = getFilter(10);
    testBuffered();
    performTest();
  }

  /**
   * Test default with many data points.
   */
  public void testNoRight() {
    m_Filter = getFilter(1000);
    testBuffered();
    performTest();
  }

  /**
   * Returns a test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(AndrewsCurvesTest.class);
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
