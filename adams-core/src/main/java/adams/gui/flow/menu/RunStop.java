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
 * RunStop.java
 * Copyright (C) 2014-2019 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.menu;

import adams.gui.flow.FlowMultiPagePane.FlowPanelFilter;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Stops the flow.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class RunStop
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
    return "Stop";
  }

  /**
   * Returns the filter to apply to the selected flow panels.
   *
   * @return		the filters
   */
  protected Map<FlowPanelFilter,Boolean> getPanelFilter() {
    Map<FlowPanelFilter,Boolean>	result;

    result = new HashMap<>();
    result.put(FlowPanelFilter.RUNNING, true);

    return result;
  }

  /**
   * Invoked when an action occurs.
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    for (int index: m_State.getFlowPanels().getSelectedIndices(getPanelFilter()))
      m_State.getFlowPanels().getPanelAt(index).stop();
  }

  /**
   * Performs the actual update of the state of the action.
   */
  @Override
  protected void doUpdate() {
    setEnabled(m_State.getFlowPanels().getSelectedIndices(getPanelFilter()).length > 0);
  }
}
