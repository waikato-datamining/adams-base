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
 * EnumHelper.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import java.lang.reflect.Method;

import adams.core.option.EnumOption;

/**
 * Helper class for enum-related operations.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class EnumHelper {

  /** the name of the static method for returning all values of an enum. */
  public static final String METHOD_VALUES = "values";

  /**
   * Gets all the values of the num.
   *
   * @param obj		the enm object to get the values from
   * @return 		an array of string tags.
   */
  public static Object[] getValues(Object obj) {
    return getValues(obj.getClass());
  }

  /**
   * Gets all the values of the num.
   *
   * @param cls		the enm class to get the values from
   * @return 		an array of string tags.
   */
  public static Object[] getValues(Class cls) {
    Method	method;
    Object[]	result;

    try {
      method = cls.getMethod(METHOD_VALUES, new Class[0]);
      result    = (Object[]) method.invoke(null, new Object[0]);
    }
    catch (Exception e) {
      System.err.println("Failed to obtain all enum values for enum: " + cls);
      e.printStackTrace();
      result = new Object[0];
    }

    return result;
  }

  /**
   * Determines the base class.
   * 
   * @param obj		the object to inspect
   * @return		the determined class
   */
  public static Class determineClass(Object obj) {
    Class	result;
    
    result = obj.getClass();
    
    if (result.isArray())
      result = result.getComponentType();
    
    return result;
  }

  /**
   * Parses the given enum string.
   * 
   * @param cls		the enum class
   * @param s		the string to parse
   * @return		the parsed enum, null in case of an error
   */
  public static Object parse(Class cls, String s) {
    Object			result;
    EnumWithCustomDisplay	enm;
    
    result = null;
    
    if (ClassLocator.hasInterface(EnumWithCustomDisplay.class, cls)) {
      enm    = EnumOption.getEnumInstance(cls);
      result = enm.parse(s);
    }
    else {
      result = Enum.valueOf(cls, s);
    }
    
    return result;
  }
}
