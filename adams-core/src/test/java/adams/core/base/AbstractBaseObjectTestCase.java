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
 * AbstractBaseObjectTestCase.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

import adams.test.AdamsTestCase;

/**
 * Ancestor for BaseObject derived classes.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of class to test
 */
public abstract class AbstractBaseObjectTestCase<T extends BaseObject>
  extends AdamsTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public AbstractBaseObjectTestCase(String name) {
    super(name);
  }

  /**
   * Returns a default base object.
   *
   * @return		the default object
   */
  protected abstract T getDefault();

  /**
   * Returns the value of a default object.
   *
   * @return		the default value
   */
  protected String getDefaultValue() {
    return getDefault().getValue();
  }

  /**
   * Returns a base object initialized with the given string.
   *
   * @param s		the string to initialize the object with
   * @return		the custom object
   */
  protected abstract T getCustom(String s);

  /**
   * Tests whether two default objects are equal (testing the "equals(Object)"
   * method).
   */
  public void testCompareDefault() {
    T defObj1 = getDefault();
    T defObj2 = getDefault();
    assertEquals("objects not equal (equals)", defObj1, defObj2);
    assertTrue("objects not equal (compareTo != 0)", (defObj1.compareTo(defObj2) == 0));
  }

  /**
   * Tests whether the default object is the same as the object that got the
   * string pushed through the parser.
   */
  public void testCompareDefaultAndCustom() {
    T defObj = getDefault();
    T custObj = getCustom(defObj.getValue());
    assertEquals("objects not equal (equals)", defObj, custObj);
    assertTrue("objects not equal (compareTo != 0)", (defObj.compareTo(custObj) == 0));
  }

  /**
   * Tests whether the string output of a default object is the same
   * as pushing the default string output through the parser.
   */
  public void testDefault() {
    T defObj = getDefault();
    T custObj = getCustom(defObj.getValue());
    assertEquals("parsed values differ", defObj.getValue(), custObj.getValue());
  }

  /**
   * Returns the string representing a typical value to parse that doesn't
   * fail.
   *
   * @return		the value
   */
  protected abstract String getTypicalValue();

  /**
   * Tests the parsing of a typical value.
   */
  public void testTypicalValue() {
    try {
      T b = getCustom(getTypicalValue());
      assertEquals("values differ", getTypicalValue(), b.getValue());
    }
    catch (Exception e) {
      fail("Parsing failed: " + e);
    }
  }
}
