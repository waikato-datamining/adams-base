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
 * PasteActorAfter.java
 * Copyright (C) 2014-2017 University of Waikato, Hamilton, NZ
 */
package adams.gui.flow.tree.menu;

import adams.gui.flow.tree.TreeOperations;

import java.awt.event.ActionEvent;

/**
 * For pasting the actor(s) from the clipboard after the current position.
 * 
 * @author fracpete
 * @version $Revision$
 */
public class PasteActorAfter
  extends AbstractTreePopupMenuItemAction {

  /** for serialization. */
  private static final long serialVersionUID = 3991575839421394939L;
  
  /**
   * Returns the caption of this action.
   * 
   * @return		the caption, null if not applicable
   */
  @Override
  protected String getTitle() {
    return "Paste after";
  }

  /**
   * Updates the action using the current state information.
   */
  @Override
  protected void doUpdate() {
    setEnabled(m_State.editable && m_State.canPaste && m_State.isParentMutable && (m_State.numSel == 1));
  }

  /**
   * The action to execute.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    m_State.tree.getOperations().addActor(m_State.selPath, TreeOperations.getActorFromClipboard(), TreeOperations.InsertPosition.AFTER);
  }
}
