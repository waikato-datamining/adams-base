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
 * RunRun.java
 * Copyright (C) 2014-2025 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.menu;

import adams.gui.flow.FlowEditorPanel;
import adams.gui.flow.FlowMultiPagePane.FlowPanelFilter;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Executes/restarts the flow.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class RunRun
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
    return "Run";
  }

  /**
   * Returns the filter to apply to the selected flow panels.
   *
   * @return		the filters
   */
  protected Map<FlowPanelFilter,Boolean> getPanelFilter() {
    Map<FlowPanelFilter,Boolean>	result;

    result = new HashMap<>();
    result.put(FlowPanelFilter.STOPPING, false);
    result.put(FlowPanelFilter.SWINGWORKER, false);
    result.put(FlowPanelFilter.DEBUG, false);
    result.put(FlowPanelFilter.FLOW, true);

    return result;
  }

  /**
   * Invoked when an action occurs.
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    for (int index: m_State.getFlowPanels().getSelectedIndices(getPanelFilter())) {
      if (m_State.getFlowPanels().getPanelAt(index).isRunning())
	m_State.getFlowPanels().getPanelAt(index).restart(true, false);
      else
	m_State.getFlowPanels().getPanelAt(index).run(true, false);
    }
  }

  /**
   * Performs the actual update of the state of the action.
   */
  @Override
  protected void doUpdate() {
    if (m_State.hasCurrentPanel() && m_State.getCurrentPanel().isRunning()) {
      setIcon(FlowEditorPanel.getPropertiesMenu().getProperty(getClass().getName() + "Restart-Icon"));
      setName("Restart");
    }
    else {
      setIcon(FlowEditorPanel.getPropertiesMenu().getProperty(getClass().getName() + "-Icon"));
      setName(getTitle());
    }
    setEnabled(m_State.getFlowPanels().getSelectedIndices(getPanelFilter()).length > 0);
  }
}
