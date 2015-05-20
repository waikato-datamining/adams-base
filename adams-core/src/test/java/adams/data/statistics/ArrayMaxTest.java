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
 * ArrayMaxTest.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.data.statistics;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;

/**
 * Tests the adams.data.statistics.ArrayMax class. Run from commandline with: <br><br>
 * java adams.data.statistics.ArrayMaxTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ArrayMaxTest
  extends AbstractArrayStatisticTestCase<ArrayMax, Number> {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ArrayMaxTest(String name) {
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
	  {1.0, 2.0, 3.0, 4.0}
	},
	{
	  {1.0, 2.0, 3.0, 4.0},
	  {4.0, 3.0, 2.0, 1.0}
	},
	{
	  {7.0, 1.0, -10.0, 4.0}
	},
	{
	  {7.0, 1.0, -10.0, 4.0}
	},
	{
	  {7.0, 1.0, -10.0, 4.0},
	  {4.0, 3.0, 2.0, 1.0}
	}
    };

    return result;
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected ArrayMax[] getRegressionSetups() {
    ArrayMax[]	result;

    result = new ArrayMax[5];

    result[0] = new ArrayMax();
    result[1] = new ArrayMax();
    result[2] = new ArrayMax();
    result[3] = new ArrayMax();
    result[3].setReturnIndex(true);
    result[4] = new ArrayMax();
    result[4].setReturnIndex(true);

    return result;
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(ArrayMaxTest.class);
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
