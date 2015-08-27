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
 * TextTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.compare;

import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test class for the JavaComparable object comparison. Run from the command line with: <br><br>
 * java adams.data.compare.JavaComparableTest
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class JavaComparableTest
  extends AbstractObjectCompareTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public JavaComparableTest(String name) {
    super(name);
  }

  /**
   * Returns the object arrays to process in regression.
   *
   * @return		the arrays
   */
  @Override
  protected Object[][] getRegressionArrays() {
    Object[][]	result;
    
    result = new Object[3][2];
    
    result[0][0] = 1;
    result[0][1] = 1;
    result[1][0] = 1.0;
    result[1][1] = 2.0;
    result[2][0] = "2";
    result[2][1] = "1";

    return result;
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractObjectCompare[] getRegressionSetups() {
    return new JavaComparable[]{
	new JavaComparable(),
    };
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(JavaComparableTest.class);
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
