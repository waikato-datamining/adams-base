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
 * Copyright (C) 2009-2024 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

import adams.core.logging.LoggingHelper;

import java.util.logging.Level;

/**
 * Wrapper for a Short object to be editable in the GOE.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class BaseShort
  extends BaseObject {

  /** for serialization. */
  private static final long serialVersionUID = -4419336990700334068L;

  /** the default value. */
  public final static String DEFAULT = "0";
  
  /**
   * Initializes the string with length 0.
   */
  public BaseShort() {
    this(DEFAULT);
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
      LoggingHelper.global().log(Level.SEVERE, "Failed to set value: " + value, e);
      m_Internal = (short) 0;
    }
  }

  /**
   * Returns the current string value.
   *
   * @return		the string value
   */
  @Override
  public String getValue() {
    return m_Internal.toString();
  }

  /**
   * Returns the short value.
   *
   * @return		the short value
   */
  public short shortValue() {
    return (Short) m_Internal;
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

  /**
   * Turns the BaseShort array into a primitive array.
   *
   * @param values	the array to convert
   * @return		the primitive array
   */
  public static short[] toPrimitive(BaseShort[] values) {
    short[]	result;
    int		i;

    result = new short[values.length];
    for (i = 0; i < values.length; i++)
      result[i] = values[i].shortValue();

    return result;
  }

  /**
   * Turns the primitive array into a BaseShort one.
   *
   * @param values	the array to convert
   * @return		the BaseObject array
   */
  public static BaseShort[] toBaseShort(short[] values) {
    BaseShort[]	result;
    int			i;

    result = new BaseShort[values.length];
    for (i = 0; i < values.length; i++)
      result[i] = new BaseShort(values[i]);

    return result;
  }

  /**
   * Turns the BaseShort array into a number array.
   *
   * @param values	the array to convert
   * @return		the number array
   */
  public static Short[] toNumber(BaseShort[] values) {
    Short[]	result;
    int		i;

    result = new Short[values.length];
    for (i = 0; i < values.length; i++)
      result[i] = values[i].shortValue();

    return result;
  }

  /**
   * Turns the primitive array into a BaseShort one.
   *
   * @param values	the array to convert
   * @return		the BaseObject array
   */
  public static BaseShort[] toBaseShort(Short[] values) {
    BaseShort[]	result;
    int			i;

    result = new BaseShort[values.length];
    for (i = 0; i < values.length; i++)
      result[i] = new BaseShort(values[i]);

    return result;
  }
}
