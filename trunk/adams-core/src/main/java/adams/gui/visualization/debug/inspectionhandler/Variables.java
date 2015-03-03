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
 * Variables.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.debug.inspectionhandler;

import java.util.Enumeration;
import java.util.Hashtable;

import adams.core.ClassLocator;

/**
 * Provides further insight into {@link adams.core.Variables} objects.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Variables
  extends AbstractInspectionHandler {

  /**
   * Checks whether the handler can handle the specified class.
   *
   * @param cls		the class to check
   * @return		true if the handler can handle this type of object
   */
  public boolean handles(Class cls) {
    return ClassLocator.isSubclass(adams.core.Variables.class, cls);
  }

  /**
   * Returns further inspection values.
   *
   * @param obj		the object to further inspect
   * @return		the named inspected values
   */
  public Hashtable<String,Object> inspect(Object obj) {
    Hashtable<String,Object>	result;
    adams.core.Variables	vars;
    Enumeration<String>		names;
    String			name;
    
    result = new Hashtable<String,Object>();

    vars  = (adams.core.Variables) obj;
    names = vars.names();
    while (names.hasMoreElements()) {
      name = names.nextElement();
      result.put(name, vars.get(name));
    }

    return result;
  }
}
