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
 * JobRunner.java
 * Copyright (C) 2008 University of Waikato, Hamilton, New Zealand
 */

package adams.multiprocess;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import adams.core.Pausable;
import adams.core.Performance;
import adams.core.management.ProcessUtils;
import adams.event.JobCompleteEvent;
import adams.event.JobCompleteListener;

/**
 * Job Running engine.
 *
 * @author  dale (dale at cs dot waikato dot ac dot nz)
 * @author  FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of job to handle
 */
public class JobRunner<T extends Job>
  implements Pausable {

  /** the number of threads to use. */
  protected int m_NumThreads;

  /** call when job complete. */
  protected HashSet<JobCompleteListener> m_JobCompleteListeners;

  /** stores Jobs. add to job queue when dependencies complete. */
  protected Vector<T> m_queue;

  /** the executor service to use for parallel execution. */
  protected PausableFixedThreadPoolExecutor m_Executor;

  /**
   * Initialise Job Runner with maximum number of threads.
   */
  public JobRunner() {
    this(-1);
  }

  /**
   * Initialise Job Runner with specified number of threads (gets limited to
   * maximum number of cores/cpus).
   *
   * @param numThreads	the number of threads to use, if less than 1, the
   * 			maximum number of processors found in Performance.props
   * 			is used
   * @see		Performance#getMaxNumProcessors()
   */
  public JobRunner(int numThreads) {
    super();

    m_queue                = new Vector<T>();
    m_JobCompleteListeners = new HashSet<JobCompleteListener>();
    m_NumThreads           = numThreads;
    if (m_NumThreads < 1)
      m_NumThreads = Performance.getMaxNumProcessors();
    if ((m_NumThreads < 1) || (m_NumThreads > ProcessUtils.getAvailableProcessors()))
      m_NumThreads = ProcessUtils.getAvailableProcessors();

    addJobCompleteListener(JobCompleteManager.getSingleton());
  }

  /**
   * Returns the number of threads being used for execution.
   *
   * @return		the number of threads
   */
  public int getNumThreads() {
    return m_NumThreads;
  }

  /**
   * Adds the listener.
   *
   * @param l		the listener to add
   */
  public void addJobCompleteListener(JobCompleteListener l) {
    synchronized(m_JobCompleteListeners) {
      m_JobCompleteListeners.add(l);
    }
  }

  /**
   * Removes the listener.
   *
   * @param l		the listener to remove
   */
  public void removeJobCompleteListener(JobCompleteListener l) {
    synchronized(m_JobCompleteListeners) {
      m_JobCompleteListeners.remove(l);
    }
  }

  /**
   * Notifies all listeners with the given event.
   *
   * @param e		the event to send to the listeners
   */
  protected void notifyJobCompleteListeners(JobCompleteEvent e) {
    Iterator<JobCompleteListener>	iter;

    synchronized(m_JobCompleteListeners) {
      iter = m_JobCompleteListeners.iterator();
      while (iter.hasNext())
	iter.next().jobCompleted(e);
    }
  }

  /**
   * Adds the job to the execution queue.
   *
   * @param job		the job to add
   */
  public void add(T job) {
    synchronized(m_queue) {
      m_queue.add(job);
    }
    enqueue();
  }

  /**
   * Adds the jobs to the execution queue.
   *
   * @param job		the jobs to add
   */
  public void add(JobList<T> jobs) {
    synchronized(m_queue) {
      m_queue.addAll(jobs);
    }
    enqueue();
  }

  /**
   * Enqueues any jobs still waiting in the queue in the executor service.
   *
   * @return		true if jobs were queued
   */
  protected boolean enqueue() {
    boolean			result;
    Callable<String>		job;
    Vector<T> 			queue;

    result = false;

    if (m_Executor == null)
      return result;

    if (m_queue.size() > 0) {
      synchronized(m_queue) {
	queue = new Vector<T>();

	// queue jobs
	for (final T j: m_queue) {
	  queue.add(j);
	  job = new Callable<String>() {
	    public String call() throws Exception {
	      JobResult jr = j.execute();
	      complete((T) j, jr);
	      String result = null;
	      if (!jr.getSuccess())
		result = jr.toString();
	      return result;
	    }
	  };
	  try {
	    m_Executor.submit(job);  // FIXME necessary to enclose in synchronized block?
	  }
	  catch (Exception e) {
	    // ignored
	  }
	}

	m_queue.removeAll(queue);
      }
      result = true;
    }

    return result;
  }

  /**
   * Starts the thread pool and execution of jobs.
   */
  public void start() {
    if (m_Executor == null)
      m_Executor = new PausableFixedThreadPoolExecutor(m_NumThreads);
    enqueue();
  }

  /**
   * Stops the execution after all currently queued jobs have been executed.
   */
  public void stop() {
    if (m_Executor == null)
      return;

    try {
      if (m_Executor.isPaused())
	m_Executor.resumeExecution();
      m_Executor.shutdown();
    }
    catch (Exception e) {
      // ignored
    }

    waitForComplete();
  }

  /**
   * Stops the execution immediately.
   */
  public void terminate() {
    if (m_Executor == null)
      return;

    try {
      if (m_Executor.isPaused())
	m_Executor.resumeExecution();
      m_Executor.shutdownNow();
    }
    catch (Exception e) {
      // ignored
    }

    waitForComplete();
  }

  /**
   * Wait until Jobs are completed.
   */
  protected void waitForComplete() {
    while ((m_Executor != null) && !m_Executor.isTerminated()) {
      try {
	m_Executor.awaitTermination(100, TimeUnit.MILLISECONDS);
      }
      catch (Exception e) {
	// ignored
      }
    }

    m_Executor = null;
  }

  /**
   * Job is complete, so check for more to add..
   *
   * @param j	job
   * @param jr	job result
   */
  public void complete(T j, JobResult jr) {
    notifyJobCompleteListeners(new JobCompleteEvent(this, j, jr));
    if (j.getJobCompleteListener() != null)
      j.getJobCompleteListener().jobCompleted(new JobCompleteEvent(this, j, jr));
    enqueue();
  }

  /**
   * Pauses the execution.
   */
  public void pauseExecution() {
    if (m_Executor != null)
      m_Executor.pauseExecution();
  }

  /**
   * Returns whether the object is currently paused.
   *
   * @return		true if object is paused
   */
  public boolean isPaused() {
    if (m_Executor != null)
      return m_Executor.isPaused();
    else
      return false;
  }

  /**
   * Resumes the execution.
   */
  public void resumeExecution() {
    if (m_Executor != null)
      m_Executor.resumeExecution();
  }
}
