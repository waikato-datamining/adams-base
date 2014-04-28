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
 * MOAPropertyValueConverter.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.core;

import moa.AbstractMOAObject;
import adams.core.ClassLocator;
import adams.core.option.MOACommandLineHandler;

/**
 * Handler for MOA classes.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MOAPropertyValueConverter
  extends AbstractPropertyValueConverter {

  /** for serialization. */
  private static final long serialVersionUID = 5709690907332699331L;

  /** the commandline handler to use. */
  protected MOACommandLineHandler m_CommandLineHandler;

  /**
   * Initializes member variables.
   */
  @Override
  protected void initialize() {
    super.initialize();
    m_CommandLineHandler = new MOACommandLineHandler();
  }
  
  /**
   * Checks whether this converter handles the particular class.
   * 
   * @param cls		the class to check
   * @return		true if it supports it
   */
  @Override
  public boolean handles(Class cls) {
    if (ClassLocator.isSubclass(AbstractMOAObject.class, cls))
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
    return m_CommandLineHandler.fromCommandLine(value);
  }
}
