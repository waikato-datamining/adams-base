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
 * RunValidateSetup.java
 * Copyright (C) 2014-2020 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.menu;

import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import adams.gui.flow.FlowMultiPagePane.FlowPanelFilter;
import adams.gui.flow.FlowPanel;
import adams.gui.flow.FlowPanelNotificationArea.NotificationType;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Validates the current setup.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class RunValidateSetup
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
    return "Validate setup";
  }

  /**
   * Returns the filter to apply to the selected flow panels.
   *
   * @param swingworker	if true then no swingworker is allowed to be active
   * @return		the filters
   */
  protected Map<FlowPanelFilter,Boolean> getPanelFilter(boolean swingworker) {
    Map<FlowPanelFilter,Boolean>	result;

    result = new HashMap<>();
    result.put(FlowPanelFilter.RUNNING, false);
    result.put(FlowPanelFilter.STOPPING, false);
    if (swingworker)
      result.put(FlowPanelFilter.SWINGWORKER, false);
    result.put(FlowPanelFilter.DEBUG, false);

    return result;
  }

  /**
   * Invoked when an action occurs.
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    Runnable	runnable;

    runnable = () -> {
      for (int index: m_State.getFlowPanels().getSelectedIndices(getPanelFilter(false))) {
	FlowPanel currentPanel = m_State.getFlowPanels().getPanelAt(index);
	String msg = null;
	StringBuilder errors = new StringBuilder();
	Actor actor = currentPanel.getCurrentFlow(errors);
	if (errors.length() > 0)
	  msg = errors.toString();

	if (msg == null) {
	  try {
	    msg = actor.setUp();
	    actor.wrapUp();
	    actor.cleanUp();
	  }
	  catch (Exception ex) {
	    msg = "Actor generated exception: ";
	    System.err.println(msg);
	    ex.printStackTrace();
	    msg += e;
	  }
	}

	// perform some checks
	if (msg == null)
	  msg = ActorUtils.checkFlow(actor, currentPanel.getCurrentFile());

	if (msg == null) {
	  msg = "The flow passed validation!";
	  currentPanel.showStatus(msg);
	  currentPanel.showNotification(msg, NotificationType.INFO);
	}
	else {
	  currentPanel.showStatus("The flow didn't pass validation!");
	  currentPanel.showNotification("The flow setup failed validation:\n" + msg, NotificationType.ERROR);
	}
      }
    };
    m_State.getCurrentPanel().startBackgroundTask(runnable, "Validating flow...", false);
  }

  /**
   * Performs the actual update of the state of the action.
   */
  @Override
  protected void doUpdate() {
    setEnabled(m_State.getFlowPanels().getSelectedIndices(getPanelFilter(true)).length > 0);
  }
}
