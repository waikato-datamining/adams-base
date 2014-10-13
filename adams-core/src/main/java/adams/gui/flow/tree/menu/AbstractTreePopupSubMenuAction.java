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
 * AbstractTreePopupSubMenuAction.java
 * Copyright (C) 2014 University of Waikato, Hamilton, NZ
 */
package adams.gui.flow.tree.menu;

import java.awt.event.ActionEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import adams.gui.core.GUIHelper;
import adams.gui.flow.tree.StateContainer;

/**
 * Ancestor for menu items in the popup menu of the flow tree.
 * 
 * @author fracpete
 * @version $Revision$
 */
public abstract class AbstractTreePopupSubMenuAction
  extends AbstractTreePopupAction {

  /** for serialization. */
  private static final long serialVersionUID = -5921557331961517641L;

  /**
   * Returns any sub menu actions. By default, this method returns null.
   * Override this method when creating a submenu, use "null" in an array
   * element to create a separator.
   * 
   * @return		the submenu actions
   */
  protected abstract AbstractTreePopupAction[] getSubMenuActions();
  
  /**
   * Creates a new menuitem using itself.
   */
  @Override
  public JMenuItem getMenuItem() {
    JMenu			result;
    AbstractTreePopupAction[]	subitems;

    subitems = getSubMenuActions();
    
    result = new JMenu(getName());
    if (getIcon() != null)
      result.setIcon(getIcon());
    else
      result.setIcon(GUIHelper.getEmptyIcon());
    for (AbstractTreePopupAction action: subitems) {
      if (action == null) {
	result.addSeparator();
      }
      else {
	action.update(m_State);
	result.add(action.getMenuItem());
      }
    }
    
    return result;
  }
  
  /**
   * Updates the action using the current state information.
   */
  @Override
  protected void doUpdate() {
  }

  /**
   * Updates the action using the provided state information.
   * 
   * @param state	the current state of the tree
   */
  @Override
  public void update(final StateContainer state) {
    m_State = state;
    doUpdate();
  }

  /**
   * The action to execute.
   *
   * @param e		the event
   */
  @Override
  public void actionPerformed(ActionEvent e) {
  }
}
