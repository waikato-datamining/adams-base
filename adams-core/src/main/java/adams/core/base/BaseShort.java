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
 * BaseShort.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

/**
 * Wrapper for a Short object to be editable in the GOE.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseShort
  extends BaseObject {

  /** for serialization. */
  private static final long serialVersionUID = -4419336990700334068L;

  /**
   * Initializes the string with length 0.
   */
  public BaseShort() {
    this("0");
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public BaseShort(String s) {
    super(s);
  }

  /**
   * Initializes the object with the given value.
   *
   * @param value	the value to use
   */
  public BaseShort(Short value) {
    this(value.toString());
  }

  /**
   * Checks whether the string value is a valid presentation for this class.
   *
   * @param value	the string value to check
   * @return		true if parseable short
   */
  @Override
  public boolean isValid(String value) {
    if (value == null)
      return false;
    try {
      Short.parseShort(value);
      return true;
    }
    catch (Exception e) {
      return false;
    }
  }

  /**
   * Sets the string value.
   *
   * @param value	the string value
   */
  @Override
  public void setValue(String value) {
    if (!isValid(value))
      return;

    try {
      m_Internal = Short.parseShort(value);
    }
    catch (Exception e) {
      e.printStackTrace();
      m_Internal = new Short((short) 0);
    }
  }

  /**
   * Returns the current string value.
   *
   * @return		the string value
   */
  @Override
  public String getValue() {
    return ((Short) m_Internal).toString();
  }

  /**
   * Returns the short value.
   *
   * @return		the short value
   */
  public short shortValue() {
    return ((Short) m_Internal).shortValue();
  }

  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return		the tool tip
   */
  @Override
  public String getTipText() {
    return "A short (from " + Short.MIN_VALUE + " to " + Short.MAX_VALUE + ").";
  }
}
