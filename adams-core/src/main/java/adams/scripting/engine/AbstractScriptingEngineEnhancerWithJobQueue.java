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

/**
 * AbstractScriptingEngineEnhancerWithJobQueue.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.scripting.engine;

import adams.multiprocess.CallableWithResult;
import adams.multiprocess.PausableFixedThreadPoolExecutor;

/**
 * Ancestor for scripting engines that enhance a base engine, but also
 * make use of a job queue internally.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractScriptingEngineEnhancerWithJobQueue
  extends AbstractScriptingEngineEnhancer
  implements JobQueueHandler {

  private static final long serialVersionUID = 984948355449016508L;

  /** the number of concurrent jobs to allow. */
  protected int m_MaxConcurrentJobs;

  /** the executor service to use for parallel execution. */
  protected PausableFixedThreadPoolExecutor m_Executor;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "max-concurrent-jobs", "maxConcurrentJobs",
      1, 1, null);
  }

  /**
   * Sets the maximum number of concurrent jobs to execute.
   *
   * @param value	the number of jobs
   */
  public void setMaxConcurrentJobs(int value) {
    if (getOptionManager().isValid("maxConcurrentJobs", value)) {
      m_MaxConcurrentJobs = value;
      reset();
    }
  }

  /**
   * Returns the maximum number of concurrent jobs to execute.
   *
   * @return		the number of jobs
   */
  public int getMaxConcurrentJobs() {
    return m_MaxConcurrentJobs;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String maxConcurrentJobsTipText() {
    return "The maximum number of concurrent jobs to execute.";
  }

  /**
   * Queues the job in the execution pipeline.
   *
   * @param job		the job to queue
   */
  public void executeJob(CallableWithResult<String> job) {
    m_Executor.submit(job);
  }

  /**
   * Pauses the execution.
   */
  @Override
  public void pauseExecution() {
    super.pauseExecution();
    if (m_Executor != null)
      m_Executor.pauseExecution();
  }

  /**
   * Resumes the execution.
   */
  @Override
  public void resumeExecution() {
    super.resumeExecution();
    if (m_Executor != null)
      m_Executor.resumeExecution();
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    super.stopExecution();
    if (m_Executor != null)
      m_Executor.shutdownNow();
  }
}
