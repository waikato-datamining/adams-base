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
 * Mat5CharToStringTest.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;
import us.hebi.matlab.mat.format.Mat5;
import us.hebi.matlab.mat.types.Char;

/**
 * Tests the Mat5CharToString conversion.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class Mat5CharToStringTest
  extends AbstractConversionTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public Mat5CharToStringTest(String name) {
    super(name);
  }

  /**
   * Returns the input data to use in the regression test.
   *
   * @return		the objects
   */
  @Override
  protected Object[] getRegressionInput() {
    Char[] chr;

    chr    = new Char[2];
    chr[0] = Mat5.newChar(1, 3);
    chr[0].setChar(new int[]{0, 0}, 'a');
    chr[0].setChar(new int[]{0, 1}, 'b');
    chr[0].setChar(new int[]{0, 2}, 'c');
    chr[1] = Mat5.newChar(3, 1);
    chr[1].setChar(new int[]{0, 0}, 'a');
    chr[1].setChar(new int[]{1, 0}, 'b');
    chr[1].setChar(new int[]{2, 0}, 'c');

    return chr;
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected Conversion[] getRegressionSetups() {
    Mat5CharToString[] result;

    result    = new Mat5CharToString[1];
    result[0] = new Mat5CharToString();

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
    return new TestSuite(Mat5CharToStringTest.class);
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
