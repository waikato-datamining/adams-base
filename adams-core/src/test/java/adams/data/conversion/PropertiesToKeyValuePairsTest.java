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

/**
 * PropertiesToKeyValuePairsTest.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.Properties;

/**
 * Tests the PropertiesToKeyValuePairs conversion.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 13655 $
 */
public class PropertiesToKeyValuePairsTest
  extends AbstractConversionTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public PropertiesToKeyValuePairsTest(String name) {
    super(name);
  }

  /**
   * Returns the input data to use in the regression test.
   *
   * @return		the objects
   */
  @Override
  protected Object[] getRegressionInput() {
    Properties[]	result;

    result    = new Properties[1];
    result[0] = new Properties();
    result[0].setProperty("hello", "world");
    result[0].setProperty("a", "value");
    result[0].setProperty("number", "10.9");

    return result;
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected Conversion[] getRegressionSetups() {
    PropertiesToKeyValuePairs[]	result;

    result = new PropertiesToKeyValuePairs[2];
    result[0] = new PropertiesToKeyValuePairs();
    result[1] = new PropertiesToKeyValuePairs();
    result[1].setUseStrings(true);

    return result;
  }

  /**
   * Returns the ignored line indices to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected int[] getRegressionIgnoredLineIndices() {
    return new int[0];
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(PropertiesToKeyValuePairsTest.class);
  }

  /**
   * Runs the test from commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    runTest(suite());
  }
}
