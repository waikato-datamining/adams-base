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
 * ContainerListPopupMenuSupplier.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.container;

import adams.gui.core.BasePopupMenu;

/**
 * Interface for components that can supply a popup menu for the
 * container list.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <M> the type of container manager to use
 * @param <C> the type of container to use
 */
public interface ContainerListPopupMenuSupplier<M extends AbstractContainerManager, C extends AbstractContainer> {

  /**
   * Returns a popup menu for the table of the container list.
   *
   * @param table	the affected table
   * @param row	the row the mouse is currently over
   * @return		the popup menu
   */
  public BasePopupMenu getContainerListPopupMenu(ContainerTable<M,C> table, int row);
}