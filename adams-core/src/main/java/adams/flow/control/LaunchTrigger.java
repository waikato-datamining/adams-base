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
 * LaunchTrigger.java
 * Copyright (C) 2024-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import adams.core.ObjectCopyHelper;
import adams.core.OptionalThreadLimiter;
import adams.core.Performance;
import adams.core.QuickInfoHelper;
import adams.core.StoppableUtils;
import adams.core.ThreadLimiter;
import adams.core.Utils;
import adams.core.base.BaseRegExp;
import adams.core.logging.LoggingLevel;
import adams.core.option.OptionUtils;
import adams.flow.control.ScopeHandler.ScopeHandling;
import adams.flow.core.Actor;
import adams.flow.core.ActorHandlerInfo;
import adams.flow.core.ActorUtils;
import adams.flow.core.InputConsumer;
import adams.flow.core.MutableActorHandler;
import adams.flow.core.Token;
import adams.flow.source.Start;
import adams.multiprocess.AbstractJob;
import adams.multiprocess.JobRunner;
import adams.multiprocess.LocalJobRunner;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Launches the sub-flow in a separate thread each time a token arrives.<br>
 * Internally, a adams.flow.control.LocalScopeTrigger is used to manage the scope.<br>
 * However, when imposing thread limits, a job gets created with the sub-actors and placed into a job runner for execution.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: LaunchTrigger
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
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-actors &lt;adams.flow.core.Actor&gt; [-actors ...] (property: actors)
 * &nbsp;&nbsp;&nbsp;The actors to launch with the incoming tokens.
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
 * &nbsp;&nbsp;&nbsp;more: https:&#47;&#47;docs.oracle.com&#47;javase&#47;tutorial&#47;essential&#47;regex&#47;
 * &nbsp;&nbsp;&nbsp;https:&#47;&#47;docs.oracle.com&#47;en&#47;java&#47;javase&#47;11&#47;docs&#47;api&#47;java.base&#47;java&#47;util&#47;regex&#47;Pattern.html
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
 * &nbsp;&nbsp;&nbsp;more: https:&#47;&#47;docs.oracle.com&#47;javase&#47;tutorial&#47;essential&#47;regex&#47;
 * &nbsp;&nbsp;&nbsp;https:&#47;&#47;docs.oracle.com&#47;en&#47;java&#47;javase&#47;11&#47;docs&#47;api&#47;java.base&#47;java&#47;util&#47;regex&#47;Pattern.html
 * </pre>
 *
 * <pre>-impose-thread-limits &lt;boolean&gt; (property: imposeThreadLimits)
 * &nbsp;&nbsp;&nbsp;If enabled, imposes the specified limit on threads.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-num-threads &lt;int&gt; (property: numThreads)
 * &nbsp;&nbsp;&nbsp;The number of threads to use for parallel execution; &gt; 0: specific number
 * &nbsp;&nbsp;&nbsp;of cores to use (capped by actual number of cores available, 1 = sequential
 * &nbsp;&nbsp;&nbsp;execution); = 0: number of cores; &lt; 0: number of free cores (eg -2 means
 * &nbsp;&nbsp;&nbsp;2 free cores; minimum of one core is used)
 * &nbsp;&nbsp;&nbsp;default: -1
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class LaunchTrigger
  extends AbstractControlActor
  implements InputConsumer, MutableActorHandler, OptionalThreadLimiter {

  public static class LaunchJob
    extends AbstractJob {

    private static final long serialVersionUID = 6406892820872772446L;

    /** the actor to execute. */
    protected LocalScopeTrigger m_Local;

    /**
     * Initializes the job.
     *
     * @param local  	the actor to execute
     */
    public LaunchJob(LocalScopeTrigger local) {
      super();
      m_Local = local;
    }

    /**
     * Checks whether all pre-conditions have been met.
     *
     * @return null if everything is OK, otherwise an error message
     */
    @Override
    protected String preProcessCheck() {
      if (m_Local == null)
	return "No actor to execute!";
      return null;
    }

    /**
     * Does the actual execution of the job.
     *
     * @throws Exception if fails to execute job
     */
    @Override
    protected void process() throws Exception {
      m_Local.execute();
    }

    /**
     * Stops the execution.
     */
    @Override
    public void stopExecution() {
      StoppableUtils.stopAnyExecution(m_Local);
      super.stopExecution();
    }

    /**
     * Checks whether all post-conditions have been met.
     *
     * @return null if everything is OK, otherwise an error message
     */
    @Override
    protected String postProcessCheck() {
      return null;
    }

    /**
     * Returns a string representation of this job.
     *
     * @return the job as string
     */
    @Override
    public String toString() {
      return OptionUtils.getCommandLine(m_Local);
    }

    /**
     * Cleans up data structures, frees up memory.
     * Removes dependencies and job parameters.
     */
    @Override
    public void cleanUp() {
      m_Local = null;
      super.cleanUp();
    }
  }

  private static final long serialVersionUID = -6434809501169213229L;

  /** the key for storing the input token in the backup. */
  public final static String BACKUP_INPUT = "input";

  /** how to handle the variables. */
  protected ScopeHandling m_ScopeHandlingVariables;

  /** the regular expression of the variables to allow into the local scope. */
  protected BaseRegExp m_VariablesFilter;

  /** how to handle the storage. */
  protected ScopeHandling m_ScopeHandlingStorage;

  /** the regular expression of the storage items to allow into the local scope. */
  protected BaseRegExp m_StorageFilter;

  /** the sub-flow to launch. */
  protected Trigger m_Actors;

  /** whether to limit the number of threads. */
  protected boolean m_ImposeThreadLimits;

  /** the number of threads to use for parallel execution. */
  protected int m_NumThreads;

  /** the job runner. */
  protected transient JobRunner m_JobRunner;

  /** the launched sub-flows. */
  protected transient List<LocalScopeTrigger> m_Launched;

  /** the input token. */
  protected transient Token m_InputToken;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Launches the sub-flow in a separate thread each time a token arrives.\n"
	     + "Internally, a " + Utils.classToString(LocalScopeTrigger.class) + " is used to manage the scope.\n"
	     + "However, when imposing thread limits, a job gets created with the sub-actors and placed into a job runner for execution.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "actors", "actors",
      new Actor[0]);

    m_OptionManager.add(
      "scope-handling-variables", "scopeHandlingVariables",
      ScopeHandling.EMPTY);

    m_OptionManager.add(
      "variables-filter", "variablesFilter",
      new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
      "scope-handling-storage", "scopeHandlingStorage",
      ScopeHandling.EMPTY);

    m_OptionManager.add(
      "storage-filter", "storageFilter",
      new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
      "impose-thread-limits", "imposeThreadLimits",
      false);

    m_OptionManager.add(
      "num-threads", "numThreads",
      -1);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Actors = new Trigger();
    m_Actors.setActors(new Actor[]{new Start()});
    m_Launched = new ArrayList<>();
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
   * Returns some information about the actor handler, e.g., whether it can
   * contain standalones and the actor execution.
   *
   * @return the info
   */
  @Override
  public ActorHandlerInfo getActorHandlerInfo() {
    return m_Actors.getActorHandlerInfo();
  }

  /**
   * Returns the size of the group.
   *
   * @return the size
   */
  @Override
  public int size() {
    return m_Actors.size();
  }

  /**
   * Returns the actor at the given position.
   *
   * @param index the position
   * @return the actor
   */
  @Override
  public Actor get(int index) {
    return m_Actors.get(index);
  }

  /**
   * Sets the actor at the given position.
   *
   * @param index the position
   * @param actor the actor to set at this position
   * @return null if successful, otherwise error message
   */
  @Override
  public String set(int index, Actor actor) {
    return m_Actors.set(index, actor);
  }

  /**
   * Returns the index of the actor.
   *
   * @param actor the name of the actor to look for
   * @return the index of -1 if not found
   */
  @Override
  public int indexOf(String actor) {
    return m_Actors.indexOf(actor);
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return m_Actors.accepts();
  }

  /**
   * The method that accepts the input token and then processes it.
   *
   * @param token	the token to accept and process
   */
  @Override
  public void input(Token token) {
    m_InputToken = token;
  }

  /**
   * Returns whether an input token is currently present.
   *
   * @return		true if input token present
   */
  @Override
  public boolean hasInput() {
    return (m_InputToken != null);
  }

  /**
   * Returns the current input token, if any.
   *
   * @return		the input token, null if none present
   */
  @Override
  public Token currentInput() {
    return m_InputToken;
  }

  /**
   * Inserts the actor at the end.
   *
   * @param actor the actor to insert
   * @return null if successful, otherwise error message
   */
  @Override
  public String add(Actor actor) {
    return m_Actors.add(actor);
  }

  /**
   * Inserts the actor at the given position.
   *
   * @param index the position
   * @param actor the actor to insert
   * @return null if successful, otherwise error message
   */
  @Override
  public String add(int index, Actor actor) {
    return m_Actors.add(index, actor);
  }

  /**
   * Removes the actor at the given position and returns the removed object.
   *
   * @param index the position
   * @return the removed actor
   */
  @Override
  public Actor remove(int index) {
    return m_Actors.remove(index);
  }

  /**
   * Removes all actors.
   */
  @Override
  public void removeAll() {
    m_Actors.removeAll();
  }

  /**
   * Performs checks on the "sub-actors".
   *
   * @return		null if checks passed or null in case of an error
   */
  @Override
  public String check() {
    return checkSubActors(getActors());
  }

  /**
   * Checks the sub actor before it is set.
   * Returns an error message if the actor is not acceptable, null otherwise.
   *
   * @param index	the index the actor gets set
   * @param actor	the actor to check
   * @return		null if accepted, otherwise error message
   */
  protected String checkSubActor(int index, Actor actor) {
    return null;
  }

  /**
   * Checks the tee actors before they are set via the setSubActors method.
   * Returns an error message if the actors are not acceptable, null otherwise.
   *
   * @param actors	the actors to check
   * @return		null if accepted, otherwise error message
   */
  protected String checkSubActors(Actor[] actors) {
    return ActorUtils.checkForSource(actors);
  }

  /**
   * Sets the actor to launch.
   *
   * @param value	the actor
   */
  public void setActors(Actor[] value) {
    String	msg;

    msg = checkSubActors(value);
    if (msg == null) {
      m_Actors.setActors(value);
      reset();
      updateParent();
    }
    else {
      throw new IllegalArgumentException(msg);
    }
  }

  /**
   * Returns the actors to launch.
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
    return "The actors to launch with the incoming tokens.";
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
   * Sets whether to limit the number of threads to use.
   *
   * @param value 	true if to limit
   */
  @Override
  public void setImposeThreadLimits(boolean value) {
    m_ImposeThreadLimits = value;
    reset();
  }

  /**
   * Returns whether to limit the number of threads to use.
   *
   * @return 		true if to limit
   */
  @Override
  public boolean getImposeThreadLimits() {
    return m_ImposeThreadLimits;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String imposeThreadLimitsTipText() {
    return "If enabled, imposes the specified limit on threads.";
  }

  /**
   * Sets the number of threads to use for parallel execution of threads.
   *
   * @param value 	the number of threads: -1 = # of CPUs/cores; 0/1 = sequential execution
   */
  @Override
  public void setNumThreads(int value) {
    m_NumThreads = value;
    reset();
  }

  /**
   * Returns the number of threads to use for parallel execution of threads.
   *
   * @return 		the number of threads: -1 = # of CPUs/cores; 0/1 = sequential execution
   */
  @Override
  public int getNumThreads() {
    return m_NumThreads;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numThreadsTipText() {
    return Performance.getNumThreadsHelp();
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
    result += "]";

    result += ", storage [";
    result += QuickInfoHelper.toString(this, "scopeHandlingStorage", m_ScopeHandlingStorage, "scope: ");
    result += QuickInfoHelper.toString(this, "storageFilter", !m_StorageFilter.isMatchAll(), "filter: " + m_StorageFilter, ", ");
    result += "]";

    if (m_ImposeThreadLimits)
      result += ", " + QuickInfoHelper.toString(this, "numThreads", Performance.getNumThreadsQuickInfo(m_NumThreads));

    return result;
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null) {
      if (m_ImposeThreadLimits) {
	m_JobRunner = new LocalJobRunner();
	if (m_JobRunner instanceof ThreadLimiter)
	  ((ThreadLimiter) m_JobRunner).setNumThreads(m_NumThreads);
      }
    }

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    final LocalScopeTrigger 	local;
    Thread			thread;

    local = new LocalScopeTrigger();
    local.removeAll();
    local.setActors(((Trigger) m_Actors.shallowCopy(false)).getActors());
    local.setParent(this);
    local.setScopeHandlingVariables(m_ScopeHandlingVariables);
    local.setVariablesFilter(ObjectCopyHelper.copyObject(m_VariablesFilter));
    if (m_ScopeHandlingVariables == ScopeHandling.COPY)
      local.useLocalVariables(getVariables().getClone());
    local.setScopeHandlingStorage(m_ScopeHandlingStorage);
    local.setStorageFilter(ObjectCopyHelper.copyObject(m_StorageFilter));
    if (m_ScopeHandlingStorage == ScopeHandling.COPY)
      local.useLocalStorage(getStorageHandler().getStorage().getClone());
    result = local.setUp();
    local.forceVariables(local.getVariables());
    local.getOptionManager().updateVariableValues(true);
    if (result == null) {
      m_Launched.add(local);
      local.input(m_InputToken.getClone());
      if (m_JobRunner != null) {
	m_JobRunner.add(new LaunchJob(local));
	m_JobRunner.start();
      }
      else {
	thread = new Thread(local::execute);
	thread.start();
      }
    }

    return result;
  }

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    if (m_JobRunner != null) {
      m_JobRunner.terminate();
      m_JobRunner = null;
    }
    if (m_Launched != null) {
      for (LocalScopeTrigger local : m_Launched)
	local.stopExecution();
    }
    super.stopExecution();
  }

  /**
   * Finishes up the execution.
   */
  @Override
  public void wrapUp() {
    if (m_JobRunner != null) {
      m_JobRunner.stop();
      m_JobRunner = null;
    }
    if (m_Launched != null) {
      for (LocalScopeTrigger local : m_Launched)
	local.wrapUp();
    }
    super.wrapUp();
  }

  /**
   * Cleans up after the execution has finished. Also removes graphical
   * components.
   */
  @Override
  public void cleanUp() {
    if (m_Launched != null) {
      for (LocalScopeTrigger local : m_Launched)
	local.cleanUp();
    }
    m_Launched = null;
    super.cleanUp();
  }
}
