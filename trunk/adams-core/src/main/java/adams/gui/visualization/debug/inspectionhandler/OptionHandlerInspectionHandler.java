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
 * OptionHandlerInspectionHandler.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.debug.inspectionhandler;

import java.util.Hashtable;

import adams.core.ClassLocator;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionHandler;

/**
 * Inspection handler for classes that support {@link AbstractOptionHandler}.
 * Outputs the global info as well.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class OptionHandlerInspectionHandler
  extends AbstractInspectionHandler {

  /**
   * Checks whether the handler can handle the specified class.
   *
   * @param cls		the class to check
   * @return		true if the handler can handle this type of object
   */
  public boolean handles(Class cls) {
    return ClassLocator.hasInterface(OptionHandler.class, cls);
  }

  /**
   * Returns further inspection values.
   *
   * @param obj		the object to further inspect
   * @return		always empty hashtable
   */
  public Hashtable<String,Object> inspect(Object obj) {
    Hashtable<String,Object>	result;
    String			info;
    
    result = new Hashtable<String,Object>();
    info   = ((AbstractOptionHandler) obj).globalInfo();
    if (info != null)
      result.put("globalInfo", info);
    
    return result;
  }
}
