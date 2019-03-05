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
 * BasePopupMenu.java
 * Copyright (C) 2015-2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;

/**
 * Extended {@link JPopupMenu}, offering better placement.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
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

  /**
   * Creates a cascading menu that has at most "max entries". Ones the
   * threshold is reached, a new submenu with the "more" label is introduced
   * and subsequent entries are placed in their.
   *
   * @param menuitems	the menuitems to create the menu from
   * @param y 		where the menu will be displayed
   * @param max		the maximum number of items (+ one for the "more") to show
   * @param more	the text label for the sub-menu
   * @return		the menu
   */
  public static BasePopupMenu createCascadingMenu(JMenuItem[] menuitems, int y, int max, String more) {
    return createCascadingMenu(Arrays.asList(menuitems), y, max, more);
  }

  /**
   * Creates a cascading menu that has at most "max entries". Ones the
   * threshold is reached, a new submenu with the "more" label is introduced
   * and subsequent entries are placed in their.
   *
   * @param menuitems	the menuitems to create the menu from
   * @param y 		where the menu will be displayed
   * @param max		the maximum number of items (+ one for the "more") to
   * 			show, use -1 for automatic maximum based on screen size
   * @param more	the text label for the sub-menu
   * @return		the menu
   */
  public static BasePopupMenu createCascadingMenu(List<JMenuItem> menuitems, int y, int max, String more) {
    BasePopupMenu	result;
    BaseMenu		submenu;
    Object		current;
    int			count;

    result  = new BasePopupMenu();
    current = result;
    count   = 0;

    if (max == -1) {
      max = (int) Math.floor(
	(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getBounds().getHeight() - y)
	  / (result.getFontMetrics(result.getFont()).getHeight() * 1.5));
    }

    for (JMenuItem menuitem: menuitems) {
      count++;
      if (current instanceof BasePopupMenu)
	((BasePopupMenu) current).add(menuitem);
      else
	((BaseMenu) current).add(menuitem);
      if (count >= max) {
	count = 0;
	submenu = new BaseMenu(more);
	if (current instanceof BasePopupMenu)
	  ((BasePopupMenu) current).add(submenu);
	else
	  ((BaseMenu) current).add(submenu);
	current = submenu;
      }
    }

    return result;
  }
}
