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
 * BaseDimensionTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;

/**
 * Tests the adams.core.base.BaseDimension class. Run from commandline with: <p/>
 * java adams.core.base.BaseDimensionTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 4584 $
 */
public class BaseDimensionTest
  extends AbstractBaseObjectTestCase<BaseDimension> {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public BaseDimensionTest(String name) {
    super(name);
  }

  /**
   * Returns a default base object.
   *
   * @return		the default object
   */
  @Override
  protected BaseDimension getDefault() {
    return new BaseDimension();
  }

  /**
   * Returns a base object initialized with the given string.
   *
   * @param s		the string to initialize the object with
   * @return		the custom object
   */
  @Override
  protected BaseDimension getCustom(String s) {
    return new BaseDimension(s);
  }

  /**
   * Returns the string representing a typical value to parse that doesn't
   * fail.
   *
   * @return		the value
   */
  @Override
  protected String getTypicalValue() {
    return "1;1";
  }

  /**
   * Tests negative values.
   */
  public void testNegative() {
    BaseDimension b = new BaseDimension("-2;-5");
    assertEquals("width differs", -2, b.dimensionValue().width);
    assertEquals("height differs", -5, b.dimensionValue().height);
  }

  /**
   * Tests negative and positive values.
   */
  public void testMixed() {
    BaseDimension b = new BaseDimension("-2;5");
    assertEquals("width differs", -2, b.dimensionValue().width);
    assertEquals("height differs", 5, b.dimensionValue().height);

    b = new BaseDimension("2;-5");
    assertEquals("width differs", 2, b.dimensionValue().width);
    assertEquals("height differs", -5, b.dimensionValue().height);
  }

  /**
   * Tests factional values.
   */
  public void testFractional() {
    BaseDimension b = new BaseDimension("-2.1;5");
    assertEquals("width differs", 0, b.dimensionValue().width);
    assertEquals("height differs", 0, b.dimensionValue().height);

    b = new BaseDimension("2;-5.1");
    assertEquals("width differs", 0, b.dimensionValue().width);
    assertEquals("height differs", 0, b.dimensionValue().height);

    b = new BaseDimension("2.1;-5.1");
    assertEquals("width differs", 0, b.dimensionValue().width);
    assertEquals("height differs", 0, b.dimensionValue().height);
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(BaseDimensionTest.class);
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
