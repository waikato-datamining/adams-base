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
 * PlaceholdersTest.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.core;

import adams.core.io.TempUtils;
import adams.core.management.OS;
import adams.env.Environment;
import adams.test.AdamsTestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.io.File;
import java.util.Enumeration;

/**
 * Tests the adams.core.Placeholders class. Run from commandline with: <br><br>
 * java adams.core.PlaceholdersTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PlaceholdersTest
  extends AdamsTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public PlaceholdersTest(String name) {
    super(name);
  }

  /**
   * Tests all placeholders.
   */
  public void testGeneric() {
    Enumeration<String>	keys;
    String		key;
    String		expanded;
    String		collapsed;
    Placeholders	ph;

    ph   = Placeholders.getSingleton();
    keys = ph.placeholders();
    while (keys.hasMoreElements()) {
      key       = keys.nextElement();
      collapsed = ph.get(key) + File.separator + "hello.world";
      expanded  = ph.expand(ph.get(key)) + File.separator + "hello.world";
      assertEquals(expanded, ph.expand(collapsed));
    }
  }

  /**
   * Tests the collapse functionality (only linux/mac).
   */
  public void testCollapseLinux() {
    if (!(OS.isLinux() || OS.isMac()))
      return;
    String path = "/some/where" + TempUtils.getTempDirectoryStr();
    assertEquals("shouldn't collapse at end", path, Placeholders.getSingleton().collapse(path));
  }

  /**
   * Tests whether partial collapse is avoided (only linux/mac).
   */
  public void testPartialCollapseLinux() {
    if (!(OS.isLinux() || OS.isMac()))
      return;
    Placeholders.getSingleton().set("BLAH", "/some/where");
    String path = "/some/where.else/completely";
    assertEquals("shouldn't do a partial collapse", path, Placeholders.getSingleton().collapse(path));
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(PlaceholdersTest.class);
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
