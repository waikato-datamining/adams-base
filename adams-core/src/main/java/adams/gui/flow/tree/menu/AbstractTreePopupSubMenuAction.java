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

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.JMenu;
import javax.swing.KeyStroke;

import adams.core.Properties;
import adams.gui.action.AbstractPropertiesAction;
import adams.gui.action.AbstractPropertiesSubMenuAction;
import adams.gui.core.GUIHelper;
import adams.gui.flow.FlowEditorPanel;
import adams.gui.flow.tree.StateContainer;

/**
 * Ancestor for menu items in the popup menu of the flow tree.
 * 
 * @author fracpete
 * @version $Revision$
 */
public abstract class AbstractTreePopupSubMenuAction
  extends AbstractPropertiesSubMenuAction<StateContainer>
  implements TreePopupAction {

  /** for serialization. */
  private static final long serialVersionUID = -5921557331961517641L;
  
  /**
   * Returns the underlying properties.
   * 
   * @return		the properties
   */
  @Override
  protected Properties getProperties() {
    return FlowEditorPanel.getPropertiesTreePopup();
  }
  
  /**
   * Checks whether the keystroke matches.
   * 
   * @param ks		the keystroke to match
   * @return		true if a match
   */
  public boolean keyStrokeApplies(KeyStroke ks) {
    return hasAccelerator() && ks.equals(getAccelerator());
  }
  
  /**
   * Tries to determine the frame this panel is part of.
   *
   * @return		the parent frame if one exists or null if not
   */
  protected Frame getParentFrame() {
    if (m_State != null)
      return m_State.tree.getParentFrame();
    else
      return null;
  }

  /**
   * Tries to determine the dialog this panel is part of.
   *
   * @return		the parent dialog if one exists or null if not
   */
  protected Dialog getParentDialog() {
    if (m_State != null)
      return m_State.tree.getParentDialog();
    else
      return null;
  }
  
  /**
   * Adds an undo point with the given comment.
   *
   * @param comment	the comment for the undo point
   */
  public void addUndoPoint(String comment) {
    if (m_State != null)
      m_State.tree.addUndoPoint(comment);
  }

  /**
   * Returns any sub menu actions. By default, this method returns null.
   * Override this method when creating a submenu, use "null" in an array
   * element to create a separator.
   * 
   * @return		the submenu actions
   */
  protected abstract AbstractPropertiesAction[] getSubMenuActions();
  
  /**
   * Creates a new menu.
   */
  @Override
  public JMenu createMenu() {
    JMenu			result;
    AbstractPropertiesAction[]	subitems;

    subitems = getSubMenuActions();
    
    result = new JMenu(getName());
    if (getIcon() != null)
      result.setIcon(getIcon());
    else
      result.setIcon(GUIHelper.getEmptyIcon());
    for (AbstractPropertiesAction action: subitems) {
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
  protected void doActionPerformed(ActionEvent e) {
  }
}
