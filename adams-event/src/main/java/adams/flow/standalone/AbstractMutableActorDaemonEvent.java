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
 * AbstractMutableActorDaemonEvent.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.core.Variables;
import adams.core.logging.LoggingLevel;
import adams.flow.control.Sequence;
import adams.flow.core.AbstractActor;
import adams.flow.core.ActorHandlerInfo;
import adams.flow.core.DaemonEvent;
import adams.flow.core.MutableActorHandler;
import adams.flow.core.Token;
import com.jidesoft.utils.SwingWorker;

/**
 * Ancestor for daemon events that handle sub-actors.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <E> the type of event
 * @param <P> the type of the processed event
 */
public abstract class AbstractMutableActorDaemonEvent<E, P>
  extends AbstractStandalone
  implements MutableActorHandler, DaemonEvent {

  /** for serialization. */
  private static final long serialVersionUID = 4670761846363281951L;

  /** the result if actors are currently being executed. */
  public final static String BUSY = "BUSY";

  /** the result preprocessing didn't generate any output. */
  public final static String NO_OUTPUT = "No output produced";

  /** whether to discard change events when busy or not. */
  protected boolean m_NoDiscard;

  /** for actors that get executed. */
  protected Sequence m_Actors;
  
  /** whether the actors are currently being executed. */
  protected boolean m_ExecutingActors;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "no-discard", "noDiscard",
	    false);

    m_OptionManager.add(
	    "actor", "actors",
	    new AbstractActor[0]);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Actors = new Sequence();
    m_Actors.setAllowSource(true);
    m_Actors.setAllowStandalones(true);
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
   * Updates the parent of all actors in this group.
   */
  protected void updateParent() {
    m_Actors.setParent(null);
    m_Actors.setParent(this);
  }

  /**
   * Sets whether to process all change events or discard if busy.
   *
   * @param value 	if true all change events get processed
   */
  public void setNoDiscard(boolean value) {
    m_NoDiscard = value;
    reset();
  }

  /**
   * Returns whether to process all change events or discard if busy.
   *
   * @return 		true if all change events get processed
   */
  public boolean getNoDiscard() {
    return m_NoDiscard;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String noDiscardTipText() {
    return "If enabled, no change event gets discarded; CAUTION: enabling this option can slow down the system significantly.";
  }

  /**
   * Checks the actors before they are set via the setActors method.
   * Returns an error message if the actors are not acceptable, null otherwise.
   *
   * @param actors	the actors to check
   * @return		null if accepted, otherwise error message
   */
  protected abstract String checkActors(AbstractActor[] actors);

  /**
   * Sets the actors to execute on schedule.
   *
   * @param value	the actors
   */
  public void setActors(AbstractActor[] value) {
    String	msg;

    msg = checkActors(value);
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
   * Returns the actors to execute on schedule.
   *
   * @return		the actors
   */
  public AbstractActor[] getActors() {
    return m_Actors.getActors();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String actorsTipText() {
    return "The actors to execute in case of a change event.";
  }

  /**
   * Checks whether the event is being handled.
   *
   * @param e		the event to check
   * @return		true if being handled
   */
  protected abstract boolean handlesEvent(E e);

  /**
   * Preprocesses the event.
   *
   * @param e		the event to preprocess
   * @return		the output of the preprocessing
   */
  protected abstract P preProcessEvent(E e);

  /**
   * Returns whether the preprocessed event is used as input token.
   *
   * @return		true if used as input token
   */
  protected abstract boolean usePreProcessedAsInput();

  /**
   * Processes the event.
   *
   * @return		null if execution successful, otherwise error message
   */
  protected String processEvent(E e) {
    SwingWorker		worker;
    final P 		preprocessed;

    if (!handlesEvent(e))
      return null;

    preprocessed = preProcessEvent(e);
    if (preprocessed == null)
      return NO_OUTPUT;

    if (m_ExecutingActors) {
      if (!m_NoDiscard) {
	return BUSY;
      }
      else {
	while (m_ExecutingActors && !isStopped()) {
	  try {
	    synchronized(this) {
	      wait(10);
	    }
	  }
	  catch (Exception ex) {
	    // ignored
	  }
	}
      }
    }

    if (isStopped())
      return null;
    
    m_ExecutingActors = true;
    worker = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
	if (usePreProcessedAsInput())
	  m_Actors.input(new Token(preprocessed));
	String result = m_Actors.execute();
	return result;
      }
      @Override
      protected void done() {
        super.done();
	m_ExecutingActors = false;
      }
    };
    worker.execute();
    
    return null;
  }

  /**
   * Returns the size of the group.
   *
   * @return		the number of actors
   */
  public int size() {
    return m_Actors.size();
  }

  /**
   * Returns the actor at the given position.
   *
   * @param index	the position
   * @return		the actor
   */
  public AbstractActor get(int index) {
    return m_Actors.get(index);
  }

  /**
   * Sets the actor at the given position.
   *
   * @param index	the position
   * @param actor	the actor to set at this position
   */
  public void set(int index, AbstractActor actor) {
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
  public int indexOf(String actor) {
    return m_Actors.indexOf(actor);
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
  public AbstractActor remove(int index) {
    AbstractActor	result;

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
  public ActorHandlerInfo getActorHandlerInfo() {
    return m_Actors.getActorHandlerInfo();
  }

  /**
   * Returns the number of non-skipped actors.
   *
   * @return		the 'active' actors
   */
  public int active() {
    return m_Actors.active();
  }

  /**
   * Returns the first non-skipped actor.
   *
   * @return		the first 'active' actor, null if none available
   */
  public AbstractActor firstActive() {
    return m_Actors.firstActive();
  }

  /**
   * Returns the last non-skipped actor.
   *
   * @return		the last 'active' actor, null if none available
   */
  public AbstractActor lastActive() {
    return m_Actors.lastActive();
  }

  /**
   * Performs checks on the "sub-actors".
   *
   * @return		null if everything OK, otherwise error message
   */
  public String check() {
    String	result;
    
    result = m_Actors.check();
    
    if (result == null)
      result = checkActors(m_Actors.getActors());
    
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
    int		i;
    
    super.forceVariables(value);
    
    for (i = 0; i < size(); i++)
      get(i).setVariables(value);
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

    if (result == null)
      result = m_Actors.setUp();

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		always null
   */
  @Override
  protected String doExecute() {
    return null;
  }
  
  /**
   * Stops the processing of tokens without stopping the flow.
   */
  public void flushExecution() {
    if (m_Actors != null)
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
   * Cleans up after the execution has finished. Graphical output is left
   * untouched.
   */
  @Override
  public void wrapUp() {
    if (m_Actors != null)
      m_Actors.wrapUp();

    super.wrapUp();
  }

  /**
   * Cleans up after the execution has finished. Also removes graphical
   * components.
   */
  @Override
  public void cleanUp() {
    if (m_Actors != null)
      m_Actors.cleanUp();

    super.cleanUp();
  }
}
