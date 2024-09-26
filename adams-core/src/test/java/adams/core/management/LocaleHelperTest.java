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
 * LocaleHelperTest.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.core.management;

import adams.env.Environment;
import adams.test.AdamsTestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test for: LocaleHelper.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class LocaleHelperTest
  extends AdamsTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name the name of the test
   */
  public LocaleHelperTest(String name) {
    super(name);
  }

  /**
   * Tests the static valueOf method.
   */
  public void testValueOf() {
    assertEquals("locales differ", "en", LocaleHelper.valueOf("en").toString());
    assertEquals("locales differ", "en_US", LocaleHelper.valueOf("en_US").toString());
    assertEquals("locales differ", "en_US_#Latn", LocaleHelper.valueOf("en_US_#Latn").toString());
  }

  /**
   * Tests the getEnUS method.
   */
  public void testEnUs() {
    assertEquals("locales differ", "en_US", LocaleHelper.getSingleton().getEnUS().toString());
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(LocaleHelperTest.class);
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
