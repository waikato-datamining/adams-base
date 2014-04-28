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
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

import adams.core.Utils;

/**
 * Wrapper for a Double object to be editable in the GOE.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseDouble
  extends BaseObject {

  /** for serialization. */
  private static final long serialVersionUID = 2527493071384732090L;

  /**
   * Initializes the string with length 0.
   */
  public BaseDouble() {
    this("0.0");
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
  public BaseDouble(BaseDouble value) {
    this(value.toString());
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
      e.printStackTrace();
      m_Internal = new Double(0.0);
    }
  }

  /**
   * Returns the current string value.
   *
   * @return		the string value
   */
  @Override
  public String getValue() {
    return ((Double) m_Internal).toString();
  }

  /**
   * Returns the double value.
   *
   * @return		the double value
   */
  public double doubleValue() {
    return ((Double) m_Internal).doubleValue();
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
}
