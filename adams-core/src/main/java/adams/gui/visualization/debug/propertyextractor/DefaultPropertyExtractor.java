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
 * DefaultPropertyExtractor.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.debug.propertyextractor;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import adams.gui.goe.Editors;

/**
 * Default extractor, which is used as fallback.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DefaultPropertyExtractor
  extends AbstractPropertyExtractor {
  
  /** the properties. */
  protected PropertyDescriptor[] m_Properties;
  
  /**
   * Checks whether this extractor actually handles this type of class.
   * 
   * @param cls		the class to check
   * @return		true if the extractor handles the object/class
   */
  @Override
  public boolean handles(Class cls) {
    return !Editors.isBlacklisted(cls, false);
  }
  
  /**
   * Initializes the extractor.
   */
  @Override
  protected void initialize() {
    BeanInfo 	bi;

    super.initialize();
    
    try {
      bi           = Introspector.getBeanInfo(m_Current.getClass());
      m_Properties = bi.getPropertyDescriptors();
    }
    catch (Exception e) {
      System.err.println("Failed to obtain bean property descriptors for class " + m_Current.getClass().getName() + ":");
      e.printStackTrace();
      m_Properties = new PropertyDescriptor[0];
    }
  }  

  /**
   * The number of properties that are available.
   * 
   * @return		the number of properties
   */
  @Override
  public int size() {
    return m_Properties.length;
  }
  
  /**
   * Returns the current value of the specified property.
   * 
   * @param index	the index of the property to retrieve
   * @return		the current value of the property
   */
  @Override
  public Object getValue(int index) {
    Object	result;
    
    result = null;
    
    if (Editors.isBlacklisted(m_Current.getClass(), m_Properties[index].getDisplayName()))
      return result;
    
    if ((m_Properties[index].getReadMethod() != null) && (m_Properties[index].getWriteMethod() != null)) {
      try {
	result = m_Properties[index].getReadMethod().invoke(m_Current, new Object[0]);
      }
      catch (Exception e) {
	System.err.println("Failed to obtain current value for " + m_Current.getClass().getName() + "/" + m_Properties[index].getDisplayName() + ":");
	e.printStackTrace();
	result = null;
      }
    }
    
    return result;
  }
  
  /**
   * Returns the label for the specified property.
   * 
   * @param index	the index of the property to get the label for
   * @return		the label for the property
   */
  @Override
  public String getLabel(int index) {
    return m_Properties[index].getDisplayName();
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    super.cleanUp();
    
    m_Properties = null;
  }
}
