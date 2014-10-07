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
 * PaddingType.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.padding;

import adams.core.EnumWithCustomDisplay;
import adams.core.option.AbstractOption;

/**
 * The type of available paddings.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9820 $
 */
public enum PaddingType
  implements EnumWithCustomDisplay<PaddingType> {

  /** pad with zeroes. */
  ZERO("Zero"),
  /** pad with last value. */
  LAST("Last");

  /** the display value. */
  private String m_Display;

  /** the commandline string. */
  private String m_Raw;

  /**
   * Initializes the element.
   *
   * @param display	the display value
   */
  private PaddingType(String display) {
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
   * Parses the given string and returns the associated enum.
   *
   * @param s		the string to parse
   * @return		the enum or null if not found
   */
  public PaddingType parse(String s) {
    return (PaddingType) valueOf((AbstractOption) null, s);
  }

  /**
   * Returns the displays string.
   *
   * @return		the display string
   */
  @Override
  public String toString() {
    return m_Display;
  }

  /**
   * Returns the enum as string.
   *
   * @param option	the current option
   * @param object	the enum object to convert
   * @return		the generated string
   */
  public static String toString(AbstractOption option, Object object) {
    return ((PaddingType) object).toRaw();
  }

  /**
   * Returns an enum generated from the string.
   *
   * @param option	the current option
   * @param str		the string to convert to an enum
   * @return		the generated enum or null in case of error
   */
  public static PaddingType valueOf(AbstractOption option, String str) {
    PaddingType	result;

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
      for (PaddingType f: values()) {
	if (f.toDisplay().equals(str)) {
	  result = f;
	  break;
	}
      }
    }

    return result;
  }
}