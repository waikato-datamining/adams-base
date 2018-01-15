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
 * RunToHere.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */
package adams.gui.flow.tree.menu;

import adams.flow.control.Flow;
import adams.flow.core.ActorPath;
import adams.flow.execution.Debug;
import adams.flow.execution.FlowExecutionListener;
import adams.flow.execution.MultiListener;
import adams.flow.execution.debug.AbstractScopeRestriction;
import adams.flow.execution.debug.DebugScopeRestrictionHelper;
import adams.flow.execution.debug.PathBreakpoint;

import java.awt.event.ActionEvent;

/**
 * Attaches a one-off breakpoint to the selected actor using its full name for
 * a {@link PathBreakpoint}.
 * 
 * @author fracpete
 */
public class RunToHere
  extends AbstractAddBreakpointAction {

  /** for serialization. */
  private static final long serialVersionUID = 3991575839421394939L;
  
  /**
   * Returns the caption of this action.
   * 
   * @return		the caption, null if not applicable
   */
  @Override
  protected String getTitle() {
    return "Run to here";
  }

  /**
   * Updates the action using the current state information.
   */
  @Override
  protected void doUpdate() {
    setEnabled(m_State.isSingleSel && (m_State.runningFlow instanceof Flow));
  }

  /**
   * The action to execute.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    PathBreakpoint		breakpoint;
    AbstractScopeRestriction	restriction;
    FlowExecutionListener 	listener;
    Debug			debug;
    MultiListener		multi;

    breakpoint = new PathBreakpoint();
    breakpoint.setOnPreExecute(true);
    breakpoint.setOneOff(true);
    breakpoint.setPath(new ActorPath(m_State.tree.getSelectedFullName()));

    restriction = DebugScopeRestrictionHelper.getDebugScopeRestriction(m_State.selNode);

    // add to set up internal structures
    ((Flow) m_State.runningFlow).addBreakpoint(breakpoint, restriction, true);
    listener = m_State.runningFlow.getFlowExecutionListener();
    debug    = null;
    if (listener instanceof Debug) {
      debug = (Debug) listener;
    }
    else if (listener instanceof MultiListener) {
      multi = (MultiListener) listener;
      for (FlowExecutionListener l: multi.getSubListeners()) {
	if (l instanceof Debug) {
	  debug = (Debug) l;
	  break;
	}
      }
    }

    if (debug == null) {
      ((Flow) m_State.runningFlow).getLogger().severe("Failed to locate debug flow execution listener to resume flow!");
    }
    else {
      // remove all one-off breakpoints but this one
      debug.removeOneOffBreakpoints(breakpoint);
      // continue execution
      debug.unblockExecution();
    }
  }
}
