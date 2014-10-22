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
 * EditListeners.java
 * Copyright (C) 2014 University of Waikato, Hamilton, NZ
 */
package adams.gui.flow.tree.menu;

import java.awt.Dialog;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;

import adams.core.Pausable;
import adams.core.ShallowCopySupporter;
import adams.core.Utils;
import adams.flow.execution.FlowExecutionListener;
import adams.gui.goe.GenericObjectEditorDialog;

/**
 * For editing currently attached listeners.
 * 
 * @author fracpete
 * @version $Revision$
 */
public class EditListeners
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
    return "Edit listeners...";
  }

  /**
   * Updates the action using the current state information.
   */
  @Override
  protected void doUpdate() {
    setEnabled((m_State.runningFlow != null) && (m_State.runningFlow instanceof Pausable) && (((Pausable) m_State.runningFlow).isPaused()));
  }

  /**
   * The action to execute.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    GenericObjectEditorDialog	dialog;

    if ((m_State.tree != null) && (m_State.tree.getParentDialog() != null))
      dialog = new GenericObjectEditorDialog(m_State.tree.getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else if ((m_State.tree != null) && (m_State.tree.getParentFrame() != null))
      dialog = new GenericObjectEditorDialog(m_State.tree.getParentFrame(), true);
    else
      dialog = new GenericObjectEditorDialog((Dialog) null, ModalityType.DOCUMENT_MODAL);
    dialog.setTitle("Edit listeners");
    dialog.getGOEEditor().setClassType(FlowExecutionListener.class);
    dialog.getGOEEditor().setCanChangeClassInDialog(true);
    if (m_State.runningFlow.getFlowExecutionListener() instanceof ShallowCopySupporter)
      dialog.setCurrent(((ShallowCopySupporter) m_State.runningFlow.getFlowExecutionListener()).shallowCopy());
    else
      dialog.setCurrent(Utils.deepCopy(m_State.runningFlow.getFlowExecutionListener()));
    dialog.setLocationRelativeTo(m_State.tree);
    dialog.setVisible(true);
    if (dialog.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return;
    m_State.runningFlow.startListeningAtRuntime((FlowExecutionListener) dialog.getCurrent());
  }
}
