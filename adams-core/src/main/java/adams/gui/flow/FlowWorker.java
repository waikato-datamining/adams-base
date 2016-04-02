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
 * FlowWorker.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow;

import adams.core.Pausable;
import adams.core.StatusMessageHandler;
import adams.core.Stoppable;
import adams.core.Utils;
import adams.core.VariablesHandler;
import adams.db.LogEntryHandler;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import adams.flow.execution.AnyActorBreakpoint;

import java.io.File;

/**
 * Specialized worker class for executing a flow.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 12532 $
 */
public class FlowWorker
  implements Runnable, Pausable, Stoppable, StatusMessageHandler {

  /** the panel this flow belongs to. */
  protected FlowPanel m_Owner;

  /** the flow to execute. */
  protected Actor m_Flow;

  /** the current flow file. */
  protected File m_File;

  /** generated output. */
  protected String m_Output;

  /** whether to show a notification. */
  protected boolean m_ShowNotification;

  /** whether to run in debug mode. */
  protected boolean m_Debug;

  /** whether the flow is still being executed. */
  protected boolean m_Running;

  /** whether the flow is being stopped. */
  protected boolean m_Stopping;

  /**
   * Initializes the worker.
   */
  public FlowWorker(FlowPanel owner, Actor flow, File file, boolean showNotification, boolean debug) {
    m_Owner            = owner;
    m_Flow             = flow;
    m_File             = file;
    m_ShowNotification = showNotification;
    m_Debug            = debug;
    m_Running          = false;
    m_Stopping         = false;
  }

  /**
   * Executes the flow.
   *
   * @return always null
   * @throws Exception if unable to compute a result
   */
  protected Object doInBackground() throws Exception {
    AnyActorBreakpoint breakpoint;

    m_Owner.update();
    m_Owner.cleanUp();
    m_Owner.clearNotification();
    m_Owner.setTabIcon("run.gif");

    m_Running = true;
    m_Owner.update();

    try {
      showStatus("Initializing");
      m_Flow = ActorUtils.removeDisabledActors(m_Flow);
      if (m_Flow instanceof Flow) {
	((Flow) m_Flow).setHeadless(m_Owner.isHeadless());
	((Flow) m_Flow).setParentComponent(m_Owner);
	if (m_Debug) {
	  if (((Flow) m_Flow).firstActive() != null) {
	    breakpoint = new AnyActorBreakpoint();
	    breakpoint.setOnPreExecute(true);
	    ((Flow) m_Flow).addBreakpoint(breakpoint);
	  }
	}
      }
      m_Output = m_Flow.setUp();
      if ((m_Output == null) && !m_Flow.isStopped()) {
	if (m_Flow instanceof VariablesHandler) {
	  if (ActorUtils.updateVariablesWithFlowFilename((VariablesHandler) m_Flow, m_File)) {
	    if (m_Owner.isModified())
	      m_Flow.getLogger().warning("Flow '" + m_File + "' not saved, flow variables like '" + ActorUtils.FLOW_DIR + "' might not be accurate!");
	  }
	}

	showStatus("Running");
	m_Owner.setLastFlow(m_Flow);
	m_Output = m_Flow.execute();
	// did the flow get stopped by a critical actor?
	if ((m_Output == null) && m_Flow.hasStopMessage())
	  m_Output = m_Flow.getStopMessage();

	// was flow stopped externally and we need to wait for it to finish?
	if (m_Stopping) {
	  while (!m_Flow.isStopped()) {
	    try {
	      synchronized(this) {
		wait(100);
	      }
	    }
	    catch (Exception e) {
	      // ignored
	    }
	  }
	}
      }
    }
    catch (Throwable e) {
      e.printStackTrace();
      m_Output = Utils.throwableToString(e);
    }

    if ((m_Owner.getVariablesPanel() != null) && (m_Owner.getVariablesPanel().getParentDialog() != null))
      m_Owner.getVariablesPanel().getParentDialog().setVisible(false);

    return null;
  }

  /**
   * Executed on the <i>Event Dispatch Thread</i> after the {@code doInBackground}
   * method is finished.
   */
  protected void done() {
    String	msg;
    String	errors;
    int	countErrors;

    showStatus("Finishing up");
    m_Flow.wrapUp();
    if (m_Owner.getRunGC())
      System.gc();

    m_Flow = null;
    errors = null;

    if (m_Owner.getLastFlow() instanceof LogEntryHandler) {
      countErrors = ((LogEntryHandler) m_Owner.getLastFlow()).countLogEntries();
      if (countErrors > 0)
	errors = countErrors + " error(s) logged";
    }

    if (m_Output != null) {
      msg = "Finished with error: " + m_Output;
      if (errors != null)
	msg += "(" + errors + ")";
      showStatus(msg);
      if (m_ShowNotification)
	showNotification(m_Output, true);
    }
    else {
      if (m_Running)
	msg = "Flow finished.";
      else
	msg = "User stopped flow.";
      if (errors != null)
	msg += " " + errors + ".";
      showStatus(msg);
      if (m_ShowNotification)
	m_Owner.showNotification(msg, !m_Running);
    }

    m_Running  = false;
    m_Stopping = false;

    m_Owner.update();
    m_Owner.finishedExecution();
  }

  /**
   * Execute the flow.
   */
  @Override
  public void run() {
    try {
      doInBackground();
    }
    catch (Throwable t) {
      m_Output = Utils.throwableToString(t);
    }
    finally {
      done();
    }
  }

  /**
   * Pauses the execution.
   */
  @Override
  public void pauseExecution() {
    showStatus("Pausing");
    ((Pausable) m_Flow).pauseExecution();
    m_Owner.setTabIcon("pause.gif");
    m_Owner.update();
  }

  /**
   * Returns whether the object is currently paused.
   *
   * @return		true if object is paused
   */
  @Override
  public boolean isPaused() {
    return ((Pausable) m_Flow).isPaused();
  }

  /**
   * Resumes the execution.
   */
  @Override
  public void resumeExecution() {
    showStatus("Resuming");
    ((Pausable) m_Flow).resumeExecution();
    m_Owner.setTabIcon("run.gif");
    m_Owner.update();
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    m_Stopping = true;
    m_Running  = false;
    showStatus("Stopping");
    m_Owner.update();
    m_Flow.stopExecution();
  }

  /**
   * Returns whether a flow is currently running.
   *
   * @return		true if a flow is being executed
   */
  public boolean isRunning() {
    return m_Running;
  }

  /**
   * Returns whether a flow is currently being stopped.
   *
   * @return		true if a flow is currently being stopped
   */
  public boolean isStopping() {
    return m_Stopping;
  }

  /**
   * Displays a message.
   *
   * @param msg	the message to display
   */
  @Override
  public void showStatus(String msg) {
    m_Owner.showStatus(msg);
  }

  /**
   * Displays the given message in a separate dialog.
   *
   * @param msg	the message to display
   * @param isError	whether it is an error message
   */
  public void showNotification(String msg, boolean isError) {
    m_Owner.showNotification(msg, isError);
  }

  /**
   * Returns the flow.
   *
   * @return		the flow
   */
  public Actor getFlow() {
    return m_Flow;
  }
}
