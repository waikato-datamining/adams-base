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
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.filters.AbstractAdamsFilterTest;
import weka.filters.Filter;
import weka.test.AdamsTestHelper;

/**
 * Tests RemoveDuplicates. Run from the command line with: <br><br>
 * java weka.filters.unsupervised.instance.RemoveDuplicatesTest
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RemoveDuplicates2Test
  extends AbstractAdamsFilterTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public RemoveDuplicates2Test(String name) {
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
    loader.setSource(ClassLoader.getSystemResource("weka/filters/data/bolts_duplicates.arff"));
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
   * Creates a default RemoveDuplicates.
   *
   * @return		the default filter
   */
  public Filter getFilter() {
    return new RemoveDuplicates2();
  }

  /**
   * Creates a specialized RemoveDuplicates.
   *
   * @param inclClass	whether to include the class
   * @param randomize	whether to randomize the data after the removal
   * @return		the filter
   */
  public Filter getFilter(boolean inclClass, boolean randomize) {
    RemoveDuplicates2 result;

    result = new RemoveDuplicates2();
    result.setIncludeClass(inclClass);
    result.setRandomize(randomize);

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

    assertEquals("Number of attributes", icopy.numAttributes(), result.numAttributes());
    assertTrue("Removal of instances", icopy.numInstances() != result.numInstances());
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
   * Test the include class flag.
   */
  public void testIncludeClass() {
    m_Filter = getFilter(true, false);
    testBuffered();
    performTest();
  }

  /**
   * Test the randomize flag.
   */
  public void testRandomize() {
    m_Filter = getFilter(false, true);
    testBuffered();
    performTest();
  }

  /**
   * Returns a test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(RemoveDuplicates2Test.class);
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
