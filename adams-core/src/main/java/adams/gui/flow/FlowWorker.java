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
 * FlowWorker.java
 * Copyright (C) 2016-2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow;

import adams.core.Pausable;
import adams.core.StatusMessageHandler;
import adams.core.Stoppable;
import adams.core.Utils;
import adams.core.VariablesHandler;
import adams.core.logging.LoggingHelper;
import adams.db.LogEntryHandler;
import adams.flow.control.Flow;
import adams.flow.control.flowrestart.NullManager;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import adams.flow.execution.debug.AnyActorBreakpoint;
import adams.flow.execution.debug.NoScopeRestriction;
import adams.gui.core.MultiPageIconSupporter;
import adams.gui.core.TabIconSupporter;
import adams.gui.flow.FlowPanelNotificationArea.NotificationType;

import javax.swing.SwingUtilities;
import java.awt.Component;
import java.io.File;

/**
 * Specialized worker class for executing a flow.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class FlowWorker
  implements Runnable, Pausable, Stoppable, StatusMessageHandler {

  /** the panel this flow belongs to. */
  protected FlowWorkerHandler m_Owner;

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
  public FlowWorker(FlowWorkerHandler owner, Actor flow, File file, boolean showNotification, boolean debug) {
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
    AnyActorBreakpoint 	breakpoint;
    boolean 		finished;

    m_Owner.update();
    m_Owner.cleanUp();
    m_Owner.clearNotification();
    updateTabIcon("run.gif");

    m_Running = true;
    m_Owner.update();

    try {
      showStatus("Initializing");
      m_Flow = ActorUtils.removeDisabledActors(m_Flow);
      if (m_Flow instanceof Flow) {
	((Flow) m_Flow).setHeadless(m_Owner.isHeadless());
	if (m_Owner instanceof  Component)
	  ((Flow) m_Flow).setParentComponent((Component) m_Owner);
	if (m_Debug) {
	  if (((Flow) m_Flow).firstActive() != null) {
	    breakpoint = new AnyActorBreakpoint();
	    breakpoint.setOnPreExecute(true);
	    ((Flow) m_Flow).addBreakpoint(breakpoint, new NoScopeRestriction(), true);
	  }
	}
      }
      if (m_Flow instanceof VariablesHandler) {
        ActorUtils.updateProgrammaticVariables((VariablesHandler & Actor) m_Flow, m_File);
        if ((m_File != null) && m_Owner.isModified())
          m_Flow.getLogger().warning("Flow '" + m_File + "' not saved, flow variables like '" + ActorUtils.FLOW_DIR + "' might not be accurate!");
      }
      m_Output = m_Flow.setUp();
      if ((m_Output == null) && !m_Flow.isStopped()) {
        ActorUtils.updateProgrammaticVariables((VariablesHandler & Actor) m_Flow, m_File);
	showStatus("Running");
	m_Owner.setLastFlow(m_Flow);
	do {
	  m_Output = m_Flow.execute();
	  finished = true;
	  // has flow been restarted?
	  if (m_Flow instanceof Flow) {
	    if (!(((Flow) m_Flow).getFlowRestartManager() instanceof NullManager)) {
	      Utils.wait(m_Flow, 2000, 100);  // TODO option in FlowEditor.props?
	      finished = m_Flow.isStopped();
	    }
	  }
	}
	while (!finished);
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
      if (m_Flow instanceof Flow)
        ((Flow) m_Flow).setParentComponent(null);
      e.printStackTrace();
      m_Output = LoggingHelper.throwableToString(e);
    }

    if ((m_Owner.getVariablesPanel() != null) && (m_Owner.getVariablesPanel().getParentDialog() != null))
      m_Owner.getVariablesPanel().getParentDialog().setVisible(false);
    if ((m_Owner.getStoragePanel() != null) && (m_Owner.getStoragePanel().getParentDialog() != null))
      m_Owner.getStoragePanel().getParentDialog().setVisible(false);

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
    if (m_Flow instanceof Flow)
      ((Flow) m_Flow).setParentComponent(null);
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
	showNotification(m_Output, NotificationType.ERROR);
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
        m_Owner.showNotification(msg, m_Running ? NotificationType.INFO : NotificationType.WARNING);
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
      m_Output = LoggingHelper.throwableToString(t);
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
    if (m_Flow != null)
      ((Pausable) m_Flow).pauseExecution();
    updateTabIcon("pause.gif");
    m_Owner.update();
  }

  /**
   * Returns whether the object is currently paused.
   *
   * @return		true if object is paused
   */
  @Override
  public boolean isPaused() {
    return (m_Flow != null) && ((Pausable) m_Flow).isPaused();
  }

  /**
   * Resumes the execution.
   */
  @Override
  public void resumeExecution() {
    showStatus("Resuming");
    if (m_Flow != null)
      ((Pausable) m_Flow).resumeExecution();
    updateTabIcon("run.gif");
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
   * @param msg		the message to display
   * @param type	the type of notification (info/warning/error)
   */
  public void showNotification(String msg, NotificationType type) {
    m_Owner.showNotification(msg, type);
  }

  /**
   * Returns the flow.
   *
   * @return		the flow
   */
  public Actor getFlow() {
    return m_Flow;
  }

  /**
   * Updates the tab icon, if possible.
   *
   * @param icon  	the icon name, can be null
   */
  protected void updateTabIcon(String icon) {
    SwingUtilities.invokeLater(() -> {
      if (m_Owner instanceof MultiPageIconSupporter)
        ((MultiPageIconSupporter) m_Owner).setPageIcon(icon);
      if (m_Owner instanceof TabIconSupporter)
        ((TabIconSupporter) m_Owner).setTabIcon(icon);
    });
  }
}
