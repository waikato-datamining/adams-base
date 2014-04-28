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
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package weka.filters.unsupervised.attribute;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import weka.core.Instances;
import weka.core.Range;
import weka.core.converters.ArffLoader;
import weka.filters.AbstractAdamsFilterTest;
import weka.filters.Filter;
import weka.test.AdamsTestHelper;

/**
 * Tests CorrelationMatrix. Run from the command line with: <p/>
 * java weka.filters.unsupervised.attribute.CorrelationMatrixTest
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CorrelationMatrixTest
  extends AbstractAdamsFilterTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public CorrelationMatrixTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs
   */
  protected void setUp() throws Exception {
    ArffLoader	loader;

    super.setUp();

    loader = new ArffLoader();
    loader.setSource(ClassLoader.getSystemResource("weka/filters/data/bolts.arff"));
    m_Instances = loader.getDataSet();
    m_Instances.setClassIndex(m_Instances.numAttributes() - 1);
  }

  /**
   * Does nothing.
   *
   * @return		null
   */
  protected Instances getFilteredClassifierData() {
    return null;
  }

  /**
   * Does not generate data for a classifier.
   */
  public void testFilteredClassifier() {
  }

  /**
   * Creates a default CorrelationMatrix.
   *
   * @return		the default filter
   */
  public Filter getFilter() {
    return new CorrelationMatrix();
  }

  /**
   * Creates a specialized CorrelationMatrix.
   *
   * @param range	the attribute range
   * @param abs		whether to absolute the coefficients
   * @return		the filter
   */
  public Filter getFilter(String range, boolean abs) {
    CorrelationMatrix 	result;

    result = new CorrelationMatrix();
    result.setAttributeRange(range);
    result.setAbsolute(abs);

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

    CorrelationMatrix filter = (CorrelationMatrix) m_Filter;
    Range range = new Range(filter.getAttributeRange());
    range.setUpper(icopy.numAttributes() - 1);
    int numeric = 0;
    for (int i = 0; i < icopy.numAttributes(); i++) {
      if (!range.isInRange(i))
	continue;
      if (icopy.attribute(i).isNumeric())
	numeric++;
    }

    assertEquals("Number of attributes", numeric + 1, result.numAttributes());
    assertEquals("Number of instances", numeric, result.numInstances());

    // ensure that values are between -1 and +1 or 0 and +1 (if absolute)
    for (int i = 1; i < result.numAttributes(); i++) {
      for (int n = 0; n < result.numInstances(); n++) {
	double val = result.instance(n).value(i);
	if (filter.getAbsolute())
	  assertTrue("0.0 <= coeff <= +1.0", (val >= 0.0) && (val <= 1.0));
	else
	  assertTrue("-1.0 <= coeff <= +1.0", (val >= -1.0) && (val <= 1.0));
      }
    }
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
   * Test "first-last" but with absolute values.
   */
  public void testAbsolute() {
    m_Filter = getFilter("first-last", true);
    testBuffered();
    performTest();
  }

  /**
   * Test with a different range of attributes.
   */
  public void testRange() {
    m_Filter = getFilter("3-last", false);
    testBuffered();
    performTest();
  }

  /**
   * Returns a test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(CorrelationMatrixTest.class);
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
