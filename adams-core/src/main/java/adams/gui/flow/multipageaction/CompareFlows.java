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
 * CompareFlows.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.multipageaction;

import adams.core.DiffUtils;
import adams.core.DiffUtils.SideBySideDiff;
import adams.core.option.NestedProducer;
import adams.gui.core.GUIHelper;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.flow.FlowMultiPagePane;
import adams.gui.flow.FlowPanel;
import adams.gui.visualization.debug.SideBySideDiffPanel;

import javax.swing.JMenuItem;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;

/**
 * Lets the user compare two flows.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class CompareFlows
  extends AbstractMultiPageMenuItem {

  private static final long serialVersionUID = 1297273340581059101L;

  /**
   * The name for the menu item.
   *
   * @return		the name
   */
  public String getName() {
    return "Compare flows";
  }

  /**
   * The name of the group this item belongs to.
   *
   * @return		the name
   */
  public String getGroup() {
    return "Admin";
  }

  /**
   * The name of the icon to use.
   *
   * @return		the name
   */
  public String getIconName() {
    return "diff.png";
  }

  /**
   * Compares the two actors.
   *
   * @param multi	the owner
   * @param actor1 	the first actor panel
   * @param actor2 	the second actor panel
   */
  protected void compare(FlowMultiPagePane multi, FlowPanel actor1, FlowPanel actor2) {
    int			i;
    NestedProducer	producer;
    String[][]		actors;
    SideBySideDiff diff;
    ApprovalDialog dialog;
    SideBySideDiffPanel	panel;

    producer = new NestedProducer();
    actors   = new String[2][];
    for (i = 0; i < 2; i++) {
      if (i == 0)
        producer.produce(actor1.getCurrentFlow());
      else
        producer.produce(actor2.getCurrentFlow());
      actors[i] = producer.toString().replace("\t", "  ").split("\n");
    }

    diff = DiffUtils.sideBySide(actors[0], actors[1]);
    if (diff == null) {
      GUIHelper.showErrorMessage(multi.getParent(), "Failed to compute differences!");
      return;
    }

    if (multi.getParentDialog() != null)
      dialog = new ApprovalDialog(multi.getParentDialog(), ModalityType.MODELESS);
    else
      dialog = new ApprovalDialog(multi.getParentFrame(), false);
    dialog.setDefaultCloseOperation(ApprovalDialog.DISPOSE_ON_CLOSE);
    dialog.setTitle("Comparison (" + (DiffUtils.isDifferent(actors[0], actors[1]) ? "different" : "no difference") + ")");
    dialog.setCancelVisible(false);
    dialog.setApproveCaption("Close");
    dialog.setApproveMnemonic('l');
    panel = new SideBySideDiffPanel();
    panel.setLabelText(true,  actor1.getTitle());
    panel.setLabelText(false, actor2.getTitle());
    panel.display(diff);
    dialog.getContentPane().add(panel, BorderLayout.CENTER);
    dialog.setSize(GUIHelper.getDefaultLargeDialogDimension());
    dialog.setLocationRelativeTo(multi.getParent());
    dialog.setVisible(true);
  }

  /**
   * Creates the menu item.
   */
  public JMenuItem getMenuItem(final FlowMultiPagePane multi) {
    JMenuItem 	result;

    result = new JMenuItem(getName());
    result.setIcon(getIcon());
    result.setEnabled(multi.getSelectedIndices().length == 2);
    if (result.isEnabled()) {
      result.addActionListener((ActionEvent ae) -> {
        int[] indices = multi.getSelectedIndices();
        compare(multi, multi.getPanelAt(indices[0]), multi.getPanelAt(indices[1]));
      });
    }

    return result;
  }
}
