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
 * Flow.java
 * Copyright (C) 2009-202 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import adams.core.DateUtils;
import adams.core.MessageCollection;
import adams.core.Properties;
import adams.core.QuickInfoHelper;
import adams.core.UniqueIDs;
import adams.core.Variables;
import adams.core.VariablesHandler;
import adams.core.io.ConsoleHelper;
import adams.core.option.UserMode;
import adams.data.id.RuntimeIDGenerator;
import adams.db.LogEntry;
import adams.db.MutableLogEntryHandler;
import adams.env.Environment;
import adams.env.FlowDefinition;
import adams.flow.control.flowrestart.AbstractFlowRestartManager;
import adams.flow.control.flowrestart.NullManager;
import adams.flow.control.postflowexecution.PostFlowExecution;
import adams.flow.core.Actor;
import adams.flow.core.ActorExecution;
import adams.flow.core.ActorHandler;
import adams.flow.core.ActorHandlerInfo;
import adams.flow.core.ActorUtils;
import adams.flow.core.CallableNamesRecorder;
import adams.flow.core.FlowVariables;
import adams.flow.core.PauseStateHandler;
import adams.flow.core.PauseStateManager;
import adams.flow.core.StopRestrictor;
import adams.flow.core.TriggerableEvent;
import adams.flow.execution.Debug;
import adams.flow.execution.FlowExecutionListener;
import adams.flow.execution.FlowExecutionListeningSupporter;
import adams.flow.execution.GraphicalFlowExecutionListener;
import adams.flow.execution.MultiListener;
import adams.flow.execution.NullListener;
import adams.flow.execution.debug.AbstractBreakpoint;
import adams.flow.execution.debug.AbstractScopeRestriction;
import adams.flow.execution.debug.MultiScopeRestriction;
import adams.flow.execution.debug.MultiScopeRestriction.ScopeCombination;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.Child;
import adams.gui.core.BaseFrame;
import adams.gui.core.GUIHelper;
import adams.gui.flow.FlowPanel;
import adams.gui.flow.tabhandler.GraphicalFlowExecutionListenersHandler;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Container object for actors, used for executing a flow.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: Flow
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-finish-before-stopping &lt;boolean&gt; (property: finishBeforeStopping)
 * &nbsp;&nbsp;&nbsp;If enabled, actor first finishes processing all data before stopping.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-stopping-timeout &lt;int&gt; (property: stoppingTimeout)
 * &nbsp;&nbsp;&nbsp;The timeout in milliseconds when waiting for actors to finish (&lt;= 0 for
 * &nbsp;&nbsp;&nbsp;infinity; see 'finishBeforeStopping').
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 * <pre>-actor &lt;adams.flow.core.Actor&gt; [-actor ...] (property: actors)
 * &nbsp;&nbsp;&nbsp;All the actors that define this flow.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-read-only &lt;boolean&gt; (property: readOnly)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow is marked as read-only and the user gets prompted
 * &nbsp;&nbsp;&nbsp;whether to succeed before allowing it to be edited.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-error-handling &lt;ACTORS_ALWAYS_STOP_ON_ERROR|ACTORS_DECIDE_TO_STOP_ON_ERROR&gt; (property: errorHandling)
 * &nbsp;&nbsp;&nbsp;Defines how errors are handled that occur during execution of the flow;
 * &nbsp;&nbsp;&nbsp;ACTORS_DECIDE_TO_STOP_ON_ERROR stops the flow only if the actor has the '
 * &nbsp;&nbsp;&nbsp;stopFlowOnError' flag set.
 * &nbsp;&nbsp;&nbsp;default: ACTORS_ALWAYS_STOP_ON_ERROR
 * </pre>
 *
 * <pre>-log-errors &lt;boolean&gt; (property: logErrors)
 * &nbsp;&nbsp;&nbsp;If set to true, errors are logged and can be retrieved after execution.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-execute-on-error &lt;adams.core.io.FlowFile&gt; (property: executeOnError)
 * &nbsp;&nbsp;&nbsp;The external flow to execute in case the flow finishes with an error; allows
 * &nbsp;&nbsp;&nbsp;the user to call a clean-up flow.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 * <pre>-execute-on-finish &lt;adams.core.io.FlowFile&gt; (property: executeOnFinish)
 * &nbsp;&nbsp;&nbsp;The external flow to execute in case the flow finishes normal, without any
 * &nbsp;&nbsp;&nbsp;errors.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 * <pre>-flow-execution-listening-enabled &lt;boolean&gt; (property: flowExecutionListeningEnabled)
 * &nbsp;&nbsp;&nbsp;Enables&#47;disables the flow execution listener.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-flow-execution-listener &lt;adams.flow.execution.FlowExecutionListener&gt; (property: flowExecutionListener)
 * &nbsp;&nbsp;&nbsp;The listener for the flow execution; must be enabled explicitly.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.execution.NullListener
 * </pre>
 *
 * <pre>-flow-restart-manager &lt;adams.flow.control.flowrestart.AbstractFlowRestartManager&gt; (property: flowRestartManager)
 * &nbsp;&nbsp;&nbsp;The manager for restarting the flow.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.control.flowrestart.NullManager
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class Flow
  extends MutableConnectedControlActor
  implements MutableLogEntryHandler, StorageHandler,
	       VariablesHandler, TriggerableEvent, PauseStateHandler,
	       FlowExecutionListeningSupporter, ScopeHandler, StopRestrictor {

  /** for serialization. */
  private static final long serialVersionUID = 723059748204261319L;

  /**
   * Enum for the error handling within the flow.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   */
  public enum ErrorHandling {
    /** actors always stop on an error. */
    ACTORS_ALWAYS_STOP_ON_ERROR,
    /** actors only stop on errors if the "stopFlowOnError" flag is set. */
    ACTORS_DECIDE_TO_STOP_ON_ERROR
  }

  /** the filename for flow settings. */
  public final static String FILENAME = "Flow.props";

  /** whether to mark this flow as read-only (user gets prompted). */
  protected boolean m_ReadOnly;

  /** the error handling. */
  protected ErrorHandling m_ErrorHandling;

  /** whether to store errors (as LogEntry records). */
  protected boolean m_LogErrors;

  /** for storing the LogEntry records. */
  protected ArrayList<LogEntry> m_LogEntries;

  /** the storage for temporary data. */
  protected transient Storage m_Storage;

  /** the 'local' variables. */
  protected FlowVariables m_Variables;

  /** generator for flow to execute in case of an abnormal stop. */
  protected PostFlowExecution m_ExecuteOnError;

  /** the actor to execute in case of an abnormal stop. */
  protected Actor m_ExecuteOnErrorActor;

  /** generator for flow to execute in case the flow finishes normal. */
  protected PostFlowExecution m_ExecuteOnFinish;

  /** the external actor to execute in case the flow finishes normal. */
  protected Actor m_ExecuteOnFinishActor;

  /** the actor that was executed after the flow finished. */
  protected Actor m_AfterExecuteActor;

  /** for managing the pause state. */
  protected PauseStateManager m_PauseStateManager;

  /** whether flow execution listening is enabled. */
  protected boolean m_FlowExecutionListeningEnabled;

  /** whether flow execution listening has started. */
  protected boolean m_FlowExecutionListeningStarted;

  /** the execution listener to use. */
  protected FlowExecutionListener m_FlowExecutionListener;

  /** the manager for restarting the flow. */
  protected AbstractFlowRestartManager m_FlowRestartManager;

  /** the callable names. */
  protected CallableNamesRecorder m_CallableNames;

  /** whether the callable name check is enforced. */
  protected boolean m_EnforceCallableNameCheck;

  /** the parent component to use for interactive actors. */
  protected transient Component m_ParentComponent;

  /** the default close operation. */
  protected int m_DefaultCloseOperation;

  /** whether the execution is to be headless, i.e., no GUI components. */
  protected boolean m_Headless;

  /** the flow ID. */
  protected int m_FlowID;

  /** whether to register the flow. */
  protected boolean m_Register;

  /** the register for windows. */
  protected Map<Window,String> m_WindowRegister;

  /** for synchronizing. */
  protected final Long m_Synchronize = UniqueIDs.nextLong();

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Container object for actors, used for executing a flow.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "read-only", "readOnly",
      false);

    m_OptionManager.add(
      "error-handling", "errorHandling",
      ErrorHandling.ACTORS_ALWAYS_STOP_ON_ERROR);

    m_OptionManager.add(
      "log-errors", "logErrors",
      false).setMinUserMode(UserMode.EXPERT);

    m_OptionManager.add(
      "execute-on-error", "executeOnError",
      new adams.flow.control.postflowexecution.Null()).setMinUserMode(UserMode.EXPERT);

    m_OptionManager.add(
      "execute-on-finish", "executeOnFinish",
      new adams.flow.control.postflowexecution.Null()).setMinUserMode(UserMode.EXPERT);

    m_OptionManager.add(
      "flow-execution-listening-enabled", "flowExecutionListeningEnabled",
      false).setMinUserMode(UserMode.EXPERT);

    m_OptionManager.add(
      "flow-execution-listener", "flowExecutionListener",
      new NullListener()).setMinUserMode(UserMode.EXPERT);

    m_OptionManager.add(
      "flow-restart-manager", "flowRestartManager",
      new NullManager()).setMinUserMode(UserMode.EXPERT);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_LogEntries               = new ArrayList<>();
    m_Variables                = new FlowVariables();
    m_ExecuteOnErrorActor      = null;
    m_ExecuteOnFinishActor     = null;
    m_PauseStateManager        = new PauseStateManager();
    m_CallableNames            = new CallableNamesRecorder();
    m_EnforceCallableNameCheck = true;
    m_ParentComponent          = null;
    m_DefaultCloseOperation    = BaseFrame.HIDE_ON_CLOSE;
    m_Headless                 = false;
    m_WindowRegister           = new HashMap<>();
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_AfterExecuteActor = null;
    m_CallableNames.clear();
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	value;

    result = "";

    if (!(m_ExecuteOnError instanceof adams.flow.control.postflowexecution.Null)) {
      value = QuickInfoHelper.toString(this, "executeOnError", m_ExecuteOnError);
      if (value != null)
	result += "on error: " + value;
    }

    if (!(m_ExecuteOnFinish instanceof adams.flow.control.postflowexecution.Null)) {
      value = QuickInfoHelper.toString(this, "executeOnFinish", m_ExecuteOnFinish);
      if (value != null) {
	if (!result.isEmpty())
	  result += ", ";
	result += "on finish: " + value;
      }
    }

    if (m_FlowExecutionListeningEnabled || QuickInfoHelper.hasVariable(this, "executionListener")) {
      value = QuickInfoHelper.toString(this, "executionListener", m_FlowExecutionListener, "listener: ");
      if (!result.isEmpty())
	result += ", ";
      result += value;
    }

    if (super.getQuickInfo() != null)
      result += ", " + super.getQuickInfo();

    if (m_ReadOnly) {
      if (!result.isEmpty())
	result += ", ";
      result += "read-only";
    }

    return result;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String actorsTipText() {
    return "All the actors that define this flow.";
  }

  /**
   * Sets whether the flow is marked as read-only and the user gets prompted
   * whether to succeed before allowing it to be edited.
   *
   * @param value 	true if read only
   */
  public void setReadOnly(boolean value) {
    m_ReadOnly = value;
    reset();
  }

  /**
   * Returns whether the flow is marked as read-only and the user gets prompted
   * whether to succeed before allowing it to be edited.
   *
   * @return 		true if read only
   */
  public boolean getReadOnly() {
    return m_ReadOnly;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String readOnlyTipText() {
    return "If set to true, the flow is marked as read-only and the user gets prompted whether to proceed before allowing it to be edited.";
  }

  /**
   * Sets how errors are handled.
   *
   * @param value 	the error handling
   */
  public void setErrorHandling(ErrorHandling value) {
    m_ErrorHandling = value;
    reset();
  }

  /**
   * Returns how errors are handled.
   *
   * @return 		the error handling
   */
  public ErrorHandling getErrorHandling() {
    return m_ErrorHandling;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String errorHandlingTipText() {
    return
      "Defines how errors are handled that occur during execution of the "
	+ "flow; " + ErrorHandling.ACTORS_DECIDE_TO_STOP_ON_ERROR + " stops the "
	+ "flow only if the actor has the 'stopFlowOnError' flag set.";
  }

  /**
   * Sets whether to log errors.
   *
   * @param value 	true if to log errors
   */
  public void setLogErrors(boolean value) {
    m_LogErrors = value;
    reset();
  }

  /**
   * Returns whether errors are logged.
   *
   * @return 		true if errors are logged
   */
  public boolean getLogErrors() {
    return m_LogErrors;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String logErrorsTipText() {
    return "If set to true, errors are logged and can be retrieved after execution.";
  }

  /**
   * Sets the generator for flow to execute in case the flow finishes with an error.
   *
   * @param value 	the flow generator
   */
  public void setExecuteOnError(PostFlowExecution value) {
    m_ExecuteOnError = value;
    reset();
  }

  /**
   * Returns the generator for flow to execute in case the flow finishes with an error.
   *
   * @return 		the flow generator
   */
  public PostFlowExecution getExecuteOnError() {
    return m_ExecuteOnError;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String executeOnErrorTipText() {
    return
      "The generator for the flow to execute in case the flow finishes with an "
	+ "error; allows the user to call a clean-up flow.";
  }

  /**
   * Sets the generator for flow to execute in case the flow finishes without
   * any errors.
   *
   * @param value 	the flow generator
   */
  public void setExecuteOnFinish(PostFlowExecution value) {
    m_ExecuteOnFinish = value;
    reset();
  }

  /**
   * Returns generator for flow to execute in case the flow finishes without
   * any errors.
   *
   * @return 		the flow generator
   */
  public PostFlowExecution getExecuteOnFinish() {
    return m_ExecuteOnFinish;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String executeOnFinishTipText() {
    return
      "The generator for the flow to execute in case the flow finishes normal, without "
	+ "any errors.";
  }

  /**
   * Sets whether flow execution listening is enabled.
   *
   * @param value	true if to enable listening
   */
  public void setFlowExecutionListeningEnabled(boolean value) {
    m_FlowExecutionListeningEnabled = value;
    reset();
  }

  /**
   * Returns whether flow execution listening is enabled.
   *
   * @return		true if listening is enabled
   */
  public boolean isFlowExecutionListeningEnabled() {
    return m_FlowExecutionListeningEnabled;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String flowExecutionListeningEnabledTipText() {
    return "Enables/disables the flow execution listener.";
  }

  /**
   * Sets the listener to use.
   *
   * @param l		the listener to use
   */
  public void setFlowExecutionListener(FlowExecutionListener l) {
    m_FlowExecutionListener = l;
    reset();
  }

  /**
   * Returns the current listener in use.
   *
   * @return		the listener
   */
  public FlowExecutionListener getFlowExecutionListener() {
    return m_FlowExecutionListener;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String flowExecutionListenerTipText() {
    return "The listener for the flow execution; must be enabled explicitly.";
  }

  /**
   * Sets the restart manager to use.
   *
   * @param value	the manager
   */
  public void setFlowRestartManager(AbstractFlowRestartManager value) {
    m_FlowRestartManager = value;
    reset();
  }

  /**
   * Returns the restart manager in use.
   *
   * @return		the manager
   */
  public AbstractFlowRestartManager getFlowRestartManager() {
    return m_FlowRestartManager;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String flowRestartManagerTipText() {
    return "The manager for restarting the flow.";
  }

  /**
   * Returns whether listeners can be attached at runtime.
   *
   * @return		true if listeners can be attached dynamically
   */
  public boolean canStartListeningAtRuntime() {
    return true;
  }

  /**
   * Attaches the listener and starts listening.
   *
   * @param l		the listener to attach and use immediately,
   * 			{@link NullListener} disables the listening
   * @return		true if listening could be started successfully
   */
  public boolean startListeningAtRuntime(FlowExecutionListener l) {
    boolean					result;
    MultiListener				multi;
    List<FlowExecutionListener>			listeners;

    result = true;

    // disable listening?
    if (l instanceof NullListener) {
      if (m_FlowExecutionListeningEnabled) {
	m_FlowExecutionListener.finishListening();
	m_FlowExecutionListeningEnabled = false;
	deregisterGraphicalFlowExecutionListener(m_FlowExecutionListener);
      }
      m_FlowExecutionListener = l;
      m_FlowExecutionListener.setOwner(this);
      return result;
    }

    // enable listening
    if (!m_FlowExecutionListeningEnabled)
      m_FlowExecutionListeningEnabled = true;

    if (m_FlowExecutionListener instanceof NullListener) {
      m_FlowExecutionListener.finishListening();
      m_FlowExecutionListener = l;
      m_FlowExecutionListener.setOwner(this);
      m_FlowExecutionListener.startListening();
      registerGraphicalFlowExecutionListener(m_FlowExecutionListener);
    }
    else {
      if (m_FlowExecutionListener instanceof MultiListener) {
	multi     = (MultiListener) m_FlowExecutionListener;
	listeners = new ArrayList<>(Arrays.asList(multi.getSubListeners()));
	deregisterGraphicalFlowExecutionListener(multi);
      }
      else {
	multi     = new MultiListener();
	listeners = new ArrayList<>();
	multi.setOwner(this);
	multi.startListening();
      }
      listeners.add(l);
      multi.setSubListeners(listeners.toArray(new FlowExecutionListener[0]));
      m_FlowExecutionListener = multi;
      m_FlowExecutionListener.setOwner(this);
      l.startListening();
      registerGraphicalFlowExecutionListener(multi);
    }

    m_FlowExecutionListeningStarted = true;

    showGraphicalFlowExecutionListeners();

    return result;
  }

  /**
   * Adds the window to the register.
   *
   * @param window	the window to register
   * @param title	the title to use
   */
  public void registerWindow(Window window, String title) {
    m_WindowRegister.put(window, title);
  }

  /**
   * Removes the window from the register.
   *
   * @param window	the window to register
   */
  public void deregisterWindow(Window window) {
    m_WindowRegister.remove(window);
  }

  /**
   * Returns the current register.
   *
   * @return		the register
   */
  public Map<Window,String> getWindowRegister() {
    return m_WindowRegister;
  }

  /**
   * Adds the breakpoint to its flow execution listener setup.
   *
   * @param breakpoint		the breakpoint to add
   * @param restriction		the scope restriction to use
   * @param showView		whether to show the debug view
   */
  public void addBreakpoint(AbstractBreakpoint breakpoint, AbstractScopeRestriction restriction, boolean showView) {
    Debug debug;
    MultiListener multiListen;
    MultiScopeRestriction multiScope;
    FlowExecutionListener listener;
    List<FlowExecutionListener> listeners;
    List<AbstractBreakpoint> breakpoints;
    boolean present;

    if (isHeadless() || GUIHelper.isHeadless())
      return;

    if (!isFlowExecutionListeningEnabled())
      setFlowExecutionListeningEnabled(true);
    listener = getFlowExecutionListener();

    if (listener instanceof NullListener) {
      debug = new Debug();
      debug.setOwner(this);
      debug.setBreakpoints(new AbstractBreakpoint[]{breakpoint});
      debug.setScopeRestriction(restriction);
      setFlowExecutionListener(debug);
    }
    else if (listener instanceof Debug) {
      debug = (Debug) listener;
      present = false;
      for (AbstractBreakpoint bp : debug.getBreakpoints()) {
	if (bp.toCommandLine().equals(breakpoint.toCommandLine())) {
	  present = true;
	  break;
	}
      }
      if (!present) {
	breakpoints = new ArrayList<>(Arrays.asList(debug.getBreakpoints()));
	breakpoints.add(breakpoint);
	debug.setBreakpoints(breakpoints.toArray(new AbstractBreakpoint[0]));
	multiScope = new MultiScopeRestriction();
	multiScope.setCombination(ScopeCombination.OR);
	multiScope.setRestrictions(new AbstractScopeRestriction[]{
	  debug.getScopeRestriction(),
	  restriction
	});
	debug.setScopeRestriction(multiScope);
	debug.refreshGUI();
      }
    }
    else if (listener instanceof MultiListener) {
      multiListen = (MultiListener) listener;
      debug = null;
      for (FlowExecutionListener l : multiListen.getSubListeners()) {
	if (l instanceof Debug) {
	  debug = (Debug) l;
	  break;
	}
      }
      if (debug == null) {
	debug = new Debug();
	debug.setOwner(this);
	debug.setBreakpoints(new AbstractBreakpoint[]{breakpoint});
	debug.setScopeRestriction(restriction);
	listeners = new ArrayList<>(Arrays.asList(multiListen.getSubListeners()));
	listeners.add(debug);
	multiListen.setSubListeners(listeners.toArray(new FlowExecutionListener[0]));
      }
      else {
	// breakpoint already present?
	present = false;
	for (AbstractBreakpoint bp : debug.getBreakpoints()) {
	  if (bp.toCommandLine().equals(breakpoint.toCommandLine())) {
	    present = true;
	    break;
	  }
	}
	if (!present) {
	  breakpoints = new ArrayList<>(Arrays.asList(debug.getBreakpoints()));
	  breakpoints.add(breakpoint);
	  debug.setBreakpoints(breakpoints.toArray(new AbstractBreakpoint[0]));
	  multiScope = new MultiScopeRestriction();
	  multiScope.setCombination(ScopeCombination.OR);
	  multiScope.setRestrictions(new AbstractScopeRestriction[]{
	    debug.getScopeRestriction(),
	    restriction
	  });
	  debug.setScopeRestriction(multiScope);
	  debug.refreshGUI();
	}
      }
    }
    else {
      multiListen = new MultiListener();
      debug = new Debug();
      debug.setOwner(this);
      debug.setBreakpoints(new AbstractBreakpoint[]{breakpoint});
      debug.setScopeRestriction(restriction);
      multiListen.setSubListeners(new FlowExecutionListener[]{listener, debug});
      setFlowExecutionListener(multiListen);
    }

    if (m_FlowExecutionListeningStarted)
      registerGraphicalFlowExecutionListener(debug);

    if (showView)
      showGraphicalFlowExecutionListeners();
  }

  /**
   * Retrieves the handler for GraphicalFlowExecutionListener objects.
   *
   * @return		the handler, null if not available
   */
  protected GraphicalFlowExecutionListenersHandler getGraphicalFlowExecutionListenersHandler() {
    FlowPanel 					panel;
    GraphicalFlowExecutionListenersHandler 	result;

    result = null;
    if ((getParentComponent() != null) && (getParentComponent() instanceof Container)) {
      panel = (FlowPanel) GUIHelper.getParent((Container) getParentComponent(), FlowPanel.class);
      result = panel.getTabHandler(GraphicalFlowExecutionListenersHandler.class);
    }

    return result;
  }

  /**
   * Registers the graphical flow execution listener.
   *
   * @param l		the listener to register
   */
  protected void registerGraphicalFlowExecutionListener(FlowExecutionListener l) {
    GraphicalFlowExecutionListenersHandler 	handler;

    if (l instanceof GraphicalFlowExecutionListener) {
      handler = getGraphicalFlowExecutionListenersHandler();
      if (handler != null)
	handler.register((GraphicalFlowExecutionListener) l);
    }
  }

  /**
   * Deregisters the graphical flow execution listener.
   *
   * @param l		the listener to register
   */
  protected void deregisterGraphicalFlowExecutionListener(FlowExecutionListener l) {
    GraphicalFlowExecutionListenersHandler 	handler;

    if (l instanceof GraphicalFlowExecutionListener) {
      handler = getGraphicalFlowExecutionListenersHandler();
      if (handler != null)
	handler.deregister((GraphicalFlowExecutionListener) l);
    }
  }

  /**
   * Displays the graphical flow executions listeners tab.
   */
  public void showGraphicalFlowExecutionListeners() {
    GraphicalFlowExecutionListenersHandler 	handler;

    handler = getGraphicalFlowExecutionListenersHandler();
    if (handler != null)
      handler.display();
  }

  /**
   * Sets the use for interactive actors.
   *
   * @param value	the parent, can be null
   */
  public void setParentComponent(Component value) {
    m_ParentComponent = value;
  }

  /**
   * Returns the current parent component for interactive actors.
   *
   * @return		the parent, null if not set
   */
  @Override
  public Component getParentComponent() {
    return m_ParentComponent;
  }

  /**
   * Uses {@link #getParentComponent()} to determine the application frame.
   *
   * @return		the application frame, null if failed to determine
   */
  public AbstractApplicationFrame getApplicationFrame() {
    AbstractApplicationFrame	result;
    Child 			child;

    result = null;

    if ((getParentComponent() != null) && (getParentComponent() instanceof Container)) {
      result = (AbstractApplicationFrame) GUIHelper.getParent(
	(Container) getParentComponent(), AbstractApplicationFrame.class);
      if (result == null) {
	child = (Child) GUIHelper.getParent(
	  (Container) getParentComponent(), Child.class);
	if (child != null)
	  result = child.getParentFrame();
      }
    }

    return result;
  }

  /**
   * Sets the default close operation for frames.
   *
   * @param value	the operation
   */
  public void setDefaultCloseOperation(int value) {
    m_DefaultCloseOperation = value;
  }

  /**
   * Returns the default close operation for frames.
   *
   * @return		the operation
   */
  public int getDefaultCloseOperation() {
    return m_DefaultCloseOperation;
  }

  /**
   * Sets whether the actor is to be run in headless mode, i.e., suppressing
   * GUI components.
   *
   * @param value	if true then GUI components will be suppressed
   */
  public void setHeadless(boolean value) {
    m_Headless = value;
  }

  /**
   * Returns whether the actor is run in headless mode.
   *
   * @return		true if GUI components are suppressed
   */
  public boolean isHeadless() {
    return m_Headless;
  }

  /**
   * Returns some information about the actor handler, e.g., whether it can
   * contain standalones and the actor execution.
   *
   * @return		the info
   */
  @Override
  public ActorHandlerInfo getActorHandlerInfo() {
    return new ActorHandlerInfo()
	     .allowStandalones(true)
	     .actorExecution(ActorExecution.SEQUENTIAL)
	     .forwardsInput(false);
  }

  /**
   * Returns the Variables instance to use.
   *
   * @return		the scope handler
   */
  @Override
  public Variables getVariables() {
    return getLocalVariables();
  }

  /**
   * Ignored.
   *
   * @param value	ignored
   */
  @Override
  public synchronized void setVariables(Variables value) {
  }

  /**
   * Returns the Variables instance to use.
   *
   * @return		the scope handler
   */
  public Variables getLocalVariables() {
    synchronized (m_Synchronize) {
      getOptionManager().setVariables(m_Variables);
    }
    return m_Variables;
  }

  /**
   * Returns the pause state manager.
   *
   * @return		the manager
   */
  public PauseStateManager getPauseStateManager() {
    return m_PauseStateManager;
  }

  /**
   * Returns the root of this actor, e.g., the group at the highest level.
   *
   * @return		the root, always itself
   */
  @Override
  public Actor getRoot() {
    return this;
  }

  /**
   * Returns the full name of the actor, i.e., the concatenated names of all
   * parents. Used in error messages.
   *
   * @return		the full name
   */
  @Override
  public String getFullName() {
    m_FullName = null;
    return super.getFullName();
  }

  /**
   * Performs the setUp of the sub-actors.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String setUpSubActors() {
    String	result;

    result = super.setUpSubActors();
    if (result == null)
      result = ActorUtils.checkForSource(getActors());

    return result;
  }

  /**
   * Updates the Variables instance in use.
   * <br><br>
   * Use with caution!
   *
   * @param value	the instance to use
   */
  @Override
  protected void forceVariables(Variables value) {
    if (value instanceof FlowVariables)
      ((FlowVariables) value).setFlow(this);
    super.forceVariables(value);
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String		result;
    MessageCollection 	errors;

    // variables
    forceVariables(getVariables());

    result = super.setUp();

    m_LogEntries.clear();

    if (result == null) {
      if (!(m_ExecuteOnError instanceof adams.flow.control.postflowexecution.Null)) {
	errors = new MessageCollection();
	m_ExecuteOnErrorActor = m_ExecuteOnError.configureExecution(errors);
	if (!errors.isEmpty())
	  result = errors.toString();
      }
    }

    if (result == null) {
      if (!(m_ExecuteOnFinish instanceof adams.flow.control.postflowexecution.Null)) {
	errors = new MessageCollection();
	m_ExecuteOnFinishActor = m_ExecuteOnFinish.configureExecution(errors);
	if (!errors.isEmpty())
	  result = errors.toString();
      }
    }

    if (result != null)
      getLogger().severe(result);

    return result;
  }

  /**
   * Removes all currently stored LogEntry records.
   */
  public void clearLogEntries() {
    m_LogEntries.clear();
  }

  /**
   * Returns the stored LogEntry records.
   *
   * @return		the stored entries
   */
  public List<LogEntry> getLogEntries() {
    return m_LogEntries;
  }

  /**
   * Adds the LogEntry record to the internal list (in case logging is enabled).
   *
   * @param entry	the record to add
   * @see		#getLogErrors()
   */
  public void addLogEntry(LogEntry entry) {
    if (m_LogErrors)
      m_LogEntries.add(entry);
  }

  /**
   * Returns the specified LogEntry record.
   *
   * @param index	the index of the record to return
   * @return		the requested LogEntry
   */
  public LogEntry getLogEntry(int index) {
    return m_LogEntries.get(index);
  }

  /**
   * Removes the specified LogEntry record from the internal list.
   *
   * @param index	the index of the record to delete
   * @return		the deleted LogEntry
   */
  public LogEntry removeLogEntry(int index) {
    return m_LogEntries.remove(index);
  }

  /**
   * Returns the number of stored LogEntry records.
   *
   * @return		the number of records
   */
  public int countLogEntries() {
    return m_LogEntries.size();
  }

  /**
   * Returns the storage container.
   *
   * @return		the container
   */
  public Storage getStorage() {
    synchronized (m_Synchronize) {
      if (m_Storage == null)
	m_Storage = new Storage();
    }

    return m_Storage;
  }

  /**
   * Returns the flow ID (runtime).
   *
   * @return		the ID
   */
  public int getFlowID() {
    return m_FlowID;
  }

  /**
   * Sets whether to register the flow with the running flows registry,
   * making it visible to remote commands.
   *
   * @param value	true if to register
   */
  public void setRegister(boolean value) {
    m_Register = value;
    reset();
  }

  /**
   * Returns whether to register the flow with the running flows registry,
   * making it visible to remote commands.
   *
   * @return		true if to register
   */
  public boolean getRegister() {
    return m_Register;
  }

  /**
   * Sets whether to enforce the callable name check.
   *
   * @param value	true if to enforce check
   */
  public void setEnforceCallableNameCheck(boolean value) {
    m_EnforceCallableNameCheck = value;
  }

  /**
   * Returns whether the check of callable names is enforced.
   *
   * @return		true if check enforced
   */
  public boolean getEnforceCallableNameCheck() {
    return m_EnforceCallableNameCheck;
  }

  /**
   * Checks whether a callable name is already in use.
   *
   * @param handler 	the handler for the actor
   * @param actor	the actor name to check
   * @see		#getEnforceCallableNameCheck()
   */
  public boolean isCallableNameUsed(ActorHandler handler, Actor actor) {
    if (!getEnforceCallableNameCheck())
      return false;
    else
      return m_CallableNames.contains(handler, actor);
  }

  /**
   * Adds the callable name to the list of used ones.
   *
   * @param handler 	the handler for the actor
   * @param actor	the actor name to add
   * @return		null if successfully added, otherwise error message
   * @see		#getEnforceCallableNameCheck()
   */
  public String addCallableName(ActorHandler handler, Actor actor) {
    if (!getEnforceCallableNameCheck())
      return null;

    if (isCallableNameUsed(handler, actor))
      return "Callable name '" + actor.getName() + "' is already used in this scope ('" + handler.getParent().getFullName() + "')!";

    m_CallableNames.add(handler, actor);
    return null;
  }

  /**
   * Returns whether stops are being restricted.
   *
   * @return		true if restricting stops
   */
  @Override
  public boolean isRestrictingStops() {
    return true;
  }

  /**
   * Executes the actor.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    Date		start;
    Date		finish;
    Properties		props;

    start    = null;
    finish   = null;
    m_FlowID = RuntimeIDGenerator.getSingleton().next();

    // properties
    props = Environment.getInstance().read(FlowDefinition.KEY);
    // register?
    if (m_Register || props.getBoolean("AutoRegister", false))
      RunningFlowsRegistry.getSingleton().addFlow(this);

    if (!m_FlowExecutionListeningStarted) {
      if (m_FlowExecutionListeningEnabled) {
	m_FlowExecutionListener.setOwner(this);
	m_FlowExecutionListener.startListening();
	m_FlowExecutionListeningStarted = true;
	registerGraphicalFlowExecutionListener(m_FlowExecutionListener);
	showGraphicalFlowExecutionListeners();
      }
    }

    start = new Date();
    if (m_Headless) {
      ConsoleHelper.printlnOut("");
      ConsoleHelper.printlnOut("--> Start: " + DateUtils.getTimestampFormatterMsecs().format(start));
      ConsoleHelper.printlnOut("");
    }

    result = m_FlowRestartManager.start(this);

    if (result == null)
      result = super.doExecute();

    finish = new Date();
    if (m_Headless) {
      ConsoleHelper.printlnOut("");
      ConsoleHelper.printlnOut("--> Finish: " + DateUtils.getTimestampFormatterMsecs().format(finish));
      ConsoleHelper.printlnOut("--> Duration: " + DateUtils.msecToString(DateUtils.difference(start, finish)) + "\n");
      ConsoleHelper.printlnOut("");
    }

    // do we execute an actor?
    m_AfterExecuteActor = null;
    if (isStopped()) {
      if (m_ExecuteOnErrorActor != null)
	m_AfterExecuteActor = m_ExecuteOnErrorActor;
    }
    else if (result == null) {
      if (m_ExecuteOnFinishActor != null)
	m_AfterExecuteActor = m_ExecuteOnFinishActor;
    }
    if (m_AfterExecuteActor != null) {
      m_AfterExecuteActor.execute();
      m_AfterExecuteActor.wrapUp();
    }

    if (m_FlowExecutionListeningEnabled)
      m_FlowExecutionListener.finishListening();

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String execute() {
    String	result;

    result = super.execute();

    if (result != null)
      getLogger().severe(result);

    return result;
  }

  /**
   * Finishes up the execution.
   */
  @Override
  public void wrapUp() {
    m_FlowRestartManager.stop(this);

    deregisterGraphicalFlowExecutionListener(m_FlowExecutionListener);

    RunningFlowsRegistry.getSingleton().removeFlow(this);

    super.wrapUp();
  }

  /**
   * Stops the (restricted) execution. No message set.
   */
  @Override
  public void restrictedStopExecution() {
    stopExecution();
  }

  /**
   * Stops the (restricted) execution.
   *
   * @param msg		the message to set as reason for stopping, can be null
   */
  @Override
  public void restrictedStopExecution(String msg) {
    stopExecution(msg);
  }

  /**
   * Returns whether the stop was a restricted one (that can be resumed).
   *
   * @return		true if restricted stop occurred
   */
  public boolean isRestrictedStop() {
    return false;
  }

  /**
   * Cleans up after the execution has finished. Also removes graphical
   * components.
   */
  @Override
  public void cleanUp() {
    m_LogEntries.clear();
    m_CallableNames.clear();
    m_WindowRegister.clear();

    if (m_Storage != null) {
      m_Storage.clear();
      m_Storage = null;
    }

    if (m_AfterExecuteActor != null) {
      m_AfterExecuteActor.destroy();
      m_AfterExecuteActor = null;
    }

    m_Variables.cleanUp();

    super.cleanUp();
  }

  /**
   * Frees up memory in a "destructive" non-reversible way.
   * <br><br>
   * Cleans up the variables.
   *
   * @see 		#m_Variables
   */
  @Override
  public void destroy() {
    m_Variables.cleanUp();

    super.destroy();
  }
}
