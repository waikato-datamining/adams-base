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
 * AbstractTee.java
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import adams.core.QuickInfoHelper;
import adams.core.logging.LoggingLevel;
import adams.flow.core.Actor;
import adams.flow.core.ActorHandlerInfo;
import adams.flow.core.InputConsumer;
import adams.flow.core.MutableActorHandler;
import adams.flow.core.OutputProducer;
import adams.flow.core.Token;
import adams.flow.core.Unknown;
import adams.flow.sink.Null;

import javax.swing.SwingWorker;
import java.util.Hashtable;

/**
 * Abstract ancestor for actors that tee-off tokens.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractTee
  extends AbstractControlActor
  implements InputConsumer, OutputProducer, MutableActorHandler, AtomicExecution {

  /** for serialization. */
  private static final long serialVersionUID = 7132280825125548047L;

  /**
   * SwingWorker for asynchronous execution of the the tee's sub-actors.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class TeeSwingWorker
    extends SwingWorker<String,Object> {

    /** the owning tee actor. */
    protected AbstractTee m_Owner;
    
    /** the input token. */
    protected Token m_Input;
    
    /**
     * Initializes the worker.
     * 
     * @param owner	the owning tee actor
     * @param input	the current input token
     */
    public TeeSwingWorker(AbstractTee owner, Token input) {
      super();
      m_Owner  = owner;
      m_Input  = input;
    }
    
    /**
     * Executes the sub-actors.
     * 
     * @return		the result of the execution, null if everything OK, otherwise error message
     */
    @Override
    protected String doInBackground() throws Exception {
      String	result;
      
      result = m_Owner.processInput(m_Input);
      if (result != null)
	result = m_Owner.getErrorHandler().handleError(m_Owner, "tee", result);
      
      return result;
    }
    
    /**
     * Execution finished.
     */
    @Override
    protected void done() {
      m_Owner.finishedAsynchronousExecution();
      super.done();
    }
  }
  
  /** the key for storing the input token in the backup. */
  public final static String BACKUP_INPUT = "input";

  /** the flow items. */
  protected Sequence m_Actors;

  /** the input token. */
  protected transient Token m_InputToken;

  /** the output token. */
  protected transient Token m_OutputToken;

  /** the minimum active actors this handler requires. */
  protected int m_MinimumActiveActors;
  
  /** whether to execute sub-actors asynchronously. */
  protected boolean m_Asynchronous;
  
  /** the swingworker in use for asynchronous execution. */
  protected TeeSwingWorker m_AsynchronousWorker;

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
	    "asynchronous", "asynchronous",
	    false);

    m_OptionManager.add(
	    "tee", "actors",
	    new Actor[0]);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Actors = new Sequence();
    m_Actors.setActors(new Actor[]{new Null()});
    m_MinimumActiveActors = 1;
    m_AsynchronousWorker  = null;
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
   * Performs checks on the "sub-actors".
   *
   * @return		null if checks passed or null in case of an error
   */
  @Override
  public String check() {
    return checkTeeActors(getActors());
  }

  /**
   * Checks the tee actor before it is set.
   * Returns an error message if the actor is not acceptable, null otherwise.
   *
   * @param index	the index the actor gets set
   * @param actor	the actor to check
   * @return		null if accepted, otherwise error message
   */
  protected abstract String checkTeeActor(int index, Actor actor);

  /**
   * Checks the tee actors before they are set via the setTeeActors method.
   * Returns an error message if the actors are not acceptable, null otherwise.
   *
   * @param actors	the actors to check
   * @return		null if accepted, otherwise error message
   */
  protected abstract String checkTeeActors(Actor[] actors);

  /**
   * Sets the actor to tee-off to.
   *
   * @param value	the actor
   */
  public void setActors(Actor[] value) {
    String	msg;

    msg = checkTeeActors(value);
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
   * Returns the actors to tee-off to.
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
    return "The actors to siphon-off the tokens to.";
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
   * Sets whether to finish processing before stopping execution.
   * 
   * @param value	if true then actor finishes processing first 
   */
  public void setAsynchronous(boolean value) {
    m_Asynchronous = value;
    reset();
  }
  
  /**
   * Returns whether to finish processing before stopping execution.
   * 
   * @return		true if actor finishes processing first
   */
  public boolean getAsynchronous() {
    return m_Asynchronous;
  }
  
  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String asynchronousTipText() {
    return 
	"If enabled, the sub-actors get executed asynchronously rather "
	+ "than the flow waiting for them to finish before proceeding with "
	+ "execution.";
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
   * @see		#checkTeeActor(int, Actor)
   */
  @Override
  public void set(int index, Actor actor) {
    String	msg;

    msg = checkTeeActor(index, actor);
    if (msg == null) {
      m_Actors.set(index, actor);
      reset();
      updateParent();
    }
    else {
      throw new IllegalArgumentException(msg);
    }
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
   * @see		#checkTeeActor(int, Actor)
   */
  public void add(int index, Actor actor) {
    String	msg;

    msg = checkTeeActor(index, actor);
    if (msg == null) {
      m_Actors.add(index, actor);
      reset();
      updateParent();
    }
    else {
      throw new IllegalArgumentException(msg);
    }
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
   * Returns the internal representation of the actors.
   * 
   * @return		the internal actors
   */
  protected Sequence getInternalActors() {
    return m_Actors;
  }
  
  /**
   * Called by the asynchronous swingworker when finished.
   */
  protected void finishedAsynchronousExecution() {
    m_AsynchronousWorker = null;
  }
  
  /**
   * Waits for the asynchronous execution to finish.
   */
  protected void waitForAsynchronousExecution() {
    while (m_AsynchronousWorker != null) {
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
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	value;
    
    result = null;
    value = QuickInfoHelper.toString(this, "finishBeforeStopping", getFinishBeforeStopping(), "finish");
    if ((value != null) && (value.length() > 0))
      result = value;
    value = QuickInfoHelper.toString(this, "asynchronous", m_Asynchronous, "asynchronous");
    if ((value != null) && (value.length() > 0))
      result = (((result == null) || (result.length() == 0)) ? "" : result + ", ") + value;
    
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

    if (m_InputToken != null)
      result.put(BACKUP_INPUT, m_InputToken);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_INPUT)) {
      m_InputToken = (Token) state.get(BACKUP_INPUT);
      state.remove(BACKUP_INPUT);
    }

    super.restoreState(state);
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.flow.core.Unknown.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Unknown.class};
  }

  /**
   * Gets called in the setUp() method. Returns null if tee-actors are fine,
   * otherwise error message.
   *
   * @return		null if everything OK, otherwise error message
   */
  protected abstract String setUpTeeActors();

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_InputToken  = null;
    m_OutputToken = null;
  }

  /**
   * Sets the minimum of (active) actors that need to be present in the
   * tee branch.
   *
   * @param value	the required minimum of actors
   */
  public void setMinimumActiveActors(int value) {
    m_MinimumActiveActors = value;
  }

  /**
   * Returns the minimum of (active) actors that need to be present in the
   * tee branch.
   *
   * @return		the required minimum of actors
   */
  public int getMinimumActiveActors() {
    return m_MinimumActiveActors;
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

    if (m_Actors.active() < getMinimumActiveActors())
      result = "Not enough (active) sub-actors provided; required " + getMinimumActiveActors() + ", found " + m_Actors.active();

    if ((result == null) && (!getSkip())) {
      updateParent();
      result = setUpTeeActors();
      if (result == null)
	result = m_Actors.setUp();
    }

    return result;
  }

  /**
   * Creates the token to tee-off.
   *
   * @param token	the input token
   * @return		the token to tee-off
   */
  protected Token createTeeToken(Token token) {
    return token;
  }

  /**
   * The method that accepts the input token and then processes it.
   *
   * @param token	the token to accept and process
   */
  public void input(Token token) {
    m_InputToken  = token;
    m_OutputToken = null;
  }

  /**
   * Returns whether an input token is currently present.
   *
   * @return		true if input token present
   */
  public boolean hasInput() {
    return (m_InputToken != null);
  }

  /**
   * Returns the current input token, if any.
   *
   * @return		the input token, null if none present
   */
  public Token currentInput() {
    return m_InputToken;
  }

  /**
   * Returns whether the token can be processed in the tee actor.
   *
   * @param token	the token to process
   * @return		true if token can be processed
   */
  protected boolean canProcessInput(Token token) {
    return true;
  }

  /**
   * Processes the token normal, i.e., not in thread.
   *
   * @param token	the token to process
   * @return		an optional error message, null if everything OK
   */
  protected String processInput(Token token) {
    String	result;

    try {
      if (getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
	getFlowExecutionListeningSupporter().getFlowExecutionListener().preInput(this, token);
      m_Actors.input(createTeeToken(token));
      if (getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
	getFlowExecutionListeningSupporter().getFlowExecutionListener().postInput(this);
      if (getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
	getFlowExecutionListeningSupporter().getFlowExecutionListener().preExecute(this);
      result = m_Actors.execute();
      if (getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
	getFlowExecutionListeningSupporter().getFlowExecutionListener().postExecute(this);
    }
    catch (Exception e) {
      result = handleException(m_Actors.getFullName() + " generated following exception:", e);
    }

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;

    result = null;

    if (canProcessInput(m_InputToken)) {
      if (isLoggingEnabled())
	getLogger().info("Teeing off: " + m_InputToken.getPayload());
      
      if (m_Asynchronous) {
	waitForAsynchronousExecution();
	m_AsynchronousWorker = new TeeSwingWorker(this, m_InputToken);
	m_AsynchronousWorker.execute();
      }
      else {
	result = processInput(m_InputToken);
	if (result != null)
	  result = getErrorHandler().handleError(this, "tee", result);
      }
    }

    // make token available on the output port
    m_OutputToken = m_InputToken;
    m_InputToken  = null;

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
    
    if (m_Skip)
      m_OutputToken = m_InputToken;
    
    return result;
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.flow.core.Unknown.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{Unknown.class};
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  public Token output() {
    Token	result;

    result        = m_OutputToken;
    m_OutputToken = null;

    return result;
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  public boolean hasPendingOutput() {
    return (m_OutputToken != null);
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
    super.stopExecution();
    m_Actors.stopExecution();
  }

  /**
   * Finishes up the execution.
   */
  @Override
  public void wrapUp() {
    waitForAsynchronousExecution();
    m_Actors.wrapUp();
    super.wrapUp();
  }

  /**
   * Cleans up after the execution has finished. Also removes graphical
   * components.
   */
  @Override
  public void cleanUp() {
    m_Actors.cleanUp();
    super.cleanUp();
  }
}
