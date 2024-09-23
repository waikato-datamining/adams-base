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
 * BaseFloat.java
 * Copyright (C) 2009-2024 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

import adams.core.logging.LoggingHelper;

import java.util.logging.Level;

/**
 * Wrapper for a Float object to be editable in the GOE.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class BaseFloat
  extends BaseObject {

  /** for serialization. */
  private static final long serialVersionUID = -3871442214322457039L;

  /** the default value. */
  public final static String DEFAULT = "0.0";
  
  /**
   * Initializes the string with length 0.
   */
  public BaseFloat() {
    this(DEFAULT);
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public BaseFloat(String s) {
    super(s);
  }

  /**
   * Initializes the object with the given value.
   *
   * @param value	the value to use
   */
  public BaseFloat(Float value) {
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
   * @return		true if parseable float
   */
  @Override
  public boolean isValid(String value) {
    if (value == null)
      return false;
    try {
      Float.parseFloat(value);
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
      m_Internal = Float.parseFloat(value);
    }
    catch (Exception e) {
      LoggingHelper.global().log(Level.SEVERE, "Failed to set value: " + value, e);
      m_Internal = 0.0f;
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
   * Returns the float value.
   *
   * @return		the float value
   */
  public float floatValue() {
    return (Float) m_Internal;
  }

  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return		the tool tip
   */
  @Override
  public String getTipText() {
    return "A floating point number (from " + Float.MIN_VALUE + " to " + Float.MAX_VALUE + ").";
  }

  /**
   * Turns the BaseFloat array into a primitive array.
   *
   * @param values	the array to convert
   * @return		the primitive array
   */
  public static float[] toPrimitive(BaseFloat[] values) {
    float[]	result;
    int		i;

    result = new float[values.length];
    for (i = 0; i < values.length; i++)
      result[i] = values[i].floatValue();

    return result;
  }

  /**
   * Turns the primitive array into a BaseFloat one.
   *
   * @param values	the array to convert
   * @return		the BaseObject array
   */
  public static BaseFloat[] toBaseFloat(float[] values) {
    BaseFloat[]	result;
    int			i;

    result = new BaseFloat[values.length];
    for (i = 0; i < values.length; i++)
      result[i] = new BaseFloat(values[i]);

    return result;
  }

  /**
   * Turns the BaseFloat array into a number array.
   *
   * @param values	the array to convert
   * @return		the number array
   */
  public static Float[] toNumber(BaseFloat[] values) {
    Float[]	result;
    int		i;

    result = new Float[values.length];
    for (i = 0; i < values.length; i++)
      result[i] = values[i].floatValue();

    return result;
  }

  /**
   * Turns the primitive array into a BaseFloat one.
   *
   * @param values	the array to convert
   * @return		the BaseObject array
   */
  public static BaseFloat[] toBaseFloat(Float[] values) {
    BaseFloat[]	result;
    int			i;

    result = new BaseFloat[values.length];
    for (i = 0; i < values.length; i++)
      result[i] = new BaseFloat(values[i]);

    return result;
  }
}
