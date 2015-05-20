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
 * AbstractBaseString.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

/**
 * Wrapper for a String object to be editable in the GOE.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractBaseString
  extends BaseObject {

  /** for serialization. */
  private static final long serialVersionUID = -5853830144343397434L;

  /**
   * Initializes the string with length 0.
   */
  protected AbstractBaseString() {
    this("");
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  protected AbstractBaseString(String s) {
    super(s);
  }

  /**
   * Initializes the internal object.
   * <br><br>
   * Uses empty string.
   */
  @Override
  protected void initialize() {
    m_Internal = "";
  }

  /**
   * Checks whether the string value is a valid presentation for this class.
   *
   * @param value	the string value to check
   * @return		true if non-null
   */
  @Override
  public boolean isValid(String value) {
    return (value != null);
  }

  /**
   * Converts the string according to the specified conversion.
   * <br><br>
   * Default implementation performs no conversion.
   *
   * @param value	the string to convert
   * @return		the converted string
   */
  protected String convert(String value) {
    return value;
  }

  /**
   * Sets the string value.
   * <br><br>
   * Calls <code>convert(String)</code> first before checking validity or
   * setting the value (if valid).
   *
   * @param value	the string value
   */
  @Override
  public void setValue(String value) {
    if (!isValid(value))
      return;

    m_Internal = convert(value);
  }

  /**
   * Returns the current string value.
   *
   * @return		the string value
   */
  @Override
  public String getValue() {
    return (String) m_Internal;
  }

  /**
   * Returns the String value.
   *
   * @return		the String value
   */
  public String stringValue() {
    return getValue();
  }

  /**
   * Whether the string is empty, i.e., "".
   *
   * @return		true if the string is empty, i.e., length 0
   */
  public boolean isEmpty() {
    return (((String) m_Internal).length() == 0);
  }

  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return		the tool tip
   */
  @Override
  public abstract String getTipText();
}
