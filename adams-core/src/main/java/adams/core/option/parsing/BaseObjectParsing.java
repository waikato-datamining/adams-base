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
 * BaseObjectParsing.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.core.option.parsing;

import adams.core.Utils;
import adams.core.base.BaseObject;
import adams.core.base.BaseString;
import adams.core.option.AbstractOption;

/**
 * For parsing BaseObject (and derived) objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class BaseObjectParsing {

  /**
   * Returns the BaseObject as string.
   *
   * @param option	the current option
   * @param object	the BaseObject object to convert
   * @return		the generated string
   */
  public static String toString(AbstractOption option, Object object) {
    return Utils.backQuoteChars(((BaseObject) object).getValue());
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

    if (result == BaseObject.class) {
      System.err.println("Falling back to BaseString class!");
      result = BaseString.class;
    }

    return result;
  }

  /**
   * Returns a BaseObject generated from the string.
   *
   * @param option	the current option
   * @param str		the string to convert to a BaseObject
   * @return		the generated BaseObject
   */
  public static Object valueOf(AbstractOption option, String str) {
    BaseObject	result;
    Class	cls;

    try {
      cls    = determineClass(option.getDefaultValue());
      result = (BaseObject) cls.newInstance();
      result.setValue(Utils.unbackQuoteChars(str));
    }
    catch (Exception e) {
      result = null;
      e.printStackTrace();
    }

    return result;
  }
}
