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
 * AbstractMetaJobRunner.java
 * Copyright (C) 2015-2025 University of Waikato, Hamilton, NZ
 */

package adams.multiprocess;

import adams.core.QuickInfoHelper;
import adams.core.option.OptionUtils;
import adams.data.distribution.T;
import adams.event.JobCompleteListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Ancestor for meta-jobrunners, that wrap around a base jobrunner.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractMetaJobRunner
  extends AbstractJobRunner {

  private static final long serialVersionUID = 6615050794532600520L;

  /** the base jobrunner to use. */
  protected JobRunner m_JobRunner;

  /** the list of jobs. */
  protected List<Job> m_Jobs;

  /** call when job complete. */
  protected transient HashSet<JobCompleteListener> m_JobCompleteListeners;

  /** the actual jobrunner. */
  protected JobRunner m_ActualJobRunner;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "job-runner", "jobRunner",
      getDefaultJobRunner());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_FlowContext          = null;
    m_Jobs                 = new ArrayList<>();
    m_JobCompleteListeners = new HashSet<>();
  }

  /**
   * Returns the default jobrunner.
   *
   * @return		the jobrunner
   */
  protected JobRunner getDefaultJobRunner() {
    return new LocalJobRunner<>();
  }

  /**
   * Sets the base jobrunner.
   *
   * @param value 	the jobrunner
   */
  public void setJobRunner(JobRunner value) {
    m_JobRunner = value;
    m_JobRunner.setFlowContext(getFlowContext());
    reset();
  }

  /**
   * Returns the base jobrunner.
   *
   * @return		the jobrunner
   */
  public JobRunner getJobRunner() {
    return m_JobRunner;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String jobRunnerTipText() {
    return "The base jobrunner to use.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "jobRunner", m_JobRunner, "jobrunner: ");
  }

  /**
   * Clears all jobs.
   */
  public void clear() {
    if (m_ActualJobRunner != null)
      m_ActualJobRunner.clear();
  }

  /**
   * Adds the job to the execution queue.
   *
   * @param job		the job to add
   */
  @Override
  public void add(Job job) {
    if (m_Terminating) {
      getLogger().warning("Terminating, cannot add new jobs!");
      return;
    }
    m_Jobs.add(job);
  }

  /**
   * Adds the jobs to the execution queue.
   *
   * @param jobs	the jobs to add
   */
  @Override
  public void add(JobList jobs) {
    if (m_Terminating) {
      getLogger().warning("Terminating, cannot add new jobs!");
      return;
    }
    m_Jobs.addAll(jobs);
  }

  /**
   * Returns the list of queued jobs.
   *
   * @return		the jobs
   */
  public List<T> getJobs() {
    if (m_ActualJobRunner != null)
      return m_ActualJobRunner.getJobs();
    else
      return new ArrayList<>();
  }

  /**
   * Adds the listener.
   *
   * @param l		the listener to add
   */
  @Override
  public void addJobCompleteListener(JobCompleteListener l) {
    m_JobCompleteListeners.add(l);
  }

  /**
   * Removes the listener.
   *
   * @param l		the listener to remove
   */
  @Override
  public void removeJobCompleteListener(JobCompleteListener l) {
    m_JobCompleteListeners.remove(l);
  }

  /**
   * Returns an instance of the actual job runner to use.
   *
   * @return		the job runner to use
   */
  protected JobRunner newActualJobRunner() {
    return (JobRunner) OptionUtils.shallowCopy(m_JobRunner);
  }

  /**
   * Returns whether to transfer the listeners to the actual job runner.
   *
   * @return		true if to transfer
   * @see		#addJobCompleteListener(JobCompleteListener)
   * @see		#removeJobCompleteListener(JobCompleteListener)
   */
  protected boolean getTransferJobCompleteListeners() {
    return true;
  }

  /**
   * Before actual start up.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String preStart() {
    String	result;

    result = super.preStart();

    if (m_JobCompleteListeners == null)
      m_JobCompleteListeners = new HashSet<>();

    if (result == null) {
      m_ActualJobRunner = newActualJobRunner();
      m_ActualJobRunner.setFlowContext(getFlowContext());
      for (Job job: m_Jobs)
	m_ActualJobRunner.add(job);
      if (getTransferJobCompleteListeners()) {
	for (JobCompleteListener l : m_JobCompleteListeners)
	  m_ActualJobRunner.addJobCompleteListener(l);
      }
    }

    return result;
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    super.cleanUp();
    if (m_ActualJobRunner != null) {
      m_ActualJobRunner.cleanUp();
      m_ActualJobRunner = null;
    }
  }
}
