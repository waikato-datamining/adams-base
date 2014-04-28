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
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import java.util.ArrayList;
import java.util.Collections;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

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
   * Constructs a new <code>JMenu</code> with no text.
   */
  public BaseMenu() {
    super();
  }

  /**
   * Constructs a new <code>JMenu</code> with the supplied string
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
   * Constructs a new <code>JMenu</code> with the supplied string as
   * its text and specified as a tear-off menu or not.
   *
   * @param s the text for the menu label
   * @param b can the menu be torn off (not yet implemented)
   */
  public BaseMenu(String s, boolean b) {
    super(s, b);
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
}
