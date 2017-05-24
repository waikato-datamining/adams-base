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
 * MatrixSubsetTest.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

import adams.core.Range;
import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the adams.core.base.MatrixSubset class. Run from commandline with: <br><br>
 * java adams.core.base.MatrixSubsetTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MatrixSubsetTest
  extends AbstractBaseObjectTestCase<MatrixSubset> {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public MatrixSubsetTest(String name) {
    super(name);
  }

  /**
   * Returns a default base object.
   *
   * @return		the default object
   */
  protected MatrixSubset getDefault() {
    return new MatrixSubset();
  }

  /**
   * Returns a base object initialized with the given string.
   *
   * @param s		the string to initialize the object with
   * @return		the custom object
   */
  protected MatrixSubset getCustom(String s) {
    return new MatrixSubset(s);
  }

  /**
   * Returns the string representing a typical value to parse that doesn't
   * fail.
   *
   * @return		the value
   */
  protected String getTypicalValue() {
    return "1:3,5:6";
  }

  /**
   *
   */
  public void testBlanks() {
    MatrixSubset subset = new MatrixSubset();

    String s = ",";
    assertTrue(s, subset.isValid(s));
    subset.setValue(s);
    assertEquals("Should be all rows", Range.ALL, subset.rowsValue().getRange());
    assertEquals("Should be all cols", Range.ALL, subset.columnsValue().getRange());

    s = "1:2,";
    assertTrue(s, subset.isValid(s));
    subset.setValue(s);
    assertEquals("Should be rows 1-2", "1-2", subset.rowsValue().getRange());
    assertEquals("Should be all cols", Range.ALL, subset.columnsValue().getRange());

    s = ",1:2";
    assertTrue(s, subset.isValid(s));
    subset.setValue(s);
    assertEquals("Should be all rows", Range.ALL, subset.rowsValue().getRange());
    assertEquals("Should be cols 1-2", "1-2", subset.columnsValue().getRange());
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(MatrixSubsetTest.class);
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
