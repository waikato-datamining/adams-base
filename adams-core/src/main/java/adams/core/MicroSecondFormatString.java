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
 * MicroSecondFormatString.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import adams.core.base.AbstractBaseString;

/**
 * Wrapper for microsecond format strings.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @see MicroSecondFormat
 */
public class MicroSecondFormatString
  extends AbstractBaseString {

  /** for serialization. */
  private static final long serialVersionUID = -7134897961930112280L;

  /** the default value. */
  public final static String DEFAULT = "";

  /**
   * Initializes the string with "0".
   */
  public MicroSecondFormatString() {
    this(DEFAULT);
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public MicroSecondFormatString(String s) {
    super(s);
  }

  /**
   * Initializes the internal object.
   */
  @Override
  protected void initialize() {
    m_Internal = DEFAULT;
  }

  /**
   * Checks whether the string value is a valid presentation for this class.
   *
   * @param value	the string value to check
   * @return		always true
   */
  @Override
  public boolean isValid(String value) {
    if (value == null)
      return false;
    try {
      new MicroSecondFormat("").isValid(value);
      return true;
    }
    catch (Exception e) {
      return false;
    }
  }
  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return		the tool tip
   */
  @Override
  public String getTipText() {
    return "Format string for microseconds, format: {t|T}[.N]{u|µ|l|s|m|h|d|w}\n"
      + "'t' outputs the amount without thousand separators, 'T' includes them.\n"
      + "'.N' prints 'N' decimal places\n"
      + "Units: u|µ=microseconds, l=milliseconds, s=seconds, m=minutes, h=hours, d=days, w=weeks"
      + "Lower case does not add a specifier like 'ms', upper case does.";
  }
  
  /**
   * Returns a configured {@link MicroSecondFormat} object.
   * 
   * @return		the configured object
   */
  public MicroSecondFormat toMicroSecondFormat() {
    return new MicroSecondFormat(getValue());
  }

  /**
   * Whether this object should have favorites support.
   *
   * @return		true if to support favorites
   */
  @Override
  public boolean hasFavoritesSupport() {
    return true;
  }
}
