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
 * AbstractIDUpdater.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.data.idupdate;

import adams.core.Utils;
import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor for schemes that update the ID of objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractIDUpdater
  extends AbstractOptionHandler
  implements IDUpdater {

  private static final long serialVersionUID = 3343502597905329739L;

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return null;
  }

  /**
   * Performs checks on the object.
   *
   * @param obj		the object to check
   * @param id 		the new ID
   * @return		null if successful, otherwise error message
   */
  protected String check(Object obj, String id) {
    if (obj == null)
      return "No data provided!";
    if (id == null)
      return "No ID provided!";
    if (!handles(obj))
      return "Data type not handled: " + Utils.classToString(obj);
    return null;
  }

  /**
   * Updates the ID of the object.
   *
   * @param obj		the object to process
   * @param id 		the new ID
   * @return		null if successful, otherwise error message
   */
  protected abstract String doUpdateID(Object obj, String id);

  /**
   * Updates the ID of the object.
   *
   * @param obj		the object to process
   * @param id 		the new ID
   * @return		null if successful, otherwise error message
   */
  public String updateID(Object obj, String id) {
    String 	result;

    result = check(obj, id);
    if (result == null)
      result = doUpdateID(obj, id);

    return result;
  }
}
