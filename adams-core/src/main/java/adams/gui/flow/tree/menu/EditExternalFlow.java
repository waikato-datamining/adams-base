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
 * EditExternalFlow.java
 * Copyright (C) 2014 University of Waikato, Hamilton, NZ
 */
package adams.gui.flow.tree.menu;

import java.awt.event.ActionEvent;

import javax.swing.tree.TreePath;

import adams.core.io.FlowFile;
import adams.flow.core.ExternalActorHandler;
import adams.gui.event.ActorChangeEvent;
import adams.gui.event.ActorChangeEvent.Type;
import adams.gui.flow.FlowEditorDialog;
import adams.gui.flow.tree.Node;
import adams.gui.flow.tree.TreeHelper;

/**
 * For editing an external flow.
 * 
 * @author fracpete
 * @version $Revision$
 */
public class EditExternalFlow
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
    return "Edit...";
  }

  /**
   * Updates the action using the current state information.
   */
  @Override
  protected void doUpdate() {
    boolean enabled = m_State.editable && m_State.isSingleSel && (m_State.selNode.getActor() instanceof ExternalActorHandler);
    if (enabled) {
      FlowFile file = ((ExternalActorHandler) m_State.selNode.getActor()).getActorFile();
      enabled = file.exists() && !file.isDirectory();
    }
    setEnabled(enabled);
  }

  /**
   * Brings up a flow window for editing the selected external actor's flow.
   *
   * @param path	the path to the node
   */
  protected void editFlow(TreePath path) {
    Node			node;
    FlowEditorDialog 		dialog;
    ExternalActorHandler	actor;

    node = TreeHelper.pathToNode(path);
    if (node == null)
      return;
    actor = (ExternalActorHandler) node.getActor();
    if (actor == null)
      return;

    if (getParentDialog() != null)
      dialog = new FlowEditorDialog(getParentDialog());
    else
      dialog = new FlowEditorDialog(getParentFrame());
    dialog.getFlowEditorPanel().loadUnsafe(actor.getActorFile());
    dialog.setVisible(true);
    if (dialog.getFlowEditorPanel().getCurrentFile() != null) {
      if ((actor.getActorFile() == null) || (!actor.getActorFile().equals(dialog.getFlowEditorPanel().getCurrentFile()))) {
	actor.setActorFile(new FlowFile(dialog.getFlowEditorPanel().getCurrentFile()));
	m_State.tree.setModified(true);
      }
    }

    // external flow might have changed, discard any inlined actors
    node.collapse();

    // notify listeners
    m_State.tree.notifyActorChangeListeners(new ActorChangeEvent(m_State.tree, node, Type.MODIFY));
  }

  /**
   * The action to execute.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    editFlow(m_State.selPath);
  }
}
