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
 * BaseShortcutTest.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

import adams.core.base.AbstractBaseObjectTestCase;
import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

import javax.swing.KeyStroke;

/**
 * Tests the adams.gui.core.BaseShortcut class. Run from commandline with: <br><br>
 * java adams.gui.core.BaseShortcutTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseShortcutTest
  extends AbstractBaseObjectTestCase<BaseShortcut> {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public BaseShortcutTest(String name) {
    super(name);
  }

  /**
   * Returns a default base object.
   *
   * @return		the default object
   */
  protected BaseShortcut getDefault() {
    return new BaseShortcut();
  }

  /**
   * Returns a base object initialized with the given string.
   *
   * @param s		the string to initialize the object with
   * @return		the custom object
   */
  protected BaseShortcut getCustom(String s) {
    return new BaseShortcut(s);
  }

  /**
   * Returns the string representing a typical value to parse that doesn't
   * fail.
   *
   * @return		the value
   */
  protected String getTypicalValue() {
    return "control shift A";
  }

  /**
   * Tests whether empty shortcut returns null for {@link BaseShortcut#keystrokeValue()}.
   */
  public void testEmptyShortcut() {
    BaseShortcut bs = getCustom("");
    assertNull("Shouldn't have keystroke", bs.keystrokeValue());
  }

  /**
   * Tests constructor that takes KeyStroke object.
   */
  public void testKeystrokeConstructor() {
    KeyStroke ks = KeyStroke.getKeyStroke("control shift A");
    BaseShortcut bs = new BaseShortcut(ks);
    assertEquals("should be same", ks.toString(), bs.keystrokeValue().toString());
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(BaseShortcutTest.class);
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
