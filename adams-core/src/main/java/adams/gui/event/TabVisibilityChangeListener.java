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
 * TabVisibilityChangeListener.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.event;

import adams.gui.core.BaseTabbedPaneWithTabHiding;

/**
 * Interface for classes that listen to changes in visibility of tabs
 * of a {@link BaseTabbedPaneWithTabHiding}.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see BaseTabbedPaneWithTabHiding
 */
public interface TabVisibilityChangeListener {

  /**
   * Gets called when a tab changes visibility.
   *
   * @param e		the trigger event
   */
  public void tabVisibilityChanged(TabVisibilityChangeEvent e);
}
