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

/**
 * BaseShortcut.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import adams.core.ExampleProvider;
import adams.core.base.AbstractBaseString;

import javax.swing.KeyStroke;

/**
 * Wrapper for keyboard shortcuts.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseShortcut
  extends AbstractBaseString
  implements ExampleProvider {

  private static final long serialVersionUID = 1589904769256830344L;

  /**
   * Initializes with an empty shortcut.
   */
  public BaseShortcut() {
    this("");
  }

  /**
   * Initializes the shortcut with the string to parse.
   *
   * @param s		the string to parse
   */
  public BaseShortcut(String s) {
    super(s);
  }

  /**
   * Initializes the object with the keystroke.
   *
   * @param keystroke	the keystroke to use
   */
  public BaseShortcut(KeyStroke keystroke) {
    this(keystroke.toString());
  }

  /**
   * Checks whether the string value is a valid presentation for this class.
   *
   * @param value	the string value to check
   * @return		true if non-null
   */
  @Override
  public boolean isValid(String value) {
    try {
      return (KeyStroke.getKeyStroke(value) != null);
    }
    catch (Exception e) {
      return false;
    }
  }

  @Override
  public String getTipText() {
    return "Keyboard shortcut (control, shift, alt)";
  }

  /**
   * Returns the instantiated KeyStroke.
   *
   * @return		true if invalid or empty
   */
  public KeyStroke keystrokeValue() {
    return KeyStroke.getKeyStroke(getValue());
  }

  /**
   * Returns the example.
   *
   * @return		the example
   */
  public String getExample() {
    return "keys: what follows the 'VK_' of the virtual key definition; "
      + "modifier keys: 'shift', 'ctrl', 'alt', 'altGr', 'meta'; "
      + "state: 'pressed' (assumed if missing), 'released'; "
      + "examples: 'ctrl shift pressed T', 'shift INSERT', 'alt DELETE'";
  }
}
