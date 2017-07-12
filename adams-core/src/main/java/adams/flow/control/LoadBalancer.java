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
 * LoadBalancer.java
 * Copyright (C) 2010-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import adams.core.ObjectCopyHelper;
import adams.core.Performance;
import adams.core.QuickInfoHelper;
import adams.core.UniqueIDs;
import adams.core.Variables;
import adams.core.base.BaseAnnotation;
import adams.core.logging.LoggingLevel;
import adams.core.option.OptionUtils;
import adams.flow.core.Actor;
import adams.flow.core.ActorHandler;
import adams.flow.core.ActorHandlerInfo;
import adams.flow.core.ActorUtils;
import adams.flow.core.InputConsumer;
import adams.flow.core.MutableActorHandler;
import adams.flow.core.Token;
import adams.flow.core.Unknown;
import adams.flow.sink.CallableSink;
import adams.flow.sink.Null;
import adams.flow.source.StorageValue;
import adams.flow.transformer.CallableTransformer;
import adams.flow.transformer.DeleteStorageValue;
import adams.multiprocess.CallableWithResult;
import adams.multiprocess.PausableFixedThreadPoolExecutor;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 <!-- globalinfo-start -->
 * Runs the specified 'load actor' in as many separate threads as specified with the 'num-threads' parameter.<br>
 * Always uses a copy of the variables.<br>
 * NB: no callable transformer or sink allowed.
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
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: LoadBalancer
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
 * <pre>-load &lt;adams.flow.core.Actor&gt; [-load ...] (property: loadActors)
 * &nbsp;&nbsp;&nbsp;The actors to 'load-balance'.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.sink.Null
 * </pre>
 * 
 * <pre>-num-threads &lt;int&gt; (property: numThreads)
 * &nbsp;&nbsp;&nbsp;The number of threads to use for load-balancing (-1 means one for each core
 * &nbsp;&nbsp;&nbsp;&#47;cpu).
 * &nbsp;&nbsp;&nbsp;default: 0
 * </pre>
 * 
 * <pre>-use-local-storage &lt;boolean&gt; (property: useLocalStorage)
 * &nbsp;&nbsp;&nbsp;If enabled, then each thread will restrict the scope of storage to be local;
 * &nbsp;&nbsp;&nbsp; initially, a shallow copy of the storage is taken at the thread's time 
 * &nbsp;&nbsp;&nbsp;of creation.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-deep-copy &lt;boolean&gt; (property: deepCopy)
 * &nbsp;&nbsp;&nbsp;If enabled, the local storage gets copied using a deep copy.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LoadBalancer
  extends AbstractControlActor
  implements InputConsumer, MutableActorHandler {

  /** for serialization. */
  private static final long serialVersionUID = -8782869993629454572L;

  /** the key for storing the current token in the backup. */
  public final static String BACKUP_CURRENT = "current";

  /** the actors to "balance". */
  protected Sequence m_Actors;

  /** the input token. */
  protected transient Token m_CurrentToken;

  /** the number of threads to use for parallel execution. */
  protected int m_NumThreads;

  /** the actual number of threads to use. */
  protected int m_ActualNumThreads;

  /** the executor service to use for parallel execution. */
  protected PausableFixedThreadPoolExecutor m_Executor;

  /** the actors to clean up in the end. */
  protected List<Actor> m_ToCleanUp;

  /** the count of threads spawned so far. */
  protected int m_ThreadsSpawned;

  /** whether to use local storage. */
  protected boolean m_UseLocalStorage;

  /** whether to perform a deep copy of the storage. */
  protected boolean m_DeepCopy;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Runs the specified 'load actor' in as many separate threads as "
	+ "specified with the 'num-threads' parameter.\n"
	+ "Always uses a copy of the variables.\n"
	+ "NB: no callable transformer or sink allowed.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "load", "loadActors",
	    new Actor[]{new Null()});

    m_OptionManager.add(
	    "num-threads", "numThreads",
	    0);

    m_OptionManager.add(
	    "use-local-storage", "useLocalStorage",
	    false);

    m_OptionManager.add(
	    "deep-copy", "deepCopy",
	    false);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_CurrentToken = null;
    m_ToCleanUp    = new ArrayList<>();
    m_Actors       = new Sequence();
    m_Actors.setAllowStandalones(true);
    m_Actors.setAllowSource(true);
  }

  /**
   * Updates the parent of all actors in this group.
   */
  @Override
  protected void updateParent() {
    m_Actors.setName(getName());
    m_Actors.setParent(null);
    m_Actors.setParent(getParent());
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
   * Sets the load actors.
   *
   * @param value	the actors
   */
  public void setLoadActors(Actor[] value) {
    m_Actors.setActors(value);
    reset();
    updateParent();
  }

  /**
   * Returns the load actors.
   *
   * @return		the actors
   */
  public Actor[] getLoadActors() {
    return m_Actors.getActors();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String loadActorsTipText() {
    return "The actors to 'load-balance'.";
  }

  /**
   * Sets the number of threads to use.
   *
   * @param value	the number of threads
   */
  public void setNumThreads(int value) {
    if (value >= -1) {
      m_NumThreads = value;
      reset();
    }
  }

  /**
   * Returns the number of threads in use.
   *
   * @return		the number of threads
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
    return "The number of threads to use for load-balancing (-1 means one for each core/cpu).";
  }

  /**
   * Sets whether to use local storage scope.
   *
   * @param value	if true local storage scope will be used
   */
  public void setUseLocalStorage(boolean value) {
    m_UseLocalStorage = value;
    reset();
  }

  /**
   * Returns whether to use user local storage scope.
   *
   * @return		true if local storage scope enabled
   */
  public boolean getUseLocalStorage() {
    return m_UseLocalStorage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useLocalStorageTipText() {
    return
        "If enabled, then each thread will restrict the scope of storage "
      + "to be local; initially, a shallow copy of the storage is taken at the "
      + "thread's time of creation.";
  }

  /**
   * Sets whether to perform a deep copy for the local storage.
   *
   * @param value	if true a deep copy for the local storage will be performed
   */
  public void setDeepCopy(boolean value) {
    m_DeepCopy = value;
    reset();
  }

  /**
   * Returns whether to perform a deep copy for the local storage.
   *
   * @return		true if a deep copy is performed
   */
  public boolean getDeepCopy() {
    return m_DeepCopy;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String deepCopyTipText() {
    return "If enabled, the local storage gets copied using a deep copy.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "numThreads", Performance.getNumThreadsQuickInfo(m_NumThreads));
    result += QuickInfoHelper.toString(this, "useLocalStorage", m_UseLocalStorage, "local storage", ", ");
    if (m_UseLocalStorage || QuickInfoHelper.hasVariable(this, "useLocalStorage"))
      result += " (" + QuickInfoHelper.toString(this, "deepCopy", m_DeepCopy, "deep copy") + ")";

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
    super.forceVariables(value);
    m_Actors.forceVariables(value);
  }

  /**
   * Returns the size of the group.
   *
   * @return		the number of actors
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
   */
  @Override
  public void set(int index, Actor actor) {
    m_Actors.set(index, actor);
    reset();
    updateParent();
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
   */
  public void add(Actor actor) {
    add(size(), actor);
  }

  /**
   * Inserts the actor at the given position.
   *
   * @param index	the position
   * @param actor	the actor to insert
   */
  public void add(int index, Actor actor) {
    m_Actors.add(index, actor);
    reset();
    updateParent();
  }

  /**
   * Removes the actor at the given position and returns the removed object.
   *
   * @param index	the position
   * @return		the removed actor
   */
  public Actor remove(int index) {
    Actor	result;

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
   * @return		<!-- flow-accepts-start -->adams.flow.core.Unknown.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    if (m_Actors != null)
      return m_Actors.accepts();
    else
      return new Class[]{Unknown.class};
  }

  /**
   * Gets called in the setUp() method. Returns null if load-actors are fine,
   * otherwise error message.
   *
   * @return		null if everything OK, otherwise error message
   */
  protected String setUpLoadActors() {
    String			result;
    Hashtable<String,Integer>	count;

    result = null;
    count  = ActorUtils.findCallableTransformers(m_Actors);
    if (count.size() > 0)
      result = "Load-actors contain callable transformers, no load-balancing possible: " + count.keySet();

    return result;
  }

  /**
   * Performs the setUp of the sub-actors.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String setUpSubActors() {
    String	result;

    result = null;

    if (m_Actors.active() == 0)
      result = "No tee-actors provided!";

    if ((result == null) && (!getSkip())) {
      updateParent();
      result = setUpLoadActors();
    }

    return result;
  }

  /**
   * Initializes the sub-actors for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String		result;
    List<Actor>	actors;

    result = super.setUp();

    if (result == null) {
      // check for callable actors
      actors = ActorUtils.enumerate(
	  m_Actors, 
	  new Class[]{CallableTransformer.class, CallableSink.class});
      if (actors.size() > 0)
	result = "No callable transformer or sink allowed!";
    }
    
    if (result == null) {
      m_ActualNumThreads = Performance.determineNumThreads(m_NumThreads);
      m_ThreadsSpawned   = 0;
      m_Executor         = new PausableFixedThreadPoolExecutor(m_ActualNumThreads);
    }

    return result;
  }

  /**
   * The method that accepts the input token and then processes it.
   *
   * @param token	the token to accept and process
   */
  public void input(Token token) {
    m_CurrentToken = token;
    while ((m_Executor.getActiveCount() >= m_Executor.getMaximumPoolSize()) && !isStopped()) {
      if (isLoggingEnabled())
	getLogger().info("Waiting for free thread...");
      try {
	synchronized(m_Executor) {
	  m_Executor.wait(100);
	}
      }
      catch (Exception e) {
	// ignored
      }
    }
  }

  /**
   * Returns whether an input token is currently present.
   *
   * @return		true if input token present
   */
  public boolean hasInput() {
    return (m_CurrentToken != null);
  }

  /**
   * Returns the current input token, if any.
   *
   * @return		the input token, null if none present
   */
  public Token currentInput() {
    return m_CurrentToken;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    CallableWithResult<String>	job;
    final Flow			shell;
    final int 			count;
    final Token			token;
    int				i;
    Object			svalue;
    String			inputName;
    StorageValue		storageValue;
    DeleteStorageValue		delValue;
    Variables			vars;

    result = null;

    // we create copies of sub-flows, which might contain callable actors
    // to avoid errors, we need to turn off check here
    // any other callable name errors should have been captured already in
    // "setUp()" call when starting the flow
    getScopeHandler().setEnforceCallableNameCheck(false);
    
    m_ThreadsSpawned++;
    token = m_CurrentToken;
    count = m_ThreadsSpawned;
    inputName = UniqueIDs.next() + "-" + count;
    vars = getVariables().getClone();
    shell = new Flow();
    shell.setName(getFullName());
    shell.setLoggingLevel(getLoggingLevel());
    shell.setAnnotations(new BaseAnnotation("Thread #" + count));
    storageValue = new StorageValue();
    storageValue.setStorageName(new StorageName(inputName));
    shell.add(storageValue);
    delValue = new DeleteStorageValue();
    delValue.setStorageName(new StorageName(inputName));
    shell.add(delValue);
    for (i = 0; i < m_Actors.size(); i++)
      shell.add((Actor) OptionUtils.shallowCopy(m_Actors.get(i), false, false));
    shell.getVariables().assign(vars);
    shell.setUp();
    for (StorageName sname: getStorageHandler().getStorage().keySet()) {
      svalue = getStorageHandler().getStorage().get(sname);
      if (m_UseLocalStorage)
	shell.getStorage().put(sname, ObjectCopyHelper.copyObject(svalue));
      else
	shell.getStorage().put(sname, svalue);
    }
    shell.getStorage().put(new StorageName(inputName), token.getPayload());
    m_ToCleanUp.add(shell);
    job = new CallableWithResult<String>() {
      protected String doCall() throws Exception {
	String result = null;
	try {
	  if (isLoggingEnabled())
	    getLogger().info("Starting thread #" + count);
	  result = shell.execute();
	  if (result != null)
	    shell.getLogger().severe(result);
	  if (isLoggingEnabled())
	    getLogger().info("...finished thread #" + (count) + ((result == null) ? "" : " with error"));
	}
	catch (Exception e) {
	  result = handleException("Failed to execute thread #" + count + ": ", e);
	}

	return result;
      }
    };
    synchronized(m_Executor) {
      m_Executor.submit(job);
    }

    return result;
  }

  /**
   * Finishes up the execution.
   */
  @Override
  public void wrapUp() {
    if (m_Executor != null) {
      m_Executor.shutdown();
      while (!m_Executor.isTerminated()) {
	try {
	  m_Executor.awaitTermination(100, TimeUnit.MILLISECONDS);
	}
	catch (Exception e) {
	  // ignored
	}
      }
      m_Executor = null;
    }
    m_CurrentToken = null;

    super.wrapUp();
  }
  
  /**
   * Stops the processing of tokens without stopping the flow.
   */
  public void flushExecution() {
    for (Actor actor: m_ToCleanUp) {
      if (actor instanceof ActorHandler)
	((ActorHandler) actor).flushExecution();
    }
  }

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    if (m_Executor != null) {
      try {
	synchronized(m_Executor) {
	  m_Executor.notifyAll();
	  m_Executor.shutdownNow();
	}
      }
      catch (Exception e) {
	// ignored
      }
    }

    super.stopExecution();
  }

  /**
   * Cleans up after the execution has finished. Also removes graphical
   * components.
   */
  @Override
  public void cleanUp() {
    int		i;

    m_CurrentToken = null;

    for (i = 0; i < m_ToCleanUp.size(); i++)
      m_ToCleanUp.get(i).cleanUp();
    m_ToCleanUp.clear();

    super.cleanUp();
  }
}
