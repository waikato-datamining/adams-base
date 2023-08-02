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
 * JavaRandomByteTest.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */
package adams.data.random;

import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test class for the JavaRandomByte random number generator filter. Run from the command line with: <br><br>
 * java adams.data.random.JavaRandomByteTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class JavaRandomByteTest
  extends AbstractRandomNumberGeneratorTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public JavaRandomByteTest(String name) {
    super(name);
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected RandomNumberGenerator[] getRegressionSetups() {
    JavaRandomByte[]	result;

    result = new JavaRandomByte[1];

    result[0] = new JavaRandomByte();

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(JavaRandomByteTest.class);
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
