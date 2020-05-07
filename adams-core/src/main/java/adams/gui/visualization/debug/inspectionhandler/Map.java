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
 * Map.java
 * Copyright (C) 2020 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.debug.inspectionhandler;

import nz.ac.waikato.cms.locator.ClassLocator;

import java.util.Hashtable;

/**
 * Provides further insight into java.util.Map objects.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class Map
  extends AbstractInspectionHandler {

  /**
   * Checks whether the handler can handle the specified class.
   *
   * @param cls		the class to check
   * @return		true if the handler can handle this type of object
   */
  @Override
  public boolean handles(Class cls) {
    return ClassLocator.hasInterface(java.util.Map.class, cls);
  }

  /**
   * Returns further inspection values.
   *
   * @param obj		the object to further inspect
   * @return		the named inspected values
   */
  @Override
  public Hashtable<String,Object> inspect(Object obj) {
    Hashtable<String,Object>	result;
    java.util.Map 		map;

    result = new Hashtable<>();

    map = (java.util.Map) obj;
    for (Object key: map.keySet())
      result.put("" + key, map.get(key));

    return result;
  }
}
