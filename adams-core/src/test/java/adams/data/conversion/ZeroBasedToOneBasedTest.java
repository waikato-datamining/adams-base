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
 * ZeroBasedToOneBasedTest.java
 * Copyright (C) 2012-2024 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the ZeroBasedToOneBased conversion.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ZeroBasedToOneBasedTest
  extends AbstractConversionTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public ZeroBasedToOneBasedTest(String name) {
    super(name);
  }

  /**
   * Returns the input data to use in the regression test.
   *
   * @return		the objects
   */
  @Override
  protected Object[] getRegressionInput() {
    return new Integer[][]{
	{
	  0,
	  1,
	  2,
	  11,
	  20,
	  31,
	  32,
	  99
	}
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  @SuppressWarnings("deprecation")
  protected Conversion[] getRegressionSetups() {
    ZeroBasedToOneBased[]		result;
    
    result = new ZeroBasedToOneBased[1];
    result[0] = new ZeroBasedToOneBased();

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
    return new TestSuite(ZeroBasedToOneBasedTest.class);
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
