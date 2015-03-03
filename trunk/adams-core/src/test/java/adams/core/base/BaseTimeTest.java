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
 * BaseTimeTest.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;

/**
 * Tests the adams.core.base.BaseTime class. Run from commandline with: <p/>
 * java adams.core.base.BaseTimeTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseTimeTest
  extends AbstractBaseObjectTestCase<BaseTime> {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public BaseTimeTest(String name) {
    super(name);
  }

  /**
   * Returns a default base object.
   *
   * @return		the default object
   */
  protected BaseTime getDefault() {
    return new BaseTime();
  }

  /**
   * Returns a base object initialized with the given string.
   *
   * @param s		the string to initialize the object with
   * @return		the custom object
   */
  protected BaseTime getCustom(String s) {
    return new BaseTime(s);
  }

  /**
   * Returns the string representing a typical value to parse that doesn't
   * fail.
   *
   * @return		the value
   */
  protected String getTypicalValue() {
    return "12:34:00";
  }

  /**
   * Tests the detection of infinity strings.
   */
  public void testInfinity() {
    // typical value
    BaseTime b = new BaseTime(getTypicalValue());
    assertFalse(b.isInfinity());
    assertFalse(b.isInfinityPast());
    assertFalse(b.isInfinityFuture());

    // future
    b = new BaseTime(BaseTime.INF_FUTURE);
    assertTrue(b.isInfinity());
    assertFalse(b.isInfinityPast());
    assertTrue(b.isInfinityFuture());

    // past
    b = new BaseTime(BaseTime.INF_PAST);
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
    return new TestSuite(BaseTimeTest.class);
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
