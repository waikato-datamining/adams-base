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
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
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
 * Tests SavitzkyGolay. Run from the command line with: <p/>
 * java weka.filters.unsupervised.attribute.SavitzkyGolayTest
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SavitzkyGolayTest
  extends AbstractAdamsFilterTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SavitzkyGolayTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs
   */
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
   * Creates a default SavitzkyGolay.
   *
   * @return		the default filter
   */
  public Filter getFilter() {
    return new SavitzkyGolay();
  }

  /**
   * Creates a specialized SavitzkyGolay.
   *
   * @param left	the points on the left side
   * @param right	the points on the right side
   * @param poly	the polynomial order
   * @param der		the order of the derivative
   * @return		the filter
   */
  public Filter getFilter(int left, int right, int poly, int der) {
    SavitzkyGolay 	result;

    result = new SavitzkyGolay();
    result.setNumPointsLeft(left);
    result.setNumPointsRight(right);
    result.setPolynomialOrder(poly);
    result.setDerivativeOrder(der);

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

    SavitzkyGolay filter = (SavitzkyGolay) m_Filter;
    int	numAtts = icopy.numAttributes();
    numAtts -= filter.getNumPointsLeft() + filter.getNumPointsRight() + 1;
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
   * Test default with no left points.
   */
  public void testNoLeft() {
    m_Filter = getFilter(0, 3, 2, 1);
    testBuffered();
    performTest();
  }

  /**
   * Test default with no right points.
   */
  public void testNoRight() {
    m_Filter = getFilter(3, 0, 2, 1);
    testBuffered();
    performTest();
  }

  /**
   * Test default no derivative.
   */
  public void testNoDerivative() {
    m_Filter = getFilter(3, 3, 2, 0);
    testBuffered();
    performTest();
  }

  /**
   * Returns a test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(SavitzkyGolayTest.class);
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
