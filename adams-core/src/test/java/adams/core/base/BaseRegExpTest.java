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
 * BaseRegExpTest.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;

/**
 * Tests the adams.core.base.BaseRegExp class. Run from commandline with: <br><br>
 * java adams.core.base.BaseRegExpTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseRegExpTest
  extends AbstractBaseObjectTestCase<BaseRegExp> {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public BaseRegExpTest(String name) {
    super(name);
  }

  /**
   * Returns a default base object.
   *
   * @return		the default object
   */
  protected BaseRegExp getDefault() {
    return new BaseRegExp();
  }

  /**
   * Returns a base object initialized with the given string.
   *
   * @param s		the string to initialize the object with
   * @return		the custom object
   */
  protected BaseRegExp getCustom(String s) {
    return new BaseRegExp(s);
  }

  /**
   * Returns the string representing a typical value to parse that doesn't
   * fail.
   *
   * @return		the value
   */
  protected String getTypicalValue() {
    return ".*";
  }

  /**
   * Tests the parsing of an invalid expression.
   */
  public void testInvalidRegExp() {
    String expr = "(";
    BaseRegExp b = new BaseRegExp(expr);
    assertFalse("values don't differ", expr.equals(b.getValue()));
  }

  /**
   * Tests the matching.
   */
  public void testMatching() {
    String expr = "[Hh]ello.*";
    BaseRegExp b = new BaseRegExp(expr);
    assertTrue("matching failed", b.isMatch("Hello"));
    assertTrue("matching failed", b.isMatch("hello"));
    assertTrue("matching failed", b.isMatch("Hello world"));
    assertFalse("matching failed", b.isMatch("blah"));
  }

  /**
   * Tests the match-all check.
   */
  public void testMatchAllCheck() {
    BaseRegExp b = new BaseRegExp(BaseRegExp.MATCH_ALL);
    assertTrue(b.isMatchAll());

    b = new BaseRegExp("blah");
    assertFalse(b.isMatchAll());

    b = new BaseRegExp("");
    assertFalse(b.isMatchAll());
  }

  /**
   * Tests the empty check.
   */
  public void testEmptyCheck() {
    BaseRegExp b = new BaseRegExp(BaseRegExp.MATCH_ALL);
    assertFalse(b.isEmpty());

    b = new BaseRegExp("blah");
    assertFalse(b.isEmpty());

    b = new BaseRegExp("");
    assertTrue(b.isEmpty());
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(BaseRegExpTest.class);
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
