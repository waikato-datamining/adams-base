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
 * EnumOption.java
 * Copyright (C) 2010-2025 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import adams.core.EnumWithCustomDisplay;
import adams.core.Utils;
import adams.core.logging.LoggingHelper;
import adams.gui.goe.EnumEditor;
import nz.ac.waikato.cms.locator.ClassLocator;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.logging.Level;

/**
 * Option class for enums. Enums get automatically registered with the
 * GenericObjectEditor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class EnumOption
  extends AbstractArgumentOption {

  /** for serialization. */
  private static final long serialVersionUID = -7052702973855610177L;

  /** an instance of an enum with a custom display, used for parsing. */
  protected EnumWithCustomDisplay m_CustomDisplayInstance;

  /**
   * Initializes the option. Will always output the default value.
   *
   * @param owner		the owner of this option
   * @param commandline		the commandline string to identify the option
   * @param property 		the name of bean property
   * @param defValue		the default value, if null then the owner's
   * 				current state is used
   */
  protected EnumOption(OptionManager owner, String commandline, String property, Object defValue) {
    super(owner, commandline, property, defValue);

    // register enums automatically with the GOE
    EnumEditor.registerEditor(this);

    m_CustomDisplayInstance = null;
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
    return toString(value).equals(toString(defValue));
  }

  /**
   * Tries to instantiate an instance of the enumeration type.
   *
   * @return		the instance or null if failed to instantiate
   * @see		#m_CustomDisplayInstance
   */
  protected synchronized EnumWithCustomDisplay getCustomDisplayInstance() {
    if (m_CustomDisplayInstance == null)
      m_CustomDisplayInstance = getEnumInstance(getBaseClass());

    return m_CustomDisplayInstance;
  }

  /**
   * Tries to instantiate an instance of the enumeration type.
   *
   * @param cls		the enum class
   * @return		the instance or null if failed to instantiate
   */
  public static EnumWithCustomDisplay getEnumInstance(Class cls) {
    EnumWithCustomDisplay	result;
    Method			method;
    Object			values;

    try {
      method = cls.getMethod("values");
      values = method.invoke(null);
      result = (EnumWithCustomDisplay) Array.get(values, 0);
    }
    catch (Exception e) {
      result = null;
      LoggingHelper.global().log(Level.SEVERE, "Failed to instantiate enum class: " + cls.getName(), e);
    }

    return result;
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
    Object			result;
    Class			cl;
    EnumWithCustomDisplay	value;

    result = null;

    if (ClassLocator.hasInterface(EnumWithCustomDisplay.class, getBaseClass())) {
      value = getCustomDisplayInstance();
      if (value != null)
	result = value.parse(s);
    }
    else if (ClassLocator.isSubclass(Enum.class, getBaseClass())) {
      cl     = getBaseClass().asSubclass(Enum.class);
      result = Enum.valueOf(cl, s);
    }

    return result;
  }

  /**
   * Returns a string representation of the specified object.
   *
   * @param obj		the object to turn into a string
   * @return		the string representation
   */
  @Override
  public String toString(Object obj) {
    String	result;

    result = "";

    if (ClassLocator.hasInterface(EnumWithCustomDisplay.class, getBaseClass()))
      result = ((EnumWithCustomDisplay) obj).toRaw();
    else if (ClassLocator.isSubclass(Enum.class, getBaseClass()))
      result = obj.toString();

    return result;
  }

  /**
   * Adds additional information about the argument, e.g., the class.
   *
   * @param buffer	the buffer to add the information to
   */
  protected void addArgumentInfo(StringBuilder buffer) {
    String	text;
    Method	method;
    Object[] 	values;

    try {
      method = getBaseClass().getMethod("values");
      values = (Object[]) method.invoke(null, new Object[0]);
      text   = Utils.arrayToString(values).replaceAll(",", "|");
    }
    catch (Exception e) {
      LoggingHelper.global().log(Level.SEVERE, "Error retrieving enum values!", e);
      text = "Error retrieving enum values";
    }
    buffer.append(" <").append(text).append(">");
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    super.cleanUp();

    m_CustomDisplayInstance = null;
  }
}
