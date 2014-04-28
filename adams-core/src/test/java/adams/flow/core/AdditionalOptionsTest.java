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
 * AdditionalOptionsTest.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.core;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;
import adams.test.AdamsTestCase;

/**
 * Tests the adams.flow.core.AdditionalOptions class. Run from commandline with: <p/>
 * java adams.flow.core.AdditionalOptionsTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class AdditionalOptionsTest
  extends AdamsTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public AdditionalOptionsTest(String name) {
    super(name);
  }

  /**
   * Tests the boolean setting/getting.
   */
  public void testBoolean() {
    AdditionalOptions	add;

    add = new AdditionalOptions();
    assertNull("Hashtable should have been empty", add.getBoolean("blah"));
    assertEquals("Empty hashtable did not return default value", add.getBoolean("blah", true), new Boolean(true));
    assertEquals("Should have contained no key-value pair", 0, add.size());

    add.putBoolean("blah", false);
    assertEquals("Non-empty hashtable did not return value", add.getBoolean("blah", true), new Boolean(false));
    assertEquals("Should have contained 1 key-value pair", 1, add.size());
  }

  /**
   * Tests the integer setting/getting.
   */
  public void testInteger() {
    AdditionalOptions	add;

    add = new AdditionalOptions();
    assertNull("Hashtable should have been empty", add.getInteger("blah"));
    assertEquals("Empty hashtable did not return default value", add.getInteger("blah", 1), new Integer(1));
    assertEquals("Should have contained no key-value pair", 0, add.size());

    add.putInteger("blah", 42);
    assertEquals("Non-empty hashtable did not return value", add.getInteger("blah", 1), new Integer(42));
    assertEquals("Should have contained 1 key-value pair", 1, add.size());
  }

  /**
   * Tests the double setting/getting.
   */
  public void testDouble() {
    AdditionalOptions	add;

    add = new AdditionalOptions();
    assertNull("Hashtable should have been empty", add.getDouble("blah"));
    assertEquals("Empty hashtable did not return default value", add.getDouble("blah", 3.1415), new Double(3.1415));
    assertEquals("Should have contained no key-value pair", 0, add.size());

    add.putDouble("blah", 2.7182);
    assertEquals("Non-empty hashtable did not return value", add.getDouble("blah", 3.1415), 2.7182);
    assertEquals("Should have contained 1 key-value pair", 1, add.size());
  }

  /**
   * Tests the string setting/getting.
   */
  public void testString() {
    AdditionalOptions	add;

    add = new AdditionalOptions();
    assertNull("Hashtable should have been empty", add.getString("blah"));
    assertEquals("Empty hashtable did not return default value", add.getString("blah", "yeah"), "yeah");
    assertEquals("Should have contained no key-value pair", 0, add.size());

    add.putString("blah", "hello");
    assertEquals("Non-empty hashtable did not return value", add.getString("blah", "yeah"), "hello");
    assertEquals("Should have contained 1 key-value pair", 1, add.size());
  }

  /**
   * Tests the constructors.
   */
  public void testConstructors() {
    AdditionalOptions	add;

    add = new AdditionalOptions();
    assertEquals("Should have contained no key-value pair", 0, add.size());
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(AdditionalOptionsTest.class);
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
