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
 * MathExpressionTest.java
 * Copyright (C) 2018-2020 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import adams.data.RoundingType;
import adams.env.Environment;
import adams.parser.MathematicalExpressionText;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the MathExpression conversion.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class MathExpressionTest
  extends AbstractConversionTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public MathExpressionTest(String name) {
    super(name);
  }

  /**
   * Returns the input data to use in the regression test.
   *
   * @return		the objects
   */
  @Override
  protected Object[] getRegressionInput() {
    return new Double[]{
	1.0,
	-1.0,
	3.1415926535,
	-3.1415926535,
	-1E2,
	1.34E2,
	-4.67E3
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected Conversion[] getRegressionSetups() {
    MathExpression[]	result;

    result = new MathExpression[4];
    result[0] = new MathExpression();
    result[1] = new MathExpression();
    result[1].setExpression(new MathematicalExpressionText("pow(X + 1, 2)"));
    result[2] = new MathExpression();
    result[2].setExpression(new MathematicalExpressionText("pow(X + 1, 2)"));
    result[2].setRoundOutput(true);
    result[2].setRoundingType(RoundingType.ROUND);
    result[3] = new MathExpression();
    result[3].setExpression(new MathematicalExpressionText("pow(X + 1, 2)"));
    result[3].setRoundOutput(true);
    result[3].setRoundingType(RoundingType.RINT);
    result[3].setNumDecimals(1);

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
    return new TestSuite(MathExpressionTest.class);
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
