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
 * JobRunnerInstance.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.core.ClassCrossReference;
import adams.core.Utils;
import adams.core.option.OptionUtils;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import adams.multiprocess.Job;
import adams.multiprocess.JobRunner;
import adams.multiprocess.JobRunnerSupporter;
import adams.multiprocess.LocalJobRunner;

/**
 <!-- globalinfo-start -->
 * Acts as job execution pool for classes implementing adams.multiprocess.JobRunnerSupporter.<br>
 * <br>
 * See also:<br>
 * adams.multiprocess.JobRunnerSupporter
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
 * &nbsp;&nbsp;&nbsp;default: JobRunnerInstance
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
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class JobRunnerInstance
  extends AbstractStandalone
  implements ClassCrossReference {

  private static final long serialVersionUID = -3119325755903662849L;

  /** the JobRunnerSetup instance to use. */
  protected transient JobRunnerSetup m_JobRunnerSetup;

  /** the actual JobRunner instance in use. */
  protected transient JobRunner m_JobRunner;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Acts as job execution pool for classes implementing " + Utils.classToString(JobRunnerSupporter.class) + ".";
  }

  /**
   * Returns the cross-referenced classes.
   *
   * @return		the classes
   */
  public Class[] getClassCrossReferences() {
    return new Class[]{JobRunnerSupporter.class};
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null) {
      m_JobRunnerSetup = (JobRunnerSetup) ActorUtils.findClosestType(this, JobRunnerSetup.class, true);
      if (isLoggingEnabled())
	getLogger().info("JobRunnerSetup actor: " + ((m_JobRunnerSetup == null) ? "-none-" : m_JobRunnerSetup.getFullName()));
      if (m_JobRunnerSetup == null) {
	m_JobRunner = new LocalJobRunner();
	getLogger().warning("No " + Utils.classToString(JobRunnerSetup.class) + " actor found, using following JobRunner instance: " + OptionUtils.getCommandLine(m_JobRunner));
      }
      else {
	m_JobRunner = m_JobRunnerSetup.newInstance();
      }
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
    return null;
  }

  // TODO adams.core.Pausable?

  /**
   * Returns the job runner in use.
   *
   * @return		the JobRunner, null if not available
   */
  public JobRunner getJobRunner() {
    return m_JobRunner;
  }

  /**
   * For queuing a job and waiting for the execution to finish.
   *
   * @param job		the job to execute
   * @return		null if successful, otherwise error message
   * @throws Exception	if no JobRunnerInstance or JobRunner available
   */
  public String executeJob(Job job) throws Exception {
    JobRunner	runner;

    runner = getJobRunner();
    if (runner == null)
      throw new Exception("No JobRunner available, cannot queue job: " + job);

    runner.add(job);
    runner.start();
    while (runner.isRunning() && !job.isComplete()) {
      Utils.wait(this, 1000, 100);
    }

    if (job.isComplete())
      return null;
    else if (job.isStopped())
      return "Job was stopped!";
    else if (job.hasExecutionError())
      return job.getExecutionError();
    else
      return "Unknown error occurred!";
  }

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    if (m_JobRunner != null) {
      m_JobRunner.terminate();
      m_JobRunner.cleanUp();
      m_JobRunner = null;
    }
    super.stopExecution();
  }

  /**
   * Cleans up after the execution has finished. Graphical output is left
   * untouched.
   */
  @Override
  public void wrapUp() {
    if (m_JobRunner != null) {
      m_JobRunner.terminate();
      m_JobRunner.cleanUp();
      m_JobRunner = null;
    }
    super.wrapUp();
  }

  /**
   * Tries to locate a JobRunnerInstance actor.
   * Logs no warning in case it fails to locate one.
   *
   * @param source	the starting point for the search
   * @return		the instance or null if not found
   */
  public static JobRunnerInstance locate(Actor source) {
    return locate(source, false);
  }

  /**
   * Tries to locate a JobRunnerInstance actor.
   *
   * @param source	the starting point for the search
   * @param warn	whether to log a warning
   * @return		the instance or null if not found
   */
  public static JobRunnerInstance locate(Actor source, boolean warn) {
    JobRunnerInstance	result;

    result = (JobRunnerInstance) ActorUtils.findClosestType(source, JobRunnerInstance.class, true);
    if ((result == null) && warn)
      source.getLogger().warning("No " + Utils.classToString(JobRunnerInstance.class) + " actor found, cannot offload jobs!");

    return result;
  }
}
