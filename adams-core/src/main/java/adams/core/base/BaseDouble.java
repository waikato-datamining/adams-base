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
 * BaseDouble.java
 * Copyright (C) 2009-2024 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

import adams.core.Utils;
import adams.core.logging.LoggingHelper;

import java.util.logging.Level;

/**
 * Wrapper for a Double object to be editable in the GOE.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class BaseDouble
  extends BaseObject {

  /** for serialization. */
  private static final long serialVersionUID = 2527493071384732090L;

  /** the default value. */
  public final static String DEFAULT = "0.0";
  
  /**
   * Initializes the string with length 0.
   */
  public BaseDouble() {
    this(DEFAULT);
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public BaseDouble(String s) {
    super(s);
  }

  /**
   * Initializes the object with the given value.
   *
   * @param value	the value to use
   */
  public BaseDouble(Double value) {
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
   * @return		true if parseable double
   */
  @Override
  public boolean isValid(String value) {
    if (value == null)
      return false;
    try {
      Utils.toDouble(value);
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
      m_Internal = Utils.toDouble(value);
    }
    catch (Exception e) {
      LoggingHelper.global().log(Level.SEVERE, "Failed to set value: " + value, e);
      m_Internal = 0.0;
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
   * Returns the double value.
   *
   * @return		the double value
   */
  public double doubleValue() {
    return (Double) m_Internal;
  }

  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return		the tool tip
   */
  @Override
  public String getTipText() {
    return "A floating point number (from " + -Double.MAX_VALUE + " to " + Double.MAX_VALUE + ").";
  }

  /**
   * Turns the BaseDouble array into a primitive array.
   *
   * @param values	the array to convert
   * @return		the primitive array
   */
  public static double[] toPrimitive(BaseDouble[] values) {
    double[]	result;
    int		i;

    result = new double[values.length];
    for (i = 0; i < values.length; i++)
      result[i] = values[i].doubleValue();

    return result;
  }

  /**
   * Turns the primitive array into a BaseDouble one.
   *
   * @param values	the array to convert
   * @return		the BaseObject array
   */
  public static BaseDouble[] toBaseDouble(double[] values) {
    BaseDouble[]	result;
    int			i;

    result = new BaseDouble[values.length];
    for (i = 0; i < values.length; i++)
      result[i] = new BaseDouble(values[i]);

    return result;
  }

  /**
   * Turns the BaseDouble array into a number array.
   *
   * @param values	the array to convert
   * @return		the number array
   */
  public static Double[] toNumber(BaseDouble[] values) {
    Double[]	result;
    int		i;

    result = new Double[values.length];
    for (i = 0; i < values.length; i++)
      result[i] = values[i].doubleValue();

    return result;
  }

  /**
   * Turns the primitive array into a BaseDouble one.
   *
   * @param values	the array to convert
   * @return		the BaseObject array
   */
  public static BaseDouble[] toBaseDouble(Double[] values) {
    BaseDouble[]	result;
    int			i;

    result = new BaseDouble[values.length];
    for (i = 0; i < values.length; i++)
      result[i] = new BaseDouble(values[i]);

    return result;
  }
}
