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
 * Copyright (C) 2008-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.multiprocess;

import adams.core.Performance;
import adams.core.ThreadLimiter;
import adams.core.management.ProcessUtils;
import adams.event.JobCompleteEvent;
import adams.event.JobCompleteListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 <!-- globalinfo-start -->
 * Executes the jobs on the local machine.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-num-threads &lt;int&gt; (property: numThreads)
 * &nbsp;&nbsp;&nbsp;The number of threads to use for executing the branches; -1 = number of 
 * &nbsp;&nbsp;&nbsp;CPUs&#47;cores; 0 or 1 = sequential execution.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  dale (dale at cs dot waikato dot ac dot nz)
 * @author  FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of job to handle
 */
public class LocalJobRunner<T extends Job>
  extends AbstractJobRunner<T>
  implements ThreadLimiter {

  private static final long serialVersionUID = -7957101716595901777L;

  /** the number of threads to use. */
  protected int m_NumThreads;

  /** call when job complete. */
  protected transient HashSet<JobCompleteListener> m_JobCompleteListeners;

  /** all the jobs. */
  protected List<T> m_Jobs;

  /** stores Jobs. add to job queue when dependencies complete. */
  protected List<T> m_Queue;

  /** the executor service to use for parallel execution. */
  protected PausableFixedThreadPoolExecutor m_Executor;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Jobs                 = new ArrayList<>();
    m_Queue                = new ArrayList<>();
    m_JobCompleteListeners = new HashSet<JobCompleteListener>();
    addJobCompleteListener(JobCompleteManager.getSingleton());
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Executes the jobs on the local machine.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "num-threads", "numThreads",
      -1, -1, null);
  }

  /**
   * Sets the number of threads to use.
   *
   * @param value 	the number of threads: -1 = # of CPUs/cores
   */
  public void setNumThreads(int value) {
    m_NumThreads = value;
    reset();
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
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numThreadsTipText() {
    return "The number of threads to use for executing the branches; -1 = number of CPUs/cores; 0 or 1 = sequential execution.";
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
   * Clears all jobs.
   */
  public void clear() {
    m_Jobs.clear();
    synchronized(m_Queue) {
      m_Queue.clear();
    }
  }

  /**
   * Adds the job to the execution queue.
   *
   * @param job		the job to add
   */
  public void add(T job) {
    m_Jobs.add(job);
    synchronized(m_Queue) {
      m_Queue.add(job);
    }
    enqueue();
  }

  /**
   * Adds the jobs to the execution queue.
   *
   * @param jobs	the jobs to add
   */
  public void add(JobList<T> jobs) {
    m_Jobs.addAll(jobs);
    synchronized(m_Queue) {
      m_Queue.addAll(jobs);
    }
    enqueue();
  }

  /**
   * Returns the list of all jobs.
   *
   * @return		the jobs
   */
  public List<T> getJobs() {
    return m_Jobs;
  }

  /**
   * Enqueues any jobs still waiting in the queue in the executor service.
   *
   * @return		true if jobs were queued
   */
  protected boolean enqueue() {
    boolean			result;
    CallableWithResult<String>	job;
    List<T> 			queue;

    result = false;

    if (m_Executor == null)
      return result;

    if (m_Queue.size() > 0) {
      synchronized(m_Queue) {
	queue = new ArrayList<>();

	// queue jobs
	for (final T j: m_Queue) {
	  queue.add(j);
	  job = new CallableWithResult<String>() {
	    protected String doCall() throws Exception {
	      JobResult jr = j.execute();
	      complete(j, jr);
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

	m_Queue.removeAll(queue);
      }
      result = true;
    }

    return result;
  }

  /**
   * Before actual start up.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String preStart() {
    int		numThreads;

    super.preStart();

    if (m_JobCompleteListeners == null)
      m_JobCompleteListeners = new HashSet<>();

    if (m_Executor == null) {
      numThreads = m_NumThreads;
      if (numThreads < 1)
	numThreads = Performance.getMaxNumProcessors();
      if ((numThreads < 1) || (numThreads > ProcessUtils.getAvailableProcessors()))
	numThreads = ProcessUtils.getAvailableProcessors();
      m_Executor = new PausableFixedThreadPoolExecutor(numThreads);
    }

    return null;
  }

  /**
   * Starts the thread pool and execution of jobs.
   * Only gets executed if {@link #preStart()} was successful.
   *
   * @return		null if successful, otherwise error message
   */
  protected String doStart() {
    enqueue();
    return null;
  }

  /**
   * Stops the execution after all currently queued jobs have been executed.
   *
   * @return		null if successful, otherwise error message
   */
  protected String doStop() {
    if (m_Executor == null)
      return null;

    try {
      if (m_Executor.isPaused())
	m_Executor.resumeExecution();
      m_Executor.shutdown();
    }
    catch (Exception e) {
      // ignored
    }

    waitForComplete();

    return null;
  }

  /**
   * Stops the execution immediately.
   *
   * @return		null if successful, otherwise error message
   */
  protected String doTerminate() {
    if (m_Executor == null)
      return null;

    try {
      if (m_Executor.isPaused())
	m_Executor.resumeExecution();
      m_Executor.shutdownNow();
    }
    catch (Exception e) {
      // ignored
    }

    waitForComplete();

    return null;
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
    super.pauseExecution();
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
    super.resumeExecution();
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    super.cleanUp();
    m_Jobs.clear();
  }
}
