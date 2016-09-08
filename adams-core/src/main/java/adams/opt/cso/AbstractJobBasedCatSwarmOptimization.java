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
 * AbstractJobBasedCatSwarmOptimization.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.opt.cso;

import adams.core.ThreadLimiter;
import adams.flow.core.Actor;
import adams.flow.standalone.JobRunnerSetup;
import adams.multiprocess.AbstractJob;
import adams.multiprocess.JobList;
import adams.multiprocess.JobRunner;
import adams.multiprocess.LocalJobRunner;
import org.jblas.DoubleMatrix;

/**
 * Ancestor for Cat Swarm Optimizers that use jobs for evaluating the swarm.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractJobBasedCatSwarmOptimization
  extends AbstractCatSwarmOptimization
  implements ThreadLimiter {

  private static final long serialVersionUID = 8103615139210955070L;

  /**
   * Job for executing swarm evaluations.
   *
   * @param <T> the type of swarm optimization
   */
  public static abstract class AbstractCatSwarmOptimizationJob<T extends AbstractCatSwarmOptimization>
    extends AbstractJob {

    private static final long serialVersionUID = -2962242270638471818L;

    /** the owner. */
    protected T m_Owner;

    /** the index to evaluate. */
    protected int m_Index;

    /** the swarm positions. */
    protected DoubleMatrix m_Positions;

    /** the calculated fitness. */
    protected double m_Fitness;

    /**
     * Initializes the job.
     *
     * @param owner     the owning optimizer
     * @param index     the index in the swarm
     * @param positions the positions of the swarm
     */
    public AbstractCatSwarmOptimizationJob(T owner, int index, DoubleMatrix positions) {
      super();

      m_Owner      = owner;
      m_Index      = index;
      m_Positions  = positions;
      m_Fitness    = Double.NaN;
    }

    /**
     * Returns the owning optimizer.
     *
     * @return		the owner
     */
    public T getOwner() {
      return m_Owner;
    }

    /**
     * Returns the index in the swarm.
     *
     * @return		the index
     */
    public int getIndex() {
      return m_Index;
    }

    /**
     * Returns the calculated fitness.
     *
     * @return		the fitness
     */
    public double getFitness() {
      return m_Fitness;
    }

    /**
     * Checks whether all pre-conditions have been met.
     *
     * @return		null if everything is OK, otherwise an error message
     */
    @Override
    protected String preProcessCheck() {
      if (m_Positions == null)
	return "No swarm positions provided!";
      return null;
    }

    /**
     * Checks whether all post-conditions have been met.
     *
     * @return		null if everything is OK, otherwise an error message
     */
    @Override
    protected String postProcessCheck() {
      if (Double.isNaN(m_Fitness))
	return "Failed to calculate fitness!";
      return null;
    }

    /**
     * Cleans up data structures, frees up memory.
     */
    @Override
    public void cleanUp() {
      super.cleanUp();

      m_Positions = null;
    }

    /**
     * Returns a string representation of the job.
     *
     * @return		a string representation
     */
    @Override
    public String toString() {
      return m_Owner.getClass().getName()+ ", fitness=" + m_Fitness;
    }
  }

  /** the number of threads to use (-1 for #of cores). */
  protected int m_NumThreads;

  /** the jobrunner setup. */
  protected transient JobRunnerSetup m_JobRunnerSetup;

  /** the flow context. */
  protected Actor m_FlowContext;

  /** the job runner in use. */
  protected JobRunner<AbstractCatSwarmOptimizationJob> m_JobRunner;

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
    if (getOptionManager().isValid("numThreads", value)) {
      m_NumThreads = value;
      reset();
    }
  }

  /**
   * Returns the number of threads to use.
   *
   * @return 		the number of threads: -1 = # of CPUs/cores
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
    return "The number of threads to use for executing the jobs; use -1 for all available cores.";
  }

  /**
   * Sets the jobrunner setup to use.
   *
   * @param value	the setup, can be null to use default
   */
  public void setJobRunnerSetup(JobRunnerSetup value) {
    m_JobRunnerSetup = value;
  }

  /**
   * Returns the jobrunner setup in use.
   *
   * @return		the setup, null if using default
   */
  public JobRunnerSetup getJobRunnerSetup() {
    return m_JobRunnerSetup;
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
   * Creates a new Job.
   *
   * @param index	the index in the swarm
   * @return		the job
   */
  protected abstract AbstractCatSwarmOptimizationJob newJob(int index);

  /**
   * Helper methods to evaluate all or part of the swarm.
   *
   */
  @Override
  protected void evalSwarm(int[] indices) {
    JobList<AbstractCatSwarmOptimizationJob>	jobs;
    AbstractCatSwarmOptimizationJob 		job;
    int						i;

    if (m_JobRunnerSetup == null)
      m_JobRunner = new LocalJobRunner<>();
    else
      m_JobRunner = m_JobRunnerSetup.newInstance();
    if (m_JobRunner instanceof ThreadLimiter)
      ((ThreadLimiter) m_JobRunner).setNumThreads(getNumThreads());
    m_JobRunner.setFlowContext(getFlowContext());

    jobs = new JobList<>();
    for (int index: indices) {
      job = newJob(index);
      jobs.add(job);
    }

    m_JobRunner.add(jobs);
    m_JobRunner.start();
    m_JobRunner.stop();

    for (i = 0; i < m_JobRunner.getJobs().size(); i++) {
      job = m_JobRunner.getJobs().get(i);
      // success? If not, just add the header of the original data
      if (Double.isNaN(job.getFitness()) || m_Stopped)
	updateFitness(job.getIndex(), Double.POSITIVE_INFINITY);
      else
	updateFitness(job.getIndex(), job.getFitness());
      job.cleanUp();
    }
    m_JobRunner.cleanUp();
    m_JobRunner.stop();

    m_JobRunner = null;
  }

  /**
   * Stops the execution of the algorithm.
   */
  @Override
  public void stopExecution() {
    super.stopExecution();
    if (m_JobRunner != null)
      m_JobRunner.terminate();
  }
}
