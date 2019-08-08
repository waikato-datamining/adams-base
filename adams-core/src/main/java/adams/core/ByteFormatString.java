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
 * ByteFormatString.java
 * Copyright (C) 2016-2019 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import adams.core.base.AbstractBaseString;

/**
 * Wrapper for byte format strings.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @see adams.core.ByteFormat
 */
public class ByteFormatString
  extends AbstractBaseString {

  /** for serialization. */
  private static final long serialVersionUID = -7134897961930112280L;

  /** the default value. */
  public final static String DEFAULT = "";

  /**
   * Initializes the string with "0".
   */
  public ByteFormatString() {
    this(DEFAULT);
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public ByteFormatString(String s) {
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
      new ByteFormat("").isValid(value);
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
    return "Format string for bytes, format: {b|B}[.N]{k|K|m|M|g|G|t|T|p|P|e|E|z|Z|y|Y[i]}\n"
      + "'b' outputs the amount without thousand separators, 'B' includes them.\n"
      + ".N prints 'N' decimal places\n"
      + "Units: k|K=kilobytes, m|M=megabytes, g|G=gigabytes, t|T=terabytes, p|P=petabytes, e|E=exabytes, z|Z=zettabytes, y|Y=yottabytes\n"
      + "Lower case does not add a specifier like 'KB', upper case does.\n"
      + "Adding 'i' at the end uses 1024 as base instead of 1000.";
  }
  
  /**
   * Returns a configured {@link ByteFormat} object.
   * 
   * @return		the configured object
   */
  public ByteFormat toByteFormat() {
    return new ByteFormat(getValue());
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
