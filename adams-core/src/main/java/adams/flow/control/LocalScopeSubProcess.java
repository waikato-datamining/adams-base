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
 * LocalScopeTransformer.java
 * Copyright (C) 2014-2023 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import adams.core.QuickInfoHelper;
import adams.core.Variables;
import adams.core.VariablesHandler;
import adams.core.base.BaseRegExp;
import adams.core.io.PlaceholderFile;
import adams.core.logging.LoggingLevel;
import adams.flow.core.Actor;
import adams.flow.core.ActorHandler;
import adams.flow.core.ActorHandlerInfo;
import adams.flow.core.ActorUtils;
import adams.flow.core.CallableNamesRecorder;
import adams.flow.core.FlowVariables;
import adams.flow.core.InputConsumer;
import adams.flow.core.MutableActorHandler;
import adams.flow.core.OutputProducer;
import adams.flow.core.StopRestrictor;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

/**
 <!-- globalinfo-start -->
 * Provides a local scope for the sub-actors.<br>
 * It is possible to 'propagate' or 'leak' variables and storage items from within the local scope back to the output scope. However, storage items from caches cannot be propagated.
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
 * &nbsp;&nbsp;&nbsp;default: LocalScopeTransformer
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
 * <pre>-actor &lt;adams.flow.core.Actor&gt; [-actor ...] (property: actors)
 * &nbsp;&nbsp;&nbsp;The actors to execute in the loop.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-scope-handling-variables &lt;EMPTY|COPY|SHARE&gt; (property: scopeHandlingVariables)
 * &nbsp;&nbsp;&nbsp;Defines how variables are handled in the local scope; whether to start with
 * &nbsp;&nbsp;&nbsp;empty set, a copy of the outer scope variables or share variables with the
 * &nbsp;&nbsp;&nbsp;outer scope.
 * &nbsp;&nbsp;&nbsp;default: EMPTY
 * </pre>
 *
 * <pre>-variables-filter &lt;adams.core.base.BaseRegExp&gt; (property: variablesFilter)
 * &nbsp;&nbsp;&nbsp;The regular expression that variable names must match in order to get into
 * &nbsp;&nbsp;&nbsp;the local scope (when using COPY).
 * &nbsp;&nbsp;&nbsp;default: .*
 * </pre>
 *
 * <pre>-propagate-variables &lt;boolean&gt; (property: propagateVariables)
 * &nbsp;&nbsp;&nbsp;If enabled and variables are not shared with outer scope, variables that
 * &nbsp;&nbsp;&nbsp;match the specified regular expression get propagated to the outer scope.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-variables-regexp &lt;adams.core.base.BaseRegExp&gt; (property: variablesRegExp)
 * &nbsp;&nbsp;&nbsp;The regular expression that variable names must match in order to get propagated.
 * &nbsp;&nbsp;&nbsp;default: .*
 * </pre>
 *
 * <pre>-scope-handling-storage &lt;EMPTY|COPY|SHARE&gt; (property: scopeHandlingStorage)
 * &nbsp;&nbsp;&nbsp;Defines how storage is handled in the local scope; whether to start with
 * &nbsp;&nbsp;&nbsp;empty set, a (deep) copy of the outer scope storage or share the storage
 * &nbsp;&nbsp;&nbsp;with the outer scope.
 * &nbsp;&nbsp;&nbsp;default: EMPTY
 * </pre>
 *
 * <pre>-storage-filter &lt;adams.core.base.BaseRegExp&gt; (property: storageFilter)
 * &nbsp;&nbsp;&nbsp;The regular expression that storage item names must match in order to get
 * &nbsp;&nbsp;&nbsp;into the local scope (when using COPY).
 * &nbsp;&nbsp;&nbsp;default: .*
 * </pre>
 *
 * <pre>-propagate-storage &lt;boolean&gt; (property: propagateStorage)
 * &nbsp;&nbsp;&nbsp;If enabled and storage is not shared with outer scope, storage items which
 * &nbsp;&nbsp;&nbsp;names match the specified regular expression get propagated to the outer
 * &nbsp;&nbsp;&nbsp;scope.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-storage-regexp &lt;adams.core.base.BaseRegExp&gt; (property: storageRegExp)
 * &nbsp;&nbsp;&nbsp;The regular expression that the names of storage items must match in order
 * &nbsp;&nbsp;&nbsp;to get propagated.
 * &nbsp;&nbsp;&nbsp;default: .*
 * </pre>
 *
 * <pre>-finish-before-stopping &lt;boolean&gt; (property: finishBeforeStopping)
 * &nbsp;&nbsp;&nbsp;If enabled, actor first finishes processing all data before stopping.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class LocalScopeSubProcess
  extends AbstractControlActor
  implements InputConsumer, OutputProducer, MutableActorHandler,
	       VariablesHandler, StorageHandler, LocalScopeHandler, ProgrammaticLocalScope, StopRestrictor, AtomicExecution {

  /** for serialization. */
  private static final long serialVersionUID = -2837014912083918343L;

  /** the actors to execute. */
  protected SubProcess m_Actors;

  /** the storage for temporary data. */
  protected transient Storage m_LocalStorage;

  /** the variables manager. */
  protected FlowVariables m_LocalVariables;

  /** whether the local storage was programmatically set. */
  protected boolean m_ProgrammaticLocalStorage;

  /** whether the local vars were programmatically set. */
  protected boolean m_ProgrammaticLocalVariables;

  /** the callable names. */
  protected CallableNamesRecorder m_CallableNames;

  /** whether the callable name check is enforced. */
  protected boolean m_EnforceCallableNameCheck;

  /** how to handle the variables. */
  protected ScopeHandling m_ScopeHandlingVariables;

  /** the regular expression of the variables to allow into the local scope. */
  protected BaseRegExp m_VariablesFilter;

  /** how to handle the storage. */
  protected ScopeHandling m_ScopeHandlingStorage;

  /** the regular expression of the storage items to allow into the local scope. */
  protected BaseRegExp m_StorageFilter;

  /** whether to propagate variables from the local scope to the outer scope. */
  protected boolean m_PropagateVariables;

  /** the regular expression of the variables to propagate. */
  protected BaseRegExp m_VariablesRegExp;

  /** whether to propagate variables from the local scope to the outer scope. */
  protected boolean m_PropagateStorage;

  /** the regular expression of the variables to propagate. */
  protected BaseRegExp m_StorageRegExp;

  /** whether a restricted stop occurred. */
  protected boolean m_RestrictedStop;

  /**
   * Default constructor.
   */
  public LocalScopeSubProcess() {
    super();
  }

  /**
   * Initializes with the specified name.
   *
   * @param name      the name to use
   */
  public LocalScopeSubProcess(String name) {
    this();
    setName(name);
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Provides a local scope for the sub-actors.\n"
	+ "It is possible to 'propagate' or 'leak' variables and storage items "
	+ "from within the local scope back to the output scope. However, "
	+ "storage items from caches cannot be propagated.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "actor", "actors",
      new Actor[0]);

    m_OptionManager.add(
      "scope-handling-variables", "scopeHandlingVariables",
      ScopeHandling.EMPTY);

    m_OptionManager.add(
      "variables-filter", "variablesFilter",
      new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
      "propagate-variables", "propagateVariables",
      false);

    m_OptionManager.add(
      "variables-regexp", "variablesRegExp",
      new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
      "scope-handling-storage", "scopeHandlingStorage",
      ScopeHandling.EMPTY);

    m_OptionManager.add(
      "storage-filter", "storageFilter",
      new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
      "propagate-storage", "propagateStorage",
      false);

    m_OptionManager.add(
      "storage-regexp", "storageRegExp",
      new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
      "finish-before-stopping", "finishBeforeStopping",
      false);

    m_OptionManager.add(
      "stopping-timeout", "stoppingTimeout",
      -1, -1, null);

    m_OptionManager.add(
      "stopping-warning-interval", "stoppingWarningInterval",
      10000, -1, null);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Actors = new SubProcess();
    m_Actors.setAllowSource(false);
    m_Actors.setAllowStandalones(false);
    m_Actors.setAllowEmpty(true);
    m_Actors.setRestrictingStops(true);

    m_CallableNames            = new CallableNamesRecorder();
    m_EnforceCallableNameCheck = true;
  }

  /**
   * Resets the actor.
   */
  @Override
  protected void reset() {
    super.reset();
    m_CallableNames.clear();
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String		result;

    result  = "variables [";
    result += QuickInfoHelper.toString(this, "scopeHandlingVariables", m_ScopeHandlingVariables, "scope: ");
    result += QuickInfoHelper.toString(this, "variablesFilter", !m_VariablesFilter.isMatchAll(), "filter: " + m_VariablesFilter, ", ");
    result += QuickInfoHelper.toString(this, "propagateVariables", getPropagateVariables(), "propagate", ", ");
    if (getPropagateVariables())
      result += QuickInfoHelper.toString(this, "variablesRegExp", getVariablesRegExp(), ", regexp: ");
    result += "]";

    result += ", storage [";
    result += QuickInfoHelper.toString(this, "scopeHandlingStorage", m_ScopeHandlingStorage, "scope: ");
    result += QuickInfoHelper.toString(this, "storageFilter", !m_StorageFilter.isMatchAll(), "filter: " + m_StorageFilter, ", ");
    result += QuickInfoHelper.toString(this, "propagateStorage", getPropagateStorage(), "propagate", ", ");
    if (getPropagateStorage())
      result += QuickInfoHelper.toString(this, "storageRegExp", getStorageRegExp(), ", regexp: ");
    result += "]";

    return result;
  }

  /**
   * Sets the logging level.
   *
   * @param value 	the level
   */
  @Override
  public void setLoggingLevel(LoggingLevel value) {
    super.setLoggingLevel(value);
    m_Actors.setLoggingLevel(value);
  }

  /**
   * Sets whether the transformation is skipped or not.
   *
   * @param value 	true if transformation is to be skipped
   */
  @Override
  public void setSkip(boolean value) {
    super.setSkip(value);
    m_Actors.setSkip(value);
  }

  /**
   * Sets the actors of the loop.
   *
   * @param value	the actors
   */
  public void setActors(Actor[] value) {
    m_Actors.setActors(value);
    reset();
    updateParent();
  }

  /**
   * Returns the actors of the loop.
   *
   * @return		the actors
   */
  public Actor[] getActors() {
    return m_Actors.getActors();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String actorsTipText() {
    return "The actors to execute in the loop.";
  }

  /**
   * Sets how to handle variables into the local scope.
   *
   * @param value	the scope handling
   */
  public void setScopeHandlingVariables(ScopeHandling value) {
    m_ScopeHandlingVariables = value;
    reset();
  }

  /**
   * Returns how variables are handled in the local scope.
   *
   * @return		the scope handling
   */
  public ScopeHandling getScopeHandlingVariables() {
    return m_ScopeHandlingVariables;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String scopeHandlingVariablesTipText() {
    return
      "Defines how variables are handled in the local scope; whether to "
	+ "start with empty set, a copy of the outer scope variables or "
	+ "share variables with the outer scope.";
  }

  /**
   * Sets the regular expression that variable names must match to get
   * into the local scope.
   *
   * @param value	the expression
   */
  public void setVariablesFilter(BaseRegExp value) {
    m_VariablesFilter = value;
    reset();
  }

  /**
   * Returns the regular expression that variable names must match to get
   * into the local scope.
   *
   * @return		the expression
   */
  public BaseRegExp getVariablesFilter() {
    return m_VariablesFilter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String variablesFilterTipText() {
    return
      "The regular expression that variable names must match in order to "
	+ "get into the local scope (when using " + ScopeHandling.COPY + ").";
  }

  /**
   * Sets whether to propagate variables from the local to the outer scope.
   *
   * @param value	if true then variables get propagated
   */
  public void setPropagateVariables(boolean value) {
    m_PropagateVariables = value;
    reset();
  }

  /**
   * Returns whether to propagate variables from the local to the outer scope.
   *
   * @return		true if variables get propagated
   */
  public boolean getPropagateVariables() {
    return m_PropagateVariables;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String propagateVariablesTipText() {
    return
      "If enabled and variables are not shared with outer scope, variables "
	+ "that match the specified regular expression get propagated to the "
	+ "outer scope.";
  }

  /**
   * Sets the regular expression that variable names must match to get
   * propagated.
   *
   * @param value	the expression
   */
  public void setVariablesRegExp(BaseRegExp value) {
    m_VariablesRegExp = value;
    reset();
  }

  /**
   * Returns the regular expression that variable names must match to get
   * propagated.
   *
   * @return		the expression
   */
  public BaseRegExp getVariablesRegExp() {
    return m_VariablesRegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String variablesRegExpTipText() {
    return "The regular expression that variable names must match in order to get propagated.";
  }

  /**
   * Sets how to handle storage in the local scope.
   *
   * @param value	the scope handling
   */
  public void setScopeHandlingStorage(ScopeHandling value) {
    m_ScopeHandlingStorage = value;
    reset();
  }

  /**
   * Returns how storage is handled in the local scope.
   *
   * @return		the scope handling
   */
  public ScopeHandling getScopeHandlingStorage() {
    return m_ScopeHandlingStorage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String scopeHandlingStorageTipText() {
    return
      "Defines how storage is handled in the local scope; whether to "
	+ "start with empty set, a (deep) copy of the outer scope storage or "
	+ "share the storage with the outer scope.";
  }

  /**
   * Sets the regular expression that storage item names must match to get
   * into the local scope.
   *
   * @param value	the expression
   */
  public void setStorageFilter(BaseRegExp value) {
    m_StorageFilter = value;
    reset();
  }

  /**
   * Returns the regular expression that storage item names must match to get
   * into the local scope.
   *
   * @return		the expression
   */
  public BaseRegExp getStorageFilter() {
    return m_StorageFilter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String storageFilterTipText() {
    return
      "The regular expression that storage item names must match in order "
	+ "to get into the local scope (when using " + ScopeHandling.COPY + ").";
  }

  /**
   * Sets whether to propagate storage items from the local to the outer scope.
   *
   * @param value	if true then storage items get propagated
   */
  public void setPropagateStorage(boolean value) {
    m_PropagateStorage = value;
    reset();
  }

  /**
   * Returns whether to propagate storage items from the local to the outer scope.
   *
   * @return		true if storage items get propagated
   */
  public boolean getPropagateStorage() {
    return m_PropagateStorage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String propagateStorageTipText() {
    return
      "If enabled and storage is not shared with outer scope, storage "
	+ "items which names match the specified regular expression get "
	+ "propagated to the outer scope.";
  }

  /**
   * Sets the regular expression that storage item names must match to get
   * propagated.
   *
   * @param value	the expression
   */
  public void setStorageRegExp(BaseRegExp value) {
    m_StorageRegExp = value;
    reset();
  }

  /**
   * Returns the regular expression that storage item names must match to get
   * propagated.
   *
   * @return		the expression
   */
  public BaseRegExp getStorageRegExp() {
    return m_StorageRegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String storageRegExpTipText() {
    return "The regular expression that the names of storage items must match in order to get propagated.";
  }

  /**
   * Sets whether to finish processing before stopping execution.
   *
   * @param value	if true then actor finishes processing first
   */
  public void setFinishBeforeStopping(boolean value) {
    m_Actors.setFinishBeforeStopping(value);
    reset();
  }

  /**
   * Returns whether to finish processing before stopping execution.
   *
   * @return		true if actor finishes processing first
   */
  public boolean getFinishBeforeStopping() {
    return m_Actors.getFinishBeforeStopping();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String finishBeforeStoppingTipText() {
    return m_Actors.finishBeforeStoppingTipText();
  }

  /**
   * Sets the timeout for waiting for the sub-flow to stop.
   *
   * @param value	timeout in milliseconds (<= 0 for infinity)
   */
  public void setStoppingTimeout(int value) {
    m_Actors.setStoppingTimeout(value);
    reset();
  }

  /**
   * Returns the timeout for waiting for the sub-flow to stop.
   *
   * @return		timeout in milliseconds (<= 0 for infinity)
   */
  public int getStoppingTimeout() {
    return m_Actors.getStoppingTimeout();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String stoppingTimeoutTipText() {
    return m_Actors.stoppingTimeoutTipText();
  }

  /**
   * Sets the interval for outputting warnings if the sub-flow hasn't stopped yet (and no stopping timeout set).
   *
   * @param value	interval in milliseconds (<= 0 no warning)
   */
  public void setStoppingWarningInterval(int value) {
    m_Actors.setStoppingWarningInterval(value);
    reset();
  }

  /**
   * Returns the interval for outputting warnings if the sub-flow hasn't stopped yet (and no stopping timeout set).
   *
   * @return		interval in milliseconds (<= 0 no warning)
   */
  public int getStoppingWarningInterval() {
    return m_Actors.getStoppingWarningInterval();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String stoppingWarningIntervalTipText() {
    return m_Actors.stoppingWarningIntervalTipText();
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
      return "Callable name '" + actor.getName() + "' is already used in this scope ('" + getFullName() + "')!";

    m_CallableNames.add(handler, actor);
    return null;
  }

  /**
   * The local variables to use.
   *
   * @param variables	the variables
   */
  @Override
  public void useLocalVariables(Variables variables) {
    m_LocalVariables = new FlowVariables();
    m_LocalVariables.setFlow(this);
    m_LocalVariables.assign(variables);
    m_ProgrammaticLocalVariables = true;
  }

  /**
   * The local storage to use.
   *
   * @param storage	the storage
   */
  @Override
  public void useLocalStorage(Storage storage) {
    m_LocalStorage = storage;
    m_ProgrammaticLocalStorage = true;
  }

  /**
   * Returns the storage container.
   *
   * @return		the container
   */
  public synchronized Storage getStorage() {
    if (m_LocalStorage == null) {
      switch (m_ScopeHandlingStorage) {
	case EMPTY:
	  m_LocalStorage = new Storage();
	  break;
	case COPY:
	  m_LocalStorage = getParent().getStorageHandler().getStorage().getClone(m_StorageFilter);
	  break;
	case SHARE:
	  m_LocalStorage = getParent().getStorageHandler().getStorage();
	  break;
	default:
	  throw new IllegalStateException("Unhandled storage scope handling type: " + m_ScopeHandlingStorage);
      }
    }

    return m_LocalStorage;
  }

  /**
   * Returns the Variables instance to use.
   *
   * @return		the local variables
   */
  public synchronized Variables getLocalVariables() {
    if (m_LocalVariables == null) {
      switch (m_ScopeHandlingVariables) {
	case EMPTY:
	  m_LocalVariables = new FlowVariables();
	  m_LocalVariables.setFlow(this);
	  if (getParent().getVariables().has(ActorUtils.FLOW_FILENAME_LONG))
	    ActorUtils.updateProgrammaticVariables(this, new PlaceholderFile(getParent().getVariables().get(ActorUtils.FLOW_FILENAME_LONG)));
	  else
	    ActorUtils.updateProgrammaticVariables(this, null);
	  break;
	case COPY:
	  m_LocalVariables = new FlowVariables();
	  m_LocalVariables.assign(getParent().getVariables(), m_VariablesFilter);
	  m_LocalVariables.setFlow(this);
	  break;
	case SHARE:
	  m_LocalVariables = (FlowVariables) getParent().getVariables();
	  break;
	default:
	  throw new IllegalStateException("Unhandled variables scope handling type: " + m_ScopeHandlingVariables);
      }
    }

    return m_LocalVariables;
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
   * Updates the Variables instance in use.
   *
   * @param value	ignored
   */
  @Override
  protected void forceVariables(Variables value) {
    super.forceVariables(getLocalVariables());
  }

  /**
   * Updates the parent of all actors in this group.
   */
  @Override
  protected void updateParent() {
    m_Actors.setName(getName());
    m_Actors.setParent(null);
    m_Actors.setParent(this);
    super.updateParent();
  }

  /**
   * Returns the size of the group.
   *
   * @return		always 1
   */
  @Override
  public int size() {
    return m_Actors.size();
  }

  /**
   * Returns the actor at the given position.
   *
   * @param index	the position
   * @return		the actor
   */
  @Override
  public Actor get(int index) {
    return m_Actors.get(index);
  }

  /**
   * Sets the actor at the given position.
   *
   * @param index	the position
   * @param actor	the actor to set at this position
   * @return		null if successful, otherwise error message
   */
  @Override
  public String set(int index, Actor actor) {
    String	result;

    result = m_Actors.set(index, actor);
    reset();
    updateParent();

    return result;
  }

  /**
   * Returns the index of the actor.
   *
   * @param actor	the name of the actor to look for
   * @return		the index of -1 if not found
   */
  @Override
  public int indexOf(String actor) {
    return m_Actors.indexOf(actor);
  }

  /**
   * Inserts the actor at the end.
   *
   * @param actor	the actor to insert
   * @return		null if successful, otherwise error message
   */
  public String add(Actor actor) {
    return add(size(), actor);
  }

  /**
   * Inserts the actor at the given position.
   *
   * @param index	the position
   * @param actor	the actor to insert
   * @return		null if successful, otherwise error message
   */
  public String add(int index, Actor actor) {
    String	result;

    if (actor == this)
      throw new IllegalArgumentException("Cannot add itself!");

    result = m_Actors.add(index, actor);
    reset();
    updateParent();

    return result;
  }

  /**
   * Removes the actor at the given position and returns the removed object.
   *
   * @param index	the position
   * @return		the removed actor
   */
  public Actor remove(int index) {
    Actor result;

    result = m_Actors.remove(index);
    reset();

    return result;
  }

  /**
   * Removes all actors.
   */
  public void removeAll() {
    m_Actors.removeAll();
    reset();
  }

  /**
   * Returns some information about the actor handler, e.g., whether it can
   * contain standalones and the actor execution.
   *
   * @return		the info
   */
  @Override
  public ActorHandlerInfo getActorHandlerInfo() {
    return m_Actors.getActorHandlerInfo();
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  public Class[] accepts() {
    return new Class[]{Unknown.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{Unknown.class};
  }

  /**
   * Gets called in the setUp() method. Returns null if loop-actors are fine,
   * otherwise error message.
   *
   * @return		null if everything OK, otherwise error message
   */
  @Override
  protected String setUpSubActors() {
    String	result;
    Actor	first;
    Actor	last;

    result = super.setUpSubActors();

    first = firstActive();
    last  = lastActive();
    if ((first != null) && (last != null)) {
      if (!ActorUtils.isTransformer(first))
	result = "First active actor (" + first.getName() + ") is not a transformer!";
      else if (!ActorUtils.isTransformer(last))
	result = "Last active actor (" + last.getName() + ") is not a transformer!";
    }

    if (result == null)
      result = m_Actors.setUp();

    return result;
  }

  /**
   * Does nothing.
   *
   * @param token	the token to accept and process
   */
  public void input(Token token) {
    m_Actors.input(token);
  }

  /**
   * Returns whether an input token is currently present.
   *
   * @return		true if input token present
   */
  public boolean hasInput() {
    return (m_Actors != null) && (m_Actors.hasInput());
  }

  /**
   * Returns the current input token, if any.
   *
   * @return		the input token, null if none present
   */
  public Token currentInput() {
    if (m_Actors != null)
      return m_Actors.currentInput();
    else
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
   * Pre-execute hook.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String preExecute() {
    if (!m_ProgrammaticLocalStorage)
      m_LocalStorage = null;
    if (!m_ProgrammaticLocalVariables)
      m_LocalVariables = null;
    m_Actors.setVariables(getVariables());
    m_Actors.getOptionManager().updateVariableValues(true);
    if (m_RestrictedStop) {
      m_RestrictedStop = false;
      m_Stopped        = false;
    }

    return super.preExecute();
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    return m_Actors.execute();
  }

  /**
   * Post-execute hook.
   *
   * @return		null if everything is fine, otherwise error message
   * @see		#m_Executed
   */
  @Override
  protected String postExecute() {
    String	result;

    result = super.postExecute();

    if (!isStopped()) {
      if ((m_ScopeHandlingVariables != ScopeHandling.SHARE) && m_PropagateVariables && (m_LocalVariables != null)) {
	for (String name: m_LocalVariables.nameSet()) {
	  if (m_VariablesRegExp.isMatch(name)) {
	    getParent().getVariables().set(name, m_LocalVariables.get(name));
	    if (isLoggingEnabled())
	      getLogger().fine("Propagating variable '" + name + "': " + m_LocalVariables.get(name));
	  }
	}
      }

      if ((m_ScopeHandlingStorage != ScopeHandling.SHARE) && m_PropagateStorage && (m_LocalStorage != null)) {
	for (StorageName name: m_LocalStorage.keySet()) {
	  if (m_StorageRegExp.isMatch(name.getValue())) {
	    getParent().getStorageHandler().getStorage().put(name, m_LocalStorage.get(name));
	    if (isLoggingEnabled())
	      getLogger().fine("Propagating storage '" + name + "': " + m_LocalStorage.get(name));
	  }
	}
      }
    }

    return result;
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  @Override
  public boolean hasPendingOutput() {
    return m_Actors.hasPendingOutput();
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    return m_Actors.output();
  }

  /**
   * Stops the processing of tokens without stopping the flow.
   */
  public void flushExecution() {
    m_Actors.flushExecution();
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    m_Actors.stopExecution();
    super.stopExecution();
  }

  /**
   * Returns whether the stop was a restricted one (that can be resumed).
   *
   * @return		true if restricted stop occurred
   */
  public boolean isRestrictedStop() {
    return m_RestrictedStop;
  }

  /**
   * Stops the (restricted) execution. No message set.
   */
  @Override
  public void restrictedStopExecution() {
    m_RestrictedStop = true;
    m_Actors.restrictedStopExecution();
  }

  /**
   * Stops the (restricted) execution.
   *
   * @param msg		the message to set as reason for stopping, can be null
   */
  @Override
  public void restrictedStopExecution(String msg) {
    m_RestrictedStop = true;
    m_Actors.restrictedStopExecution(msg);
  }

  /**
   * Cleans up after the execution has finished. Also removes graphical
   * components.
   */
  @Override
  public void cleanUp() {
    if (m_LocalVariables != null) {
      switch (m_ScopeHandlingVariables) {
	case EMPTY:
	case COPY:
	  m_LocalVariables.cleanUp();
	  break;
      }
      m_LocalVariables = null;
    }

    if (m_LocalStorage != null) {
      switch (m_ScopeHandlingStorage) {
	case EMPTY:
	case COPY:
	  m_LocalStorage.clear();
	  break;
      }
      m_LocalStorage = null;
    }

    m_CallableNames.clear();

    super.cleanUp();
  }
}
