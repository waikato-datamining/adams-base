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
 * AbstractGroupUpdater.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.data.groupupdate;

import adams.core.Utils;
import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor for schemes that update the group of objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractGroupUpdater
  extends AbstractOptionHandler
  implements GroupUpdater {

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
   * @param group 	the new group
   * @return		null if successful, otherwise error message
   */
  protected String check(Object obj, String group) {
    if (obj == null)
      return "No data provided!";
    if (group == null)
      return "No group provided!";
    if (!handles(obj))
      return "Data type not handled: " + Utils.classToString(obj);
    return null;
  }

  /**
   * Updates the group of the object.
   *
   * @param obj		the object to process
   * @param group 	the new group
   * @return		null if successful, otherwise error message
   */
  protected abstract String doUpdateGroup(Object obj, String group);

  /**
   * Updates the group of the object.
   *
   * @param obj		the object to process
   * @param group 	the new group
   * @return		null if successful, otherwise error message
   */
  public String updateGroup(Object obj, String group) {
    String	result;

    result = check(obj, group);
    if (result == null)
      result = doUpdateGroup(obj, group);

    return result;
  }
}
