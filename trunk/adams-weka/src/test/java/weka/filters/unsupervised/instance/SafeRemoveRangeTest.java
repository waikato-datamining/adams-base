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
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package weka.filters.unsupervised.instance;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.M5P;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.TestInstances;
import weka.filters.AbstractAdamsFilterTest;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.SafeRemoveRange;
import weka.test.AdamsTestHelper;

/**
 * Tests SafeRemoveRange. Run from the command line with: <p/>
 * java weka.filters.unsupervised.instance.SafeRemoveRangeTest
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SafeRemoveRangeTest
  extends AbstractAdamsFilterTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SafeRemoveRangeTest(String name) {
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
   * returns the configured FilteredClassifier. Since the base classifier is
   * determined heuristically, derived tests might need to adjust it.
   *
   * @return the configured FilteredClassifier
   */
  protected FilteredClassifier getFilteredClassifier() {
    FilteredClassifier	result;

    result = new FilteredClassifier();
    result.setFilter(getFilter());
    result.setClassifier(new M5P());

    return result;
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
    testinst.setClassType(Attribute.NUMERIC);
    testinst.setNumInstances(50);

    return testinst.generate();
  }

  /**
   * Creates a SafeRemoveRange with range "5-10".
   *
   * @return		the filter
   */
  public Filter getFilter() {
    return getFilter("5-10", false);
  }

  /**
   * Creates a SafeRemoveRange with the defined range of instances.
   *
   * @param range	the range to use
   * @param invert	whether to invert matching
   * @return		the filter
   */
  public Filter getFilter(String range, boolean invert) {
    SafeRemoveRange	result;

    result = new SafeRemoveRange();
    result.setInstancesIndices(range);
    result.setInvertSelection(invert);

    return result;
  }

  /**
   * performs the actual test.
   *
   * @param num		the expected number of instances
   */
  protected void performTest(int num) {
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

    assertEquals(m_Instances.numAttributes(), result.numAttributes());
    assertEquals(num, result.numInstances());
  }

  /**
   * Test default.
   */
  public void testDefault() {
    m_Filter = getFilter();
    testBuffered();
    performTest(44);
  }

  /**
   * Test empty, removing first-last.
   */
  public void testEmpty() {
    m_Filter = getFilter("first-last", false);
    testBuffered();
    performTest(0);
  }

  /**
   * Test inverted (first-last).
   */
  public void testInverted() {
    m_Filter = getFilter("first-last", true);
    testBuffered();
    performTest(50);
  }

  /**
   * Test inverted (5-10).
   */
  public void testInverted2() {
    m_Filter = getFilter("5-10", true);
    testBuffered();
    performTest(6);
  }

  /**
   * Returns a test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(SafeRemoveRangeTest.class);
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
