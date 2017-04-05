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
 * BaseIntervalTest.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the adams.core.base.BaseInterval class. Run from commandline with: <br><br>
 * java adams.core.base.BaseIntervalTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseIntervalTest
  extends AbstractBaseObjectTestCase<BaseInterval> {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public BaseIntervalTest(String name) {
    super(name);
  }

  /**
   * Returns a default base object.
   *
   * @return		the default object
   */
  protected BaseInterval getDefault() {
    return new BaseInterval();
  }

  /**
   * Returns a base object initialized with the given string.
   *
   * @param s		the string to initialize the object with
   * @return		the custom object
   */
  protected BaseInterval getCustom(String s) {
    return new BaseInterval(s);
  }

  /**
   * Returns the string representing a typical value to parse that doesn't
   * fail.
   *
   * @return		the value
   */
  protected String getTypicalValue() {
    return "[-3.4;12.67)";
  }

  /**
   * Tests valid intervals.
   */
  public void testValidIntervals() {
    BaseInterval b = new BaseInterval();
    String i;

    i = "";
    assertTrue("Testing: " + i, b.isValid(i));
    i = "(-Infinity;+Infinity)";
    assertTrue("Testing: " + i, b.isValid(i));
    i = "[-1;1]";
    assertTrue("Testing: " + i, b.isValid(i));
    i = "(-1;1)";
    assertTrue("Testing: " + i, b.isValid(i));
    i = "[-1;1)";
    assertTrue("Testing: " + i, b.isValid(i));
    i = "(-1;1]";
    assertTrue("Testing: " + i, b.isValid(i));
  }

  /**
   * Tests invalid intervals.
   */
  public void testInvalidIntervals() {
    BaseInterval b = new BaseInterval();
    String i;

    i = "(+Infinity;-Infinity)";
    assertFalse("Testing: " + i, b.isValid(i));
    i = "[1;-1]";
    assertFalse("Testing: " + i, b.isValid(i));
    i = "(1;-1)";
    assertFalse("Testing: " + i, b.isValid(i));
    i = "[1;-1)";
    assertFalse("Testing: " + i, b.isValid(i));
    i = "(1;-1]";
    assertFalse("Testing: " + i, b.isValid(i));
    i = "]1;-1]";
    assertFalse("Testing: " + i, b.isValid(i));
    i = ")1;-1]";
    assertFalse("Testing: " + i, b.isValid(i));
    i = "(1;-1(";
    assertFalse("Testing: " + i, b.isValid(i));
    i = "(1;-1[";
    assertFalse("Testing: " + i, b.isValid(i));
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(BaseIntervalTest.class);
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
