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
 * ArrayDimensionsTest.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;

/**
 * Tests the adams.core.base.ArrayDimensions class. Run from commandline with: <br><br>
 * java adams.core.base.ArrayDimensionsTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ArrayDimensionsTest
  extends AbstractBaseObjectTestCase<ArrayDimensions> {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ArrayDimensionsTest(String name) {
    super(name);
  }

  /**
   * Returns a default base object.
   *
   * @return		the default object
   */
  @Override
  protected ArrayDimensions getDefault() {
    return new ArrayDimensions();
  }

  /**
   * Returns a base object initialized with the given string.
   *
   * @param s		the string to initialize the object with
   * @return		the custom object
   */
  @Override
  protected ArrayDimensions getCustom(String s) {
    return new ArrayDimensions(s);
  }

  /**
   * Returns the string representing a typical value to parse that doesn't
   * fail.
   *
   * @return		the value
   */
  @Override
  protected String getTypicalValue() {
    return "[2]";
  }

  /**
   * Tests the parsing.
   * 
   * @param dim		the dimension string to test
   */
  protected void performTest(String dim) {
    try {
      ArrayDimensions b = getCustom(dim);
      assertEquals("values differ", dim, b.getValue());
    }
    catch (Exception e) {
      fail("Parsing failed: " + e);
    }
  }
  
  /**
   * Tests the parsing of 2 dimensions.
   */
  public void testTwoDimensions() {
    performTest("[2][3]");
  }

  /**
   * Tests the parsing of 3 dimensions.
   */
  public void testThreeDimensions() {
    performTest("[2][3][1]");
  }

  /**
   * Tests the parsing which must fail.
   * 
   * @param dim		the dimension string to test
   */
  public void testIsValid() {
    ArrayDimensions b = new ArrayDimensions();
    String value;
    
    value = "[2]";
    assertTrue("Testing: " + value, b.isValid(value));
    
    value = "2]";
    assertFalse("Testing: " + value, b.isValid(value));
    
    value = "[]";
    assertFalse("Testing: " + value, b.isValid(value));
    
    value = "[2";
    assertFalse("Testing: " + value, b.isValid(value));
    
    value = "[2][4][7][8]";
    assertTrue("Testing: " + value, b.isValid(value));
    
    value = "[2][";
    assertFalse("Testing: " + value, b.isValid(value));
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(ArrayDimensionsTest.class);
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
