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
 * MetaDataSupporter.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.mapobject;

import java.util.HashMap;
import java.util.Set;

import org.openstreetmap.gui.jmapviewer.interfaces.MapObject;

/**
 * Interface for {@link MapObject} objects that support meta-data.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface MetaDataSupporter 
  extends MapObject {

  /**
   * Adds a key-value pair to the meta-data store.
   * 
   * @param key		the key to store the value for
   * @param value	the value to store
   * @return		any previously stored object for this key, otherwise null
   */
  public Object addMetaData(String key, Object value);

  /**
   * Removes a key-value pair from the meta-data store.
   * 
   * @param key		the key to remove the value for
   * @return		any previously stored object for this key, null if none was present
   */
  public Object removeMetaData(String key);

  /**
   * Returns the specified value from the meta-data store.
   * 
   * @param key		the key to retrieve the value for
   * @return		stored object for this key, null if none was present
   */
  public Object getMetaData(String key);
  
  /**
   * Returns the meta-data store.
   * 
   * @return		the meta-data
   */
  public HashMap<String,Object> getMetaData();
  
  /**
   * Returns the set of meta-data keys.
   * 
   * @return		the meta-data keys
   */
  public Set<String> metaDataKeys();
}
