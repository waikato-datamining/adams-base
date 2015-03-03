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
 * PropertiesAction.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.action;

import javax.swing.JMenuItem;

/**
 * Ancestor for actions that use a Properties file as basis for shortcuts,
 * icons and mnemonics.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of state
 */
public interface PropertiesAction<T>
  extends BaseAction {
  
  /**
   * Creates a new menuitem.
   */
  public abstract JMenuItem getMenuItem();

  /**
   * Updates the state of the action.
   * 
   * @param state	the current state
   */
  public void update(T state);
}
