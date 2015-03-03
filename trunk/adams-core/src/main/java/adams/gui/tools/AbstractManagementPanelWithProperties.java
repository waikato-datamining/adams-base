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
 * AbstractManagementPanelWithProperties.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import adams.core.Properties;

/**
 * Ancestor panel for properties-based management panels.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractManagementPanelWithProperties<T extends Comparable>
  extends AbstractManagementPanel<T> {
  
  /** for serialization. */
  private static final long serialVersionUID = 153385171161348941L;

  /** the underlying properties. */
  protected Properties m_Properties;
  
  /**
   * Returns the properties to work with. Loads them, if necessary.
   * 
   * @return		the properties
   */
  protected abstract Properties getProperties();
  
  /**
   * Saves the properties on disk.
   * 
   * @return		true if successfully saved
   */
  protected abstract boolean storeProperties();

  /**
   * Creates the key for storing the object in the properties.
   * 
   * @param value	the object to create the key from
   * @return		the generated key
   */
  protected abstract String createKey(T value);

  /**
   * Turns the string obtained from the properties file into an object.
   * 
   * @param s		the string to parse
   * @return		the generated object, null if failed to generate
   */
  protected abstract T fromString(String s);

  /**
   * Turns the object into a string to be stored in the properties file.
   * 
   * @param value	the object to convert
   * @return		the generated strings
   */
  protected abstract String toString(T value);
  
  /**
   * Loads all the objects.
   * 
   * @return		all available Objects
   */
  @Override
  protected List<T> loadAll() {
    List<T>	result;
    T		value;
    Enumeration	enm;
    String	key;
    
    result = new ArrayList<T>();
    enm    = getProperties().propertyNames();
    while (enm.hasMoreElements()) {
      key   = enm.nextElement().toString();
      value = fromString(getProperties().getProperty(key));
      if (value != null)
	result.add(value);
    }
    
    return result;
  }
  
  /**
   * Checks whether the object already exists.
   * 
   * @param value	the value to look for
   * @return		true if already available
   */
  @Override
  protected boolean exists(T value) {
    String	key;
    
    key = createKey(value);
    if (key == null)
      return false;
    else
      return getProperties().containsKey(key);
  }
  
  /**
   * Stores the object.
   * 
   * @param value	the value to store
   * @return		true if successfully stored
   */
  @Override
  protected boolean store(T value) {
    String	key;
    
    key = createKey(value);
    getProperties().setProperty(key, toString(value));
    
    return storeProperties();
  }
  
  /**
   * Removes the object.
   * 
   * @param value	the value to remove
   * @return		true if successfully removed
   */
  @Override
  protected boolean remove(T value) {
    getProperties().remove(createKey(value));
    return true;
  }
  
  /**
   * Returns the ID from the object.
   * 
   * @param value	the object to get the ID from
   * @return		the ID, null if it could not be retrieved
   */
  @Override
  protected String getID(T value) {
    return createKey(value);
  }
}
