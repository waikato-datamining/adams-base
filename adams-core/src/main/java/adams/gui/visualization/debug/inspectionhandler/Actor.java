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
 * Actor.java
 * Copyright (C) 2012-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.debug.inspectionhandler;

import adams.core.ClassLocator;

import java.util.Hashtable;

/**
 * Inspection handler for actors.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Actor
  extends OptionHandlerInspectionHandler {

  /**
   * Checks whether the handler can handle the specified class.
   *
   * @param cls		the class to check
   * @return		true if the handler can handle this type of object
   */
  public boolean handles(Class cls) {
    return ClassLocator.hasInterface(Actor.class, cls);
  }

  /**
   * Returns further inspection values.
   *
   * @param obj		the object to further inspect
   * @return		always empty hashtable
   */
  public Hashtable<String,Object> inspect(Object obj) {
    Hashtable<String,Object>	result;
    String			name;
    adams.core.Variables	vars;
    
    result = super.inspect(obj);
    name   = ((adams.flow.core.Actor) obj).getFullName();
    if (name != null)
      result.put("fullName", name);
    vars   = ((adams.flow.core.Actor) obj).getVariables();
    if (vars != null)
      result.put("variables", vars);
    
    return result;
  }
}
