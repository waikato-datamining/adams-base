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
 * BaseMenu.java
 * Copyright (C) 2010-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import adams.gui.action.PropertiesAction;

/**
 * Extended JMenu class that also supports sorting of its menu items.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseMenu
  extends JMenu {

  /** for serialization. */
  private static final long serialVersionUID = -4652341738136722664L;

  /**
   * Constructs a new <code>BaseMenu</code> with no text.
   */
  public BaseMenu() {
    super();
  }

  /**
   * Constructs a new <code>BaseMenu</code> with the supplied string
   * as its text.
   *
   * @param s  the text for the menu label
   */
  public BaseMenu(String s) {
    super(s);
  }

  /**
   * Constructs a menu whose properties are taken from the
   * <code>Action</code> supplied.
   * @param a an <code>Action</code>
   */
  public BaseMenu(Action a) {
    super(a);
  }

  /**
   * Constructs a new <code>BaseMenu</code> with the supplied string as
   * its text and specified as a tear-off menu or not.
   *
   * @param s the text for the menu label
   * @param b can the menu be torn off (not yet implemented)
   */
  public BaseMenu(String s, boolean b) {
    super(s, b);
  }

  /**
   * Creates a new menu item attached to the specified
   * <code>Action</code> object and appends it to the end of this menu.
   *
   * @param a the <code>Action</code> for the menu item to be added
   * @see Action
   */
  @Override
  public JMenuItem add(Action a) {
    if (a instanceof PropertiesAction)
      return add(((PropertiesAction) a).getMenuItem());
    else
      return super.add(a);
  }

  /**
   * Inserts a new menu item attached to the specified <code>Action</code>
   * object at a given position.
   *
   * @param a the <code>Action</code> object for the menu item to add
   * @param pos an integer specifying the position at which to add the
   *               new menu item
   * @exception IllegalArgumentException if the value of
   *                  <code>pos</code> < 0
   */
  @Override
  public JMenuItem insert(Action a, int pos) {
    if (a instanceof PropertiesAction)
      return insert(((PropertiesAction) a).getMenuItem(), pos);
    else
      return super.insert(a, pos);
  }

  /**
   * Sorts the menu items alphabetically.
   */
  public void sort() {
    ArrayList<JMenuItem>	items;
    int				i;

    items = new ArrayList<JMenuItem>();
    for (i = 0; i < getItemCount(); i++)
      items.add(getItem(i));
    Collections.sort(items, new MenuItemComparator());
    removeAll();
    for (JMenuItem item: items)
      add(item);
  }
  
  /**
   * Creates a cascading menu that has at most "max entries". Ones the
   * threshold is reached, a new submenu with the "more" label is introduced
   * and subsequent entries are placed in their.
   * 
   * @param menuitems	the menuitems to create the menu from
   * @param max		the maximum number of items (+ one for the "more") to show
   * @param more	the text label for the sub-menu
   * @return		the menu
   */
  public static BaseMenu createCascadingMenu(JMenuItem[] menuitems, int max, String more) {
    return createCascadingMenu(Arrays.asList(menuitems), max, more);
  }
  
  /**
   * Creates a cascading menu that has at most "max entries". Ones the
   * threshold is reached, a new submenu with the "more" label is introduced
   * and subsequent entries are placed in their.
   * 
   * @param menuitems	the menuitems to create the menu from
   * @param max		the maximum number of items (+ one for the "more") to 
   * 			show, use -1 for automatic maximum based on screen size
   * @param more	the text label for the sub-menu
   * @return		the menu
   */
  public static BaseMenu createCascadingMenu(List<JMenuItem> menuitems, int max, String more) {
    BaseMenu	result;
    BaseMenu	submenu;
    BaseMenu	current;
    int		count;
    
    result  = new BaseMenu();
    current = result;
    count   = 0;
    
    if (max == -1) {
      max = (int) Math.floor(
	  GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getBounds().getHeight() 
	  / (result.getFontMetrics(result.getFont()).getHeight() * 1.5));
    }
    
    for (JMenuItem menuitem: menuitems) {
      count++;
      current.add(menuitem);
      if (count >= max) {
	count = 0;
	submenu = new BaseMenu(more);
	current.add(submenu);
	current = submenu;
      }
    }
    
    return result;
  }
}
