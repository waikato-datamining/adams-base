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
 * BooleanOption.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.core.option;

/**
 * Option for boolean flags.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BooleanOption
  extends AbstractArgumentOption {

  /** for serialization. */
  private static final long serialVersionUID = 8842321259180570339L;

  /**
   * Initializes the option. Will always output the default value.
   *
   * @param owner		the owner of this option
   * @param commandline		the commandline string to identify the option
   * @param property 		the name of bean property
   * @param defValue		the default value, if null then the owner's
   * 				current state is used
   */
  protected BooleanOption(OptionManager owner, String commandline, String property, Object defValue) {
    this(owner, commandline, property, defValue, true);
  }

  /**
   * Initializes the option.
   *
   * @param owner		the owner of this option
   * @param commandline		the commandline string to identify the option
   * @param property 		the name of bean property
   * @param defValue		the default value, if null then the owner's
   * 				current state is used
   * @param outputDefValue	whether to output the default value or not
   */
  protected BooleanOption(OptionManager owner, String commandline, String property, Object defValue, boolean outputDefValue) {
    super(owner, commandline, property, defValue, outputDefValue);
  }

  /**
   * Turns the string into the appropriate object.
   *
   * @param s		the string to parse
   * @return		the generated object
   * @throws Exception	if parsing of string fails
   */
  @Override
  public Object valueOf(String s) throws Exception {
    return Boolean.parseBoolean(s);
  }

  /**
   * Returns a string representation of the specified object.
   *
   * @param obj		the object to turn into a string
   * @return		the string representation
   */
  @Override
  public String toString(Object obj) {
    if (obj == null)
      return getDefaultValue().toString();
    else
      return obj.toString();
  }

  /**
   * Compares the two values.
   *
   * @param value	the value to compare against the default value
   * @param defValue	the default value to compare against
   * @return		true if both are equal
   */
  @Override
  protected boolean compareValues(Object value, Object defValue) {
    return ((Boolean) value).equals((Boolean) defValue);  }
}
