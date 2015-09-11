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
 * Job.java
 * Copyright (C) 2008-2015 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.multiprocess;

import adams.core.CleanUpHandler;
import adams.event.JobCompleteListener;

/**
 * A job is a unit of execution.
 *
 * @author dale
 * @version $Revision: 11631 $
 */
public interface Job
  extends CleanUpHandler {

  /**
   * Returns the job info/identifier.
   *
   * @return		the info
   */
  public String getJobInfo();

  /**
   * Called once a job has completed execution.
   *
   * @param j		Job
   * @param jr		Result of Job
   */
  public void jobCompleted(Job j, JobResult jr);

  /**
   * Sets the listener that gets notified when the job got finished.
   *
   * @param l		the listener
   */
  public void setJobCompleteListener(JobCompleteListener l);

  /**
   * Returns the listener that gets notified when the job got finished.
   *
   * @return		the listener, can be null
   */
  public JobCompleteListener getJobCompleteListener();

  /**
   * Whether the job has been finished.
   *
   * @return		true if the job has finished, false otherwise
   */
  public boolean isComplete();

  /**
   * Override to do computation.
   *
   * @return		JobResult
   */
  public JobResult execute();

  /**
   * Checks whether there was a problem with the job execution.
   *
   * @return		true if an error occurred
   */
  public boolean hasExecutionError();

  /**
   * Returns the execution error, if any.
   *
   * @return		the error, null if none occurred
   */
  public String getExecutionError();

  /**
   * Cleans up data structures, frees up memory.
   * Removes dependencies and job parameters.
   */
  public void cleanUp();
}
