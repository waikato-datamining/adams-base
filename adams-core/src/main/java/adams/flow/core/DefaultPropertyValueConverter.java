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
 * DefaultPropertyValueConverter.java
 * Copyright (C) 2012-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.core;

import adams.core.ClassLocator;
import adams.core.base.BaseObject;
import adams.core.io.PlaceholderFile;
import adams.core.option.OptionUtils;

import java.io.File;
import java.lang.reflect.Array;

/**
 * Default handler for primitives.
 * Values for arrays are assumed to be blank-separated strings (one element
 * per array value).
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DefaultPropertyValueConverter
  extends AbstractPropertyValueConverter {

  /** for serialization. */
  private static final long serialVersionUID = 5709690907332699331L;

  /**
   * Checks whether this converter handles the particular class.
   * 
   * @param cls	the class to check
   * @return		true if it supports it
   */
  @Override
  public boolean handles(Class cls) {
    // array?
    if (cls.isArray())
      return handles(cls.getComponentType());

    // boolean
    if ((cls == Boolean.class) || (cls == Boolean.TYPE))
      return true;
    // byte
    if ((cls == Byte.class) || (cls == Byte.TYPE))
      return true;
    // short
    if ((cls == Short.class) || (cls == Short.TYPE))
      return true;
    // integer
    if ((cls == Integer.class) || (cls == Integer.TYPE))
      return true;
    // long
    if ((cls == Long.class) || (cls == Long.TYPE))
      return true;
    // float
    if ((cls == Float.class) || (cls == Float.TYPE))
      return true;
    // double
    if ((cls == Double.class) || (cls == Double.TYPE))
      return true;
    // String
    if (cls == String.class)
      return true;
    // File (we assume that the string is a placeholder file)
    if (cls == File.class)
      return true;
    // BaseObject (or derived class)
    if (ClassLocator.isSubclass(BaseObject.class, cls))
      return true;

    return false;
  }

  /**
   * Converts the variable value into the appropriate object, if possible.
   *
   * @param cls		the type of the property
   * @param value	the string to convert
   * @return		the converted value or null if it cannot be converted
   * @throws Exception	if conversion fails with an error
   */
  @Override
  public Object convert(Class cls, String value) throws Exception {
    Object	result;
    String[]    values;
    int         i;
    
    result = null;

    // array?
    if (cls.isArray()) {
      values = OptionUtils.splitOptions(value);
      result = Array.newInstance(cls.getComponentType(), values.length);
      for (i = 0; i < values.length; i++)
        Array.set(result, i, convert(cls.getComponentType(), values[i]));
      return result;
    }

    // boolean
    if ((cls == Boolean.class) || (cls == Boolean.TYPE))
      result = new Boolean(value);
    // byte
    else if ((cls == Byte.class) || (cls == Byte.TYPE))
      result = new Byte(value);
    // short
    else if ((cls == Short.class) || (cls == Short.TYPE))
      result = new Short(value);
    // integer
    else if ((cls == Integer.class) || (cls == Integer.TYPE))
      result = new Integer(value);
    // long
    else if ((cls == Long.class) || (cls == Long.TYPE))
      result = new Long(value);
    // float
    else if ((cls == Float.class) || (cls == Float.TYPE))
      result = new Float(value);
    // double
    else if ((cls == Double.class) || (cls == Double.TYPE))
      result = new Double(value);
    // String
    else if (cls == String.class)
      result = value;
    // File (we assume that the string is a placeholder file)
    else if (cls == File.class)
      result = new PlaceholderFile(value).getAbsoluteFile();
    // BaseObject (or derived class)
    else if (ClassLocator.isSubclass(BaseObject.class, cls)) {
      result = cls.newInstance();
      ((BaseObject) result).setValue(value);
    }

    return result;
  }
}
