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
 * ActiveValidateSetup.java
 * Copyright (C) 2019-2023 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.menu;

import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import adams.gui.flow.FlowPanelNotificationArea.NotificationType;

import java.awt.event.ActionEvent;

/**
 * Validates the active setup.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ActiveValidateSetup
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
    return "Validate";
  }

  /**
   * Invoked when an action occurs.
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    Runnable	runnable;

    runnable = () -> {
      String msg = null;
      StringBuilder errors = new StringBuilder();
      Actor actor = m_State.getActiveFlow(errors);
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
	msg = ActorUtils.checkFlow(actor, m_State.getCurrentFile());

      if (msg == null) {
	msg = "The flow passed validation!";
	m_State.getActivePanel().showStatus(msg);
	m_State.getActivePanel().showNotification(msg, NotificationType.INFO);
      }
      else {
	m_State.getActivePanel().showStatus("The flow didn't pass validation!");
	m_State.getActivePanel().showNotification("The flow failed validation:\n" + msg, NotificationType.ERROR);
      }
    };
    m_State.getActivePanel().startBackgroundTask(runnable, "Validating flow...", false);
  }

  /**
   * Performs the actual update of the state of the action.
   */
  @Override
  protected void doUpdate() {
    setEnabled(
	   m_State.hasActivePanel()
        && !m_State.getActivePanel().getTree().isDebug()
	&& m_State.getActivePanel().isInputEnabled());
  }
}
