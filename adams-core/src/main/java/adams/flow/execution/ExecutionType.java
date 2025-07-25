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
 * ExecutionType.java
 * Copyright (C) 2025 University of Waikato, Hamilton, NZ
 */

package adams.flow.execution;

import adams.core.EnumWithCustomDisplay;
import adams.core.option.AbstractOption;

/**
 * Enumeration of flow execution types.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public enum ExecutionType
  implements EnumWithCustomDisplay<ExecutionType> {

  INPUT("Input"),
  EXECUTE("Execute"),
  OUTPUT("Output");

  /** the display string. */
  private String m_Display;

  /** the commandline string. */
  private String m_Raw;

  /**
   * The constructor.
   *
   * @param display	the string to use as display
   */
  private ExecutionType(String display) {
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
  public String toString() {
    return toDisplay();
  }

  /**
   * Parses the given string and returns the associated enum.
   *
   * @param s		the string to parse
   * @return		the enum or null if not found
   */
  public ExecutionType parse(String s) {
    return (ExecutionType) valueOf((AbstractOption) null, s);
  }

  /**
   * Returns the enum as string.
   *
   * @param option	the current option
   * @param object	the enum object to convert
   * @return		the generated string
   */
  public static String toString(AbstractOption option, Object object) {
    return ((ExecutionType) object).toRaw();
  }

  /**
   * Returns an enum generated from the string.
   *
   * @param option	the current option
   * @param str	the string to convert to an enum
   * @return		the generated enum or null in case of error
   */
  public static ExecutionType valueOf(AbstractOption option, String str) {
    ExecutionType result;

    result = null;

    // default parsing
    try {
      result = valueOf(str);
    }
    catch (Exception e) {
      // ignored
    }

    // try display
    if (result == null) {
      for (ExecutionType dt: values()) {
	if (dt.toDisplay().equals(str)) {
	  result = dt;
	  break;
	}
      }
    }

    return result;
  }
}
