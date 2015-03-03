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
 * ArrayEuclideanDistanceTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.statistics;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;

/**
 * Tests the adams.data.statistics.ArrayEuclideanDistance class. Run from commandline with: <p/>
 * java adams.data.statistics.ArrayEuclideanDistanceTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8769 $
 */
public class ArrayEuclideanDistanceTest
  extends AbstractArrayStatisticTestCase<ArrayEuclideanDistance, Number> {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ArrayEuclideanDistanceTest(String name) {
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
	  {5.1, 3.5, 1.4, 0.2},
	  {7.0, 3.2, 4.7, 1.4},
	  {6.3, 3.3, 6.0, 2.5}
	},
    };

    return result;
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected ArrayEuclideanDistance[] getRegressionSetups() {
    ArrayEuclideanDistance[]	result;

    result = new ArrayEuclideanDistance[1];

    result[0] = new ArrayEuclideanDistance();

    return result;
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(ArrayEuclideanDistanceTest.class);
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
