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
 * ArrayStandardizeTest.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */

package adams.data.statistics;

import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the adams.data.statistics.ArrayStandardize class. Run from commandline with: <br><br>
 * java adams.data.statistics.ArrayStandardizeTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ArrayStandardizeTest
  extends AbstractArrayStatisticTestCase<ArrayStandardize, Number> {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ArrayStandardizeTest(String name) {
    super(name);
  }

  /**
   * Returns the data used in the regression test.
   *
   * @return		the data
   */
  protected Number[][][] getRegressionInputData() {
    Number[][][]	result;

    result = new Number[][][]{
	{
	  {1.0, 2.0, 3.0, 4.0},
	},
	{
	  {7.0, 1.0, 10.0, 4.0},
	},
	{
	  {1.0, 2.0, 3.0, 4.0},
	},
	{
	  {7.0, 1.0, 10.0, 4.0},
	},
    };

    return result;
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected ArrayStandardize[] getRegressionSetups() {
    ArrayStandardize[]	result;

    result = new ArrayStandardize[4];

    result[0] = new ArrayStandardize();
    result[1] = new ArrayStandardize();
    result[2] = new ArrayStandardize();
    result[2].setIsSample(true);
    result[3] = new ArrayStandardize();
    result[3].setIsSample(true);

    return result;
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(ArrayStandardizeTest.class);
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
