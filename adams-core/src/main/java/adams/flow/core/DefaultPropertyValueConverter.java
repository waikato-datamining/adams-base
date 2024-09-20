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
 * DefaultPropertyValueConverter.java
 * Copyright (C) 2012-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.core;

import adams.core.base.BaseObject;
import adams.core.io.PlaceholderFile;
import adams.core.option.OptionUtils;
import nz.ac.waikato.cms.locator.ClassLocator;

import java.io.File;
import java.lang.reflect.Array;

/**
 * Default handler for primitives.
 * Values for arrays are assumed to be blank-separated strings (one element
 * per array value).
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
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
    if (ClassLocator.isSubclass(File.class, cls))
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
      result = Boolean.parseBoolean(value);
    // byte
    else if ((cls == Byte.class) || (cls == Byte.TYPE))
      result = Byte.parseByte(value);
    // short
    else if ((cls == Short.class) || (cls == Short.TYPE))
      result = Short.parseShort(value);
    // integer
    else if ((cls == Integer.class) || (cls == Integer.TYPE))
      result = Integer.parseInt(value);
    // long
    else if ((cls == Long.class) || (cls == Long.TYPE))
      result = Long.parseLong(value);
    // float
    else if ((cls == Float.class) || (cls == Float.TYPE))
      result = Float.parseFloat(value);
    // double
    else if ((cls == Double.class) || (cls == Double.TYPE))
      result = Double.parseDouble(value);
    // String
    else if (cls == String.class)
      result = value;
    // File (we assume that the string is a placeholder file)
    else if (ClassLocator.isSubclass(File.class, cls))
      result = new PlaceholderFile(value).getAbsoluteFile();
    // BaseObject (or derived class)
    else if (ClassLocator.isSubclass(BaseObject.class, cls)) {
      result = cls.getDeclaredConstructor().newInstance();
      ((BaseObject) result).setValue(value);
    }

    return result;
  }
}
