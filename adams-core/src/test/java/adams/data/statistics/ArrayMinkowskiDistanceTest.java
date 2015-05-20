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
 * ArrayMinkowskiDistanceTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.statistics;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;

/**
 * Tests the adams.data.statistics.ArrayMinkowskiDistance class. Run from commandline with: <br><br>
 * java adams.data.statistics.ArrayMinkowskiDistanceTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8769 $
 */
public class ArrayMinkowskiDistanceTest
  extends AbstractArrayStatisticTestCase<ArrayMinkowskiDistance, Number> {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ArrayMinkowskiDistanceTest(String name) {
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
	{
	  {5.1, 3.5, 1.4, 0.2},
	  {7.0, 3.2, 4.7, 1.4},
	  {6.3, 3.3, 6.0, 2.5}
	},
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
  protected ArrayMinkowskiDistance[] getRegressionSetups() {
    ArrayMinkowskiDistance[]	result;

    result = new ArrayMinkowskiDistance[3];

    result[0] = new ArrayMinkowskiDistance();
    result[1] = new ArrayMinkowskiDistance();
    result[1].setExponent(1.5);
    result[2] = new ArrayMinkowskiDistance();
    result[2].setExponent(-1.5);

    return result;
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(ArrayMinkowskiDistanceTest.class);
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
