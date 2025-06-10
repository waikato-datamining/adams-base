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
 * LocalScopeTrigger.java
 * Copyright (C) 2012-2025 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.control;

import adams.core.QuickInfoHelper;
import adams.core.UniqueIDs;
import adams.core.Variables;
import adams.core.VariablesHandler;
import adams.core.base.BaseRegExp;
import adams.core.io.PlaceholderFile;
import adams.flow.core.Actor;
import adams.flow.core.ActorHandler;
import adams.flow.core.ActorUtils;
import adams.flow.core.CallableNamesRecorder;
import adams.flow.core.FlowVariables;
import adams.flow.core.StopRestrictor;

/**
 <!-- globalinfo-start -->
 * Executes the sub-actors whenever a token gets passed through, just like the adams.flow.control.Trigger actor, but also provides its own scope for variables and internal storage.<br>
 * It is possible to 'propagate' or 'leak' variables and storage items from within the local scope back to the output scope. However, storage items from caches cannot be propagated.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
 * <br><br>
 * Conditional equivalent:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.control.ConditionalTrigger
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: LocalScopeTrigger
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-finish-before-stopping &lt;boolean&gt; (property: finishBeforeStopping)
 * &nbsp;&nbsp;&nbsp;If enabled, actor first finishes processing all data before stopping.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-asynchronous &lt;boolean&gt; (property: asynchronous)
 * &nbsp;&nbsp;&nbsp;If enabled, the sub-actors get executed asynchronously rather than the flow
 * &nbsp;&nbsp;&nbsp;waiting for them to finish before proceeding with execution.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-tee &lt;adams.flow.core.AbstractActor&gt; [-tee ...] (property: actors)
 * &nbsp;&nbsp;&nbsp;The actors to siphon-off the tokens to.
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
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class LocalScopeTrigger
  extends Trigger
  implements VariablesHandler, StorageHandler, LocalScopeHandler, ProgrammaticLocalScope, StopRestrictor {

  /** for serialization. */
  private static final long serialVersionUID = -8344934611549310497L;

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

  /** for synchronizing. */
  protected final Long m_Synchronize = UniqueIDs.nextLong();

  /**
   * Default constructor.
   */
  public LocalScopeTrigger() {
    super();
  }

  /**
   * Initializes with the specified name.
   *
   * @param name      the name to use
   */
  public LocalScopeTrigger(String name) {
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
      "Executes the sub-actors whenever a token gets passed through, just "
	+ "like the " + Trigger.class.getName() + " actor, but also provides "
	+ "its own scope for variables and internal storage.\n"
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
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_CallableNames            = new CallableNamesRecorder();
    m_EnforceCallableNameCheck = true;
    m_Actors.setRestrictingStops(true);
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
  public Storage getStorage() {
    synchronized (m_Synchronize) {
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
    }

    return m_LocalStorage;
  }

  /**
   * Returns the Variables instance to use.
   *
   * @return		the local variables
   */
  public Variables getLocalVariables() {
    synchronized (m_Synchronize) {
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
    }

    return m_LocalVariables;
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
   * Updates the Variables instance in use.
   *
   * @param value	ignored
   */
  @Override
  protected void forceVariables(Variables value) {
    super.forceVariables(getLocalVariables());
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
   * Returns whether the stop was a restricted one (that can be resumed).
   *
   * @return		true if restricted stop occurred
   */
  public boolean isRestrictedStop() {
    return m_RestrictedStop;
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
