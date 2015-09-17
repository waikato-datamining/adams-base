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
 * AbstractMetaJobRunner.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.multiprocess;

import adams.core.option.OptionUtils;
import adams.event.JobCompleteListener;
import adams.flow.core.Actor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Ancestor for meta-jobrunners, that wrap around a base jobrunner.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractMetaJobRunner
  extends AbstractJobRunner {

  private static final long serialVersionUID = 6615050794532600520L;

  /** the base jobrunner to use. */
  protected JobRunner m_JobRunner;

  /** the flow context. */
  protected Actor m_FlowContext;

  /** the list of jobs. */
  protected List<Job> m_Jobs;

  /** call when job complete. */
  protected HashSet<JobCompleteListener> m_JobCompleteListeners;

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
   * Sets the flow context, if any.
   *
   * @param value	the context
   */
  public void setFlowContext(Actor value) {
    m_FlowContext = value;
  }

  /**
   * Return the flow context, if any.
   *
   * @return		the context, null if none available
   */
  public Actor getFlowContext() {
    return m_FlowContext;
  }

  /**
   * Adds the job to the execution queue.
   *
   * @param job		the job to add
   */
  @Override
  public void add(Job job) {
    m_Jobs.add(job);
  }

  /**
   * Adds the jobs to the execution queue.
   *
   * @param jobs	the jobs to add
   */
  @Override
  public void add(JobList jobs) {
    m_Jobs.addAll(jobs);
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
}
