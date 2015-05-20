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
 * StringOption.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

/**
 * Handles options with string arguments.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StringOption
  extends AbstractArgumentOption {

  /** for serialization. */
  private static final long serialVersionUID = -2043258692860515443L;

  /**
   * Initializes the option. Will always output the default value.
   *
   * @param owner		the owner of this option
   * @param commandline		the commandline string to identify the option (no leading dash)
   * @param property 		the name of bean property
   * @param defValue		the default value, if null then the owner's
   * 				current state is used
   */
  protected StringOption(OptionManager owner, String commandline, String property,
      Object defValue) {

    this(owner, commandline, property, defValue, true);
  }

  /**
   * Initializes the option.
   *
   * @param owner		the owner of this option
   * @param commandline		the commandline string to identify the option (no leading dash)
   * @param property 		the name of bean property
   * @param defValue		the default value, if null then the owner's
   * 				current state is used
   * @param outputDefValue	whether to output the default value or not
   */
  protected StringOption(OptionManager owner, String commandline, String property,
      Object defValue, boolean outputDefValue) {

    super(owner, commandline, property, defValue, outputDefValue);
  }

  /**
   * Compares the two values.
   *
   * @param value	the value to compare against the default value
   * @param defValue	the default value to compare against
   * @return		true if both are equal
   */
  protected boolean compareValues(Object value, Object defValue) {
    return ((String) value).equals((String) defValue);
  }

  /**
   * Turns the string into the appropriate object.
   *
   * @param s		the string to parse
   * @return		the generated object
   * @throws Exception	if parsing of string fails
   */
  public String valueOf(String s) throws Exception {
    return s;
  }

  /**
   * Allows additional checks whether to include that particular string
   * represents a valid option, i.e., is parseable.
   * <br><br>
   * Any string is valid, even empty ones.
   *
   * @param s		the option string to test.
   * @return		always true
   */
  protected boolean isParseable(String s) {
    return true;
  }

  /**
   * Returns a string representation of the specified object.
   *
   * @param obj		the object to turn into a string
   * @return		the string representation
   */
  public String toString(Object obj) {
    if (obj == null)
      return getDefaultValue().toString();
    else
      return obj.toString();
  }
}
