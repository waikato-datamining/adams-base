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
 * AbstractJob.java
 * Copyright (C) 2008-2015 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.multiprocess;

import adams.core.Utils;
import adams.core.logging.LoggingObject;
import adams.event.JobCompleteEvent;
import adams.event.JobCompleteListener;

/**
 * A job is a unit of execution.
 *
 * @author dale
 * @version $Revision$
 */
public abstract class AbstractJob
  extends LoggingObject
  implements Job {

  /** for serialization. */
  private static final long serialVersionUID = -4365906331615932775L;

  /** identifying name of job. */
  protected String m_jobInfo;

  /** Has this job completed processing? */
  protected boolean m_complete;

  /** Object to call once job has been completed. */
  protected JobCompleteListener m_completed;

  /** whether an error occurred in the execution. */
  protected String m_ExecutionError;

  /**
   * Job constructor. Create a new Job with no identifier.
   */
  public AbstractJob() {
    this("");
  }

  /**
   * Job constructor. Create a new Job with given identifier.
   *
   * @param info  	Job function
   */
  public AbstractJob(String info) {
    super();

    m_jobInfo        = info;
    m_complete       = false;
    m_completed      = null;
    m_ExecutionError = null;
  }

  /**
   * Returns the job info/identifier.
   *
   * @return		the info
   */
  public String getJobInfo() {
    return m_jobInfo;
  }

  /**
   * Called once a job has completed execution.
   *
   * @param j		Job
   * @param jr		Result of Job
   */
  public void jobCompleted(Job j, JobResult jr) {
    if (m_completed != null)
      m_completed.jobCompleted(new JobCompleteEvent(this, j,jr));
  }

  /**
   * Sets the listener that gets notified when the job got finished.
   *
   * @param l		the listener
   */
  public void setJobCompleteListener(JobCompleteListener l) {
    m_completed = l;
  }

  /**
   * Returns the listener that gets notified when the job got finished.
   *
   * @return		the listener, can be null
   */
  public JobCompleteListener getJobCompleteListener() {
    return m_completed;
  }

  /**
   * Whether the job has been finished.
   *
   * @return		true if the job has finished, false otherwise
   */
  public boolean isComplete() {
    return m_complete;
  }

  /**
   * Checks whether all pre-conditions have been met.
   *
   * @return		null if everything is OK, otherwise an error message
   */
  protected abstract String preProcessCheck();

  /**
   * Does the actual execution of the job.
   * 
   * @throws Exception if fails to execute job
   */
  protected abstract void process() throws Exception;

  /**
   * Checks whether all post-conditions have been met.
   *
   * @return		null if everything is OK, otherwise an error message
   */
  protected abstract String postProcessCheck();

  /**
   * Returns additional information to be added to the error message.
   * Default returns an empty string.
   *
   * @return		the additional information
   */
  protected String getAdditionalErrorInformation() {
    return "";
  }

  /**
   * Override to do computation.
   *
   * @return		JobResult
   */
  public JobResult execute() {
    JobResult		result;
    boolean		success;
    String		addInfo;

    // pre-check
    m_ExecutionError = preProcessCheck();
    success          = (m_ExecutionError == null);
    if (!success)
      m_ExecutionError = "'pre-check' failed: " + m_ExecutionError;

    // process data
    if (success) {
      try {
	process();
      }
      catch (Exception e) {
	m_ExecutionError = "'process' failed with exception: " + Utils.throwableToString(e);
	success          = false;
      }
    }

    // post-check
    if (success) {
      m_ExecutionError = postProcessCheck();
      success          = (m_ExecutionError == null);
      if (!success)
	m_ExecutionError = "'post-check' failed: " + m_ExecutionError;
    }

    if (!success) {
      addInfo = getAdditionalErrorInformation();
      if (addInfo.length() > 0)
	m_ExecutionError += "\n" + addInfo;
    }

    // assemble result
    m_complete = true;
    result     = new JobResult(success ? toString() : m_ExecutionError, success);

    return result;
  }

  /**
   * Checks whether there was a problem with the job execution.
   *
   * @return		true if an error occurred
   */
  public boolean hasExecutionError() {
    return (m_ExecutionError != null);
  }

  /**
   * Returns the execution error, if any.
   *
   * @return		the error, null if none occurred
   */
  public String getExecutionError() {
    return m_ExecutionError;
  }

  /**
   * Cleans up data structures, frees up memory.
   * Removes dependencies and job parameters.
   */
  public void cleanUp() {
  }

  /**
   * Returns a string representation of this job.
   *
   * @return		the job as string
   */
  @Override
  public abstract String toString();
}
