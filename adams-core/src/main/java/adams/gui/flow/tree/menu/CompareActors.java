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
 * CompareActors.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */
package adams.gui.flow.tree.menu;

import adams.core.DiffUtils;
import adams.core.DiffUtils.SideBySideDiff;
import adams.core.option.NestedProducer;
import adams.gui.core.GUIHelper;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.flow.tree.Node;
import adams.gui.visualization.debug.SideBySideDiffPanel;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;

/**
 * Performs a diff on two actors.
 * 
 * @author fracpete
 * @version $Revision: 9906 $
 */
public class CompareActors
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
    return "Compare actors...";
  }

  /**
   * Updates the action using the current state information.
   */
  @Override
  protected void doUpdate() {
    setEnabled(	     
	   m_State.editable 
	&& (m_State.numSel == 2)
	&& (m_State.tree.getOwner() != null));
  }

  /**
   * The action to execute.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    Node[]		nodes;
    int			i;
    NestedProducer	producer;
    String[][]		actors;
    SideBySideDiff 	diff;
    ApprovalDialog 	dialog;
    SideBySideDiffPanel	panel;

    producer = new NestedProducer();
    actors   = new String[2][];
    nodes    = new Node[2];
    for (i = 0; i < 2; i++) {
      nodes[i] = (Node) m_State.selPaths[i].getLastPathComponent();
      producer.produce((nodes[i]).getActor());
      actors[i] = producer.toString().replace("\t", "  ").split("\n");
    }

    diff = DiffUtils.sideBySide(actors[0], actors[1]);
    if (diff == null) {
      GUIHelper.showErrorMessage(m_State.tree, "Failed to compute differences!");
      return;
    }

    if (getParentDialog() != null)
      dialog = new ApprovalDialog(getParentDialog(), ModalityType.MODELESS);
    else
      dialog = new ApprovalDialog(getParentFrame(), false);
    dialog.setDefaultCloseOperation(ApprovalDialog.DISPOSE_ON_CLOSE);
    dialog.setTitle("Comparison (" + (DiffUtils.isDifferent(actors[0], actors[1]) ? "different" : "no difference") + ")");
    dialog.setCancelVisible(false);
    dialog.setApproveCaption("Close");
    dialog.setApproveMnemonic('l');
    panel = new SideBySideDiffPanel();
    panel.setLabelText(true,  nodes[0].getFullName());
    panel.setLabelText(false, nodes[0].getFullName());
    panel.display(diff);
    dialog.getContentPane().add(panel, BorderLayout.CENTER);
    dialog.setSize(GUIHelper.getDefaultLargeDialogDimension());
    dialog.setLocationRelativeTo(m_State.tree);
    dialog.setVisible(true);

  }
}
