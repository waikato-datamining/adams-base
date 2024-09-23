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
 * BaseByte.java
 * Copyright (C) 2009-2024 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

import adams.core.logging.LoggingHelper;

import java.util.logging.Level;

/**
 * Wrapper for a Byte object to be editable in the GOE.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class BaseByte
  extends BaseObject {

  /** for serialization. */
  private static final long serialVersionUID = -3504062141216626521L;

  /** the default value. */
  public final static String DEFAULT = "0";
  
  /**
   * Initializes the string with length 0.
   */
  public BaseByte() {
    this(DEFAULT);
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public BaseByte(String s) {
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
   * Initializes the object with the given value.
   *
   * @param value	the value to use
   */
  public BaseByte(Byte value) {
    this(value.toString());
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
      Byte.parseByte(value);
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
      m_Internal = Byte.parseByte(value);
    }
    catch (Exception e) {
      LoggingHelper.global().log(Level.SEVERE, "Failed to set value: " + value, e);
      m_Internal = (byte) 0;
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
   * Returns the byte value.
   *
   * @return		the byte value
   */
  public byte byteValue() {
    return (Byte) m_Internal;
  }

  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return		the tool tip
   */
  @Override
  public String getTipText() {
    return "A byte (from " + Byte.MIN_VALUE + " to " + Byte.MAX_VALUE + ").";
  }

  /**
   * Turns the BaseByte array into a primitive array.
   *
   * @param values	the array to convert
   * @return		the primitive array
   */
  public static byte[] toPrimitive(BaseByte[] values) {
    byte[]	result;
    int		i;

    result = new byte[values.length];
    for (i = 0; i < values.length; i++)
      result[i] = values[i].byteValue();

    return result;
  }

  /**
   * Turns the primitive array into a BaseByte one.
   *
   * @param values	the array to convert
   * @return		the BaseObject array
   */
  public static BaseByte[] toBaseByte(byte[] values) {
    BaseByte[]	result;
    int			i;

    result = new BaseByte[values.length];
    for (i = 0; i < values.length; i++)
      result[i] = new BaseByte(values[i]);

    return result;
  }

  /**
   * Turns the BaseByte array into a number array.
   *
   * @param values	the array to convert
   * @return		the number array
   */
  public static Byte[] toNumber(BaseByte[] values) {
    Byte[]	result;
    int		i;

    result = new Byte[values.length];
    for (i = 0; i < values.length; i++)
      result[i] = values[i].byteValue();

    return result;
  }

  /**
   * Turns the primitive array into a BaseByte one.
   *
   * @param values	the array to convert
   * @return		the BaseObject array
   */
  public static BaseByte[] toBaseByte(Byte[] values) {
    BaseByte[]	result;
    int			i;

    result = new BaseByte[values.length];
    for (i = 0; i < values.length; i++)
      result[i] = new BaseByte(values[i]);

    return result;
  }
}
