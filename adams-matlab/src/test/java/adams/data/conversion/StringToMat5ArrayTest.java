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
 * StringToMat5ArrayTest.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;
import us.hebi.matlab.mat.format.Mat5;
import us.hebi.matlab.mat.types.Matrix;

/**
 * Tests the StringToMat5Array conversion.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class StringToMat5ArrayTest
  extends AbstractConversionTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public StringToMat5ArrayTest(String name) {
    super(name);
  }

  /**
   * Turns the data object into a useful string representation.
   *
   * @param data	the object to convert
   * @return		the string representation
   */
  @Override
  protected String toString(Object data) {
    Mat5ArrayToString 	conv;
    String		msg;

    conv = new Mat5ArrayToString();
    conv.setInput(data);
    msg = conv.convert();
    if (msg == null)
      return "" + conv.getOutput();
    else
      return super.toString(data);
  }

  /**
   * Returns the input data to use in the regression test.
   *
   * @return		the objects
   */
  @Override
  protected Object[] getRegressionInput() {
    Matrix    mat;

    mat = Mat5.newMatrix(new int[]{2, 2});
    mat.setDouble(new int[]{0, 0}, 1.0);
    mat.setDouble(new int[]{0, 1}, 2.0);
    mat.setDouble(new int[]{1, 0}, 3.0);
    mat.setDouble(new int[]{1, 1}, 4.0);

    return new Object[]{
      "[1, 2; 3, 4]",
      "[1.1, 2.2, 3.3; 4.4, 5.5, 6.6; 7.7, 8.8, 9.9]",
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected Conversion[] getRegressionSetups() {
    return new Conversion[]{new StringToMat5Array()};
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
    return new TestSuite(StringToMat5ArrayTest.class);
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
