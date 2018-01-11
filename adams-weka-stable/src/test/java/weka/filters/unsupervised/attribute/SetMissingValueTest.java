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
import weka.core.Utils;
import weka.filters.AbstractAdamsFilterTest;
import weka.filters.Filter;
import weka.test.AdamsTestHelper;

/**
 * Tests SetMissingValue. Run from the command line with: <br><br>
 * java weka.filters.unsupervised.attribute.SetMissingValueTest
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SetMissingValueTest
  extends AbstractAdamsFilterTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SetMissingValueTest(String name) {
    super(name);
  }

  /**
   * Creates a SetMissingValue with 4 points.
   *
   * @return		the default filter
   */
  public Filter getFilter() {
    return getFilter("last");
  }

  /**
   * Creates a specialized SetMissingValue.
   *
   * @param range	the range of attributes
   * @return		the filter
   */
  public Filter getFilter(String range) {
    SetMissingValue 	result;

    result = new SetMissingValue();
    result.setAttributeRange(range);

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

    assertEquals(icopy.numAttributes(), result.numAttributes());
    assertEquals(icopy.numInstances(), m_Instances.numInstances());

    SetMissingValue filter = (SetMissingValue) m_Filter;
    Range range = new Range(filter.getAttributeRange());
    range.setUpper(icopy.numAttributes() - 1);
    int[] indices = range.getSelection();
    for (int n = 0; n < result.numInstances(); n++) {
      for (int i = 0; i < indices.length; i++) {
	assertTrue(
	    "Value at " + (n+1) + "/" + (indices[i]+1) + " not set to missing!",
	    Utils.isMissingValue(result.instance(n).value(indices[i])));
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
   * Test default with first attribute set to missing.
   */
  public void testFirst() {
    m_Filter = getFilter("first");
    testBuffered();
    performTest();
  }

  /**
   * Test default with second attribute set to missing.
   */
  public void testSecond() {
    m_Filter = getFilter("2");
    testBuffered();
    performTest();
  }

  /**
   * Test default with all values set to missing.
   */
  public void testAll() {
    m_Filter = getFilter("first-last");
    testBuffered();
    performTest();
  }

  /**
   * Returns a test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(SetMissingValueTest.class);
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
