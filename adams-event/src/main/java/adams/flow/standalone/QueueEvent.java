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
 * QueueEvent.java
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.core.QuickInfoHelper;
import adams.core.Variables;
import adams.core.logging.LoggingLevel;
import adams.flow.control.Sequence;
import adams.flow.control.StorageName;
import adams.flow.control.StorageQueueHandler;
import adams.flow.core.AbstractActor;
import adams.flow.core.ActorHandlerInfo;
import adams.flow.core.DaemonEvent;
import adams.flow.core.EventRunnable;
import adams.flow.core.MutableActorHandler;
import adams.flow.core.QueueHelper;
import adams.flow.core.Token;

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
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: QueueEvent
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
 * <pre>-actor &lt;adams.flow.core.AbstractActor&gt; [-actor ...] (property: actors)
 * &nbsp;&nbsp;&nbsp;The actor to use for processing the queue element.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-storage-name &lt;adams.flow.control.StorageName&gt; (property: storageName)
 * &nbsp;&nbsp;&nbsp;The name of the queue in the internal storage.
 * &nbsp;&nbsp;&nbsp;default: queue
 * </pre>
 * 
 * <pre>-interval &lt;int&gt; (property: interval)
 * &nbsp;&nbsp;&nbsp;The polling interval in milli-seconds.
 * &nbsp;&nbsp;&nbsp;default: 50
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9014 $
 */
public class QueueEvent
  extends AbstractStandalone
  implements MutableActorHandler, DaemonEvent {

  /** for serialization. */
  private static final long serialVersionUID = 4670761846363281951L;

  /**
   * Specialized runnable for the {@link QueueEvent} actor.
   *  
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision: 9014 $
   */
  public static class QueueEventRunnable
    extends EventRunnable<QueueEvent> {

    /** for serialization. */
    private static final long serialVersionUID = -8381810698902721155L;

    /**
     * Initializes the runnable.
     * 
     * @param owner	the owning event
     */
    public QueueEventRunnable(QueueEvent owner) {
      super(owner);
    }
    
    @Override
    protected void doRun() {
      StorageQueueHandler	queue;
      Token			token;

      queue = QueueHelper.getQueue(getOwner(), getOwner().getStorageName());

      if (queue != null) {
	while (!m_Stopped) {
	  try {
	    if (queue.size() > 0) {
	      token = new Token(queue.remove());
	      getOwner().getInternalActors().input(token);
	      getOwner().getInternalActors().execute();
	    }
	    else {
	      synchronized(this) {
		wait(getOwner().getInterval());
	      }
	    }
	  }
	  catch (Exception e) {
	    getLogger().log(Level.SEVERE, "Error during execution occurred!", e);
	  }
	}
      }
      else {
	if (isLoggingEnabled())
	  getLogger().info("Queue '" + getOwner().getStorageName() + "' not found, exiting.");
      }
    }
  }
  
  /** for actors that get executed. */
  protected Sequence m_Actors;

  /** the name of the queue in the internal storage. */
  protected StorageName m_StorageName;

  /** the poll interval in msec. */
  protected int m_Interval;
  
  /** the runnable used for polling. */
  protected QueueEventRunnable m_Runnable;
  
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
	    "actor", "actors",
	    new AbstractActor[0]);

    m_OptionManager.add(
	    "storage-name", "storageName",
	    new StorageName("queue"));

    m_OptionManager.add(
	    "interval", "interval",
	    50, 1, null);
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
    
    m_Runnable = null;
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "storageName", m_StorageName, "storage: ");
    result += QuickInfoHelper.toString(this, "interval", m_Interval, ", interval: ");

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
  protected Sequence getInternalActors() {
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
  protected String checkActors(AbstractActor[] actors) {
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
    return "The actor to use for processing the queue element.";
  }

  /**
   * Sets the name for the queue in the internal storage.
   *
   * @param value	the name
   */
  public void setStorageName(StorageName value) {
    m_StorageName = value;
    reset();
  }

  /**
   * Returns the name for the queue in the internal storage.
   *
   * @return		the name
   */
  public StorageName getStorageName() {
    return m_StorageName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String storageNameTipText() {
    return "The name of the queue in the internal storage.";
  }

  /**
   * Sets the polling interval in seconds.
   *
   * @param value	the interval
   */
  public void setInterval(int value) {
    m_Interval = value;
    reset();
  }

  /**
   * Returns the polling interval in milli-seconds.
   *
   * @return		the interval
   */
  public int getInterval() {
    return m_Interval;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String intervalTipText() {
    return "The polling interval in milli-seconds.";
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

    if (result == null)
      result = m_Actors.setUp();

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    m_Runnable = new QueueEventRunnable(this);
    m_Runnable.setLoggingLevel(getLoggingLevel());
    new Thread(m_Runnable).start();
    return null;
  }

  /**
   * Stops the runnable if necessary, waits for it to finish.
   */
  protected void stopRunnable() {
    if (m_Runnable != null) {
      m_Runnable.stopExecution();
      while (m_Runnable.isRunning()) {
	try {
	  synchronized(this) {
	    wait(100);
	  }
	}
	catch (Exception e) {
	  // ignored
	}
      }
      m_Runnable = null;
    }
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
    stopRunnable();
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
    stopRunnable();

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
