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
 * Bookmark.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.tree.menu;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import adams.gui.core.BaseMenu;
import adams.gui.core.GUIHelper;
import adams.gui.flow.tree.StateContainer;

/**
 * Submenu for adding/removing bookmarks.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Bookmark
  extends AbstractTreePopupMenuItem {

  /** for serialization. */
  private static final long serialVersionUID = -6613835287817108048L;

  /**
   * Creates the menuitem to add to the menus.
   * 
   * @param state	the current state of the tree
   * @return		the menu item, null if not possible to use
   */
  @Override
  protected JMenuItem getMenuItem(final StateContainer state) {
    JMenu	result;
    
    result = new BaseMenu("Bookmark");
    result.setIcon(GUIHelper.getIcon("bookmark.png"));
    result.setEnabled(state.numSel > 0);
    new AddBookmark().add(state, result);
    new RemoveBookmark().add(state, result);
    
    return result;
  }

  /**
   * Creates the associated shortcut.
   * 
   * @return		the shortcut, null if not used
   */
  @Override
  protected AbstractTreeShortcut newShortcut() {
    return null;
  }
}
