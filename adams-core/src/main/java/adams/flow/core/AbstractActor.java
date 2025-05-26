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
 * AbstractActor.java
 * Copyright (C) 2009-2022 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.core;

import adams.core.ClassLister;
import adams.core.Properties;
import adams.core.SizeOf;
import adams.core.Utils;
import adams.core.Variables;
import adams.core.VariablesHandler;
import adams.core.base.BaseAnnotation;
import adams.core.logging.LoggingHelper;
import adams.core.option.AbstractOptionConsumer;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.ArrayConsumer;
import adams.core.option.OptionUtils;
import adams.core.option.UserMode;
import adams.core.sizeof.ActorFilter;
import adams.db.LogEntry;
import adams.db.MutableLogEntryHandler;
import adams.event.VariableChangeEvent;
import adams.event.VariableChangeEvent.Type;
import adams.flow.container.AbstractContainer;
import adams.flow.control.Flow;
import adams.flow.control.ScopeHandler;
import adams.flow.control.StorageHandler;
import adams.flow.execution.DefaultFlowExecutionListeningSupporter;
import adams.flow.execution.FlowExecutionListeningSupporter;
import adams.gui.core.GUIHelper;
import nz.ac.waikato.cms.locator.ClassLocator;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

/**
 * Abstract base class for actors.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractActor
    extends AbstractOptionHandler
    implements Actor {

  /** for serialization. */
  private static final long serialVersionUID = 6658513163932343273L;

  /** the actor itself. */
  protected Actor m_Self;

  /** the name of the actor. */
  protected String m_Name;

  /** the full name of the actor. */
  protected String m_FullName;

  /** the logging prefix. */
  protected String m_LoggingPrefix;

  /** the parent actor this actor is part of, e.g., a sequence. */
  protected transient Actor m_Parent;

  /** whether the actor was executed at least once after setup. */
  protected boolean m_Executed;

  /** whether the actor is currently being executed. */
  protected boolean m_Executing;

  /** whether the execution was stopped. */
  protected boolean m_Stopped;

  /** the message that was used when stopping the execution. */
  protected String m_StopMessage;

  /** annotations for the actor. */
  protected BaseAnnotation m_Annotations;

  /** whether to skip the transformation and just forward the token. */
  protected boolean m_Skip;

  /** whether to stop the flow in case of an error. */
  protected boolean m_StopFlowOnError;

  /** whether to suppress output in the console. */
  protected boolean m_Silent;

  /** the variable names that are used within this actor. */
  protected HashSet<String> m_DetectedVariables;

  /** the variable names (referencing callable actors or storage) that are used within this actor. */
  protected HashSet<String> m_DetectedObjectVariables;

  /** whether the actor needs re-setting up because of modified variables. */
  protected HashSet<String> m_VariablesUpdated;

  /** for backing up the state of an actor. */
  protected Hashtable<String,Object> m_BackupState;

  /** the storage handler. */
  protected StorageHandler m_StorageHandler;

  /** the scope handler. */
  protected ScopeHandler m_ScopeHandler;

  /** the error handler to use. */
  protected ErrorHandler m_ErrorHandler;

  /** the flow execution handler. */
  protected FlowExecutionListeningSupporter m_ExecutionListeningSupporter;

  /**
   * Returns the additional information.
   *
   * @return		the additional information
   */
  public String getAdditionalInformation() {
    StringBuilder	result;
    boolean 		standalone;
    Class[]		cls;
    int			i;
    List<Class>		containers;
    AbstractContainer	cont;
    Iterator<String>	enm;
    ActorHandlerInfo	info;
    String		name;
    String		help;

    result = new StringBuilder();

    result.append("Flow input/output:");
    standalone = true;
    containers = new ArrayList<Class>();

    if (this instanceof InputConsumer) {
      standalone = false;
      result.append("\n- input: ");
      cls       = ((InputConsumer) this).accepts();
      result.append(Utils.classesToString(cls));
      for (i = 0; i < cls.length; i++) {
	if (ClassLocator.isSubclass(AbstractContainer.class, cls[i]) && !cls[i].equals(AbstractContainer.class))
	  containers.add(cls[i]);
	if (cls[i].isArray() && ClassLocator.isSubclass(AbstractContainer.class, cls[i].getComponentType()) && !cls[i].getComponentType().equals(AbstractContainer.class))
	  containers.add(cls[i].getComponentType());
      }
    }

    if (this instanceof OutputProducer) {
      standalone = false;
      result.append("\n- output: ");
      cls       = ((OutputProducer) this).generates();
      for (i = 0; i < cls.length; i++) {
	if (i > 0)
	  result.append(", ");
	result.append(Utils.classToString(cls[i]));
	if (ClassLocator.isSubclass(AbstractContainer.class, cls[i]) && !cls[i].equals(AbstractContainer.class)) {
	  if (!containers.contains(cls[i]))
	    containers.add(cls[i]);
	}
	if (cls[i].isArray() && ClassLocator.isSubclass(AbstractContainer.class, cls[i].getComponentType()) && !cls[i].getComponentType().equals(AbstractContainer.class)) {
	  if (!containers.contains(cls[i].getComponentType()))
	    containers.add(cls[i].getComponentType());
	}
      }
    }

    if (standalone)
      result.append("\n-standalone-");

    if (!containers.isEmpty()) {
      result.append("\n\nContainer information:");
      for (i = 0; i < containers.size(); i++) {
	result.append("\n- ").append(containers.get(i).getName()).append(": ");
	try {
	  cont  = (AbstractContainer) containers.get(i).getDeclaredConstructor().newInstance();
	  enm   = cont.names();
	  while (enm.hasNext()) {
	    name = enm.next();
	    help = cont.getHelp(name);
	    result.append("\n   - ").append(name).append((help == null) ? "" : ": " + help);
	  }
	}
	catch (Exception e) {
	  result.append("[error]");
	  getLogger().log(Level.SEVERE, "Failed to instantiate container '" + containers.get(i).getName() + "':", e);
	}
      }
    }

    if (this instanceof ActorHandler) {
      info = ((ActorHandler) this).getActorHandlerInfo();
      result.append("\nActor handler information:");
      if (info.getActorExecution() != ActorExecution.UNDEFINED)
	result.append("\n- Actor execution: ").append(info.getActorExecution());
      result.append("\n- Standalones allowed: ").append(info.canContainStandalones());
      result.append("\n- Source allowed: ").append(info.canContainSource());
      result.append("\n- Forwards input: ").append(info.getForwardsInput());
      if (info.hasRestrictions()) {
	result.append("\n- Restrictions: ");
	for (i = 0; i < info.getRestrictions().length; i++) {
	  if (i > 0)
	    result.append(", ");
	  result.append(info.getRestrictions()[i].getName());
	}
      }
    }

    if (this instanceof StopRestrictor) {
      result.append("\nStop restriction: ");
      if (this instanceof OptionalStopRestrictor)
	result.append("optional (currently: ").append(((OptionalStopRestrictor) this).isRestrictingStops() ? "on" : "off").append(")");
      else
	result.append("always");
    }

    if (this instanceof HeadlessExecutionSupporter)
      result.append("\n\nHeadless execution supported: ").append(((HeadlessExecutionSupporter) this).supportsHeadlessExecution() ? "yes" : "no");

    if (this instanceof InteractiveActor) {
      result.append("\n\nInteractive actor information:");
      result.append("\n- supports interaction in headless mode: ").append(((InteractiveActor) this).supportsHeadlessInteraction() ? "yes" : "no");
    }

    return result.toString();
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Parent                  = null;
    m_FullName                = null;
    m_Name                    = "";
    m_DetectedVariables       = null;
    m_DetectedObjectVariables = null;
    m_VariablesUpdated        = new HashSet<String>();
    m_Self                    = this;
    m_LoggingPrefix           = "";
    setErrorHandler(this);

    updatePrefix();
  }

  /**
   * Initializes the logger.
   */
  @Override
  protected void configureLogger() {
    m_Logger = LoggingHelper.getLogger(m_LoggingPrefix);
    m_Logger.setLevel(m_LoggingLevel.getLevel());
  }

  /**
   * Updates the prefix of the logger.
   */
  protected void updatePrefix() {
    String	prefix;
    Actor	root;

    root = getRoot();
    if (root instanceof Flow)
      prefix = getFullName() + "-" + ((Flow) root).getFlowID();
    else
      prefix = getFullName();
    if (prefix.isEmpty())
      prefix = getClass().getName();
    m_LoggingPrefix = prefix;
    m_Logger        = null;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"name", "name",
	getDefaultName());

    m_OptionManager.add(
	"annotation", "annotations",
	new BaseAnnotation(""));

    m_OptionManager.add(
	"skip", "skip",
	false);
    m_OptionManager.disableVariables("skip");

    m_OptionManager.add(
	"stop-flow-on-error", "stopFlowOnError",
	false, UserMode.EXPERT);

    m_OptionManager.add(
	"silent", "silent",
	false, UserMode.EXPERT);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    pruneBackup();
  }

  /**
   * Returns the default name of the actor.
   *
   * @return		the default name
   */
  public String getDefaultName() {
    return getClass().getName().replaceAll(".*\\.", "");
  }

  /**
   * Returns whether the actor is run in headless mode.
   *
   * @return		true if GUI components are suppressed
   */
  public boolean isHeadless() {
    if ((getRoot() != null) && !(getRoot() == this))
      return getRoot().isHeadless();
    else
      return GUIHelper.isHeadless();
  }

  /**
   * Sets the name of the actor.
   *
   * @param value 	the name
   */
  public void setName(String value) {
    m_Name = value;
    reset();
  }

  /**
   * Returns the name of the actor.
   *
   * @return 		the name
   */
  public String getName() {
    return m_Name;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String nameTipText() {
    return "The name of the actor.";
  }

  /**
   * Sets the annoations.
   *
   * @param value	the annotations
   */
  public void setAnnotations(BaseAnnotation value) {
    m_Annotations = value;
    reset();
  }

  /**
   * Returns the current annotations.
   *
   * @return		the annotations
   */
  public BaseAnnotation getAnnotations() {
    return m_Annotations;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String annotationsTipText() {
    return "The annotations to attach to this actor.";
  }

  /**
   * Sets whether the transformation is skipped or not.
   *
   * @param value 	true if transformation is to be skipped
   */
  public void setSkip(boolean value) {
    m_Skip = value;
    reset();
  }

  /**
   * Returns whether transformation is skipped.
   *
   * @return 		true if transformation is skipped
   */
  public boolean getSkip() {
    return m_Skip;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String skipTipText() {
    return "If set to true, transformation is skipped and the input token is just forwarded as it is.";
  }

  /**
   * Sets whether to stop the flow execution at this level in case this actor encounters an error.
   *
   * @param value 	true if flow gets stopped in case of an error
   */
  public void setStopFlowOnError(boolean value) {
    m_StopFlowOnError = value;
    reset();
  }

  /**
   * Returns whether to stop the flow execution at this level in case this actor encounters an error.
   *
   * @return 		true if flow gets stopped in case of an error
   */
  public boolean getStopFlowOnError() {
    return m_StopFlowOnError;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String stopFlowOnErrorTipText() {
    return "If set to true, the flow execution at this level gets stopped in case this actor encounters an error; the error gets propagated; useful for critical actors.";
  }

  /**
   * Sets whether to suppress output in the console.
   * Note: the enclosing actor handler needs to have this enabled as well.
   *
   * @param value 	true if to suppress output in the console
   */
  public void setSilent(boolean value) {
    m_Silent = value;
    reset();
  }

  /**
   * Returns whether to suppress output in the console.
   * Note: the enclosing actor handler needs to have this enabled as well.
   *
   * @return 		true if to suppress output in the console
   */
  public boolean getSilent() {
    return m_Silent;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String silentTipText() {
    return "If enabled, then no errors are output in the console; Note: the enclosing actor handler must have this enabled as well.";
  }

  /**
   * Handles the given error message with the flow that this actor belongs to,
   * if the flow has error logging turned on. Might stop the flow as well.
   *
   * @param source	the source of the error
   * @param type	the type of error
   * @param msg		the error message to log
   * @return		null if error has been handled, otherwise the error message
   * @see		Flow#getLogErrors()
   * @see		Flow#getErrorHandling()
   * @see		#getStopFlowOnError()
   */
  public String handleError(Actor source, String type, String msg) {
    Flow			flow;
    LogEntry			entry;
    MutableLogEntryHandler	handler;
    Properties			props;
    boolean			stop;

    flow    = null;
    handler = null;
    entry   = null;
    props   = null;

    // add log entry?
    if (source.getRoot() instanceof Flow) {
      flow = (Flow) source.getRoot();
      if (flow.getLogErrors())
	entry = new LogEntry();
    }
    else if (source.getRoot() instanceof MutableLogEntryHandler) {
      entry = new LogEntry();
    }
    if (entry != null) {
      handler = (MutableLogEntryHandler) source.getRoot();
      props   = new Properties();
      props.setProperty("Message", msg);
      entry.setGeneration(new Date());
      entry.setSource(getFullName());
      entry.setType(type);
      entry.setStatus(LogEntry.STATUS_NEW);
      entry.setMessage(props);
      handler.addLogEntry(entry);
    }

    // stop execution?
    msg = source.getFullName() + ": " + msg;
    if (flow != null) {
      switch (flow.getErrorHandling()) {
	case ACTORS_ALWAYS_STOP_ON_ERROR:
	  flow.stopExecution(msg);
	  msg = null;
	  break;

	case ACTORS_DECIDE_TO_STOP_ON_ERROR:
	  stop = false;
	  if (getStopFlowOnError())
	    stop = true;
	  if ((source instanceof InteractiveActor) && ((InteractiveActor) source).getStopFlowIfCanceled())
	    stop = true;
	  if (source instanceof OptionalPasswordPrompt)
	    stop = true;
	  if (stop)
	    flow.stopExecution(msg);
	  else if (!getSilent())
	    source.getLogger().warning(msg);
	  msg = null;
	  break;

	default:
	  throw new IllegalStateException("Unhandled error handling: " + flow.getErrorHandling());
      }
    }
    else {
      if (getStopFlowOnError()) {
	source.getRoot().stopExecution(msg);
	msg = null;
      }
      else if (!getSilent()) {
	source.getLogger().warning(msg);
      }
    }

    return msg;
  }

  /**
   * Outputs the stacktrace along with the message on stderr and returns a
   * combination of both of them as string.
   *
   * @param msg		the message for the exception
   * @param t		the exception
   * @return		the full error message (message + stacktrace)
   */
  protected String handleException(String msg, Throwable t) {
    return LoggingHelper.handleException(this, msg, t, m_Silent);
  }

  /**
   * Sets the parent of this actor, e.g., the group it belongs to.
   *
   * @param value	the new parent
   */
  @Override
  public void setParent(Actor value) {
    if ((value != m_Parent) || (value == null)) {
      m_Parent                      = value;
      m_FullName                    = null;
      m_StorageHandler              = null;
      m_ScopeHandler                = null;
      m_ExecutionListeningSupporter = null;
      updatePrefix();
    }
  }

  /**
   * Returns the parent of this actor, e.g., the group.
   *
   * @return		the current parent, can be null
   */
  public Actor getParent() {
    return m_Parent;
  }

  /**
   * Returns the current parent component for interactive actors.
   *
   * @return		the parent, null if not set
   */
  public Component getParentComponent() {
    if (getRoot() != null)
      return getRoot().getParentComponent();
    else
      return null;
  }

  /**
   * Returns the index of this actor in its parent's collection.
   *
   * @return		the index, -1 if not applicable (e.g., no parent set)
   */
  public int index() {
    int		result;

    result = -1;

    if ((m_Parent != null) && (m_Parent instanceof ActorHandler))
      result = ((ActorHandler) m_Parent).indexOf(getName());

    return result;
  }

  /**
   * Returns the root of this actor, e.g., the group at the highest level.
   *
   * @return		the root, can be null
   */
  public Actor getRoot() {
    if (getParent() == null)
      return this;
    else
      return getParent().getRoot();
  }

  /**
   * Updates the detected variables.
   */
  protected void updateDetectedVariables() {
    getOptionManager().registerVariables();
    m_DetectedVariables = findVariables();

    // split off callable refs/storage refs
    m_DetectedObjectVariables = new HashSet<String>();
    for (String var: m_DetectedVariables) {
      if (getVariables().isObject(var))
	m_DetectedObjectVariables.add(var);
    }
    m_DetectedVariables.removeAll(m_DetectedObjectVariables);

    if (m_DetectedVariables.size() > 0)
      getVariables().addVariableChangeListener(this);
  }

  /**
   * Updates the Variables instance in use.
   * <br><br>
   * Use with caution!
   *
   * @param value	the instance to use
   */
  protected void forceVariables(Variables value) {
    getOptionManager().getVariables().removeVariableChangeListener(this);
    getOptionManager().deregisterVariables();
    getOptionManager().setVariables(value);
    getOptionManager().getVariables().addVariableChangeListener(this);
    getOptionManager().updateVariablesInstance(value);
    updateDetectedVariables();
  }

  /**
   * Updates the Variables instance in use, if different from current one.
   * <br><br>
   * Use with caution!
   *
   * @param value	the instance to use
   * @see		#forceVariables(Variables)
   */
  public synchronized void setVariables(Variables value) {
    if (getVariables() != value)
      forceVariables(value);
  }

  /**
   * Returns the Variables instance to use.
   *
   * @return		the variables instance
   */
  public Variables getVariables() {
    if (this instanceof VariablesHandler)
      return ((VariablesHandler) this).getLocalVariables();
    else
      return getOptionManager().getVariables();
  }

  /**
   * Returns the storage handler to use.
   *
   * @return		the storage handler
   */
  public StorageHandler getStorageHandler() {
    if (m_StorageHandler == null) {
      if (this instanceof StorageHandler)
	m_StorageHandler = (StorageHandler) this;
      else if (getParent() != null)
	m_StorageHandler = getParent().getStorageHandler();
    }

    return m_StorageHandler;
  }

  /**
   * Returns the scope handler for this actor.
   *
   * @return		the scope handler
   */
  public ScopeHandler getScopeHandler() {
    if (m_ScopeHandler == null) {
      if (this instanceof ScopeHandler)
	m_ScopeHandler = (ScopeHandler) this;
      else if (getParent() != null)
	m_ScopeHandler = getParent().getScopeHandler();
    }

    return m_ScopeHandler;
  }

  /**
   * Returns the flow execution handler in use.
   *
   * @return		the execution handler
   */
  public FlowExecutionListeningSupporter getFlowExecutionListeningSupporter() {
    if (m_ExecutionListeningSupporter == null) {
      if (this instanceof FlowExecutionListeningSupporter)
	m_ExecutionListeningSupporter = (FlowExecutionListeningSupporter) this;
      else if (getParent() != null)
	m_ExecutionListeningSupporter = getParent().getFlowExecutionListeningSupporter();
      // nothing found and root actor? -> set default supporter
      if ((m_ExecutionListeningSupporter == null) && (this == getRoot()))
	m_ExecutionListeningSupporter = new DefaultFlowExecutionListeningSupporter();
    }

    return m_ExecutionListeningSupporter;
  }

  /**
   * Returns the full name of the actor, i.e., the concatenated names of all
   * parents. Used in error messages.
   *
   * @return		the full name
   */
  public String getFullName() {
    StringBuilder	result;
    Actor		parent;

    if (m_FullName == null) {
      result = new StringBuilder(getName().replace(".", "\\."));
      parent = getParent();
      if (parent != null) {
	if (!parent.getFullName().isEmpty())
	  result.insert(0, parent.getFullName() + ".");
	else
	  result.insert(0, parent.getName().replace(".", "\\.") + ".");
      }
      m_FullName = result.toString();
    }

    return m_FullName;
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   * <br><br>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return null;
  }

  /**
   * Checks if an error handler is set.
   *
   * @return		true if an error handler is set
   */
  public boolean hasErrorHandler() {
    return (m_ErrorHandler != null);
  }

  /**
   * Sets the error handler to use for handling errors in the flow.
   *
   * @param value	the error handler
   */
  public void setErrorHandler(ErrorHandler value) {
    m_ErrorHandler = value;
  }

  /**
   * Returns the current error handler for handling errors in the flow.
   *
   * @return		the error handler
   */
  public ErrorHandler getErrorHandler() {
    return m_ErrorHandler;
  }

  /**
   * Checks whether the class' options can be inspected.
   * <br><br>
   * Default implementation returns true.
   *
   * @param cls		the class to check
   * @return		true if it can be inspected, false otherwise
   */
  public boolean canInspectOptions(Class cls) {
    return true;
  }

  /**
   * Recursively finds all the variables used in the actor's setup.
   *
   * @return		the variables that were found
   */
  public HashSet<String> findVariables() {
    return findVariables(this);
  }

  /**
   * Recursively finds all the variables used in the specified actor's setup.
   *
   * @param actor	the actor to search
   * @return		the variables that were found
   */
  protected HashSet<String> findVariables(Actor actor) {
    ActorVariablesFinder	finder;
    HashSet<String>		result;

    getLogger().finest("Locating variables in " + actor.getFullName() + "...");

    finder = new ActorVariablesFinder();
    finder.setInspection(actor);
    actor.getOptionManager().traverse(finder);
    result = finder.getResult();

    getLogger().finest("Found variables in " + actor.getFullName() + " (" + result.size() + "): " + result);

    return result;
  }

  /**
   * Gets triggered when a variable changed (added, modified, removed).
   *
   * @param e		the event
   */
  public void variableChanged(VariableChangeEvent e) {
    if ((m_DetectedVariables == null) || (m_DetectedVariables.size() == 0))
      return;
    if (m_VariablesUpdated.contains(e.getName()))
      return;

    if (m_DetectedVariables.contains(e.getName()) && (e.getType() != Type.REMOVED)) {
      m_VariablesUpdated.add(e.getName());
      getLogger().info("Changes in variable '" + e.getName() + "' (" + e.getVariables().hashCode() + ")");
    }
  }

  /**
   * Returns the variables this actor is responsible for.
   *
   * @return		the variables
   */
  public HashSet<String> getDetectedVariables() {
    HashSet<String>	result;

    if (m_DetectedVariables != null)
      result = m_DetectedVariables;
    else
      result = new HashSet<String>();

    return result;
  }

  /**
   * Initializes the item for flow execution. Also calls the reset() method
   * first before anything else.
   *
   * @return		null if everything is fine, otherwise error message
   * @see		#reset()
   */
  public String setUp() {
    String	result;

    reset();

    m_FullName = null;
    updatePrefix();

    updateDetectedVariables();

    m_Stopped     = false;
    m_StopMessage = null;
    m_Executed    = false;

    result = performSetUpChecks(true);

    return result;
  }

  /**
   * Checks whether a specified key is present in the current backup state.
   *
   * @param key		the key of the object to look for in the backup state
   * @return		true if key present
   */
  protected boolean isBackedUp(String key) {
    if (m_BackupState == null)
      return false;
    else
      return m_BackupState.containsKey(key);
  }

  /**
   * Removes the object with the specified key from the current backup state.
   *
   * @param key		the key of the object to remove from the backup state
   */
  protected void pruneBackup(String key) {
    if (m_BackupState == null)
      return;
    if (!m_BackupState.containsKey(key))
      return;
    m_BackupState.remove(key);
  }

  /**
   * Removes entries from the backup.
   * <br><br>
   * Default implementation does nothing.
   *
   * @see		#reset()
   */
  protected void pruneBackup() {
  }

  /**
   * Backs up the current state of the actor before update the variables.
   * <br><br>
   * Default implementation only returns an empty hashtable.
   *
   * @return		the backup
   * @see		#updateVariables()
   * @see		#restoreState(Hashtable)
   */
  protected Hashtable<String,Object> backupState() {
    return new Hashtable<String,Object>();
  }

  /**
   * Restores the state of the actor before the variables got updated.
   * <br><br>
   * Default implementation does nothing.
   *
   * @param state	the backup of the state to restore from
   * @see		#updateVariables()
   * @see		#backupState()
   */
  protected void restoreState(Hashtable<String,Object> state) {
  }

  /**
   * Gets called when the actor needs to be re-setUp when a variable changes.
   *
   * @return		null if everything is fine, otherwise error message
   */
  protected String updateVariables() {
    String		result;

    if (isLoggingEnabled()) {
      getLogger().info(
	  "Attempting updating variables (" + getOptionManager().getVariables().hashCode() + "): "
	      + m_VariablesUpdated + "/" + m_DetectedObjectVariables);
    }

    // obtain the new value(s)
    m_BackupState = backupState();
    getOptionManager().updateVariableValues(false, isLoggingEnabled() ? getLogger() : null);

    // re-initialize the actor
    result = setUp();
    if (result == null)
      restoreState(m_BackupState);

    if (isLoggingEnabled()) {
      getLogger().info(
	  "Finished updating variables " + m_VariablesUpdated + "/" + m_DetectedObjectVariables + ": "
	      + ((result == null) ? "successful" : result));
    }

    m_BackupState = null;

    m_VariablesUpdated.clear();

    return result;
  }

  /**
   * Returns whether a check can be performed currently. Depending on whether
   * a variable is attached to a property the property cannot be checked at
   * setUp() time, but needs to be done at preExecute() time.
   *
   * @param fromSetUp	whether the method has been called from within setUp()
   * @param property	the property to check
   * @return		true if the check can be performed
   */
  protected boolean canPerformSetUpCheck(boolean fromSetUp, String property) {
    boolean	result;
    String	variable;

    result   = true;
    variable = getOptionManager().getVariableForProperty(property);

    if (fromSetUp) {
      // needs to be deferred into preExecute()
      if (variable != null)
	result = false;
    }
    else {
      // should be done in setUp()
      if (variable == null)
	result = false;
    }

    return result;
  }

  /**
   * Hook for performing setup checks -- used in setUp() and preExecute().
   * <br><br>
   * Default implementation performs no checks.
   *
   * @param fromSetUp	whether the method has been called from within setUp()
   * @return		null if everything OK, otherwise error message
   * @see		#setUp()
   * @see		#preExecute()
   */
  protected String performSetUpChecks(boolean fromSetUp) {
    return null;
  }

  /**
   * For performing variable checks.
   *
   * @return		null if checks passed, otherwise error messages
   * @see		#preExecute()
   */
  protected String performVariableChecks() {
    String  result;

    result = ActorUtils.ensureValidVariables(this);
    if ((result != null) && ActorUtils.checkInvalidVariablesLenient()) {
      getLogger().warning(result);
      result = null;
    }

    return result;
  }

  /**
   * Pre-execute hook.
   * <br><br>
   * Default implementation checks only whether the actor needs to be setup
   * again due to changes in variables.
   *
   * @return		null if everything is fine, otherwise error message
   */
  protected String preExecute() {
    String	result;

    result = null;

    if (LoggingHelper.isAtLeast(getLogger(), Level.FINEST))
      getLogger().finest("Size before execute: " + sizeOf());

    // do we need to re-setup the actor, due to changes in variables?
    if ((m_VariablesUpdated.size() > 0)
	|| ((m_DetectedVariables != null) && (m_DetectedObjectVariables.size() > 0))) {
      updateVariables();
      result = performSetUpChecks(false);
    }

    if (!isExecuted() && (result == null))
      result = performVariableChecks();

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  protected abstract String doExecute();

  /**
   * Post-execute hook.
   * <br><br>
   * Default implementation does nothing apart from setting m_Executed to true
   * and only returns null.
   *
   * @return		null if everything is fine, otherwise error message
   * @see		#m_Executed
   */
  protected String postExecute() {
    if (LoggingHelper.isAtLeast(getLogger(), Level.FINEST))
      getLogger().finest("Size after execute: " + sizeOf());

    m_Executed = !isStopped();

    return null;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  public String execute() {
    String	result;

    result = null;

    if (!m_Skip) {
      m_Executing = true;
      if (!isStopped())
	result = preExecute();
    }
    if (!m_Skip) {
      if ((result == null) && !isStopped())
	result = doExecute();
      if ((result == null) && !isStopped())
	result = postExecute();
      m_Executing = false;
    }

    return result;
  }

  /**
   * Returns whether the item has finished. The <code>execute()</code> will be
   * called as long as the <code>isFinished()</code> method returns false.
   *
   * @return		true if finished, false if further calls to execute()
   * 			are necessary. Default implementation returns always
   * 			true, i.e., fires only once.
   */
  public boolean isFinished() {
    return true;
  }

  /**
   * Gets called when the actor needs to re-evaluate variables before wrapping up.
   */
  protected void finalUpdateVariables() {
    if (isLoggingEnabled()) {
      getLogger().info(
	  "Attempting final update of variables (" + getOptionManager().getVariables().hashCode() + "): "
	      + m_VariablesUpdated + "/" + m_DetectedObjectVariables);
    }

    // obtain the new value(s)
    getOptionManager().updateVariableValues();

    m_VariablesUpdated.clear();
  }

  /**
   * Cleans up after the execution has finished. Graphical output is left
   * untouched.
   */
  public void wrapUp() {
    if (    (m_VariablesUpdated.size() > 0)
	|| ((m_DetectedVariables != null) && (m_DetectedObjectVariables.size() > 0))) {
      finalUpdateVariables();
    }

    getOptionManager().deregisterVariables();
    m_ErrorHandler = null;
    if (m_DetectedVariables != null) {
      m_DetectedVariables.clear();
      m_DetectedVariables = null;
    }
    if (m_DetectedObjectVariables != null) {
      m_DetectedObjectVariables.clear();
      m_DetectedObjectVariables = null;
    }
  }

  /**
   * Cleans up after the execution has finished. Also removes graphical
   * components.
   */
  public void cleanUp() {
  }

  /**
   * Frees up memory in a "destructive" non-reversible way.
   * <br><br>
   * Calls cleanUp().
   */
  @Override
  public void destroy() {
    cleanUp();

    super.destroy();
  }

  /**
   * Stops the execution. No message set.
   */
  public void stopExecution() {
    m_Stopped = true;
  }

  /**
   * Stops the execution.
   *
   * @param msg		the message to set as reason for stopping, can be null
   */
  public void stopExecution(String msg) {
    m_StopMessage = msg;
    stopExecution();
  }

  /**
   * Returns whether the execution was stopped.
   *
   * @return		true if the execution was stopped
   */
  public boolean isStopped() {
    return m_Stopped;
  }

  /**
   * Returns whether a stop message is available (in case the flow was stopped
   * with a message).
   *
   * @return		true if a message is available
   */
  public boolean hasStopMessage() {
    return (m_StopMessage != null);
  }

  /**
   * Returns the stop message.
   *
   * @return		the message, can be null
   */
  public String getStopMessage() {
    return m_StopMessage;
  }

  /**
   * Returns whether the actor has been executed, after setting it up.
   *
   * @return		true if the actor has been executed
   */
  public boolean isExecuted() {
    return m_Executed;
  }

  /**
   * Returns whether the actor is currently being executed, i.e., processing
   * some data or similar.
   *
   * @return		true if currently active
   */
  public boolean isExecuting() {
    return m_Executing;
  }

  /**
   * If the actor is part of a group, this method returns the actor
   * preceding it in that group.
   *
   * @return		the preceding actor, null if not available
   */
  public Actor getPreviousSibling() {
    Actor	result;
    int		index;

    result = null;

    if (getParent() instanceof ActorHandler) {
      index = ((ActorHandler) getParent()).indexOf(getName());
      if (index > 0)
	result = ((ActorHandler) getParent()).get(index - 1);
    }

    return result;
  }

  /**
   * If the actor is part of a group, this method returns the actor
   * following it in that group.
   *
   * @return		the following actor, null if not available
   */
  public Actor getNextSibling() {
    Actor	result;
    int			index;

    result = null;

    if (getParent() instanceof ActorHandler) {
      index = ((ActorHandler) getParent()).indexOf(getName());
      if (index < ((ActorHandler) getParent()).size() - 1)
	result = ((ActorHandler) getParent()).get(index + 1);
    }

    return result;
  }

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   * <br><br>
   * Only compares the commandlines of the two objects.
   *
   * @param o 	the object to be compared.
   * @return  	a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   *
   * @throws ClassCastException 	if the specified object's type prevents it
   *         				from being compared to this object.
   */
  public int compareTo(Object o) {
    if (o == null)
      return 1;

    return OptionUtils.getCommandLine(this).compareTo(OptionUtils.getCommandLine(o));
  }

  /**
   * Returns whether the two objects are the same.
   * <br><br>
   * Only compares the commandlines of the two objects.
   *
   * @param o	the object to be compared
   * @return	true if the object is the same as this one
   */
  @Override
  public boolean equals(Object o) {
    return (compareTo(o) == 0);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @return		the shallow copy
   */
  public Actor shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public Actor shallowCopy(boolean expand) {
    return (Actor) OptionUtils.shallowCopy(this, expand);
  }

  /**
   * Returns the size of the object.
   *
   * @return		the size of the object
   */
  @Override
  public synchronized int sizeOf() {
    int		result;
    Actor	parent;

    parent   = m_Parent;
    m_Parent = null;
    result   = SizeOf.sizeOf(this, new ActorFilter());
    m_Parent = parent;

    return result;
  }

  /**
   * Returns a list with classnames of items.
   *
   * @return		the item classnames
   */
  public static String[] getFlowActors() {
    return ClassLister.getSingleton().getClassnames(Actor.class);
  }

  /**
   * Instantiates the item with the given options.
   *
   * @param classname	the classname of the item to instantiate
   * @param options	the options for the item
   * @return		the instantiated item or null if an error occurred
   */
  public static Actor forName(String classname, String[] options) {
    return forName(classname, options, false);
  }

  /**
   * Instantiates the item with the given options.
   *
   * @param classname	the classname of the item to instantiate
   * @param options	the options for the item
   * @param quiet	whether to suppress error messages
   * @return		the instantiated item or null if an error occurred
   */
  public static Actor forName(String classname, String[] options, boolean quiet) {
    Actor	result;

    try {
      result = (Actor) OptionUtils.forName(Actor.class, classname, options);
    }
    catch (Exception e) {
      if (!quiet)
	e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Instantiates the item from the given commandline
   * (i.e., classname and optional options).
   *
   * @param cmdline	the classname (and optional options) of the
   * 			item to instantiate
   * @return		the instantiated item
   * 			or null if an error occurred
   */
  public static Actor forCommandLine(String cmdline) {
    return (Actor) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
  }
}
