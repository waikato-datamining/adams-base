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
 * SavitzkyGolayTest.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.utils;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;
import adams.test.AdamsTestCase;

/**
 * Tests the adams.data.utils.SavitzkyGolayTest class. Run from commandline with: <br><br>
 * java adams.data.utils.SavitzkyGolayTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SavitzkyGolayTest
  extends AdamsTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SavitzkyGolayTest(String name) {
    super(name);
  }

  /**
   * Performs a regression test on various parameter combinations.
   */
  public void testRegression() {
    double[][]		coefficients;
    int[][]	 	params;
    int			i;
    int			n;
    StringBuilder	result;

    if (m_NoRegressionTest)
      return;

    params = new int[][]{
	{3, 3, 2, 0},
	{3, 3, 2, 1},
	{3, 3, 2, 2},
	{7, 7, 2, 1},
	{0, 3, 2, 1},
	{3, 0, 2, 1},
	{5, 5, 2, 1},
	{5, 5, 3, 1},
	{5, 5, 4, 1}
    };
    coefficients = new double[params.length][];

    result = new StringBuilder();
    for (i = 0; i < params.length; i++) {
      // parameters
      result.append(
	  (i+1) + ": left=" + params[i][0] + ", right=" + params[i][1]
	  + ", poly=" + params[i][2] + ", deriv=" + params[i][3] + "\n");

      // coefficients
      coefficients[i] = SavitzkyGolay.determineCoefficients(
		params[i][0], params[i][1], params[i][2], params[i][3]);
      for (n = 0; n < coefficients[i].length; n++) {
	if (n > 0)
	  result.append(", ");
	result.append(coefficients[i][n]);
      }
      result.append("\n");

      // separator
      result.append("\n");
    }

    m_Regression.compare(result.toString());
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(SavitzkyGolayTest.class);
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
