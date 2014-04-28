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
 * PopupMenuCustomizer.java
 * Copyright (C) 2008-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.core;

import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

/**
 * For classes that customize popup menus, before they're displayed.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface PopupMenuCustomizer 
  extends adams.gui.core.PopupMenuCustomizer<MouseEvent> {

  /**
   * Optional customizing of the menu that is about to be popped up.
   *
   * @param e		The mouse event
   * @param menu	The menu to customize.
   */
  public void customizePopupMenu(MouseEvent e, JPopupMenu menu);
}
