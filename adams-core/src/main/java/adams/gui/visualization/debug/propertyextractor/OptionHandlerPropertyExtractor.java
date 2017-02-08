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
 * OptionHandlerPropertyExtractor.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.debug.propertyextractor;

import adams.core.option.AbstractArgumentOption;
import adams.core.option.AbstractOption;
import adams.core.option.OptionHandler;
import nz.ac.waikato.cms.locator.ClassLocator;

import java.util.List;

/**
 * Extractor for objects supporting the {@link OptionHandler} interface.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class OptionHandlerPropertyExtractor
  extends AbstractPropertyExtractor {
  
  /** Stores the options. */
  protected List<AbstractOption> m_Options;
  
  /**
   * Checks whether this extractor actually handles this type of class.
   * 
   * @param cls		the class to check
   * @return		true if the extractor handles the object/class
   */
  public boolean handles(Class cls) {
    return ClassLocator.hasInterface(OptionHandler.class, cls);
  }
  
  /**
   * Initializes the extractor.
   */
  protected void initialize() {
    super.initialize();
    
    m_Options = ((OptionHandler) m_Current).getOptionManager().getOptionsList();
  }  

  /**
   * The number of properties that are available.
   * 
   * @return		the number of properties
   */
  public int size() {
    return m_Options.size();
  }
  
  /**
   * Returns the current value of the specified property.
   * 
   * @param index	the index of the property to retrieve
   * @return		the current value of the property
   */
  public Object getValue(int index) {
    return m_Options.get(index).getCurrentValue();
  }
  
  /**
   * Returns the label for the specified property.
   * 
   * @param index	the index of the property to get the label for
   * @return		the label for the property
   */
  public String getLabel(int index) {
    String		result;
    AbstractOption	option;
    
    option = m_Options.get(index);
    result = option.getProperty();
    if (option instanceof AbstractArgumentOption) {
      if (((AbstractArgumentOption) option).isVariableAttached())
	result += "/" + ((AbstractArgumentOption) option).getVariable();
    }
    
    return result;
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    super.cleanUp();
    
    m_Options = null;
  }
}
