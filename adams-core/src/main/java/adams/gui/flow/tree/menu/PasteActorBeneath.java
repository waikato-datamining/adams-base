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
 * PasteActorBeneath.java
 * Copyright (C) 2014-2018 University of Waikato, Hamilton, NZ
 */
package adams.gui.flow.tree.menu;

import adams.flow.core.Actor;
import adams.gui.core.GUIHelper;
import adams.gui.flow.tree.TreeOperations;

import java.awt.event.ActionEvent;

/**
 * For pasting the actor(s) from the clipboard beneath the current actor.
 * 
 * @author fracpete
 */
public class PasteActorBeneath
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
    return "Paste beneath";
  }

  /**
   * Updates the action using the current state information.
   */
  @Override
  protected void doUpdate() {
    setEnabled(m_State.editable && m_State.canPaste && m_State.isMutable && (m_State.numSel == 1));
  }

  /**
   * The action to execute.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    Actor actor;

    if (TreeOperations.hasNodesOnClipboard()) {
      m_State.tree.getOperations().pasteNodes(m_State.selPath, TreeOperations.getNodesFromClipboard(), TreeOperations.InsertPosition.BENEATH);
    }
    else {
      actor = TreeOperations.getActorFromClipboard();
      if (actor != null)
        m_State.tree.getOperations().addActor(m_State.selPath, actor, TreeOperations.InsertPosition.BENEATH);
      else
	GUIHelper.showErrorMessage(m_State.tree.getParent(), "Failed to parse clipboard content as actor(s)!");
    }
  }
}
