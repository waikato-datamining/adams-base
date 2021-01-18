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
 * SequentialDirector.java
 * Copyright (C) 2009-2019 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import adams.core.logging.LoggingHelper;
import adams.core.logging.LoggingLevel;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import adams.flow.core.InputConsumer;
import adams.flow.core.OptionalStopRestrictor;
import adams.flow.core.OutputProducer;
import adams.flow.core.Token;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;

/**
 * Manages the execution of actors in sequential order.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SequentialDirector
  extends AbstractDirector {

  /** for serialization. */
  private static final long serialVersionUID = -1634725837304059804L;

  /** for storing the token that the last actor generated. */
  protected transient List<Token> m_FinalOutput;

  /** whether execution has finished. */
  protected boolean m_Finished;

  /** whether the director was executed at all. */
  protected boolean m_Executed;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_FinalOutput = null;
  }

  /**
   * Returns whether the final output of actors is recorded.
   * <br><br>
   * Default implementation returns false.
   *
   * @return		true if final output is to be recorded
   */
  protected boolean isFinalOutputRecorded() {
    return false;
  }

  /**
   * Returns the vector for storing final outputs. Gets instantiated if
   * necessary.
   *
   * @return		the vector for storing the tokens
   * @see		#m_FinalOutput
   */
  protected List<Token> getFinalOutput() {
    if (m_FinalOutput == null)
      m_FinalOutput = new ArrayList<>();

    return m_FinalOutput;
  }

  /**
   * Checks whether the actor has already stopped. If so, outputs a message
   * on the commandline and returns the error message.
   *
   * @param actor	the actor to check
   * @return		null if actor not stopped, otherwise the error message
   */
  protected String checkActorHasStopped(Actor actor) {
    String	result;
    Throwable	th;
    boolean	reportStopped;

    result = null;

    if (actor.isStopped()) {
      reportStopped = true;
      if (actor instanceof OptionalStopRestrictor) {
        if (((OptionalStopRestrictor) actor).isRestrictedStop())
          reportStopped = false;
      }
      if (reportStopped) {
	th = new Throwable();
	th.fillInStackTrace();
	result = th.getStackTrace()[1].getMethodName() + ": Actor '" + actor.getFullName() + "' is stopped!";
      }
    }

    return result;
  }

  /**
   * Handles the error message, by outputting the exception on stderr and
   * generating/returning a combined error string.
   *
   * @param actor	the actor that generated the exception
   * @param msg		the error message
   * @param t		the exception
   * @return		the combined error string
   */
  protected String handleException(Actor actor, String msg, Throwable t) {
    return LoggingHelper.handleException(actor, msg, t, !hasControlActor() || m_ControlActor.getSilent());
  }

  /**
   * Handles the given error message, calling the actor's error handler, if
   * defined.
   *
   * @param actor       the actor to let the error handle
   * @param action      the action when the error occurred
   * @param msg         the error message, skips handling if null
   * @return            the (potentially) updated error message
   */
  protected String handleError(Actor actor, String action, String msg) {
    if (msg == null)
      return null;
    if (actor.hasErrorHandler()) {
      if (getLoggingLevel().isAtLeast(Level.INFO))
        getLogger().info("Error handler: " + actor.getErrorHandler().hashCode());
      return actor.getErrorHandler().handleError(actor, action, msg);
    }
    return msg;
  }

  /**
   * Presents the specified token to the actor.
   *
   * @param actor	the actor to use (InputConsumer)
   * @param input	the input token
   * @return		the error message, null if everything OK
   */
  protected String doInput(Actor actor, Token input) {
    String	result;

    result = null;

    if ((result = checkActorHasStopped(actor)) != null)
      return result;

    try {
      if (actor.getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
	actor.getFlowExecutionListeningSupporter().getFlowExecutionListener().preInput(actor, input);

      ((InputConsumer) actor).input(input);

      if (actor.getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
	actor.getFlowExecutionListeningSupporter().getFlowExecutionListener().postInput(actor);
    }
    catch (Throwable t) {
      result = handleException(actor, "Calling the 'input(Token)' method with token '" + input + "' generated error: ", t);
    }

    result = handleError(actor, "input", result);

    return result;
  }

  /**
   * Calls the execute() method of the actor.
   *
   * @param actor	the actor to use
   * @return		a potential error message
   */
  protected String doExecute(Actor actor) {
    String	result;
    String	msg;

    if ((msg = checkActorHasStopped(actor)) != null)
      return msg;
    if (isFlushing())
      return "Flushing execution!";

    try {
      if (getLoggingLevel().isAtLeast(Level.FINEST))
	getLogger().finest("Size before 'execute()': " + actor.sizeOf() + " [" + actor.getFullName() + "]");
      if (actor.getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
	actor.getFlowExecutionListeningSupporter().getFlowExecutionListener().preExecute(actor);

      result = actor.execute();

      if (actor.getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
	actor.getFlowExecutionListeningSupporter().getFlowExecutionListener().postExecute(actor);
      if (getLoggingLevel().isAtLeast(Level.FINEST))
	getLogger().finest("Size after 'execute()': " + actor.sizeOf() + " [" + actor.getFullName() + "]");
    }
    catch (Throwable t) {
      result = handleException(actor, "Calling the 'execute()' method generated error: ", t);
    }

    result = handleError(actor, "execute", result);

    return result;
  }

  /**
   * Checks whether the actor has pending output.
   *
   * @param actor	the actor to use (OutputProducer)
   * @return		the token that was retrieved from the actor, null
   * 			in case of an error and not 'stopping on errors'
   * @see		AbstractDirectedControlActor#getStopFlowOnError()
   */
  protected boolean doHasOutput(Actor actor) {
    boolean	result;
    String	msgFull;

    if (checkActorHasStopped(actor) != null)
      return false;
    if (isFlushing())
      return false;

    msgFull = null;

    try {
      result = ((OutputProducer) actor).hasPendingOutput();
    }
    catch (Throwable t) {
      msgFull = handleException(actor, "Calling the 'hasPendingOutput()' method generated error: ", t);
      result = false;
    }

    handleError(actor, "hasPendingOutput", msgFull);

    return result;
  }

  /**
   * Retrieves the token from the actor.
   *
   * @param actor	the actor to use
   * @return		the token that was retrieved from the actor, null
   * 			in case of an error and not 'stopping on errors'
   * @see		AbstractDirectedControlActor#getStopFlowOnError()
   */
  protected Token doOutput(Actor actor) {
    Token	result;
    String	msgFull;

    if (checkActorHasStopped(actor) != null)
      return null;
    if (isFlushing())
      return null;

    msgFull = null;

    try {
      if (getLoggingLevel().isAtLeast(Level.FINEST))
	getLogger().finest("Size before 'output()': " + actor.sizeOf() + " [" + actor.getFullName() + "]");
      if (actor.getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
	actor.getFlowExecutionListeningSupporter().getFlowExecutionListener().preOutput(actor);

      result = ((OutputProducer) actor).output();

      if (actor.getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
	actor.getFlowExecutionListeningSupporter().getFlowExecutionListener().postOutput(actor, result);
      if (getLoggingLevel().isAtLeast(Level.FINEST))
	getLogger().finest("Size after 'output()': " + actor.sizeOf() + " [" + actor.getFullName() + "]");
    }
    catch (Throwable t) {
      msgFull = handleException(actor, "Calling the 'output()' method generated error: ", t);
      result = null;
    }

    handleError(actor, "output", msgFull);

    return result;
  }

  /**
   * Returns the indices of the active (= non-skipped) actors.
   *
   * @return		the indices of active actors
   */
  protected int[] activeIndices() {
    return activeIndices(0, m_ControlActor.size() - 1);
  }

  /**
   * Returns the indices of the active (= non-skipped) actors in the given range.
   *
   * @param from	the first actor (incl)
   * @param to		the last actor (incl)
   * @return		the indices of active actors
   */
  protected int[] activeIndices(int from, int to) {
    TIntList	result;
    int		i;

    result = new TIntArrayList();
    for (i = from; i <= to; i++) {
      if (!m_ControlActor.get(i).getSkip())
        result.add(i);
    }

    return result.toArray();
  }

  /**
   * Executes all the standalone actors. Returns the first non-standalone actor.
   *
   * @return		the first non-standalone actor or null if non present
   */
  protected Actor doExecuteStandalones() {
    Actor	result;
    Actor	curr;
    String	actorResult;
    int[]	indices;

    result = null;

    indices = activeIndices();
    for (int i: indices) {
      // paused?
      if (m_Paused)
	pause();

      // stopped?
      if (isStopped() || isStopping() || isFlushing())
	break;

      curr = m_ControlActor.get(i);

      if (!ActorUtils.isStandalone(curr)) {
	result = curr;
	break;
      }
      else {
	actorResult = doExecute(curr);
	if (actorResult == null)
	  actorResult = checkActorHasStopped(curr);
	if ((actorResult != null) && !m_ControlActor.getSilent()) {
	  getLogger().severe(
	      curr.getFullName() + " generated following error output:\n"
		  + actorResult);
	  if (curr.getStopFlowOnError()) {
	    stopExecution();
	    break;
	  }
	}
      }
    }

    return result;
  }

  /**
   * Peforms the execution of the actors.
   *
   * @param startActor	the actor to start with
   * @return		null if everything ok, otherwise the error message
   */
  protected String doExecuteActors(Actor startActor) {
    String		result;
    boolean		finished;
    int			startIndex;
    Actor		notFinishedActor;
    Stack<Actor>	pendingActors;
    Token		token;
    Actor		curr;
    String		actorResult;
    int			lastActive;
    int[]		indices;

    result           = null;
    notFinishedActor = startActor;
    pendingActors    = new Stack<>();
    getFinalOutput().clear();
    do {
      if (getLoggingLevel().isAtLeast(Level.INFO))
	getLogger().info("--> iteration start");

      // paused?
      if (isPaused())
	pause();

      // do we have to stop the execution?
      if (isStopped() || isStopping() || isFlushing())
	break;

      // determing starting point of next iteration
      if (pendingActors.size() > 0) {
	startIndex = m_ControlActor.indexOf(pendingActors.peek().getName());
      }
      else {
	startIndex       = m_ControlActor.indexOf(notFinishedActor.getName());
	notFinishedActor = null;
      }
      if (getLoggingLevel().isAtLeast(Level.FINE))
	getLogger().fine("Start index: " + startIndex);

      // iterate over actors
      curr       = null;
      token      = null;
      lastActive = -1;
      if (m_ControlActor.active() > 0)
	lastActive = m_ControlActor.lastActive().index();
      indices = activeIndices(startIndex, lastActive);
      for (int i: indices) {
	// paused?
	if (isPaused())
	  pause();

	// do we have to stop the execution?
	if (isStopped() || isStopping() || isFlushing())
	  break;

	curr = m_ControlActor.get(i);
	if (curr.getSkip())
	  continue;
	if (getLoggingLevel().isAtLeast(Level.FINE))
	  getLogger().fine("Current actor: " + curr.getFullName());

	// no token? get pending one or produce new one
	if (token == null) {
	  if ((curr instanceof OutputProducer) && doHasOutput(curr)) {
            if (pendingActors.size() > 0)
              pendingActors.pop();
	    if (getLoggingLevel().isAtLeast(Level.FINE))
	      getLogger().fine("Actor holds another output token: " + curr.getFullName());
	  }
	  else {
	    actorResult = doExecute(curr);
	    if ((actorResult != null) && !m_ControlActor.getSilent()) {
	      getLogger().severe(
		  curr.getFullName() + " generated following error output:\n"
		  + actorResult);
              if (curr.getStopFlowOnError())
                result = actorResult;
	      if (curr.getStopFlowOnError() || isFlushing())
		break;
	    }
	    if (!curr.isFinished() && (notFinishedActor == null))
	      notFinishedActor = curr;
	    if (getLoggingLevel().isAtLeast(Level.FINER))
	      getLogger().finer("Actor needed to be executed: " + curr.getFullName());
	  }

	  if ((curr instanceof OutputProducer) && doHasOutput(curr))
	    token = doOutput(curr);
	  else
	    token = null;
	  if (getLoggingLevel() == LoggingLevel.FINEST)
	    getLogger().finest("Token obtained from output: " + token);
	  else if (getLoggingLevel().isAtLeast(Level.FINE))
	    getLogger().fine("Token obtained from output");

	  // still more to come?
	  if ((curr instanceof OutputProducer) && doHasOutput(curr)) {
	    pendingActors.push(curr);
	    if (getLoggingLevel().isAtLeast(Level.FINE))
	      getLogger().fine("Actor has more tokens on output: " + curr.getFullName());
	  }
	}
	else {
	  // process token
	  doInput(curr, token);
	  actorResult = doExecute(curr);
	  if ((actorResult != null) && !m_ControlActor.getSilent()) {
	    getLogger().severe(
		curr.getFullName() + " generated following error output:\n"
		+ actorResult);
            if (curr.getStopFlowOnError())
              result = actorResult;
	    if (curr.getStopFlowOnError() || isFlushing())
	      break;
	  }
	  if (!curr.isFinished() && (notFinishedActor == null))
	    notFinishedActor = curr;
	  if (getLoggingLevel() == LoggingLevel.FINEST)
	    getLogger().finer("Actor processes token: " + curr.getFullName() + "/" + token);
	  else if (getLoggingLevel().isAtLeast(Level.FINE))
	    getLogger().fine("Actor processes token: " + curr.getFullName());

	  // was a new token produced?
	  if (curr instanceof OutputProducer) {
	    if (doHasOutput(curr))
	      token = doOutput(curr);
	    else
	      token = null;
	    if (getLoggingLevel().isAtLeast(Level.FINE))
	      getLogger().fine("Actor also produces tokens: " + curr.getFullName());

	    // still more to come?
	    if (doHasOutput(curr)) {
	      if (getLoggingLevel().isAtLeast(Level.FINE))
		getLogger().fine("Actor also has more tokens on output: " + curr.getFullName());
	      pendingActors.push(curr);
	    }
	  }
	  else {
	    token = null;
	  }
	}

        if (!hasControlActor())
          continue;

	// token from last actor generated? -> store
	if ((i == lastActive) && (token != null)) {
	  if (isFinalOutputRecorded() && !isFlushing())
	    getFinalOutput().add(token);
	}

	// no token produced, ignore rest of actors
	if ((curr instanceof OutputProducer) && (token == null)) {
	  if (getLoggingLevel().isAtLeast(Level.FINE))
	    getLogger().fine("No token generated, skipping rest of actors: " + curr.getFullName());
	  break;
	}
      }

      // all actors finished?
      if (getLoggingLevel().isAtLeast(Level.FINE))
	getLogger().fine("notFinishedActor=" + notFinishedActor + ", pendingActors.size=" + pendingActors.size() + ", stopped=" + isStopped());
      finished = (notFinishedActor == null) && (pendingActors.size() == 0);
      if (getLoggingLevel().isAtLeast(Level.INFO))
	getLogger().info("---> execution finished: " + finished);
    }
    while (!(finished || isStopped() || isStopping() || isFlushing()));

    return result;
  }

  /**
   * Executes the group of actors.
   *
   * @return		null if everything went smooth
   */
  @Override
  public String execute() {
    String	result;
    String	msg;
    Actor	start;

    result     = null;
    start      = null;
    m_Finished = false;
    m_Executed = true;
    m_Flushing = false;

    if (m_ControlActor.getActorHandlerInfo().canContainStandalones() && !isFlushing()) {
      try {
	start = doExecuteStandalones();
	if (getLoggingLevel().isAtLeast(Level.INFO))
	  getLogger().info("doExecuteStandalones: " + ((start == null) ? "only standalones" : start.getFullName()));
      }
      catch (Throwable t) {
	result = handleException("Execution of standalones failed: ", t);
	if (getLoggingLevel().isAtLeast(Level.INFO))
	  getLogger().info("doExecuteStandalones: " + result);
      }
    }
    else {
      start = m_ControlActor.firstActive();
    }

    // execute other actors until finished
    if ((result == null) && !isStopped() && !isStopping() && !isFlushing()) {
      if (start != null) {
	if (getLoggingLevel().isAtLeast(Level.INFO))
	  getLogger().info("doExecuteActors: start");
	try {
	  msg = doExecuteActors(start);
	  if (getLoggingLevel().isAtLeast(Level.INFO))
	    getLogger().info("doExecuteActors: " + ((msg == null) ? "OK" : msg));
	  if (msg != null)
	    result = "Execution of actors failed: " + msg;
	}
	catch (Throwable t) {
	  result = handleException("Execution of actors died: ", t);
	  if (getLoggingLevel().isAtLeast(Level.INFO))
	    getLogger().info("doExecuteActors: " + result);
	}
	if (getLoggingLevel().isAtLeast(Level.INFO))
	  getLogger().info("doExecuteActors: end");
      }
    }

    getFinalOutput().clear();

    m_Finished = true;

    return result;
  }

  /**
   * Checks whether the director has finished.
   *
   * @return		true if execution finished (or stopped)
   */
  @Override
  public boolean isFinished() {
    return (m_Executed && m_Finished) || !m_Executed || m_Stopped;
  }

  /**
   * Stops the processing of tokens without stopping the flow.
   */
  @Override
  public void flushExecution() {
    super.flushExecution();

    if (m_FinalOutput != null)
      m_FinalOutput.clear();
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    super.stopExecution();

    getFinalOutput().clear();
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    super.cleanUp();

    if (m_FinalOutput != null)
      m_FinalOutput.clear();
  }
}

