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
 * BasePopupMenu.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import javax.swing.JPopupMenu;
import java.awt.Component;
import java.awt.event.MouseEvent;

/**
 * Extended {@link JPopupMenu}, offering better placement.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BasePopupMenu
  extends JPopupMenu {

  private static final long serialVersionUID = -2475983585897650618L;

  /**
   * Displays the popup menu using the absolute screen position.
   *
   * @param invoker	the invoking component
   * @param e		the absolute positions of the event are used
   */
  public void showAbsolute(Component invoker, MouseEvent e) {
    GUIHelper.showPopupMenu(this, invoker, e);
  }

  /**
   * Displays the popup menu using the absolute screen position.
   *
   * @param invoker	the invoking component
   * @param x		the absolute X position on screen
   * @param y		the absolute Y position on screen
   */
  public void showAbsolute(Component invoker, int x, int y) {
    GUIHelper.showPopupMenu(this, invoker, x, y);
  }
}
