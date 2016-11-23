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
 * BaseActionWithGroup.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.action;

/**
 * Action that offers a group name to be used for sorting.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface BaseActionWithGroup
  extends BaseAction {

  /**
   * Sets the group of the action.
   *
   * @param value	the name of the group, null to unset
   */
  public void setGroup(String value);

  /**
   * Returns the group this action belongs to.
   *
   * @return		the name of the group, null if not set
   */
  public String getGroup();
}
