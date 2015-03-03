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
 * BaseList.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

/**
 * Wrapper for a comma-separated list (String).
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseList
  extends AbstractBaseString {

  /** for serialization. */
  private static final long serialVersionUID = -8131365256300704071L;

  /**
   * Enumeration for the conversion of the string, when setting it.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum Conversion {
    /** unchanged. */
    UNCHANGED,
    /** lower case. */
    LOWER_CASE,
    /** upper case. */
    UPPER_CASE
  }

  /**
   * Initializes the list with length 0.
   */
  public BaseList() {
    this("");
  }

  /**
   * Initializes the object with the list to parse.
   *
   * @param s		the list to parse
   */
  public BaseList(String s) {
    super(s);
  }

  /**
   * Returns the conversion of the string before setting its value.
   *
   * @return		the type of conversion to apply
   */
  protected Conversion getConversion() {
    return Conversion.UNCHANGED;
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
    return super.isValid(convert(value));
  }

  /**
   * Converts the string according to the specified conversion.
   *
   * @param value	the string to convert
   * @return		the converted string
   * @see		#getConversion()
   */
  @Override
  protected String convert(String value) {
    String	result;

    switch (getConversion()) {
      case UNCHANGED:
	result = value;
	break;

      case LOWER_CASE:
	result = value.toLowerCase();
	break;

      case UPPER_CASE:
	result = value.toUpperCase();
	break;

      default:
	throw new IllegalStateException("Unhandled conversion: " + getConversion());
    }

    return result;
  }

  /**
   * Returns the list items.
   *
   * @return		the list array
   */
  public String[] listValue() {
    String 	value;

    value = getValue();
    if (value.length() == 0)
      return new String[0];
    else if (value.indexOf(',') == -1)
      return new String[]{value};
    else
      return value.split(",");
  }

  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return		the tool tip
   */
  @Override
  public String getTipText() {
    return "An arbitrary comma-separated list.";
  }
}
