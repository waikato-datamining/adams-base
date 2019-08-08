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
 * SecondFormatString.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import adams.core.base.AbstractBaseString;

/**
 * Wrapper for microsecond format strings.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @see SecondFormat
 */
public class SecondFormatString
  extends AbstractBaseString {

  /** for serialization. */
  private static final long serialVersionUID = -7134897961930112280L;

  /** the default value. */
  public final static String DEFAULT = "";

  /**
   * Initializes the string with "0".
   */
  public SecondFormatString() {
    this(DEFAULT);
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public SecondFormatString(String s) {
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
      new SecondFormat("").isValid(value);
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
    return "Format string for seconds, format: {t|T}[.N]{s|m|h|d|w}\n"
      + "'t' outputs the amount without thousand separators, 'T' includes them.\n"
      + "'.N' prints 'N' decimal places\n"
      + "Units: s=seconds, m=minutes, h=hours, d=days, w=weeks"
      + "Lower case does not add a specifier like 's', upper case does.";
  }
  
  /**
   * Returns a configured {@link SecondFormat} object.
   * 
   * @return		the configured object
   */
  public SecondFormat toSecondFormat() {
    return new SecondFormat(getValue());
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
