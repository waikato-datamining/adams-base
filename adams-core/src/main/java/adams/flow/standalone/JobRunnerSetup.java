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
 * JobRunnerSetup.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.flow.standalone;

import adams.core.QuickInfoHelper;
import adams.core.option.OptionUtils;
import adams.multiprocess.JobRunner;
import adams.multiprocess.LocalJobRunner;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class JobRunnerSetup
  extends AbstractStandalone {

  private static final long serialVersionUID = 4221231366291664608L;

  /** the job runner to use. */
  protected JobRunner m_JobRunner;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Defines the job runner setup to use for parallel/distributed computation.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "job-runner", "jobRunner",
      new LocalJobRunner(-1));
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "jobRunner", m_JobRunner);
  }

  /**
   * Sets the job runner to use.
   *
   * @param value	the job runner to use
   */
  public void setJobRunner(JobRunner value) {
    m_JobRunner = value;
    reset();
  }

  /**
   * Returns the job runner to use.
   *
   * @return		the job runner to use
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
    return "The job runner to use for parallel/distributed computation.";
  }

  /**
   * Returns a new instance of the job runner.
   *
   * @return		the new instance
   */
  public JobRunner newInstance() {
    return (JobRunner) OptionUtils.shallowCopy(m_JobRunner);
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    return null;
  }
}
