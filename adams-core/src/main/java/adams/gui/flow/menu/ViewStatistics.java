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
 * ViewStatistics.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.menu;

import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.util.Vector;

import adams.data.statistics.InformativeStatistic;
import adams.flow.core.ActorStatistic;
import adams.gui.visualization.statistics.InformativeStatisticFactory;

/**
 * Displays statistics about the flow.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
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
    ActorStatistic			stats;
    InformativeStatisticFactory.Dialog	dialog;
    Vector<InformativeStatistic>	statsList;

    stats = null;
    if (m_State.getCurrentPanel().getTree().getSelectedNode() != null)
      stats = new ActorStatistic(m_State.getCurrentPanel().getTree().getSelectedNode().getFullActor());
    else if (m_State.getCurrentRoot() != null)
      stats = new ActorStatistic(m_State.getCurrentFlow());
    statsList = new Vector<InformativeStatistic>();
    statsList.add(stats);

    if (m_State.getParentDialog() != null)
      dialog = InformativeStatisticFactory.getDialog(m_State.getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      dialog = InformativeStatisticFactory.getDialog(m_State.getParentFrame(), true);
    dialog.setStatistics(statsList);
    dialog.setTitle("Actor statistics");
    dialog.pack();
    dialog.setLocationRelativeTo(m_State);
    dialog.setVisible(true);
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
