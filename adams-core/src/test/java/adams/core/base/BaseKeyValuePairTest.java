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
 * BaseKeyValuePairTest.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the adams.core.base.BaseKeyValuePair class. Run from commandline with: <br><br>
 * java adams.core.base.BaseKeyValuePairTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseKeyValuePairTest
  extends AbstractBaseObjectTestCase<BaseKeyValuePair> {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public BaseKeyValuePairTest(String name) {
    super(name);
  }

  /**
   * Returns a default base object.
   *
   * @return		the default object
   */
  protected BaseKeyValuePair getDefault() {
    return new BaseKeyValuePair();
  }

  /**
   * Returns a base object initialized with the given string.
   *
   * @param s		the string to initialize the object with
   * @return		the custom object
   */
  protected BaseKeyValuePair getCustom(String s) {
    return new BaseKeyValuePair(s);
  }

  /**
   * Returns the string representing a typical value to parse that doesn't
   * fail.
   *
   * @return		the value
   */
  protected String getTypicalValue() {
    return "key=value";
  }

  /**
   * Tests a simple key=value pair.
   */
  public void testSimple() {
    BaseKeyValuePair pair = new BaseKeyValuePair();
    pair.setValue("blah=something else");
    assertEquals("blah", pair.getPairKey());
    assertEquals("something else", pair.getPairValue());
  }

  /**
   * Tests empty value.
   */
  public void testEmptyValue() {
    BaseKeyValuePair pair = new BaseKeyValuePair();
    pair.setValue("blah=");
    assertEquals("blah", pair.getPairKey());
    assertEquals("", pair.getPairValue());
  }

  /**
   * Tests an invalid value.
   */
  public void testInvalidValue() {
    BaseKeyValuePair pair = new BaseKeyValuePair();
    pair.setValue("blah");
    assertEquals("", pair.getPairKey());
    assertEquals("", pair.getPairValue());
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(BaseKeyValuePairTest.class);
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
