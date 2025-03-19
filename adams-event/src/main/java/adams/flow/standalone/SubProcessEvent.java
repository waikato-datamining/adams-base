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
 * SubProcessEvent.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.core.MessageCollection;
import adams.core.Pausable;
import adams.core.QuickInfoHelper;
import adams.core.Variables;
import adams.core.logging.LoggingLevel;
import adams.flow.control.LocalScopeSubProcess;
import adams.flow.control.ScopeHandler.ScopeHandling;
import adams.flow.core.Actor;
import adams.flow.core.ActorHandlerInfo;
import adams.flow.core.DaemonEvent;
import adams.flow.core.MutableActorHandler;
import adams.flow.core.Token;
import adams.flow.standalone.subprocessevent.Null;
import adams.flow.standalone.subprocessevent.SubProcessEventTrigger;
import adams.flow.transformer.PassThrough;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Executes its sub-flow after a predefined number of milli-seconds.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
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
 * &nbsp;&nbsp;&nbsp;default: SubProcessEvent
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
 * <pre>-event-trigger &lt;adams.flow.standalone.subprocessevent.SubProcessEventTrigger&gt; (property: eventTrigger)
 * &nbsp;&nbsp;&nbsp;The event trigger to reveive data from, process it and send back via.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.standalone.subprocessevent.Null
 * </pre>
 *
 * <pre>-actor &lt;adams.flow.core.Actor&gt; [-actor ...] (property: actors)
 * &nbsp;&nbsp;&nbsp;The actor to use for processing the data.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.transformer.PassThrough
 * </pre>
 *
 * <pre>-scope-handling-variables &lt;EMPTY|COPY|SHARE&gt; (property: scopeHandlingVariables)
 * &nbsp;&nbsp;&nbsp;Defines how variables are handled in the local scope; whether to start with
 * &nbsp;&nbsp;&nbsp;empty set, a copy of the outer scope variables or share variables with the
 * &nbsp;&nbsp;&nbsp;outer scope.
 * &nbsp;&nbsp;&nbsp;default: EMPTY
 * </pre>
 *
 * <pre>-scope-handling-storage &lt;EMPTY|COPY|SHARE&gt; (property: scopeHandlingStorage)
 * &nbsp;&nbsp;&nbsp;Defines how storage is handled in the local scope; whether to start with
 * &nbsp;&nbsp;&nbsp;empty set, a (deep) copy of the outer scope storage or share the storage
 * &nbsp;&nbsp;&nbsp;with the outer scope.
 * &nbsp;&nbsp;&nbsp;default: EMPTY
 * </pre>
 *
 * <pre>-finish-before-stopping &lt;boolean&gt; (property: finishBeforeStopping)
 * &nbsp;&nbsp;&nbsp;If enabled, actor first finishes processing all data before stopping.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-stopping-timeout &lt;int&gt; (property: stoppingTimeout)
 * &nbsp;&nbsp;&nbsp;The timeout in milliseconds when waiting for actors to finish (&lt;= 0 for
 * &nbsp;&nbsp;&nbsp;infinity; see 'finishBeforeStopping').
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 * <pre>-stopping-warning-interval &lt;int&gt; (property: stoppingWarningInterval)
 * &nbsp;&nbsp;&nbsp;The interval in milliseconds to output logging warnings if the actors haven't
 * &nbsp;&nbsp;&nbsp;stopped yet (and no stopping timeout set); no warning if &lt;= 0.
 * &nbsp;&nbsp;&nbsp;default: 10000
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SubProcessEvent
  extends AbstractStandalone
  implements MutableActorHandler, DaemonEvent, Pausable {

  /** for serialization. */
  private static final long serialVersionUID = 4670761846363281951L;

  /** the trigger. */
  protected SubProcessEventTrigger m_EventTrigger;

  /** for actors that get executed. */
  protected LocalScopeSubProcess m_Actors;

  /** whether data is currently being processed. */
  protected boolean m_Busy;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Executes its sub-flow after a predefined number of milli-seconds.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "event-trigger", "eventTrigger",
      new Null());

    m_OptionManager.add(
      "actor", "actors",
      new Actor[]{new PassThrough()});

    m_OptionManager.add(
      "scope-handling-variables", "scopeHandlingVariables",
      ScopeHandling.EMPTY);

    m_OptionManager.add(
      "scope-handling-storage", "scopeHandlingStorage",
      ScopeHandling.EMPTY);

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

    m_Actors = new LocalScopeSubProcess();
  }

  /**
   * Sets the even trigger to use.
   *
   * @param value	the trigger
   */
  public void setEventTrigger(SubProcessEventTrigger value) {
    m_EventTrigger = value;
    reset();
  }

  /**
   * Returns the event trigger in use.
   *
   * @return		the trigger
   */
  public SubProcessEventTrigger getEventTrigger() {
    return m_EventTrigger;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String eventTriggerTipText() {
    return "The event trigger to reveive data from, process it and send back via.";
  }

  /**
   * Sets how to handle variables into the local scope.
   *
   * @param value	the scope handling
   */
  public void setScopeHandlingVariables(ScopeHandling value) {
    m_Actors.setScopeHandlingVariables(value);
    reset();
  }

  /**
   * Returns how variables are handled in the local scope.
   *
   * @return		the scope handling
   */
  public ScopeHandling getScopeHandlingVariables() {
    return m_Actors.getScopeHandlingVariables();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String scopeHandlingVariablesTipText() {
    return m_Actors.scopeHandlingVariablesTipText();
  }

  /**
   * Sets how to handle storage in the local scope.
   *
   * @param value	the scope handling
   */
  public void setScopeHandlingStorage(ScopeHandling value) {
    m_Actors.setScopeHandlingStorage(value);
    reset();
  }

  /**
   * Returns how storage is handled in the local scope.
   *
   * @return		the scope handling
   */
  public ScopeHandling getScopeHandlingStorage() {
    return m_Actors.getScopeHandlingStorage();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String scopeHandlingStorageTipText() {
    return m_Actors.scopeHandlingStorageTipText();
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
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "eventTrigger", getEventTrigger(), "trigger: ");
    result += QuickInfoHelper.toString(this, "scopeHandlingVariables", getScopeHandlingVariables(), ", variable scope: ");
    result += QuickInfoHelper.toString(this, "scopeHandlingStorage", getScopeHandlingStorage(), ", storage scope: ");

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
   * Returns the internal representation of the actors.
   *
   * @return		the actors
   */
  protected LocalScopeSubProcess getInternalActors() {
    return m_Actors;
  }

  /**
   * Checks the cron actors before they are set via the setActors method.
   * Returns an error message if the actors are not acceptable, null otherwise.
   * <br><br>
   * Default implementation always returns null.
   *
   * @param actors	the actors to check
   * @return		null if accepted, otherwise error message
   */
  protected String checkActors(Actor[] actors) {
    return null;
  }

  /**
   * Updates the parent of all actors in this group.
   */
  protected void updateParent() {
    m_Actors.setParent(null);
    m_Actors.setParent(getParent());
    m_Actors.setName(getName());
  }

  /**
   * Sets the actors for processing the data.
   *
   * @param value	the actors
   */
  public void setActors(Actor[] value) {
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
   * Returns the actors for processing the data.
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
    return "The actor to use for processing the data.";
  }

  /**
   * Returns the size of the group.
   *
   * @return		always 1
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
  public Actor firstActive() {
    return m_Actors.firstActive();
  }

  /**
   * Returns the last non-skipped actor.
   *
   * @return		the last 'active' actor, null if none available
   */
  public Actor lastActive() {
    return m_Actors.lastActive();
  }

  /**
   * Performs checks on the "sub-actors".
   *
   * @return		null if everything OK, otherwise error message
   */
  public String check() {
    return m_Actors.check();
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

    if (result == null) {
      updateParent();
      result = m_Actors.setUp();
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
    return m_EventTrigger.setUp(this);
  }

  /**
   * Processes the data with the sub-flow.
   *
   * @param data	the data to process
   * @param errors	for collecting errors
   * @return		the processed data, null if failed to process or no data generated
   */
  protected Object doProcess(Object data, MessageCollection errors) {
    List<Object> 	result;
    String		msg;
    Token		out;

    if (data == null) {
      msg = "Input data is null!";
      errors.add(msg);
      if (isLoggingEnabled())
	getLogger().severe(msg);
      return null;
    }

    // 1. set data
    m_Actors.input(new Token(data));

    // 2. process data
    try {
      msg = m_Actors.execute();
      if (msg != null) {
	errors.add(msg);
	if (isLoggingEnabled())
	  getLogger().severe(msg);
	return null;
      }
    }
    catch (Exception e) {
      msg = "Failed to process input data!";
      errors.add(msg, e);
      getLogger().log(Level.SEVERE, msg, e);
      return null;
    }

    // 3. retrieve data
    result = new ArrayList<>();
    try {
      while (m_Actors.hasPendingOutput()) {
	out = m_Actors.output();
	result.add(out.getPayload());
      }
    }
    catch (Exception e) {
      msg = "Failed to retrieve processed data!";
      errors.add(msg, e);
      getLogger().log(Level.SEVERE, msg, e);
      return null;
    }

    if (result.isEmpty())
      return null;
    else if (result.size() == 1)
      return result.get(0);
    else
      return result.toArray(new Object[0]);
  }

  /**
   * Processes the data with the sub-flow.
   *
   * @param data	the data to process
   * @param errors	for collecting errors
   * @return		the processed data, null if failed to process or no data generated
   */
  public synchronized Object process(Object data, MessageCollection errors) {
    Object	result;
    long	start;
    long	end;

    start  = System.currentTimeMillis();
    m_Busy = true;
    result = doProcess(data, errors);
    m_Busy = false;
    end    = System.currentTimeMillis();
    if (isLoggingEnabled())
      getLogger().info("Processing time: " + (end - start) + "msec");

    return result;
  }

  /**
   * Checks whether data is currently being processed.
   *
   * @return		true if data is being processed
   */
  public boolean isBusy() {
    return m_Busy;
  }

  /**
   * Pauses the execution.
   */
  public void pauseExecution() {
    if (m_Actors != null)
      m_Actors.pauseExecution();
  }

  /**
   * Returns whether the object is currently paused.
   *
   * @return		true if object is paused
   */
  public boolean isPaused() {
    return (m_Actors != null) && m_Actors.isPaused();
  }

  /**
   * Resumes the execution.
   */
  public void resumeExecution() {
    if (m_Actors != null)
      m_Actors.resumeExecution();
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
    m_EventTrigger.wrapUp();

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
