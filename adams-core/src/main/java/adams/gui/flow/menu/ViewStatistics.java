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
 * ViewStatistics.java
 * Copyright (C) 2014-2018 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.menu;

import adams.data.statistics.InformativeStatistic;
import adams.flow.core.ActorStatistic;
import adams.gui.core.GUIHelper;
import adams.gui.flow.tree.Node;
import adams.gui.visualization.statistics.InformativeStatisticFactory;

import javax.swing.tree.TreeNode;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Displays statistics about the flow.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ViewStatistics
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
    return "Statistics...";
  }
  
  /**
   * Invoked when an action occurs.
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    Runnable	runnable;

    runnable = () -> {
      Node node = null;
      if (m_State.getCurrentTree().getSelectedNode() != null)
	node = m_State.getCurrentTree().getSelectedNode();
      else if (m_State.getCurrentRoot() != null)
	node = m_State.getCurrentTree().getRootNode();
      ActorStatistic stats = new ActorStatistic();
      if (node != null) {
        Enumeration<TreeNode> all = node.breadthFirstEnumeration();
        while (all.hasMoreElements()) {
          Node child = (Node) all.nextElement();
          stats.update(child.getActor());
        }
      }
      List<InformativeStatistic> statsList = new ArrayList<>();
      statsList.add(stats);

      InformativeStatisticFactory.Dialog dialog;
      if (m_State.getParentDialog() != null)
	dialog = InformativeStatisticFactory.getDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
      else
	dialog = InformativeStatisticFactory.getDialog(getParentFrame(), true);
      dialog.setStatistics(statsList);
      dialog.setTitle("Actor statistics");
      dialog.setSize(GUIHelper.makeWider(GUIHelper.rotate(GUIHelper.getDefaultSmallDialogDimension())));
      dialog.setLocationRelativeTo(m_State);
      dialog.setVisible(true);
    };

    m_State.getCurrentPanel().startBackgroundTask(runnable, "Generating statistics...", true);
  }

  /**
   * Performs the actual update of the state of the action.
   */
  @Override
  protected void doUpdate() {
    setEnabled(
	   m_State.hasCurrentPanel()
	&& !m_State.isSwingWorkerRunning());
  }
}
