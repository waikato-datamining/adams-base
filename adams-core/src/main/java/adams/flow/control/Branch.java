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
 * Branch.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import adams.core.QuickInfoHelper;
import adams.core.management.ProcessUtils;
import adams.flow.core.AbstractActor;
import adams.flow.core.ActorExecution;
import adams.flow.core.ActorHandler;
import adams.flow.core.ActorHandlerInfo;
import adams.flow.core.ActorUtils;
import adams.flow.core.Compatibility;
import adams.flow.core.InputConsumer;
import adams.flow.core.MutableActorHandler;
import adams.flow.core.OutputProducer;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Branches off the flow into several sub-branches, each being supplied with a copy of the same object being passed into this meta-actor.
 * <p/>
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
 * &nbsp;&nbsp;&nbsp;default: Branch
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
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
 * <pre>-branch &lt;adams.flow.core.AbstractActor&gt; [-branch ...] (property: branches)
 * &nbsp;&nbsp;&nbsp;The different branches that branch off and will be supplied with a copy 
 * &nbsp;&nbsp;&nbsp;of the same object.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-num-threads &lt;int&gt; (property: numThreads)
 * &nbsp;&nbsp;&nbsp;The number of threads to use for executing the branches; -1 = number of 
 * &nbsp;&nbsp;&nbsp;CPUs&#47;cores; 0 or 1 = sequential execution.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Branch
  extends AbstractControlActor
  implements InputConsumer, MutableActorHandler, AtomicExecution {

  /** for serialization. */
  private static final long serialVersionUID = -706232800503932715L;

  /** the key for storing the current token in the backup. */
  public final static String BACKUP_CURRENT = "current";

  /** the branches. */
  protected List<AbstractActor> m_Branches;

  /** the number of threads to use for parallel execution. */
  protected int m_NumThreads;

  /** the actual number of threads to use. */
  protected int m_ActualNumThreads;

  /** the executor service to use for parallel execution. */
  protected ExecutorService m_Executor;

  /** the token that gets passed on to all sub-branches. */
  protected transient Token m_CurrentToken;

  /** whether the branch contains global transformers somewhere or not. */
  protected boolean m_HasGlobalTransformers;
  
  /** whether to finish execution first before stopping. */
  protected boolean m_FinishBeforeStopping;

  /** whether to collect the output of the branches. */
  protected boolean m_CollectOutput;
  
  /** the collected output. */
  protected HashMap<Integer,Token> m_CollectedOutput;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Branches off the flow into several sub-branches, each being supplied "
      + "with a copy of the same object being passed into this meta-actor.";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Branches = new ArrayList<AbstractActor>();
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "finish-before-stopping", "finishBeforeStopping",
	    false);

    m_OptionManager.add(
	    "branch", "branches",
	    new AbstractActor[0]);

    m_OptionManager.add(
	    "num-threads", "numThreads",
	    -1, -1, null);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    if ((m_NumThreads == 0) || (m_NumThreads == 1)) {
      result = "sequential";
    }
    else {
      result = "parallel, threads: ";
      if (m_NumThreads == -1)
	result += "#cores";
      else
	result += m_NumThreads;
    }

    result  = QuickInfoHelper.toString(this, "numThreads", result);
    result += QuickInfoHelper.toString(this, "finishBeforeStopping", m_FinishBeforeStopping, "atomic execution", ", ");

    return result;
  }

  /**
   * Sets whether to finish processing before stopping execution.
   * 
   * @param value	if true then actor finishes processing first 
   */
  public void setFinishBeforeStopping(boolean value) {
    m_FinishBeforeStopping = value;
    reset();
  }
  
  /**
   * Returns whether to finish processing before stopping execution.
   * 
   * @return		true if actor finishes processing first
   */
  public boolean getFinishBeforeStopping() {
    return m_FinishBeforeStopping;
  }
  
  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String finishBeforeStoppingTipText() {
    return "If enabled, actor first finishes processing all data before stopping.";
  }

  /**
   * Sets the branches.
   *
   * @param value 	the branches
   */
  public void setBranches(AbstractActor[] value) {
    int		i;

    ActorUtils.uniqueNames(value);

    m_Branches.clear();
    for (i = 0; i < value.length; i++)
      m_Branches.add(value[i]);

    updateParent();
    reset();
  }

  /**
   * Returns the branches.
   *
   * @return 		the branches
   */
  public AbstractActor[] getBranches() {
    return m_Branches.toArray(new AbstractActor[m_Branches.size()]);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String branchesTipText() {
    return "The different branches that branch off and will be supplied with a copy of the same object.";
  }

  /**
   * Sets the number of threads to use for executing the branches.
   *
   * @param value 	the number of threads: -1 = # of CPUs/cores; 0/1 = sequential execution
   */
  public void setNumThreads(int value) {
    if (value >= -1) {
      m_NumThreads = value;
      reset();
    }
    else {
      getLogger().warning("Number of threads must be >= -1, provided: " + value);
    }
  }

  /**
   * Returns the number of threads to use for executing the branches.
   *
   * @return 		the number of threads: -1 = # of CPUs/cores; 0/1 = sequential execution
   */
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
    return "The number of threads to use for executing the branches; -1 = number of CPUs/cores; 0 or 1 = sequential execution.";
  }

  /**
   * Whether to collect the output of the branches.
   * 
   * @param value	true if to collect the output
   */
  public void setCollectOutput(boolean value) {
    m_CollectOutput = value;
    reset();
  }
  
  /**
   * Returns whether the output of the branches is collected.
   * 
   * @return		true if output collected
   */
  public boolean getCollectOutput() {
    return m_CollectOutput;
  }
  
  /**
   * Returns the collected output from the branches, if any.
   * 
   * @return		the collected output, null if not available
   */
  public HashMap<Integer,Token> getCollectedOutput() {
    return m_CollectedOutput;
  }
  
  /**
   * Initializes the sub-actors for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null) {
      if (m_NumThreads == -1)
	m_ActualNumThreads = ProcessUtils.getAvailableProcessors();
      else if (m_NumThreads > 1)
	m_ActualNumThreads = Math.min(m_NumThreads, size());
      else
	m_ActualNumThreads = 0;

      if (m_ActualNumThreads > 0)
	m_HasGlobalTransformers = hasGlobalTransformers();

      if (m_CollectOutput)
	m_CollectedOutput = new HashMap<Integer,Token>();
    }

    return result;
  }

  /**
   * Returns some information about the actor handler, e.g., whether it can
   * contain standalones and the actor execution.
   *
   * @return		the info
   */
  @Override
  public ActorHandlerInfo getActorHandlerInfo() {
    return new ActorHandlerInfo(false, ActorExecution.PARALLEL, true);
  }

  /**
   * Returns the size of the group.
   *
   * @return		the size
   */
  @Override
  public int size() {
    return m_Branches.size();
  }

  /**
   * Returns the actor at the given position.
   *
   * @param index	the position
   * @return		the actor
   */
  @Override
  public AbstractActor get(int index) {
    return m_Branches.get(index);
  }

  /**
   * Sets the actor at the given position.
   *
   * @param index	the position
   * @param actor	the actor to set at this position
   */
  @Override
  public void set(int index, AbstractActor actor) {
    if ((index > -1) && (index < m_Branches.size())) {
      ActorUtils.uniqueName(actor, this, index);
      m_Branches.set(index, actor);
      reset();
      updateParent();
    }
    else {
      getLogger().severe("Index out of range (0-" + (m_Branches.size() - 1) + "): " + index);
    }
  }

  /**
   * Inserts the actor at the end.
   *
   * @param actor	the actor to insert
   */
  public void add(AbstractActor actor) {
    add(size(), actor);
  }

  /**
   * Inserts the actor at the given position.
   *
   * @param index	the position
   * @param actor	the actor to insert
   */
  public void add(int index, AbstractActor actor) {
    m_Branches.add(index, actor);
    reset();
    updateParent();
  }

  /**
   * Removes the actor at the given position and returns the removed object.
   *
   * @param index	the position
   * @return		the removed actor
   */
  public AbstractActor remove(int index) {
    AbstractActor	result;

    result = m_Branches.remove(index);
    reset();

    return result;
  }

  /**
   * Removes all actors.
   */
  public void removeAll() {
    m_Branches.clear();
    reset();
  }

  /**
   * Returns the index of the actor.
   *
   * @param actor	the name of the actor to look for
   * @return		the index of -1 if not found
   */
  @Override
  public int indexOf(String actor) {
    int		result;
    int		i;

    result = -1;

    for (i = 0; i < size(); i++) {
      if (get(i).getName().equals(actor)) {
	result = i;
	break;
      }
    }

    return result;
  }

  /**
   * Backs up the current state of the actor before update the variables.
   *
   * @return		the backup
   */
  @Override
  protected Hashtable<String,Object> backupState() {
    Hashtable<String,Object>	result;

    result = super.backupState();

    if (m_CurrentToken != null)
      result.put(BACKUP_CURRENT, m_CurrentToken);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_CURRENT)) {
      m_CurrentToken = (Token) state.get(BACKUP_CURRENT);
      state.remove(BACKUP_CURRENT);
    }

    super.restoreState(state);
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		depends on the sub-branches
   */
  public Class[] accepts() {
    Class[]		result;
    int			i;
    int			n;
    AbstractActor	actor;
    AbstractActor	actor2;
    HashSet<Class>	all;
    HashSet<Class>	curr;
    Class[]		cls;
    Compatibility	comp;
    boolean		compatible;

    // check compatibility
    compatible = true;
    comp       = new Compatibility();
    for (i = 0; i < size() - 1; i++) {
      actor = get(i);
      if (actor instanceof InputConsumer) {
	cls = ((InputConsumer) actor).accepts();
	for (n = i + 1; n < size(); n++) {
	  actor2 = get(n);
	  if (actor2 instanceof InputConsumer) {
	    if (    !comp.isCompatible(cls, ((InputConsumer) actor2).accepts())
		 && !comp.isCompatible(((InputConsumer) actor2).accepts(), cls) ) {
	      compatible = false;
	      break;
	    }
	  }
	}
      }
      if (!compatible)
	break;
    }
    if (compatible)
      result = new Class[]{Unknown.class};
    else
      result = new Class[0];

    // gather all common classes
    all = new HashSet<Class>();
    for (i = 0; i < size(); i++) {
      actor = (AbstractActor) get(i);
      if (actor instanceof InputConsumer) {
	cls  = ((InputConsumer) actor).accepts();
	curr = new HashSet<Class>();
	for (n = 0; n < cls.length; n++)
	  curr.add(cls[n]);
	if (i == 0)
	  all.addAll(curr);
	else
	  all.retainAll(curr);
      }
    }

    // only return the common class if it is the same in all sub-branches
    if (all.size() > 0)
      result = all.toArray(new Class[all.size()]);

    return result;
  }

  /**
   * The method that accepts the input token and then processes it.
   *
   * @param token	the token to accept and process
   */
  public void input(Token token) {
    m_CurrentToken = token;
    if (isLoggingEnabled())
      getLogger().fine("input token: " + token);
  }

  /**
   * Checks the sub-branch.
   *
   * @param branch	the branch to check
   * @return		null if everything correct, otherwise the error message
   */
  protected String checkBranch(AbstractActor branch) {
    String			result;
    int				i;
    AbstractDirectedControlActor	group;
    AbstractActor		curr;

    result = null;

    if (!(branch instanceof InputConsumer))
      result = "'" + branch.getFullName() + "' doesn't accept tokens!";

    if ((result == null) && (branch instanceof AbstractDirectedControlActor)) {
      group  = (AbstractDirectedControlActor) branch;
      result = group.check();
      if (result == null) {
	for (i = 0; i < group.size(); i++) {
	  curr = group.get(i);
	  if (curr.getSkip())
	    continue;

	  // only if we have token to process we need first actor to be an
	  // InputConsumer
	  if (!(curr instanceof InputConsumer) && (m_CurrentToken != null)) {
	    result = "First actor '" + curr.getFullName() + "' does not accept inputs!";
	    break;
	  }

	  // we only check first actor in group
	  break;
	}
      }
    }

    if (result != null) {
      if (isLoggingEnabled())
	getLogger().info("Branch '" + branch.getFullName() + "' has errors: " + result);
    }

    return result;
  }

  /**
   * Checks whether all the connections are valid, i.e., the input and
   * output types fit and whether the flow chain is connected properly.
   *
   * @return		null if everything is fine, otherwise the offending
   * 			connection
   */
  @Override
  public String check() {
    String	result;
    int		i;

    result = super.check();

    if (result == null) {
      for (i = 0; i < size(); i++) {
	if (isLoggingEnabled())
	  getLogger().info("Checking branch #" + (i+1) + ": " + get(i).getFullName());
	if (!get(i).getSkip())
	  result = checkBranch(get(i));
	if (result != null) {
	  result = "Problem in branch " + get(i).getFullName() + ": " + result;
	  break;
	}
      }
    }

    return result;
  }

  /**
   * Checks for global transformers that might get accessed by parallel threads.
   *
   * @return		true if the branch contains global transformers that are
   * 			accessed by
   */
  protected boolean hasGlobalTransformers() {
    boolean			result;
    Hashtable<String,Integer>	count;
    Enumeration<String>		names;
    String			name;

    result = false;

    // check if global transformers get accesed more than once
    count = ActorUtils.findCallableTransformers(this);
    names = count.keys();
    while (names.hasMoreElements()) {
      name = names.nextElement();
      if (count.get(name) > 1) {
	result = true;
	break;
      }
    }

    return result;
  }

  /**
   * Executes the branches in parallel.
   *
   * @return		null if everything is fine, otherwise error message
   */
  protected String executeParallel() {
    String			result;
    int				i;
    ArrayList<Callable<String>>	jobs;
    Callable<String>		job;
    List<Future<String>>	results;
    String			jobResult;

    result = null;

    if (isLoggingEnabled())
      getLogger().info("Starting parallel execution...");

    // create jobs
    jobs = new ArrayList<Callable<String>>();
    for (i = 0; i < size(); i++) {
      final AbstractActor branch = get(i);
      final int index = i;
      if (branch.getSkip())
	continue;
      job = new Callable<String>() {
	public String call() throws Exception {
	  if (isLoggingEnabled())
	    getLogger().info("Executing branch #" + (index+1) + "...");
	  String result;
	  if (m_HasGlobalTransformers) {
	    synchronized(m_Self) {
	      ((InputConsumer) branch).input(m_CurrentToken);
	      result = branch.execute();
	    }
	  }
	  else {
	    ((InputConsumer) branch).input(m_CurrentToken);
	    result = branch.execute();
	  }
	  if (isLoggingEnabled())
	    getLogger().info("...finished branch #" + (index+1) + ((result == null) ? "" : " with error"));
	  return result;
	}
      };
      jobs.add(job);
    }

    // execute jobs
    m_Executor = Executors.newFixedThreadPool(m_ActualNumThreads);
    results    = null;
    try {
      results = m_Executor.invokeAll(jobs);
    }
    catch (InterruptedException e) {
      // ignored
    }
    catch (RejectedExecutionException e) {
      // ignored
    }
    catch (Exception e) {
      handleException("Failed to start up jobs", e);
    }
    m_Executor.shutdown();

    // wait for threads to finish
    while (!m_Executor.isTerminated()) {
      try {
	m_Executor.awaitTermination(100, TimeUnit.MILLISECONDS);
      }
      catch (InterruptedException e) {
	// ignored
      }
      catch (Exception e) {
	result = handleException("Failed to await termination", e);
      }
    }

    // check for errors
    if (results != null) {
      for (i = 0; i < results.size(); i++) {
	try {
	  jobResult = results.get(i).get();
	  if (jobResult != null) {
	    if (result == null)
	      result = "";
	    else
	      result += ", ";
	    result += "Branch #" + (i+1) + ": " + jobResult;
	  }
	  // collect output?
	  if (m_CollectOutput && ((OutputProducer) get(i)).hasPendingOutput())
	    m_CollectedOutput.put(i, ((OutputProducer) get(i)).output());
	}
	catch (InterruptedException e) {
	  // ignored
	}
	catch (Exception e) {
	  handleException("Failed to get job results", e);
	}
      }
    }

    if (isLoggingEnabled())
      getLogger().info("Finished parallel execution.");

    return result;
  }

  /**
   * Executes the branches sequentially.
   *
   * @return		null if everything is fine, otherwise error message
   */
  protected String executeSequential() {
    String		result;
    int			i;
    AbstractActor	actor;
    String		msg;

    result = null;

    if (isLoggingEnabled())
      getLogger().info("Starting sequential execution...");

    for (i = 0; i < size(); i++) {
      actor = get(i);
      if (actor.getSkip())
	continue;

      if (isLoggingEnabled())
        getLogger().info("Executing branch #" + (i+1) + "...");

      try {
	// input
	if (getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
	  getFlowExecutionListeningSupporter().getFlowExecutionListener().preInput(actor, m_CurrentToken);
	((InputConsumer) actor).input(m_CurrentToken);
	if (getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
	  getFlowExecutionListeningSupporter().getFlowExecutionListener().postInput(actor);
	// execute
	if (getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
	  getFlowExecutionListeningSupporter().getFlowExecutionListener().preExecute(actor);
	result = actor.execute();
	if (getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
	  getFlowExecutionListeningSupporter().getFlowExecutionListener().postExecute(actor);
	// collect output?
	if (m_CollectOutput && ((OutputProducer) actor).hasPendingOutput())
	  m_CollectedOutput.put(i, ((OutputProducer) actor).output());
      }
      catch (Exception e) {
	msg    = "Failed to execute branch #" + (i+1) + ": ";
	result = msg + e;
	getLogger().log(Level.SEVERE, msg, e);
      }

      if (isLoggingEnabled())
        getLogger().info("...finished" + ((result == null) ? "" : " with error"));

      if (result != null)
	break;
      if (isStopped())
	break;
    }

    if (isLoggingEnabled())
      getLogger().info("Finished sequential execution.");

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    if (m_CollectOutput)
      m_CollectedOutput.clear();
    
    if (m_ActualNumThreads == 0)
      return executeSequential();
    else
      return executeParallel();
  }
  
  /**
   * Stops the processing of tokens without stopping the flow.
   */
  public void flushExecution() {
    int		i;
    
    for (i = size() - 1; i >= 0; i--) {
      if (get(i).getSkip())
	continue;
      if (get(i) instanceof ActorHandler)
	((ActorHandler) get(i)).flushExecution();
    }
  }

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    int		i;

    if (m_FinishBeforeStopping) {
      while (isExecuting()) {
	synchronized(this)  {
	  try {
	    wait(100);
	  }
	  catch (Exception e) {
	  }
	}
      }
    }

    for (i = size() - 1; i >= 0; i--) {
      if (!get(i).getSkip())
	get(i).stopExecution();
    }

    super.stopExecution();

    if ((m_Executor != null) && !m_Executor.isShutdown())
      m_Executor.shutdownNow();
  }

  /**
   * Finishes up the execution.
   */
  @Override
  public void wrapUp() {
    m_CurrentToken = null;

    super.wrapUp();
  }
}
