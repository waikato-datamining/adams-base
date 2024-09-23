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
 * BaseInteger.java
 * Copyright (C) 2009-2024 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

import adams.core.logging.LoggingHelper;

import java.util.logging.Level;

/**
 * Wrapper for an Integer object to be editable in the GOE.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class BaseInteger
  extends BaseObject {

  /** for serialization. */
  private static final long serialVersionUID = 4461135181234402629L;

  /** the default value. */
  public final static String DEFAULT = "0";
  
  /**
   * Initializes the string with length 0.
   */
  public BaseInteger() {
    this(DEFAULT);
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public BaseInteger(String s) {
    super(s);
  }

  /**
   * Initializes the object with the given value.
   *
   * @param value	the value to use
   */
  public BaseInteger(Integer value) {
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
   * @return		true if parseable int
   */
  @Override
  public boolean isValid(String value) {
    if (value == null)
      return false;
    try {
      Integer.parseInt(value);
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
      m_Internal = Integer.parseInt(value);
    }
    catch (Exception e) {
      LoggingHelper.global().log(Level.SEVERE, "Failed to set value: " + value, e);
      m_Internal = 0;
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
   * Returns the int value.
   *
   * @return		the int value
   */
  public int intValue() {
    return (Integer) m_Internal;
  }

  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return		the tool tip
   */
  @Override
  public String getTipText() {
    return "An integer (from " + Integer.MIN_VALUE + " to " + Integer.MAX_VALUE + ").";
  }

  /**
   * Turns the BaseInteger array into a primitive array.
   *
   * @param values	the array to convert
   * @return		the primitive array
   */
  public static int[] toPrimitive(BaseInteger[] values) {
    int[]	result;
    int		i;

    result = new int[values.length];
    for (i = 0; i < values.length; i++)
      result[i] = values[i].intValue();

    return result;
  }

  /**
   * Turns the primitive array into a BaseInteger one.
   *
   * @param values	the array to convert
   * @return		the BaseObject array
   */
  public static BaseInteger[] toBaseInteger(int[] values) {
    BaseInteger[]	result;
    int			i;

    result = new BaseInteger[values.length];
    for (i = 0; i < values.length; i++)
      result[i] = new BaseInteger(values[i]);

    return result;
  }

  /**
   * Turns the BaseInteger array into a number array.
   *
   * @param values	the array to convert
   * @return		the number array
   */
  public static Integer[] toNumber(BaseInteger[] values) {
    Integer[]	result;
    int		i;

    result = new Integer[values.length];
    for (i = 0; i < values.length; i++)
      result[i] = values[i].intValue();

    return result;
  }

  /**
   * Turns the primitive array into a BaseInteger one.
   *
   * @param values	the array to convert
   * @return		the BaseObject array
   */
  public static BaseInteger[] toBaseInteger(Integer[] values) {
    BaseInteger[]	result;
    int			i;

    result = new BaseInteger[values.length];
    for (i = 0; i < values.length; i++)
      result[i] = new BaseInteger(values[i]);

    return result;
  }
}
