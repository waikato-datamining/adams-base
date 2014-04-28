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
 * VariablesPropertyExtractor.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.debug.propertyextractor;

import java.util.Enumeration;
import java.util.Vector;

import adams.core.ClassLocator;
import adams.core.Variables;


/**
 * Handles {@link Variables} and derived classes, listing each variable
 * as a separate property.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class VariablesPropertyExtractor
  extends AbstractPropertyExtractor {

  /** the variable names. */
  protected Vector<String> m_Names;
  
  /**
   * Initializes the extractor.
   */
  @Override
  protected void initialize() {
    Enumeration<String>	names;
    
    super.initialize();
    
    m_Names = new Vector<String>();
    names   = ((Variables) m_Current).names();
    while (names.hasMoreElements())
      m_Names.add(names.nextElement());
  }  

  /**
   * Checks whether this extractor actually handles this type of class.
   * 
   * @param cls		the class to check
   * @return		true if the extractor handles the object/class
   */
  @Override
  public boolean handles(Class cls) {
    return ClassLocator.isSubclass(adams.core.Variables.class, cls);
  }

  /**
   * The number of properties that are available.
   * 
   * @return		the number of variables
   */
  @Override
  public int size() {
    return m_Names.size();
  }

  /**
   * Returns the current value of the specified property.
   * 
   * @param index	the index of the property to retrieve
   * @return		the variable value
   */
  @Override
  public Object getValue(int index) {
    return ((Variables) m_Current).get(m_Names.get(index));
  }

  /**
   * Returns the label for the specified property.
   * 
   * @param index	the index of the property to get the label for
   * @return		the variable name
   */
  @Override
  public String getLabel(int index) {
    return m_Names.get(index);
  }
}
