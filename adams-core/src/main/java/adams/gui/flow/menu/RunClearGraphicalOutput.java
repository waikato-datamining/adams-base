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
 * RunClearGraphicalOutput.java
 * Copyright (C) 2014-2019 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.menu;

import adams.gui.flow.FlowMultiPagePane.FlowPanelFilter;
import adams.gui.flow.FlowPanel;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Removes all graphical output.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class RunClearGraphicalOutput
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
    return "Clear graphical output";
  }

  /**
   * Returns the filter to apply to the selected flow panels.
   *
   * @return		the filters
   */
  protected Map<FlowPanelFilter,Boolean> getPanelFilter() {
    Map<FlowPanelFilter,Boolean>	result;

    result = new HashMap<>();
    result.put(FlowPanelFilter.RUNNING, false);
    result.put(FlowPanelFilter.STOPPING, false);
    result.put(FlowPanelFilter.SWINGWORKER, false);
    result.put(FlowPanelFilter.HAS_LAST_FLOW, true);

    return result;
  }

  /**
   * Invoked when an action occurs.
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    FlowPanel	current;

    for (int index: m_State.getFlowPanels().getSelectedIndices(getPanelFilter())) {
      current = m_State.getFlowPanels().getPanelAt(index);
      if (current.getDebugTargetPanel() != null)
        current.getDebugTargetPanel().close();
      current.cleanUp();
      current.clearNotification();
    }

    m_State.update();
  }

  /**
   * Performs the actual update of the state of the action.
   */
  @Override
  protected void doUpdate() {
    setEnabled(m_State.getFlowPanels().getSelectedIndices(getPanelFilter()).length > 0);
  }
}
