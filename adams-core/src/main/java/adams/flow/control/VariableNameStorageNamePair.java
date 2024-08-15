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
 * VariableNameStorageNamePair.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import adams.core.Utils;
import adams.core.base.AbstractBaseString;

/**
 * Wrapper for a variable name/storage name pair.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class VariableNameStorageNamePair
  extends AbstractBaseString {

  /** for serialization. */
  private static final long serialVersionUID = -7223597009565454854L;

  /** the separator. */
  public final static String SEPARATOR = "=";

  /**
   * Initializes the string with length 0.
   */
  public VariableNameStorageNamePair() {
    this("");
  }

  /**
   * Initializes the object with the variable name/storage name pair.
   *
   * @param varName	the variable name to use
   * @param storagName	the storage name to use
   */
  public VariableNameStorageNamePair(String varName, String storagName) {
    super(varName + SEPARATOR + storagName);
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public VariableNameStorageNamePair(String s) {
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
    String[]	parts;

    if (value.isEmpty())
      return true;
    if (!value.contains(SEPARATOR))
      return false;

    parts = value.split(SEPARATOR);
    if (parts.length != 2)
      return false;

    if (parts[0].isEmpty())
      return false;

    return Storage.isValidName(parts[1]);
  }

  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return		the tool tip
   */
  @Override
  public String getTipText() {
    return "Variable name/storage name pair, uses '" + SEPARATOR + "' as separator.";
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
   * @return		the variable name
   */
  public String variableNameValue() {
    if (getValue().contains(SEPARATOR))
      return getValue().substring(0, getValue().indexOf(SEPARATOR));
    else
      return getValue();
  }

  /**
   * Returns the storage name.
   *
   * @return		the storage name
   */
  public StorageName storageNameValue() {
    if (getValue().contains(SEPARATOR))
      return new StorageName(getValue().substring(getValue().indexOf(SEPARATOR) + 1));
    else
      return new StorageName("");
  }
}
