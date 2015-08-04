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
 * PropertyHelper.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.core;

import adams.core.discovery.PropertyPath.PropertyContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for updating properties of objects.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
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
      converters = new ArrayList<AbstractPropertyValueConverter>();
      classnames = AbstractPropertyValueConverter.getConverters();
      for (String classname: classnames) {
	if (classname.equals(m_DefaultConverter.getClass().getName()))
	  continue;
	try {
	  converter = (AbstractPropertyValueConverter) Class.forName(classname).newInstance();
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
   * Converts the value into the appropriate object, if possible.
   *
   * @param cont	the property container to use
   * @param value	the string to convert
   * @return		the converted value or null if it cannot be converted
   */
  public static Object convertValue(PropertyContainer cont, String value) {
    Object	result;
    Class	cls;

    result = null;
    cls    = cont.getReadMethod().getReturnType();
    if (value == null)
      return result;

    initialize();

    // default one first
    if (m_DefaultConverter.handles(cls)) {
      try {
	result = m_DefaultConverter.convert(cls, value);
      }
      catch (Exception e) {
	System.err.println("Failed to convert '" + value + "' with " + m_DefaultConverter.getClass().getName() + "!");
	e.printStackTrace();
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
	    System.err.println("Failed to convert '" + value + "' with " + converter.getClass().getName() + "!");
	    e.printStackTrace();
	  }
	  if (result != null)
	    break;
	}
      }
    }

    if (result == null)
      System.err.println(
	  "Class " + cls.getName() + " not (yet) supported for setting property!");

    return result;
  }

}
