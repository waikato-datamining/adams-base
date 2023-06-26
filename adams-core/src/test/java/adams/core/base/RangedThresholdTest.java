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
 * RangedThresholdTest.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the adams.core.base.RangedThreshold class. Run from commandline with: <br><br>
 * java adams.core.base.RangedThresholdTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class RangedThresholdTest
  extends AbstractBaseObjectTestCase<RangedThreshold> {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public RangedThresholdTest(String name) {
    super(name);
  }

  /**
   * Returns a default base object.
   *
   * @return		the default object
   */
  protected RangedThreshold getDefault() {
    return new RangedThreshold();
  }

  /**
   * Returns a base object initialized with the given string.
   *
   * @param s		the string to initialize the object with
   * @return		the custom object
   */
  protected RangedThreshold getCustom(String s) {
    return new RangedThreshold(s);
  }

  /**
   * Returns the string representing a typical value to parse that doesn't
   * fail.
   *
   * @return		the value
   */
  protected String getTypicalValue() {
    return "0,100:10";
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(RangedThresholdTest.class);
  }

  /**
   * Tests multiple specs.
   */
  public void testMultiple() {
    RangedThreshold t = new RangedThreshold();
    String value = "0,100:10;100,200:20;200,300:30";
    assertTrue("Should be valid: " + value, t.isValid(value));
    assertEquals("Should be three specs: " + value, 3, new RangedThreshold(value).thresholdsValue().length);
  }

  /**
   * Tests min < max.
   */
  public void testMinMax() {
    RangedThreshold t = new RangedThreshold();
    String value;

    value = "100,0:10";
    assertFalse("Should be invalid: " + value, t.isValid(value));

    value = "100,100:10";
    assertFalse("Should be invalid: " + value, t.isValid(value));
  }

  /**
   * Tests incomplete specs.
   */
  public void testIncomplete() {
    RangedThreshold t = new RangedThreshold();
    String value;

    value = "0";
    assertFalse("Should be invalid: " + value, t.isValid(value));

    value = "0:10";
    assertFalse("Should be invalid: " + value, t.isValid(value));

    value = ":10";
    assertFalse("Should be invalid: " + value, t.isValid(value));

    value = "0,10";
    assertFalse("Should be invalid: " + value, t.isValid(value));

    value = "0,:10";
    assertFalse("Should be invalid: " + value, t.isValid(value));
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
