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
 * BaseDateTest.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;

/**
 * Tests the adams.core.base.BaseDate class. Run from commandline with: <br><br>
 * java adams.core.base.BaseDateTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseDateTest
  extends AbstractBaseObjectTestCase<BaseDate> {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public BaseDateTest(String name) {
    super(name);
  }

  /**
   * Returns a default base object.
   *
   * @return		the default object
   */
  protected BaseDate getDefault() {
    return new BaseDate();
  }

  /**
   * Returns a base object initialized with the given string.
   *
   * @param s		the string to initialize the object with
   * @return		the custom object
   */
  protected BaseDate getCustom(String s) {
    return new BaseDate(s);
  }

  /**
   * Returns the string representing a typical value to parse that doesn't
   * fail.
   *
   * @return		the value
   */
  protected String getTypicalValue() {
    return "2010-06-01";
  }

  /**
   * Tests the detection of infinity strings.
   */
  public void testInfinity() {
    // typical value
    BaseDate b = new BaseDate(getTypicalValue());
    assertFalse(b.isInfinity());
    assertFalse(b.isInfinityPast());
    assertFalse(b.isInfinityFuture());

    // future
    b = new BaseDate(BaseDate.INF_FUTURE);
    assertTrue(b.isInfinity());
    assertFalse(b.isInfinityPast());
    assertTrue(b.isInfinityFuture());

    // past
    b = new BaseDate(BaseDate.INF_PAST);
    assertTrue(b.isInfinity());
    assertTrue(b.isInfinityPast());
    assertFalse(b.isInfinityFuture());
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(BaseDateTest.class);
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
