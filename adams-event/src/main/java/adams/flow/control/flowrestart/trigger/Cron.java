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
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.control.flowrestart.trigger;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.CronSchedule;
import adams.flow.control.Flow;
import adams.flow.core.EventHelper;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;

import java.util.Date;

/**
 * Triggers the restart according to the specified schedule.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Cron
  extends AbstractTrigger {

  private static final long serialVersionUID = -1840524349531675772L;

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
      String 	result;
      Cron 	owner;

      result = null;
      owner = (Cron) context.getJobDetail().getJobDataMap().get(KEY_OWNER);

      if (owner.getTriggerHandler() != null)
	result = owner.getTriggerHandler().trigger();

      if (result != null)
	owner.getLogger().warning(result);
    }
  }

  /** the key for the owner in the JobExecutionContent. */
  public final static String KEY_OWNER = "owner";

  /** the cron schedule. */
  protected CronSchedule m_Schedule;

  /** the scheduler. */
  protected Scheduler m_Scheduler;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Triggers the restart according to the specified schedule.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "schedule", "schedule",
      new CronSchedule(CronSchedule.DEFAULT));
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
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "schedule", m_Schedule.getValue());
  }

  /**
   * Starts the trigger.
   *
   * @param flow	the flow to handle
   * @return		null if successfully started, otherwise error message
   */
  @Override
  protected String doStart(Flow flow) {
    String	result;
    JobDetail	job;
    CronTrigger	trigger;
    Date 	first;

    result = null;

    try {
      if (m_Scheduler == null)
	m_Scheduler = EventHelper.getDefaultScheduler();
      job         = new JobDetail(flow.getFlowID() + ".job", flow.getFlowID() + ".group", CronJob.class);
      job.getJobDataMap().put(KEY_OWNER, this);
      trigger     = new CronTrigger(
	  flow.getFlowID() + ".trigger",
	  flow.getFlowID() + ".group",
	  flow.getFlowID() + ".job",
	  flow.getFlowID() + ".group",
	  m_Schedule.getValue());
      m_Scheduler.addJob(job, true);
      first = m_Scheduler.scheduleJob(trigger);
      if (isLoggingEnabled())
	getLogger().info("First restart of flow: " + first);
      m_Scheduler.start();
    }
    catch (Exception e) {
      result = Utils.handleException(this, "Failed to set up cron job: ", e);
    }

    return result;
  }

  /**
   * Stops the trigger.
   *
   * @return		null if successfully stopped, otherwise error message
   */
  @Override
  public String stop() {
    String	result;

    result = null;

    if (m_Scheduler != null) {
      try {
	m_Scheduler.shutdown(true);
      }
      catch (Exception e) {
	result = Utils.handleException(this, "Error shutting down scheduler:", e);
      }
    }

    return result;
  }
}
