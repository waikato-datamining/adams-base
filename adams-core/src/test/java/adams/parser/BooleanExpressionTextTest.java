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
 * BooleanExpressionTextTest.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package adams.parser;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;
import adams.core.base.AbstractBaseObjectTestCase;

/**
 * Tests the adams.parser.BooleanExpressionText class. Run from commandline with: <br><br>
 * java adams.parser.BooleanExpressionTextTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BooleanExpressionTextTest
  extends AbstractBaseObjectTestCase<BooleanExpressionText> {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public BooleanExpressionTextTest(String name) {
    super(name);
  }

  /**
   * Returns a default base object.
   *
   * @return		the default object
   */
  protected BooleanExpressionText getDefault() {
    return new BooleanExpressionText();
  }

  /**
   * Returns a base object initialized with the given string.
   *
   * @param s		the string to initialize the object with
   * @return		the custom object
   */
  protected BooleanExpressionText getCustom(String s) {
    return new BooleanExpressionText(s);
  }

  /**
   * Returns the string representing a typical value to parse that doesn't
   * fail.
   *
   * @return		the value
   */
  protected String getTypicalValue() {
    return "exp(2, 10) > 0";
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(BooleanExpressionTextTest.class);
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
