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
 * JobQueueHandler.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.scripting.engine;

import adams.multiprocess.CallableWithResult;

/**
 * Interface for scripting engines that manage a job queue.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface JobQueueHandler
  extends RemoteScriptingEngine {

  /**
   * Sets the maximum number of concurrent jobs to execute.
   *
   * @param value	the number of jobs
   */
  public void setMaxConcurrentJobs(int value);

  /**
   * Returns the maximum number of concurrent jobs to execute.
   *
   * @return		the number of jobs
   */
  public int getMaxConcurrentJobs();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String maxConcurrentJobsTipText();

  /**
   * Queues the job in the execution pipeline.
   *
   * @param job		the job to queue
   */
  public void queueJob(CallableWithResult<String> job);
}
