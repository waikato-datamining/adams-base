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
 * EditDiff.java
 * Copyright (C) 2014-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.menu;

import adams.core.DiffUtils;
import adams.core.DiffUtils.SideBySideDiff;
import adams.gui.core.GUIHelper;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.flow.tree.Tree.TreeState;
import adams.gui.visualization.debug.SideBySideDiffPanel;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Shows differences between versions of flows.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class EditDiff
  extends AbstractFlowEditorMenuItemAction {

  /** for serialization. */
  private static final long serialVersionUID = 5235570137451285010L;

  /**
   * Returns the caption of this action.
   * 
   * @return		the caption, null if not applicable
   */
  @Override
  protected String getTitle() {
    return "Show changes";
  }

  /**
   * Generates a diff between the current flow and the closest undo step.
   *
   * @return		the diff, null if failed to generate
   */
  protected SideBySideDiff getDiff() {
    SideBySideDiff	result;
    List<String> 	current;
    List<String>	prev;
    TreeState		state;

    if (!canDiff())
      return null;

    // current flow
    current = m_State.getCurrentTree().getCommandLines();

    // undo step
    state = (TreeState) m_State.getCurrentPanel().getUndo().peekUndo().getData();
    if (state.actors.size() == 0)
      return null;
    prev = state.actors;

    // generate diff
    result = DiffUtils.sideBySide(prev, current);

    return result;
  }

  /**
   * Invoked when an action occurs.
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    SideBySideDiff	diff;
    ApprovalDialog	dialog;
    SideBySideDiffPanel	panel;

    if (!canDiff())
      return;

    diff = getDiff();
    if (diff == null) {
      GUIHelper.showErrorMessage(m_State, "Failed to compute differences!");
      return;
    }

    if (getParentDialog() != null)
      dialog = new ApprovalDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new ApprovalDialog(getParentFrame(), true);
    dialog.setDefaultCloseOperation(ApprovalDialog.DISPOSE_ON_CLOSE);
    dialog.setTitle("Differences");
    dialog.setCancelVisible(false);
    dialog.setApproveCaption("Close");
    dialog.setApproveMnemonic('l');
    panel = new SideBySideDiffPanel();
    panel.setLabelText(true, "Previous");
    panel.setLabelText(false, "Current");
    panel.display(diff);
    dialog.getContentPane().add(panel, BorderLayout.CENTER);
    dialog.setSize(GUIHelper.getDefaultDialogDimension());
    dialog.setLocationRelativeTo(m_State);
    dialog.setVisible(true);
  }

  /**
   * Whether a diff between current flow and undo-flow can be generated.
   *
   * @return		true if diff can be generated
   */
  protected boolean canDiff() {
    return 
	   m_State.getCurrentPanel().getUndo().isEnabled() 
	&& !m_State.getCurrentPanel().getUndo().isWorking() 
	&& m_State.getCurrentPanel().getUndo().canUndo();
  }

  /**
   * Performs the actual update of the state of the action.
   */
  @Override
  protected void doUpdate() {
    setEnabled(
	   m_State.hasCurrentPanel() 
	&& isInputEnabled() 
	&& canDiff());
  }
}
