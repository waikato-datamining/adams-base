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
 * BaseURITest.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;

/**
 * Tests the adams.core.base.BaseURI class. Run from commandline with: <p/>
 * java adams.core.base.BaseURITest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseURITest
  extends AbstractBaseObjectTestCase<BaseURI> {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public BaseURITest(String name) {
    super(name);
  }

  /**
   * Returns a default base object.
   *
   * @return		the default object
   */
  protected BaseURI getDefault() {
    return new BaseURI();
  }

  /**
   * Returns a base object initialized with the given string.
   *
   * @param s		the string to initialize the object with
   * @return		the custom object
   */
  protected BaseURI getCustom(String s) {
    return new BaseURI(s);
  }

  /**
   * Returns the string representing a typical value to parse that doesn't
   * fail.
   *
   * @return		the value
   */
  protected String getTypicalValue() {
    return "http://www.waikato.ac.nz/";
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(BaseURITest.class);
  }

  /**
   * Tests the parsing of a URL with a port.
   */
  public void testPort() {
    String value = "https://www.waikato.ac.nz:443/";
    try {
      BaseURI b = getCustom(value);
      assertEquals("values differ", value, b.getValue());
    }
    catch (Exception e) {
      fail("Parsing failed: " + e);
    }
  }

  /**
   * Tests the parsing of a JDBC URI.
   */
  public void testJDBC() {
    String value = "jdbc:mysql://localhost:3306/adams";
    try {
      BaseURI b = getCustom(value);
      assertEquals("values differ", value, b.getValue());
    }
    catch (Exception e) {
      fail("Parsing failed: " + e);
    }
  }

  /**
   * Tests the parsing of a mailto URI.
   */
  public void testMailto() {
    String value = "mailto:help@nowhere.com";
    try {
      BaseURI b = getCustom(value);
      assertEquals("values differ", value, b.getValue());
    }
    catch (Exception e) {
      fail("Parsing failed: " + e);
    }
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
