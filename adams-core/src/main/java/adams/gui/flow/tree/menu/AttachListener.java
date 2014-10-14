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
 * AttachListener.java
 * Copyright (C) 2014 University of Waikato, Hamilton, NZ
 */
package adams.gui.flow.tree.menu;

import java.awt.Dialog;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;

import adams.core.Pausable;
import adams.flow.core.ActorUtils;
import adams.flow.execution.AbstractBreakpoint;
import adams.flow.execution.Debug;
import adams.flow.execution.FlowExecutionListener;
import adams.flow.execution.PathBreakpoint;
import adams.gui.goe.GenericObjectEditorDialog;

/**
 * Allows the attaching of flow execution listeners.
 * 
 * @author fracpete
 * @version $Revision$
 */
public class AttachListener
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
    return "Attach listener...";
  }

  /**
   * Updates the action using the current state information.
   */
  @Override
  protected void doUpdate() {
    setEnabled((m_State.numSel == 1) && (m_State.runningFlow != null) && (m_State.runningFlow instanceof Pausable) && (((Pausable) m_State.runningFlow).isPaused()));
  }

  /**
   * The action to execute.
   *
   * @param e		the event
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    GenericObjectEditorDialog	dialog;
    Debug				debug;
    PathBreakpoint			pbreak;

    pbreak = new PathBreakpoint();
    pbreak.setPath(m_State.selNode.getFullName());
    if (ActorUtils.isSource(m_State.selNode.getActor()))
      pbreak.setOnPostOutput(true);
    if (ActorUtils.isTransformer(m_State.selNode.getActor()))
      pbreak.setOnPreInput(true);
    if (ActorUtils.isSink(m_State.selNode.getActor()))
      pbreak.setOnPreInput(true);
    debug = new Debug();
    debug.setBreakpoints(new AbstractBreakpoint[]{
	pbreak
    });

    if ((m_State.tree != null) && (m_State.tree.getParentDialog() != null))
      dialog = new GenericObjectEditorDialog(m_State.tree.getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else if ((m_State.tree != null) && (m_State.tree.getParentFrame() != null))
      dialog = new GenericObjectEditorDialog(m_State.tree.getParentFrame(), true);
    else
      dialog = new GenericObjectEditorDialog((Dialog) null, ModalityType.DOCUMENT_MODAL);
    dialog.setTitle("Attach listener [" + m_State.selNode.getFullName() + "]");
    dialog.getGOEEditor().setClassType(FlowExecutionListener.class);
    dialog.getGOEEditor().setCanChangeClassInDialog(true);
    dialog.setCurrent(debug);
    dialog.setLocationRelativeTo(m_State.tree);
    dialog.setVisible(true);
    if (dialog.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return;
    m_State.runningFlow.startListeningAtRuntime((FlowExecutionListener) dialog.getCurrent());
  }
}
