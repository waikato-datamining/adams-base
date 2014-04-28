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
 * AbstractPropertyValueConverter.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.core;

import java.io.Serializable;

import adams.core.ClassLister;

/**
 * Ancestor for custom helper classes for updating properties of objects.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractPropertyValueConverter
  implements Serializable {

  /** for serialization. */
  private static final long serialVersionUID = -896540132422815231L;

  /**
   * Default constructor.
   */
  protected AbstractPropertyValueConverter() {
    super();
    initialize();
  }

  /**
   * Initializes member variables.
   * <p/>
   * Default implementation does nothing.
   */
  protected void initialize() {
  }

  /**
   * Checks whether this converter handles the particular class.
   * 
   * @param cls		the class to check
   * @return		true if it supports it
   */
  public abstract boolean handles(Class cls);
  
  /**
   * Converts the variable value into the appropriate object, if possible.
   *
   * @param cls		the type of the property
   * @param value	the string to convert
   * @return		the converted value or null if it cannot be converted
   * @throws Exception	if conversion fails with an error
   */
  public abstract Object convert(Class cls, String value) throws Exception;

  /**
   * Returns a list with classnames of converters.
   *
   * @return		the converter classnames
   */
  public static String[] getConverters() {
    return ClassLister.getSingleton().getClassnames(AbstractPropertyValueConverter.class);
  }
}