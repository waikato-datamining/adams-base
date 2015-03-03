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
 * VariableNameNoUpdateTest.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.core;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;
import adams.test.AdamsTestCase;

/**
 * Tests the adams.core.VariableNameNoUpdate class. Run from commandline with: <p/>
 * java adams.core.VariableNameNoUpdateTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class VariableNameNoUpdateTest
  extends AdamsTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public VariableNameNoUpdateTest(String name) {
    super(name);
  }

  /**
   * Checks the default constructor.
   */
  public void testDefault() {
    VariableNameNoUpdate name = new VariableNameNoUpdate();
    assertTrue("invalid variable name", Variables.isValidName(name.getValue()));
  }

  /**
   * Tests whether names are valid.
   *
   * @param name	the name to check
   * @param valid	the expected validity
   */
  protected void performNameTest(String name, boolean valid) {
    VariableNameNoUpdate vname = new VariableNameNoUpdate();
    assertEquals("validity differs", valid, vname.isValid(name));
  }

  /**
   * Checks the default constructor.
   */
  public void testNames() {
    performNameTest("", false);
    performNameTest("1", true);
    performNameTest("a", true);
    performNameTest("A", true);
    performNameTest("a1", true);
    performNameTest("a-z", true);
    performNameTest("a_b", true);
    performNameTest("a\tb", false);
    performNameTest("a&b", false);
    performNameTest("a@b", false);
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(VariableNameNoUpdateTest.class);
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
