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
 * VariableChangedEvent.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.core.QuickInfoHelper;
import adams.core.VariableNameNoUpdate;
import adams.core.Variables;
import adams.core.logging.LoggingLevel;
import adams.event.VariableChangeEvent;
import adams.flow.control.Sequence;
import adams.flow.core.AbstractActor;
import adams.flow.core.ActorHandlerInfo;
import adams.flow.core.ActorUtils;
import adams.flow.core.DaemonEvent;
import adams.flow.core.MutableActorHandler;

import com.jidesoft.utils.SwingWorker;

/**
 <!-- globalinfo-start -->
 * Listens to a any changes to the specified variable.<br/>
 * This allows, for instance, the monitoring of a variable.<br/>
 * Enable the 'noDiscard' property to process all change events - NB: this can slow down the system significantly.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
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
 * &nbsp;&nbsp;&nbsp;default: VariableChangedEvent
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
 * <pre>-variable &lt;adams.core.VariableNameNoUpdate&gt; (property: variable)
 * &nbsp;&nbsp;&nbsp;The variable to monitor for changes.
 * &nbsp;&nbsp;&nbsp;default: variable
 * </pre>
 * 
 * <pre>-no-discard &lt;boolean&gt; (property: noDiscard)
 * &nbsp;&nbsp;&nbsp;If enabled, no change event gets discarded; CAUTION: enabling this option 
 * &nbsp;&nbsp;&nbsp;can slow down the system significantly.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-actor &lt;adams.flow.core.AbstractActor&gt; [-actor ...] (property: actors)
 * &nbsp;&nbsp;&nbsp;The actors to execute in case of a change event.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * For more information on the schedule format, see
 * <a href="http://www.quartz-scheduler.org/docs/tutorials/crontrigger.html" target="_blank">CronTrigger Tutorial</a>.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class VariableChangedEvent
  extends AbstractStandalone
  implements MutableActorHandler, DaemonEvent {

  /** for serialization. */
  private static final long serialVersionUID = 4670761846363281951L;

  /** the result if actors are currently being executed. */
  public final static String BUSY = "BUSY";

  /** the variable to listen to. */
  protected VariableNameNoUpdate m_Variable;
  
  /** whether to discard change events when busy or not. */
  protected boolean m_NoDiscard;

  /** for actors that get executed. */
  protected Sequence m_Actors;
  
  /** whether the actors are currently being executed. */
  protected boolean m_ExecutingActors;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Listens to a any changes to the specified variable.\n"
        + "This allows, for instance, the monitoring of a variable.\n"
        + "Enable the 'noDiscard' property to process all change events - NB: this "
	+ "can slow down the system significantly.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "variable", "variable",
	    new VariableNameNoUpdate());

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
   * Checks the cron actors before they are set via the setActors method.
   * Returns an error message if the actors are not acceptable, null otherwise.
   *
   * @param actors	the actors to check
   * @return		null if accepted, otherwise error message
   */
  protected String checkActors(AbstractActor[] actors) {
    int		i;
    
    for (i = 0; i < actors.length; i++) {
      if (actors[i].getSkip())
	continue;
      if (ActorUtils.isStandalone(actors[i]))
	continue;
      if (!ActorUtils.isSource(actors[i]))
	return "Actor #" + (i+1) + " does not produce any output!";
      break;
    }
    
    return null;
  }

  /**
   * Updates the parent of all actors in this group.
   */
  protected void updateParent() {
    m_Actors.setParent(null);
    m_Actors.setParent(this);
  }

  /**
   * Sets the variable to monitor.
   *
   * @param value 	the variable
   */
  public void setVariable(VariableNameNoUpdate value) {
    m_Variable = value;
    reset();
  }

  /**
   * Returns the variable to monitor
   *
   * @return 		the variable
   */
  public VariableNameNoUpdate getVariable() {
    return m_Variable;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String variableTipText() {
    return "The variable to monitor for changes.";
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
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	value;
    
    result  = QuickInfoHelper.toString(this, "variable", m_Variable, "var: ");
    value   = QuickInfoHelper.toString(this, "noDiscard", m_NoDiscard, "no discard", ", ");
    if (value != null)
      result += value;
    
    return result;
  }

  /**
   * Processes the change event.
   *
   * @param e		the change event
   * @return		null if execution successful, otherwise error message
   */
  protected String processChangeEvent(VariableChangeEvent e) {
    SwingWorker		worker;

    if (m_ExecutingActors) {
      if (!m_NoDiscard) {
	return BUSY;
      }
      else {
	while (m_ExecutingActors && !m_Stopped) {
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

    if (m_Stopped)
      return null;
    
    m_ExecutingActors = true;
    worker = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
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
   * Sets whether the actor is to be run in headless mode, i.e., suppressing
   * GUI components.
   *
   * @param value	if true then GUI components will be suppressed
   */
  @Override
  public void setHeadless(boolean value) {
    super.setHeadless(value);
    m_Actors.setHeadless(value);
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
   * <p/>
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
   * Gets triggered when a variable changed (added, modified, removed).
   *
   * @param e		the event
   */
  @Override
  public void variableChanged(VariableChangeEvent e) {
    if (e.getName().equals(m_Variable.getValue()))
      processChangeEvent(e);
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
