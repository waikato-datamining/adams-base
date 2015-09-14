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
 * JobRunner.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.multiprocess;

import adams.core.Pausable;
import adams.core.option.OptionHandler;
import adams.event.JobCompleteListener;

/**
 * Interface for runners that execute jobs.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface JobRunner<T extends Job>
  extends Pausable, OptionHandler {

  /**
   * Adds the listener.
   *
   * @param l		the listener to add
   */
  public void addJobCompleteListener(JobCompleteListener l);

  /**
   * Removes the listener.
   *
   * @param l		the listener to remove
   */
  public void removeJobCompleteListener(JobCompleteListener l);

  /**
   * Adds the job to the execution queue.
   *
   * @param job		the job to add
   */
  public void add(T job);

  /**
   * Adds the jobs to the execution queue.
   *
   * @param jobs	the jobs to add
   */
  public void add(JobList<T> jobs);

  /**
   * Starts the thread pool and execution of jobs.
   */
  public void start();

  /**
   * Stops the execution after all currently queued jobs have been executed.
   */
  public void stop();

  /**
   * Stops the execution immediately.
   */
  public void terminate();

  /**
   * Job is complete, so check for more to add..
   *
   * @param j	job
   * @param jr	job result
   */
  public void complete(T j, JobResult jr);

  /**
   * Pauses the execution.
   */
  public void pauseExecution();

  /**
   * Returns whether the object is currently paused.
   *
   * @return		true if object is paused
   */
  public boolean isPaused();

  /**
   * Resumes the execution.
   */
  public void resumeExecution();
}
