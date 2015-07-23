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
 * Cron.java
 * Copyright (C) 2010-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.core.QuickInfoHelper;
import adams.core.Variables;
import adams.core.base.CronSchedule;
import adams.core.logging.LoggingLevel;
import adams.flow.condition.bool.True;
import adams.flow.control.Sequence;
import adams.flow.core.AbstractActor;
import adams.flow.core.ActorHandlerInfo;
import adams.flow.core.DaemonEvent;
import adams.flow.core.EventHelper;
import adams.flow.core.MutableActorHandler;
import adams.flow.template.EndlessLoop;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;

import java.util.Date;

/**
 <!-- globalinfo-start -->
 * Executes an actor according to a pre-defined schedule.<br>
 * Note: since the actor merely starts the cron scheduler in the background, the actor finishes the execution pretty much immediately. Therefore, the flow needs to be kept alive in order to let the background jobs getting executed. This can be done with a simple WhileLoop actor using the 'adams.flow.condition.bool.True' condition and a nested Start&#47;Sleep actor. You can use the adams.flow.template.EndlessLooptemplate to generate this loop automatically.<br>
 * <br>
 * NB: Any newly scheduled jobs get dropped if the previous execution is still running.<br>
 * <br>
 * For more information on the scheduler format see:<br>
 * http:&#47;&#47;www.quartz-scheduler.org&#47;docs&#47;tutorials&#47;crontrigger.html
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: Cron
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 * 
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 * 
 * <pre>-cron-actor &lt;adams.flow.core.AbstractActor&gt; [-cron-actor ...] (property: cronActors)
 * &nbsp;&nbsp;&nbsp;The actor to execute according to the cron schedule.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-schedule &lt;adams.core.base.CronSchedule&gt; (property: schedule)
 * &nbsp;&nbsp;&nbsp;The schedule for execution the cron actor; format 'SECOND MINUTE HOUR DAYOFMONTH 
 * &nbsp;&nbsp;&nbsp;MONTH WEEKDAY [YEAR]'.
 * &nbsp;&nbsp;&nbsp;default: 0 0 1 * * ?
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
public class Cron
  extends AbstractStandalone
  implements MutableActorHandler, DaemonEvent {

  /** for serialization. */
  private static final long serialVersionUID = 4670761846363281951L;

  /**
   * Encapsulates a job to run.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class CronJob
    implements Job {

    /**
     * Gets executed when the cron event gets triggered.
     *
     * @param context			the context of the execution
     * @throws JobExecutionException	if job fails
     */
    public void execute(JobExecutionContext context) throws JobExecutionException {
      String	result;
      Cron	owner;

      owner = (Cron) context.getJobDetail().getJobDataMap().get(KEY_OWNER);

      result = owner.executeCronActors();
      if (result != null) {
	if (!result.equals(BUSY)) {
	  result = owner.getErrorHandler().handleError(owner, "execute/cron", owner.getFullName() + ": " + result);
	  if (result != null)
	    throw new JobExecutionException(owner.getFullName() + ": " + result);
	}
	else {
	  if (owner.isLoggingEnabled())
	    owner.getLogger().info("Job dropped since still executing previous job: " + context.getJobDetail());
	}
      }
    }
  }

  /** the key for the owner in the JobExecutionContent. */
  public final static String KEY_OWNER = "owner";

  /** the result if actors are currently being executed. */
  public final static String BUSY = "BUSY";

  /** for actors that get executed. */
  protected Sequence m_CronActors;

  /** the cron schedule. */
  protected CronSchedule m_Schedule;

  /** the scheduler. */
  protected Scheduler m_Scheduler;

  /** whether the cron-actors are currently being executed. */
  protected boolean m_ExecutingCronActors;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Executes an actor according to a pre-defined schedule.\n"
      + "Note: since the actor merely starts the cron scheduler in the background, "
      + "the actor finishes the execution pretty much immediately. Therefore, "
      + "the flow needs to be kept alive in order to let the background jobs "
      + "getting executed. This can be done with a simple WhileLoop actor "
      + "using the '" + True.class.getName() + "' condition and a nested "
      + "Start/Sleep actor. You can use the " + EndlessLoop.class.getName() 
      + "template to generate this loop automatically.\n"
      + "\n"
      + "NB: Any newly scheduled jobs get dropped if the previous execution "
      + "is still running.\n"
      + "\n"
      + "For more information on the scheduler format see:\n"
      + "http://www.quartz-scheduler.org/docs/tutorials/crontrigger.html";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "cron-actor", "cronActors",
	    new AbstractActor[0]);

    m_OptionManager.add(
	    "schedule", "schedule",
	    new CronSchedule(CronSchedule.DEFAULT));
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_CronActors = new Sequence();
    m_CronActors.setAllowSource(true);
    m_CronActors.setAllowStandalones(true);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "schedule", m_Schedule.getValue());
  }

  /**
   * Sets the logging level.
   *
   * @param value 	the level
   */
  @Override
  public void setLoggingLevel(LoggingLevel value) {
    super.setLoggingLevel(value);
    m_CronActors.setLoggingLevel(value);
  }

  /**
   * Checks the cron actors before they are set via the setCronActors method.
   * Returns an error message if the actors are not acceptable, null otherwise.
   * <br><br>
   * Default implementation always returns null.
   *
   * @param actors	the actors to check
   * @return		null if accepted, otherwise error message
   */
  protected String checkCronActors(AbstractActor[] actors) {
    return null;
  }

  /**
   * Updates the parent of all actors in this group.
   */
  protected void updateParent() {
    m_CronActors.setParent(null);
    m_CronActors.setParent(this);
  }

  /**
   * Sets the actors to execute on schedule.
   *
   * @param value	the actors
   */
  public void setCronActors(AbstractActor[] value) {
    String	msg;

    msg = checkCronActors(value);
    if (msg == null) {
      m_CronActors.setActors(value);
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
  public AbstractActor[] getCronActors() {
    return m_CronActors.getActors();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String cronActorsTipText() {
    return "The actor to execute according to the cron schedule.";
  }

  /**
   * Sets the execution schedule.
   *
   * @param value 	the schedule
   */
  public void setSchedule(CronSchedule value) {
    m_Schedule = value;
    reset();
  }

  /**
   * Returns the execution schedule.
   *
   * @return 		the schedule
   */
  public CronSchedule getSchedule() {
    return m_Schedule;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String scheduleTipText() {
    return
        "The schedule for execution the cron actor; "
      + "format 'SECOND MINUTE HOUR DAYOFMONTH MONTH WEEKDAY [YEAR]'.";
  }

  /**
   * Executes the cron actors.
   *
   * @return		null if execution successful, otherwise error message
   */
  protected String executeCronActors() {
    String	result;

    if (m_ExecutingCronActors)
      return BUSY;
    
    m_ExecutingCronActors = true;
    result = m_CronActors.execute();
    m_ExecutingCronActors = false;
    
    return result;
  }

  /**
   * Returns the size of the group.
   *
   * @return		the number of actors
   */
  public int size() {
    return m_CronActors.size();
  }

  /**
   * Returns the actor at the given position.
   *
   * @param index	the position
   * @return		the actor
   */
  public AbstractActor get(int index) {
    return m_CronActors.get(index);
  }

  /**
   * Sets the actor at the given position.
   *
   * @param index	the position
   * @param actor	the actor to set at this position
   */
  public void set(int index, AbstractActor actor) {
    m_CronActors.set(index, actor);
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
    return m_CronActors.indexOf(actor);
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
    m_CronActors.add(index, actor);
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

    result = m_CronActors.remove(index);
    reset();

    return result;
  }

  /**
   * Removes all actors.
   */
  public void removeAll() {
    m_CronActors.removeAll();
    reset();
  }

  /**
   * Returns some information about the actor handler, e.g., whether it can
   * contain standalones and the actor execution.
   *
   * @return		the info
   */
  public ActorHandlerInfo getActorHandlerInfo() {
    return m_CronActors.getActorHandlerInfo();
  }

  /**
   * Returns the number of non-skipped actors.
   *
   * @return		the 'active' actors
   */
  public int active() {
    return m_CronActors.active();
  }

  /**
   * Returns the first non-skipped actor.
   *
   * @return		the first 'active' actor, null if none available
   */
  public AbstractActor firstActive() {
    return m_CronActors.firstActive();
  }

  /**
   * Returns the last non-skipped actor.
   *
   * @return		the last 'active' actor, null if none available
   */
  public AbstractActor lastActive() {
    return m_CronActors.lastActive();
  }

  /**
   * Performs checks on the "sub-actors".
   *
   * @return		null if everything OK, otherwise error message
   */
  public String check() {
    return m_CronActors.check();
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
      result = m_CronActors.setUp();

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
    JobDetail	job;
    CronTrigger	trigger;
    Date	first;

    result = null;

    try {
      if (m_Scheduler == null)
	m_Scheduler = EventHelper.getDefaultScheduler();
      job         = new JobDetail(getFullName() + ".job", getFullName() + ".group", CronJob.class);
      job.getJobDataMap().put(KEY_OWNER, this);
      trigger     = new CronTrigger(
	  getFullName() + ".trigger",
	  getFullName() + ".group",
	  getFullName() + ".job",
	  getFullName() + ".group",
	  m_Schedule.getValue());
      m_Scheduler.addJob(job, true);
      first = m_Scheduler.scheduleJob(trigger);
      if (isLoggingEnabled())
	getLogger().info("First execution of actor: " + first);
      m_Scheduler.start();
    }
    catch (Exception e) {
      result = handleException("Failed to set up cron job: ", e);
    }

    return result;
  }

  /**
   * Stops the internal cron scheduler, if possible.
   */
  protected void stopScheduler() {
    if (m_Scheduler != null) {
      try {
	m_Scheduler.shutdown(true);
      }
      catch (Exception e) {
	handleException("Error shutting down scheduler:", e);
      }
    }
  }
  
  /**
   * Stops the processing of tokens without stopping the flow.
   */
  public void flushExecution() {
    if (m_CronActors != null)
      m_CronActors.flushExecution();
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    stopScheduler();
    m_CronActors.stopExecution();
    super.stopExecution();
  }

  /**
   * Cleans up after the execution has finished. Graphical output is left
   * untouched.
   */
  @Override
  public void wrapUp() {
    if (m_CronActors != null)
      m_CronActors.wrapUp();
    stopScheduler();
    super.wrapUp();
  }

  /**
   * Cleans up after the execution has finished. Also removes graphical
   * components.
   */
  @Override
  public void cleanUp() {
    if (m_CronActors != null)
      m_CronActors.cleanUp();

    super.cleanUp();
  }
}
