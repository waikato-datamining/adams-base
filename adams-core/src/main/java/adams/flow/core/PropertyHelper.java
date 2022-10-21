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
 * PropertyHelper.java
 * Copyright (C) 2011-2020 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.core;

import adams.core.MessageCollection;
import adams.core.classmanager.ClassManager;
import adams.core.discovery.PropertyPath.PropertyContainer;
import adams.core.option.OptionUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for updating properties of objects.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class PropertyHelper {

  /** the available converters. */
  protected static List<AbstractPropertyValueConverter> m_Converters;
  
  /** the default converter. */
  protected static AbstractPropertyValueConverter m_DefaultConverter;
  
  /**
   * Initializes the converter list.
   * 
   * @see		#m_Converters
   */
  protected static synchronized void initialize() {
    ArrayList<AbstractPropertyValueConverter>	converters;
    AbstractPropertyValueConverter		converter;
    String[]					classnames;
    
    if (m_Converters == null) {
      m_DefaultConverter = new DefaultPropertyValueConverter();
      converters = new ArrayList<>();
      classnames = AbstractPropertyValueConverter.getConverters();
      for (String classname: classnames) {
	if (classname.equals(m_DefaultConverter.getClass().getName()))
	  continue;
	try {
	  converter = (AbstractPropertyValueConverter) ClassManager.getSingleton().forName(classname).getDeclaredConstructor().newInstance();
	  converters.add(converter);
	}
	catch (Exception e) {
	  System.err.println("Failed to instantiate property value converter: " + classname);
	  e.printStackTrace();
	}
      }
      m_Converters = converters;
    }
  }

  /**
   * Converts the string value into an object.
   *
   * @param cls		the requested class
   * @param value	the string representation
   * @param errors	for collecting errors
   * @return		the object or null if failed to convert
   */
  protected static Object convertValue(Class cls, String value, MessageCollection errors) {
    Object	result;

    result = null;

    initialize();

    // default one first
    if (m_DefaultConverter.handles(cls)) {
      try {
	result = m_DefaultConverter.convert(cls, value);
      }
      catch (Exception e) {
	errors.add("Failed to convert '" + value + "' with " + m_DefaultConverter.getClass().getName() + "!", e);
      }
    }

    // try other converters
    if (result == null) {
      for (AbstractPropertyValueConverter converter: m_Converters) {
	if (converter.handles(cls)) {
	  try {
	    result = converter.convert(cls, value);
	  }
	  catch (Exception e) {
	    errors.add("Failed to convert '" + value + "' with " + converter.getClass().getName() + "!", e);
	  }
	  if (result != null)
	    break;
	}
      }
    }

    if (result == null)
      errors.add("Class " + cls.getName() + " not (yet) supported for setting property!");

    return result;
  }
  
  /**
   * Converts the value into the appropriate object, if possible.
   *
   * @param cont	the property container to use
   * @param value	the string to convert
   * @return		the converted value or null if it cannot be converted
   */
  public static Object convertValue(PropertyContainer cont, String value, MessageCollection errors) {
    Object	result;
    Class	cls;
    String[]	parts;
    int		i;

    if (value == null)
      return null;

    cls = cont.getReadMethod().getReturnType();
    // are we setting an element of an array?
    if (cont.getPath().get(cont.getPath().size() - 1).getIndex() > -1)
      cls = cls.getComponentType();

    // setting an array? split and assemble elements
    if (cls.isArray()) {
      try {
	parts  = OptionUtils.splitOptions(value);
	result = Array.newInstance(cls.getComponentType(), parts.length);
	for (i = 0; i < parts.length; i++)
	  Array.set(result, i, convertValue(cls.getComponentType(), parts[i], errors));
	return result;
      }
      catch (Exception e) {
        errors.add("Failed to split value for array: " + value, e);
        return null;
      }
    }
    // non-array
    else {
      return convertValue(cls, value, errors);
    }
  }

}
