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
 * SearchParametersTest.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;
import adams.test.AdamsTestCase;

/**
 * Tests the adams.gui.core.SearchParameters class. Run from commandline with: <br><br>
 * java adams.gui.core.SearchParametersTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SearchParametersTest
  extends AdamsTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SearchParametersTest(String name) {
    super(name);
  }

  /**
   * Tests using an empty search string.
   */
  public void testNullSearchString() {
    SearchParameters params = new SearchParameters(null);
    assertTrue("matching non-empty string", params.matches("hello"));
    assertFalse("matching 0", params.matches(0));
    assertFalse("matching !0", params.matches(42));
    assertFalse("matching 0.0", params.matches(0.0));
    assertFalse("matching !0.0", params.matches(42.0));
  }

  /**
   * Tests using an empty search string.
   */
  public void testEmptySearchString() {
    SearchParameters params = new SearchParameters("");
    assertTrue("matching non-empty string", params.matches("hello"));
    assertFalse("matching 0", params.matches(0));
    assertFalse("matching !0", params.matches(42));
    assertFalse("matching 0.0", params.matches(0.0));
    assertFalse("matching !0.0", params.matches(42.0));
  }

  /**
   * Tests the simple substring matching.
   */
  public void testSubstringMatching() {
    SearchParameters params = new SearchParameters("h");
    assertTrue("matching non-empty string", params.matches("hello"));
    assertFalse("matching 0", params.matches(0));
    assertFalse("matching !0", params.matches(42));
    assertFalse("matching 0.0", params.matches(0.0));
    assertFalse("matching !0.0", params.matches(42.0));

    params = new SearchParameters("blah");
    assertFalse("matching non-empty string", params.matches("hello"));
    assertFalse("matching 0", params.matches(0));
    assertFalse("matching !0", params.matches(42));
    assertFalse("matching 0.0", params.matches(0.0));
    assertFalse("matching !0.0", params.matches(42.1));

    params = new SearchParameters("42");
    assertFalse("matching non-empty string", params.matches("hello"));
    assertFalse("matching 0", params.matches(0));
    assertTrue("matching !0", params.matches(42));
    assertFalse("matching 0.0", params.matches(0.0));
    assertFalse("matching !0.0", params.matches(42.1));

    params = new SearchParameters("42.1");
    assertFalse("matching non-empty string", params.matches("hello"));
    assertFalse("matching 0", params.matches(0));
    assertFalse("matching !0", params.matches(42));
    assertFalse("matching 0.0", params.matches(0.0));
    assertTrue("matching !0.0", params.matches(42.1));
  }

  /**
   * Tests the regular expression matching.
   */
  public void testRegExpMatching() {
    SearchParameters params = new SearchParameters("h.*", true);
    assertTrue("matching non-empty string", params.matches("hello"));
    assertFalse("matching 0", params.matches(0));
    assertFalse("matching !0", params.matches(42));
    assertFalse("matching 0.0", params.matches(0.0));
    assertFalse("matching !0.0", params.matches(42.0));

    params = new SearchParameters("h", true);
    assertFalse("matching non-empty string", params.matches("hello"));
    assertFalse("matching 0", params.matches(0));
    assertFalse("matching !0", params.matches(42));
    assertFalse("matching 0.0", params.matches(0.0));
    assertFalse("matching !0.0", params.matches(42.0));

    params = new SearchParameters("blah", true);
    assertFalse("matching non-empty string", params.matches("hello"));
    assertFalse("matching 0", params.matches(0));
    assertFalse("matching !0", params.matches(42));
    assertFalse("matching 0.0", params.matches(0.0));
    assertFalse("matching !0.0", params.matches(42.1));

    params = new SearchParameters("42", true);
    assertFalse("matching non-empty string", params.matches("hello"));
    assertFalse("matching 0", params.matches(0));
    assertTrue("matching !0", params.matches(42));
    assertFalse("matching 0.0", params.matches(0.0));
    assertFalse("matching !0.0", params.matches(42.1));

    params = new SearchParameters("42.1", true);
    assertFalse("matching non-empty string", params.matches("hello"));
    assertFalse("matching 0", params.matches(0));
    assertFalse("matching !0", params.matches(42));
    assertFalse("matching 0.0", params.matches(0.0));
    assertTrue("matching !0.0", params.matches(42.1));

    params = new SearchParameters("[0-9][0-9]", true);
    assertTrue("matching non-empty string", params.matches("42"));
    assertFalse("matching non-empty string", params.matches("421"));
    assertFalse("matching 0", params.matches(0));
    assertFalse("matching !0", params.matches(42));
    assertFalse("matching 0.0", params.matches(0.0));
    assertFalse("matching !0.0", params.matches(42.1));
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(SearchParametersTest.class);
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
