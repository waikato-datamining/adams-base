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
 * AbstractTreePopupMenuItemAction.java
 * Copyright (C) 2014 University of Waikato, Hamilton, NZ
 */
package adams.gui.flow.tree.menu;

import java.awt.Dialog;
import java.awt.Frame;

import javax.swing.KeyStroke;

import adams.core.Properties;
import adams.gui.action.AbstractPropertiesMenuItemAction;
import adams.gui.flow.FlowEditorPanel;
import adams.gui.flow.tree.StateContainer;
import adams.gui.goe.GenericObjectEditorDialog;

/**
 * Ancestor for menu items in the popup menu of the flow tree.
 * 
 * @author fracpete
 * @version $Revision$
 */
public abstract class AbstractTreePopupMenuItemAction
  extends AbstractPropertiesMenuItemAction<StateContainer, GenericObjectEditorDialog>
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
}
