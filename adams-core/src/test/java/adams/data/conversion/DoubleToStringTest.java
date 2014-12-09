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
 * DoubleToStringTest.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;

/**
 * Tests the DoubleToString conversion.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DoubleToStringTest
  extends AbstractConversionTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public DoubleToStringTest(String name) {
    super(name);
  }

  /**
   * Returns the input data to use in the regression test.
   *
   * @return		the objects
   */
  @Override
  protected Object[] getRegressionInput() {
    return new Double[]{
	1.0,
	-1.0,
	3.1415926535,
	-3.1415926535,
	1E6,
	-1E2,
	1.34E2,
	-4.67E3
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected Conversion[] getRegressionSetups() {
    DoubleToString[]	result;

    result    = new DoubleToString[3];
    result[0] = new DoubleToString();
    result[1] = new DoubleToString();
    result[1].setNumDecimals(3);
    result[2] = new DoubleToString();
    result[2].setNumDecimals(3);
    result[2].setFixedDecimals(true);

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
    return new TestSuite(DoubleToStringTest.class);
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
