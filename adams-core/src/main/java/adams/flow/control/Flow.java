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
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import adams.core.DateUtils;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.Variables;
import adams.core.VariablesHandler;
import adams.core.io.FlowFile;
import adams.data.id.RuntimeIDGenerator;
import adams.db.LogEntry;
import adams.db.MutableLogEntryHandler;
import adams.flow.core.Actor;
import adams.flow.core.ActorExecution;
import adams.flow.core.ActorHandlerInfo;
import adams.flow.core.ActorUtils;
import adams.flow.core.FlowVariables;
import adams.flow.core.PauseStateHandler;
import adams.flow.core.PauseStateManager;
import adams.flow.core.TriggerableEvent;
import adams.flow.execution.AbstractBreakpoint;
import adams.flow.execution.Debug;
import adams.flow.execution.FlowExecutionListener;
import adams.flow.execution.FlowExecutionListeningSupporter;
import adams.flow.execution.GraphicalFlowExecutionListener;
import adams.flow.execution.ListenerUtils;
import adams.flow.execution.MultiListener;
import adams.flow.execution.NullListener;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.Child;
import adams.gui.core.BaseFrame;
import adams.gui.core.GUIHelper;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Container object for actors, used for executing a flow.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: Flow
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 * 
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 * 
 * <pre>-finish-before-stopping (property: finishBeforeStopping)
 * &nbsp;&nbsp;&nbsp;If enabled, actor first finishes processing all data before stopping.
 * </pre>
 * 
 * <pre>-actor &lt;adams.flow.core.Actor&gt; [-actor ...] (property: actors)
 * &nbsp;&nbsp;&nbsp;All the actors that define this flow.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-error-handling &lt;ACTORS_ALWAYS_STOP_ON_ERROR|ACTORS_DECIDE_TO_STOP_ON_ERROR&gt; (property: errorHandling)
 * &nbsp;&nbsp;&nbsp;Defines how errors are handled that occur during execution of the flow; 
 * &nbsp;&nbsp;&nbsp;ACTORS_DECIDE_TO_STOP_ON_ERROR stops the flow only if the actor has the '
 * &nbsp;&nbsp;&nbsp;stopFlowOnError' flag set.
 * &nbsp;&nbsp;&nbsp;default: ACTORS_ALWAYS_STOP_ON_ERROR
 * </pre>
 * 
 * <pre>-log-errors (property: logErrors)
 * &nbsp;&nbsp;&nbsp;If set to true, errors are logged and can be retrieved after execution.
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
 * <pre>-flow-execution-listening-enabled (property: flowExecutionListeningEnabled)
 * &nbsp;&nbsp;&nbsp;Enables&#47;disables the flow execution listener.
 * </pre>
 * 
 * <pre>-flow-execution-listener &lt;adams.flow.execution.FlowExecutionListener&gt; (property: flowExecutionListener)
 * &nbsp;&nbsp;&nbsp;The listener for the flow execution; must be enabled explicitly.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.execution.NullListener
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Flow
  extends MutableConnectedControlActor
  implements MutableLogEntryHandler, StorageHandler,
             VariablesHandler, TriggerableEvent, PauseStateHandler,
             FlowExecutionListeningSupporter, ScopeHandler {

  /** for serialization. */
  private static final long serialVersionUID = 723059748204261319L;

  /**
   * Enum for the error handling within the flow.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum ErrorHandling {
    /** actors always stop on an error. */
    ACTORS_ALWAYS_STOP_ON_ERROR,
    /** actors only stop on errors if the "stopFlowOnError" flag is set. */
    ACTORS_DECIDE_TO_STOP_ON_ERROR
  }

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

  /** the external flow to execute in case of an abnormal stop. */
  protected FlowFile m_ExecuteOnError;

  /** the external actor to execute in case of an abnormal stop. */
  protected Actor m_ExecuteOnErrorActor;

  /** the external flow to execute in case the flow finishes normal. */
  protected FlowFile m_ExecuteOnFinish;

  /** the external actor to execute in case the flow finishes normal. */
  protected Actor m_ExecuteOnFinishActor;

  /** the actor that was executed after the flow finished. */
  protected Actor m_AfterExecuteActor;
  
  /** for managing the pause state. */
  protected PauseStateManager m_PauseStateManager;

  /** whether flow execution listening is enabled. */
  protected boolean m_FlowExecutionListeningEnabled;
  
  /** the execution listener to use. */
  protected FlowExecutionListener m_FlowExecutionListener;
  
  /** the frame for graphical flow execution listeners. */
  protected transient BaseFrame m_FlowExecutionListenerFrame;
  
  /** the callable names. */
  protected HashSet<String> m_CallableNames;
  
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
	"error-handling", "errorHandling",
	ErrorHandling.ACTORS_ALWAYS_STOP_ON_ERROR);

    m_OptionManager.add(
	"log-errors", "logErrors",
	false);

    m_OptionManager.add(
	"execute-on-error", "executeOnError",
	new FlowFile("."));

    m_OptionManager.add(
	"execute-on-finish", "executeOnFinish",
	new FlowFile("."));
    
    m_OptionManager.add(
	"flow-execution-listening-enabled", "flowExecutionListeningEnabled",
	false);
    
    m_OptionManager.add(
	"flow-execution-listener", "flowExecutionListener",
	new NullListener());
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
    m_CallableNames            = new HashSet<>();
    m_EnforceCallableNameCheck = true;
    m_ParentComponent          = null;
    m_DefaultCloseOperation    = BaseFrame.HIDE_ON_CLOSE;
    m_Headless                 = false;
    m_FlowID = RuntimeIDGenerator.getSingleton().next();
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

    value = QuickInfoHelper.toString(this, "executeOnError", (m_ExecuteOnError.isDirectory() ? null : m_ExecuteOnError));
    if (value != null)
      result += "on error: " + value;

    value = QuickInfoHelper.toString(this, "executeOnFinish", (m_ExecuteOnFinish.isDirectory() ? null : m_ExecuteOnFinish));
    if (value != null) {
      if (result.length() > 0)
	result += ", ";
      result += "on finish: " + value;
    }
    
    if (m_FlowExecutionListeningEnabled || QuickInfoHelper.hasVariable(this, "executionListener")) {
      value = QuickInfoHelper.toString(this, "executionListener", m_FlowExecutionListener, "listener: ");
      if (result.length() > 0)
	result += ", ";
      result += value;
    }
    
    if (super.getQuickInfo() != null)
      result += ", " + super.getQuickInfo();

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
   * Sets the external flow to execute in case the flow finishes with an error.
   *
   * @param value 	the external flow
   */
  public void setExecuteOnError(FlowFile value) {
    m_ExecuteOnError = value;
    reset();
  }

  /**
   * Returns the external flow to execute in case the flow finishes with an error.
   *
   * @return 		the external flow
   */
  public FlowFile getExecuteOnError() {
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
        "The external flow to execute in case the flow finishes with an "
      + "error; allows the user to call a clean-up flow.";
  }

  /**
   * Sets the external flow to execute in case the flow finishes without
   * any errors.
   *
   * @param value 	the external flow
   */
  public void setExecuteOnFinish(FlowFile value) {
    m_ExecuteOnFinish = value;
    reset();
  }

  /**
   * Returns the external flow to execute in case the flow finishes without
   * any errors.
   *
   * @return 		the external flow
   */
  public FlowFile getExecuteOnFinish() {
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
        "The external flow to execute in case the flow finishes normal, without "
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
    boolean			result;
    MultiListener		multi;
    List<FlowExecutionListener>	listeners;
    
    result = true;
    
    // disable listening?
    if (l instanceof NullListener) {
      if (m_FlowExecutionListeningEnabled) {
	m_FlowExecutionListener.finishListening();
	m_FlowExecutionListeningEnabled = false;
	if (m_FlowExecutionListenerFrame != null)
	  m_FlowExecutionListenerFrame.dispose();
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
    }
    else {
      if (m_FlowExecutionListener instanceof MultiListener) {
	multi     = (MultiListener) m_FlowExecutionListener;
	listeners = new ArrayList<>(Arrays.asList(multi.getSubListeners()));
      }
      else {
	multi     = new MultiListener();
	listeners = new ArrayList<>();
        multi.setOwner(this);
	multi.startListening();
      }
      listeners.add(l);
      multi.setSubListeners(listeners.toArray(new FlowExecutionListener[listeners.size()]));
      m_FlowExecutionListener = multi;
      m_FlowExecutionListener.setOwner(this);
      l.startListening();
    }

    if (m_FlowExecutionListenerFrame != null)
      m_FlowExecutionListenerFrame.dispose();
    if (!isHeadless())
      m_FlowExecutionListenerFrame = ListenerUtils.createFrame(this);
    
    return result;
  }

  /**
   * Adds the breakpoint to its flow execution listener setup.
   *
   * @param breakpoint		the breakpoint to add
   */
  public void addBreakpoint(AbstractBreakpoint breakpoint) {
    Debug			debug;
    MultiListener		multi;
    FlowExecutionListener	listener;
    List<FlowExecutionListener>	listeners;
    List<AbstractBreakpoint> 	breakpoints;

    if (!isFlowExecutionListeningEnabled())
      setFlowExecutionListeningEnabled(true);
    listener = getFlowExecutionListener();

    if (listener instanceof NullListener) {
      debug = new Debug();
      debug.setBreakpoints(new AbstractBreakpoint[]{breakpoint});
      setFlowExecutionListener(debug);
    }
    else if (listener instanceof Debug) {
      debug = (Debug) listener;
      breakpoints = new ArrayList<>(Arrays.asList(debug.getBreakpoints()));
      breakpoints.add(breakpoint);
      debug.setBreakpoints(breakpoints.toArray(new AbstractBreakpoint[breakpoints.size()]));
    }
    else if (listener instanceof MultiListener) {
      multi = (MultiListener) listener;
      debug = null;
      for (FlowExecutionListener l: multi.getSubListeners()) {
	if (l instanceof Debug) {
	  debug = (Debug) l;
	  break;
	}
      }
      if (debug == null) {
	debug = new Debug();
	debug.setBreakpoints(new AbstractBreakpoint[]{breakpoint});
	listeners = new ArrayList<>(Arrays.asList(multi.getSubListeners()));
	listeners.add(debug);
	multi.setSubListeners(listeners.toArray(new FlowExecutionListener[listeners.size()]));
      }
      else {
	breakpoints = new ArrayList<>(Arrays.asList(debug.getBreakpoints()));
	breakpoints.add(breakpoint);
	debug.setBreakpoints(breakpoints.toArray(new AbstractBreakpoint[breakpoints.size()]));
      }
    }
    else {
      multi = new MultiListener();
      debug = new Debug();
      multi.setSubListeners(new FlowExecutionListener[]{listener, debug});
      setFlowExecutionListener(multi);
    }
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
    return new ActorHandlerInfo(true, ActorExecution.SEQUENTIAL, false);
  }

  /**
   * Returns the Variables instance to use.
   *
   * @return		the scope handler
   */
  @Override
  public synchronized Variables getVariables() {
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
  public synchronized Variables getLocalVariables() {
    getOptionManager().setVariables(m_Variables);
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
    List<String>	errors;

    // variables
    forceVariables(getVariables());
    getVariables().set(ActorUtils.FLOW_ID, "" + getFlowID());
    
    result = super.setUp();

    m_LogEntries.clear();

    if (result == null) {
      if (!m_ExecuteOnError.isDirectory() && m_ExecuteOnError.exists()) {
	errors = new ArrayList<>();
	m_ExecuteOnErrorActor = ActorUtils.read(m_ExecuteOnError.getAbsolutePath(), errors);
	if (!errors.isEmpty())
	  result = "Error loading execute-on-error actor '" + m_ExecuteOnError.getAbsolutePath() + "':\n" + Utils.flatten(errors, "\n");
	else if (m_ExecuteOnErrorActor == null)
	  result = "Error loading execute-on-error actor '" + m_ExecuteOnError.getAbsolutePath() + "'!";
	else
	  result = m_ExecuteOnErrorActor.setUp();
      }
    }

    if (result == null) {
      if (!m_ExecuteOnFinish.isDirectory() && m_ExecuteOnFinish.exists()) {
	errors = new ArrayList<>();
	m_ExecuteOnFinishActor = ActorUtils.read(m_ExecuteOnFinish.getAbsolutePath(), errors);
	if (!errors.isEmpty())
	  result = "Finish loading execute-on-finish actor '" + m_ExecuteOnFinish.getAbsolutePath() + "':\n" + Utils.flatten(errors, "\n");
	else if (m_ExecuteOnFinishActor == null)
	  result = "Finish loading execute-on-finish actor '" + m_ExecuteOnFinish.getAbsolutePath() + "'!";
	else
	  result = m_ExecuteOnFinishActor.setUp();
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
  public synchronized Storage getStorage() {
    if (m_Storage == null)
      m_Storage = new Storage();

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
   * @param name	the name to check
   * @see		#getEnforceCallableNameCheck()
   */
  public boolean isCallableNameUsed(String name) {
    if (!getEnforceCallableNameCheck())
      return false;
    else
      return m_CallableNames.contains(name);
  }

  /**
   * Adds the callable name to the list of used ones.
   * 
   * @param name	the name to add
   * @return		null if successfully added, otherwise error message
   * @see		#getEnforceCallableNameCheck()
   */
  public String addCallableName(String name) {
    if (!getEnforceCallableNameCheck())
      return null;
    
    if (isCallableNameUsed(name))
      return "Callable name '" + name + "' is already used in this scope ('" + getFullName() + "')!";
    
    m_CallableNames.add(name);
    return null;
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

    start  = null;
    finish = null;

    m_FlowExecutionListenerFrame = null;
    if (m_FlowExecutionListeningEnabled) {
      m_FlowExecutionListener.setOwner(this);
      m_FlowExecutionListener.startListening();
      if (!isHeadless())
        m_FlowExecutionListenerFrame = ListenerUtils.createFrame(this);
    }
    
    if (m_Headless) {
      start = new Date();
      System.out.println();
      System.out.println("--> Start: " + DateUtils.getTimestampFormatterMsecs().format(start));
      System.out.println();
    }

    result = super.doExecute();

    if (m_Headless) {
      finish = new Date();
      System.out.println();
      System.out.println("--> Finish: " + DateUtils.getTimestampFormatterMsecs().format(finish));
      System.out.println("--> Duration: " + DateUtils.msecToString(DateUtils.difference(start, finish)) + "\n");
      System.out.println();
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
    if (m_FlowExecutionListenerFrame != null) {
      if (m_FlowExecutionListener instanceof GraphicalFlowExecutionListener) {
	if (((GraphicalFlowExecutionListener) m_FlowExecutionListener).getDisposeOnFinish()) {
	  m_FlowExecutionListenerFrame.dispose();
	  m_FlowExecutionListenerFrame = null;
	}
      }
    }

    super.wrapUp();
  }

  /**
   * Cleans up after the execution has finished. Also removes graphical
   * components.
   */
  @Override
  public void cleanUp() {
    m_LogEntries.clear();
    m_CallableNames.clear();

    if (m_Storage != null) {
      m_Storage.clear();
      m_Storage = null;
    }

    if (m_AfterExecuteActor != null) {
      m_AfterExecuteActor.destroy();
      m_AfterExecuteActor = null;
    }

    if (m_FlowExecutionListenerFrame != null) {
      m_FlowExecutionListenerFrame.dispose();
      m_FlowExecutionListenerFrame = null;
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
