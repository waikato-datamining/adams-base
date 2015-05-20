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
 * ArrayLinearRegressionTest.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.data.statistics;

import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the adams.data.statistics.ArrayLinearRegression class. Run from commandline with: <br><br>
 * java adams.data.statistics.ArrayLinearRegressionTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ArrayLinearRegressionTest
  extends AbstractArrayStatisticTestCase<ArrayLinearRegression, Number> {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ArrayLinearRegressionTest(String name) {
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
	  {17.8,18.4,3,29.6,29,25.3,16,18.5,12.5,14.8,18.7,20.1,11.5,20.9,27.3,26.7,31.4,3.7,33.6,8.5},
	  {17.690966295,18.8127779145,3.3837374692,29.313412074,28.1588867403,24.7165833722,15.9520974295,14.0074367493,12.4837409942,15.1978102491,19.2109292622,19.9498787108,11.9924352325,20.9209357764,27.9838522852,26.2497247507,30.4140318409,3.8298797721,32.7872438394,8.8162095405}
	},
    };

    return result;
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected ArrayLinearRegression[] getRegressionSetups() {
    ArrayLinearRegression[]	result;

    result = new ArrayLinearRegression[1];

    result[0] = new ArrayLinearRegression();

    return result;
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(ArrayLinearRegressionTest.class);
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
