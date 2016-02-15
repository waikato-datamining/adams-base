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
 * RunValidateSetup.java
 * Copyright (C) 2014-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.menu;

import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;

import java.awt.event.ActionEvent;

/**
 * Validates the current setup.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
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
   * Invoked when an action occurs.
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    Actor 		actor;
    StringBuilder	errors;
    String		msg;

    msg    = null;
    errors = new StringBuilder();
    actor  = m_State.getCurrentFlow(errors);
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
      msg = ActorUtils.checkFlow(actor);

    if (msg == null) {
      msg = "The flow passed validation!";
      m_State.getCurrentPanel().showStatus(msg);
      m_State.getCurrentPanel().showNotification(msg, false);
    }
    else {
      m_State.getCurrentPanel().showStatus(msg);
      m_State.getCurrentPanel().showNotification("The flow setup failed validation:\n" + msg, true);
    }
  }

  /**
   * Performs the actual update of the state of the action.
   */
  @Override
  protected void doUpdate() {
    setEnabled(
	   m_State.hasCurrentPanel() 
	&& isInputEnabled());
  }
}
