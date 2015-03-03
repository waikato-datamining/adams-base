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
 * BaseBoolean.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

/**
 * Wrapper for a Boolean object to be editable in the GOE.
 * Handles "f" or "t" (case-insensitive) as strings as well.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseBoolean
  extends BaseObject {

  /** for serialization. */
  private static final long serialVersionUID = -3504062141216626521L;

  /** the default value. */
  public final static String DEFAULT = "false";
  
  /**
   * Initializes the string with length 0.
   */
  public BaseBoolean() {
    this(DEFAULT);
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public BaseBoolean(String s) {
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
  public BaseBoolean(Boolean value) {
    this(value.toString());
  }

  /**
   * Turns "t" and "f" into "true" and "false" respectively.
   * 
   * @param s		the string to fix
   * @return		the fixed string
   */
  protected String fixString(String s) {
    s = s.toLowerCase();
    if (s.equalsIgnoreCase("t"))
      return "true";
    else if (s.equalsIgnoreCase("f"))
      return "false";
    else
      return s;
  }
  
  /**
   * Checks whether the string value is a valid presentation for this class.
   *
   * @param value	the string value to check
   * @return		true if parseable boolean
   */
  @Override
  public boolean isValid(String value) {
    if (value == null)
      return false;
    try {
      Boolean.parseBoolean(fixString(value));
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
      m_Internal = Boolean.parseBoolean(fixString(value));
    }
    catch (Exception e) {
      e.printStackTrace();
      m_Internal = new Boolean(false);
    }
  }

  /**
   * Returns the current string value.
   *
   * @return		the string value
   */
  @Override
  public String getValue() {
    return ((Boolean) m_Internal).toString();
  }

  /**
   * Returns the boolean value.
   *
   * @return		the boolean value
   */
  public boolean booleanValue() {
    return ((Boolean) m_Internal).booleanValue();
  }

  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return		the tool tip
   */
  @Override
  public String getTipText() {
    return "A boolean value.";
  }
}
