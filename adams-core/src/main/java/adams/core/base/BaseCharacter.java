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
 * BaseCharacter.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

/**
 * Wrapper for a Character object to be editable in the GOE.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseCharacter
  extends BaseObject {

  /** for serialization. */
  private static final long serialVersionUID = 7599320288778710037L;

  /** the default value. */
  public final static String DEFAULT = "\u0000";
  
  /**
   * Initializes the string with length 0.
   */
  public BaseCharacter() {
    this(DEFAULT);
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public BaseCharacter(String s) {
    super(s);
  }

  /**
   * Initializes the internal object.
   * <br><br>
   * Uses \u0000.
   */
  @Override
  protected void initialize() {
    m_Internal = "\u0000";
  }

  /**
   * Initializes the object with the given value.
   *
   * @param value	the value to use
   */
  public BaseCharacter(Character value) {
    this(value.toString());
  }

  /**
   * Checks whether the string value is a valid presentation for this class.
   *
   * @param value	the string value to check
   * @return		true if the string has length 1
   */
  @Override
  public boolean isValid(String value) {
    if (value == null)
      return false;
    return (value.length() == 1);
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

    m_Internal = value.charAt(0);
  }

  /**
   * Returns the current string value.
   *
   * @return		the string value
   */
  @Override
  public String getValue() {
    return ((Character) m_Internal).toString();
  }

  /**
   * Returns the char value.
   *
   * @return		the char value
   */
  public char charValue() {
    return ((Character) m_Internal).charValue();
  }

  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return		the tool tip
   */
  @Override
  public String getTipText() {
    return "A single character.";
  }
}
