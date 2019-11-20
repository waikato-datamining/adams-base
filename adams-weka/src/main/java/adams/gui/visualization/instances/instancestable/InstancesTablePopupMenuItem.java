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
 * InstancesTablePopupMenuItem.java
 * Copyright (C) 2016-2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.instances.instancestable;

import adams.gui.visualization.instances.instancestable.InstancesTablePopupMenuItemHelper.TableState;

/**
 * Ancestor for menu items of popups for the InstancesTable.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface InstancesTablePopupMenuItem
  extends Comparable<InstancesTablePopupMenuItem> {

  /**
   * Returns the name for the menu item.
   *
   * @return            the name
   */
  public String getMenuItem();

  /**
   * Returns the name of the icon.
   *
   * @return            the name, null if none available
   */
  public String getIconName();

  /**
   * Returns whether the menu item is available.
   *
   * @param state 	the state to use
   * @return            true if available
   */
  public boolean isAvailable(TableState state);
}
