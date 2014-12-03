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

/**
 * VariableName.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import adams.core.base.AbstractBaseString;

/**
 * Wrapper around the name of a variable (= string).
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class VariableName
  extends AbstractBaseString {

  /** for serialization. */
  private static final long serialVersionUID = -5338530688258339927L;

  /** the default value. */
  public final static String DEFAULT = "variable";
  
  /**
   * Initializes the name with a default value.
   */
  public VariableName() {
    this(DEFAULT);
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public VariableName(String s) {
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
   * Checks whether the string value is a valid presentation for this class.
   *
   * @param value	the string value to check
   * @return		true if a valid variable name
   * @see		Variables#isValidName(String)
   */
  @Override
  public boolean isValid(String value) {
    if (value == null)
      return false;
    return Variables.isValidName(value);
  }

  /**
   * Returns the variable name with start and end tag enclosed.
   *
   * @return		the padded variable name if an actual value available,
   * 			otherwise an empty string
   */
  public String paddedValue() {
    if (getValue().length() > 0)
      return Variables.padName(getValue());
    else
      return "";
  }

  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return		the tool tip
   */
  @Override
  public String getTipText() {
    return "Variable name.";
  }
}
