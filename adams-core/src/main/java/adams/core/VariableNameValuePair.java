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
 * VariableNameValuePair.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */

package adams.core;

import adams.core.base.AbstractBaseString;

/**
 * Wrapper for a name/value pair of a variable.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class VariableNameValuePair
  extends AbstractBaseString {

  /** for serialization. */
  private static final long serialVersionUID = -7223597009565454854L;

  /** the separator. */
  public final static String SEPARATOR = "=";

  /**
   * Initializes the string with length 0.
   */
  public VariableNameValuePair() {
    this("");
  }

  /**
   * Initializes the object with the key/value pair.
   *
   * @param key		the key to use
   * @param value	the value to use
   */
  public VariableNameValuePair(String key, String value) {
    super(key + SEPARATOR + value);
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public VariableNameValuePair(String s) {
    super(s);
  }

  /**
   * Checks whether the string value is a valid presentation for this class.
   *
   * @param value	the string value to check
   * @return		true if non-null
   */
  @Override
  public boolean isValid(String value) {
    return value.isEmpty()
      || (value.contains(SEPARATOR) && Variables.isValidName(value.substring(0, value.indexOf(SEPARATOR))));
  }

  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return		the tool tip
   */
  @Override
  public String getTipText() {
    return "Variable name/value pair, uses '" + SEPARATOR + "' as separator.";
  }

  /**
   * Returns the backquoted String pair.
   *
   * @return		the backquoted pair
   */
  public String pairValue() {
    return Utils.backQuoteChars(getValue());
  }

  /**
   * Returns the variable name.
   *
   * @return		the key
   */
  public VariableName varName() {
    if (getValue().contains(SEPARATOR))
      return new VariableName(getValue().substring(0, getValue().indexOf(SEPARATOR)));
    else
      return new VariableName(getValue());
  }

  /**
   * Returns the variable value.
   *
   * @return		the value
   */
  public String varValue() {
    if (getValue().contains(SEPARATOR))
      return getValue().substring(getValue().indexOf(SEPARATOR) + 1);
    else
      return "";
  }
}
