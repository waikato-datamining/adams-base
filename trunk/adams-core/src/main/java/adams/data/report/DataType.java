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
 * DataType.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.data.report;

import adams.core.EnumWithCustomDisplay;
import adams.core.option.AbstractOption;

/**
 * The type of the fields.
 *
 * @author  dale (dale at cs dot waikato dot ac dot nz)
 * @version $Revision$
 */
public enum DataType
  implements EnumWithCustomDisplay<DataType> {

  /** a string value. */
  STRING("S"),
  /** a numeric value. */
  NUMERIC("N"),
  /** a boolean value. */
  BOOLEAN("B"),
  /** unknown type. */
  UNKNOWN("U");

  /** the display string. */
  private String m_Display;

  /** the commandline string. */
  private String m_Raw;

  /**
   * The constructor.
   *
   * @param display	the string to use as display
   */
  private DataType(String display) {
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
  public DataType parse(String s) {
    return (DataType) valueOf((AbstractOption) null, s);
  }

  /**
   * Returns the enum as string.
   *
   * @param option	the current option
   * @param object	the enum object to convert
   * @return		the generated string
   */
  public static String toString(AbstractOption option, Object object) {
    return ((DataType) object).toRaw();
  }

  /**
   * Returns an enum generated from the string.
   *
   * @param option	the current option
   * @param str	the string to convert to an enum
   * @return		the generated enum or null in case of error
   */
  public static DataType valueOf(AbstractOption option, String str) {
    DataType	result;

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
      for (DataType dt: values()) {
	if (dt.toDisplay().equals(str)) {
	  result = dt;
	  break;
	}
      }
    }

    return result;
  }
}