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
 * ArrayLengthTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.statistics;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;

/**
 * Tests the adams.data.statistics.ArrayLength class. Run from commandline with: <p/>
 * java adams.data.statistics.ArrayLengthTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ArrayLengthTest
  extends AbstractArrayStatisticTestCase<ArrayLength, Number> {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ArrayLengthTest(String name) {
    super(name);
  }

  /**
   * Returns the data used in the regression test.
   *
   * @return		the data
   */
  @Override
  protected Number[][][] getRegressionInputData() {
    Number[][][]	result;

    result = new Number[][][]{
	{
	  {1.0, 2.0, 3.0, 4.0}
	},
	{
	  {1.0, 2.0, 3.0, 4.0},
	  {4.0, 3.0, 2.0, 1.0, 0.0}
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
  @Override
  protected ArrayLength[] getRegressionSetups() {
    ArrayLength[]	result;

    result = new ArrayLength[5];

    result[0] = new ArrayLength();
    result[1] = new ArrayLength();
    result[2] = new ArrayLength();
    result[3] = new ArrayLength();
    result[4] = new ArrayLength();

    return result;
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(ArrayLengthTest.class);
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
