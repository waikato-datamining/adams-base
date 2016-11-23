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
 * AbstractBaseActionWithGroup.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.action;

/**
 * Ancestor for actions that support a group.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractBaseActionWithGroup
  extends AbstractBaseAction
  implements BaseActionWithGroup {

  private static final long serialVersionUID = 8317879939178154300L;

  /** the key for the group. */
  public final static String GROUP = "group";

  /**
   * Sets the group of the action.
   *
   * @param value	the name of the group, null to unset
   */
  public void setGroup(String value) {
    putValue(GROUP, value);
  }

  /**
   * Returns the group this action belongs to.
   *
   * @return		the name of the group, null if not set
   */
  public String getGroup() {
    return (String) getValue(GROUP);
  }
}
