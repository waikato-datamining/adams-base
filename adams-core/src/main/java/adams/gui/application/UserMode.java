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
 * UserMode.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.application;

import adams.core.EnumWithCustomDisplay;

/**
 * The user mode, determines the knowledge level of the user.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public enum UserMode
  implements EnumWithCustomDisplay<UserMode> {
  
  /** basic. */
  BASIC("Basic"),
  
  /** expert. */
  EXPERT("Expert"),
  
  /** developer. */
  DEVELOPER("Developer"),
  
  /** for debugging purposes only. */
  DEBUGGER("Debugger");

  /** the display string. */
  private String m_Display;

  /** the commandline string. */
  private String m_Raw;

  /**
   * The constructor.
   *
   * @param display	the string to use as display
   */
  private UserMode(String display) {
    m_Display = display;
    m_Raw     = super.toString();
  }

  /**
   * Returns the display string.
   *
   * @return		the display string
   */
  public String toDisplay() {
    return m_Display;
  }

  /**
   * Returns the raw enum string.
   *
   * @return		the raw enum string
   */
  public String toRaw() {
    return m_Raw;
  }

  /**
   * Returns the display string.
   *
   * @return		the display string
   */
  @Override
  public String toString() {
    return toDisplay();
  }

  /**
   * Parses the given string and returns the associated enum.
   *
   * @param s		the string to parse
   * @return		the enum or null if not found
   */
  public UserMode parse(String s) {
    UserMode	result;

    result = null;

    // default parsing
    try {
      result = valueOf(s);
    }
    catch (Exception e) {
      // ignored
    }

    // try display
    if (result == null) {
	for (UserMode dt: values()) {
	  if (dt.toDisplay().equals(s)) {
	    result = dt;
	    break;
	  }
	}
    }

    return result;
  }
}
