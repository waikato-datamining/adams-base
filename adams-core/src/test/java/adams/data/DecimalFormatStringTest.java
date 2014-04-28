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
 * DecimalFormatStringTest.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package adams.data;

import adams.core.Utils;
import adams.data.DecimalFormatString;
import adams.env.Environment;
import adams.test.AdamsTestCase;

/**
 * Tests the adams.data.DecimalFormatString class.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DecimalFormatStringTest
  extends AdamsTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public DecimalFormatStringTest(String name) {
    super(name);
  }

  /**
   * Tests default constructor.
   */
  public void testDefaultConstructor() {
    try {
      new DecimalFormatString();
    }
    catch (Exception e) {
      fail("Failed to use default constructor: " + e);
    }
  }

  /**
   * Subjects a format to a test.
   * 
   * @param format		the format to test
   * @param succeedOrFail	if true then a failure results in a test failure,
   * 				otherwise a success results in test failure
   */
  protected void runFormatTest(String format, boolean succeedOrFail) {
    try {
      DecimalFormatString dfs = new DecimalFormatString(format);
      if (!succeedOrFail && dfs.getValue().equals(format))
	fail("Unexpected success using constructor with format string '" + format + "'!");
      if (succeedOrFail && !dfs.getValue().equals(format))
	fail("Failed to use constructor with format string '" + format + "'!");
    }
    catch (Exception e) {
      if (succeedOrFail)
	fail("Failed to use constructor with format string '" + format + "': " + e);
    }
  }
  
  /**
   * Tests custom constructor with empty format string.
   */
  public void testCustomConstructorEmptyFormat() {
    runFormatTest("", true);
  }

  /**
   * Tests custom constructor with custom format string.
   */
  public void testCustomConstructorValidFormat() {
    runFormatTest("0.0", true);
  }

  /**
   * Tests custom constructor with custom format string.
   */
  public void testCustomConstructorValidFormat2() {
    runFormatTest("0.0;-0.0", true);
  }

  /**
   * Tests custom constructor with custom format string.
   */
  public void testCustomConstructorInvalidFormat() {
    runFormatTest("0.0.0", false);
  }
}
