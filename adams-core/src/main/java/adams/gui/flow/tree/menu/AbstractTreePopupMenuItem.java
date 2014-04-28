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
 * AbstractTreePopupMenuItem.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.tree.menu;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import adams.core.ClassLister;
import adams.core.logging.LoggingObject;
import adams.gui.flow.tree.StateContainer;

/**
 * Ancestor for additional menu items in the flow editor's tree popup.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractTreePopupMenuItem
  extends LoggingObject {

  /** for serialization. */
  private static final long serialVersionUID = -2957040948643353777L;

  /** the associated shortcut. */
  protected AbstractTreeShortcut m_Shortcut;
  
  /**
   * Initializes the menu item.
   */
  protected AbstractTreePopupMenuItem() {
    super();
    initialize();
  }
  
  /**
   * Initializes the members.
   */
  protected void initialize() {
    m_Shortcut = newShortcut();
  }
  
  /**
   * Creates the associated shortcut.
   * 
   * @return		the shortcut, null if not used
   */
  protected abstract AbstractTreeShortcut newShortcut();
  
  /**
   * Checks whether a shortcut is available.
   * 
   * @return		true if shortcut is available
   */
  public boolean hasShortcut() {
    return (m_Shortcut != null);
  }
  
  /**
   * Returns the associated shortcut.
   * 
   * @return		the shortcut, null if not available
   */
  public AbstractTreeShortcut getShortcut() {
    return m_Shortcut;
  }
  
  /**
   * Creates the menuitem to add to the menus.
   * 
   * @param state	the current state of the tree
   * @return		the menu item, null if not possible to use
   */
  protected abstract JMenuItem getMenuItem(final StateContainer state);
  
  /**
   * Adds itself, if possible, to the menu.
   * 
   * @param state	the current state of the tree
   * @param menu	the menu to add the menu item to
   * @return		the action to add
   */
  public void add(StateContainer state, JMenu menu) {
    JMenuItem	menuitem;
    
    menuitem = getMenuItem(state);
    if (menuitem != null)
      menu.add(menuitem);
  }
  
  /**
   * Adds itself, if possible, to the menu.
   * 
   * @param state	the current state of the tree
   * @param menu	the popup menu to add the menu item to
   * @return		the action to add
   */
  public void add(StateContainer state, JPopupMenu menu) {
    JMenuItem	menuitem;

    menuitem = getMenuItem(state);
    if (menuitem != null)
      menu.add(menuitem);
  }

  /**
   * Returns a list with classnames of menu items.
   *
   * @return		the menu item classnames
   */
  public static String[] getMenuItems() {
    return ClassLister.getSingleton().getClassnames(AbstractTreePopupMenuItem.class);
  }
}
