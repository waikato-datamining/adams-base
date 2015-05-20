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
 * ClassOption.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import java.lang.reflect.Method;

import adams.core.Utils;

/**
 * Option class for options with custom hooks for valueOf and toString.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see AbstractOption#m_HooksValueOf
 * @see AbstractOption#m_HooksToString
 */
public class CustomHooksOption
  extends AbstractArgumentOption {

  /** for serialization. */
  private static final long serialVersionUID = -3175126088440187209L;

  /**
   * Initializes the option. Will always output the default value.
   *
   * @param owner		the owner of this option
   * @param commandline		the commandline string to identify the option
   * @param property 		the name of bean property
   * @param defValue		the default value, if null then the owner's
   * 				current state is used
   */
  protected CustomHooksOption(OptionManager owner, String commandline, String property,
      Object defValue) {

    super(owner, commandline, property, defValue);
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
  protected CustomHooksOption(OptionManager owner, String commandline, String property,
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
  @Override
  protected boolean compareValues(Object value, Object defValue) {
    String	sValue;
    String	sDefValue;
    
    sValue    = toString(value);
    sDefValue = toString(defValue);
    
    if ((sValue != null) && (sDefValue != null))
      return sValue.equals(sDefValue);
    else
      return false;
  }

  /**
   * Turns the string into the appropriate object.
   * <br><br>
   * Needs to be overridden if no custom hook available.
   *
   * @param s		the string to parse
   * @return		the generated object
   * @throws Exception	if parsing of string fails
   */
  @Override
  public Object valueOf(String s) throws Exception {
    Object	result;
    Method	method;

    method = OptionUtils.getValueOfHook(getBaseClass());
    if (method == null)
      throw new IllegalStateException("No 'valueOf' method defined for base class '" + Utils.getArrayClass(getBaseClass()).getName() + "'?");
    else
      result = method.invoke(getOptionHandler(), new Object[]{this, s});

    return result;
  }

  /**
   * Returns a string representation of the specified object.
   * <br><br>
   * Needs to be overridden if no custom hook available.
   *
   * @param obj		the object to turn into a string
   * @return		the string representation
   */
  @Override
  public String toString(Object obj) {
    String	result;
    Method	method;

    result = "";

    method = OptionUtils.getToStringHook(getBaseClass());
    if (method == null) {
      if (!m_Owner.isQuiet())
	System.err.println("No 'toString' method defined for base class '" + Utils.getArrayClass(getBaseClass()).getName() + "'?");
    }
    else {
      try {
	result = (String) method.invoke(getOptionHandler(), new Object[]{this, obj});
      }
      catch (Exception e) {
	if (!m_Owner.isQuiet()) {
	  System.err.println("Error obtaining string representation for '" + getProperty() + "' (" + Utils.getArrayClass(obj.getClass()).getName() + "):");
	  e.printStackTrace();
	}
      }
    }

    return result;
  }
}
