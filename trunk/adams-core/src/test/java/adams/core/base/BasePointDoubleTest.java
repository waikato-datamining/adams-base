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
 * BasePointDoubleTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;

/**
 * Tests the adams.core.base.BasePointDouble class. Run from commandline with: <p/>
 * java adams.core.base.BasePointDoubleTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 4584 $
 */
public class BasePointDoubleTest
  extends AbstractBaseObjectTestCase<BasePointDouble> {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public BasePointDoubleTest(String name) {
    super(name);
  }

  /**
   * Returns a default base object.
   *
   * @return		the default object
   */
  @Override
  protected BasePointDouble getDefault() {
    return new BasePointDouble();
  }

  /**
   * Returns a base object initialized with the given string.
   *
   * @param s		the string to initialize the object with
   * @return		the custom object
   */
  @Override
  protected BasePointDouble getCustom(String s) {
    return new BasePointDouble(s);
  }

  /**
   * Returns the string representing a typical value to parse that doesn't
   * fail.
   *
   * @return		the value
   */
  @Override
  protected String getTypicalValue() {
    return "1.0;1.0";
  }

  /**
   * Tests negative values.
   */
  public void testNegative() {
    BasePointDouble b = new BasePointDouble("-2;-5");
    assertEquals("x differs", -2.0, b.pointValue().x);
    assertEquals("y differs", -5.0, b.pointValue().y);
  }

  /**
   * Tests negative and positive values.
   */
  public void testMixed() {
    BasePointDouble b = new BasePointDouble("-2;5");
    assertEquals("x differs", -2.0, b.pointValue().x);
    assertEquals("y differs", 5.0, b.pointValue().y);

    b = new BasePointDouble("2;-5");
    assertEquals("x differs", 2.0, b.pointValue().x);
    assertEquals("y differs", -5.0, b.pointValue().y);
  }

  /**
   * Tests factional values.
   */
  public void testFractional() {
    BasePointDouble b = new BasePointDouble("-2.1;5");
    assertEquals("x differs", -2.1, b.pointValue().x);
    assertEquals("y differs", 5.0, b.pointValue().y);

    b = new BasePointDouble("2;-5.1");
    assertEquals("x differs", 2.0, b.pointValue().x);
    assertEquals("y differs", -5.1, b.pointValue().y);

    b = new BasePointDouble("2.1;-5.1");
    assertEquals("x differs", 2.1, b.pointValue().x);
    assertEquals("y differs", -5.1, b.pointValue().y);
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(BasePointDoubleTest.class);
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
