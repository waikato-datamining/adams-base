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
 * Listeners.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.tree.menu;

import javax.swing.JMenuItem;

import adams.core.Pausable;
import adams.gui.core.BaseMenu;
import adams.gui.core.GUIHelper;
import adams.gui.flow.tree.StateContainer;

/**
 * Manages listeners (add/remove).
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Listeners
  extends AbstractTreePopupMenuItem {

  /** for serialization. */
  private static final long serialVersionUID = -1359983192445709718L;

  /**
   * Creates the menuitem to add to the menus.
   * 
   * @param state	the current state of the tree
   * @return		the menu item, null if not possible to use
   */
  @Override
  protected JMenuItem getMenuItem(final StateContainer state) {
    BaseMenu	result;
    
    result = new BaseMenu("Listeners");
    result.setIcon(GUIHelper.getIcon("listen.png"));
    result.setEnabled((state.runningFlow != null) && (state.runningFlow instanceof Pausable) && (((Pausable) state.runningFlow).isPaused()));
    new AttachListener().add(state, result);
    result.addSeparator();
    new EditListeners().add(state, result);
    new RemoveListeners().add(state, result);
    
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
