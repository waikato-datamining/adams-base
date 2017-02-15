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
 * WekaExperimentExecution.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.option.OptionUtils;
import adams.flow.container.WekaExperimentContainer;
import adams.flow.core.Token;
import adams.gui.tools.wekamultiexperimenter.experiment.AbstractExperiment;
import adams.multiprocess.JobRunner;
import adams.multiprocess.LocalJobRunner;

/**
 <!-- globalinfo-start -->
 * Executes an experiment. The jobrunner of the experiment can be overriden.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.gui.tools.wekamultiexperimenter.experiment.AbstractExperiment<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaExperimentContainer<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.WekaExperimentContainer: Experiment, Instances, Spreadsheet
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: WekaExperimentExecution
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this 
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical 
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing 
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-override-jobrunner &lt;boolean&gt; (property: overrideJobRunner)
 * &nbsp;&nbsp;&nbsp;If enabled, the jobrunner of the experiments gets replaced with the one 
 * &nbsp;&nbsp;&nbsp;specified here.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-jobrunner &lt;adams.multiprocess.JobRunner&gt; (property: jobRunner)
 * &nbsp;&nbsp;&nbsp;The JobRunner to use for processing the jobs.
 * &nbsp;&nbsp;&nbsp;default: adams.multiprocess.LocalJobRunner
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaExperimentExecution
  extends AbstractTransformer {

  private static final long serialVersionUID = 5060803438523115907L;

  /** whether to override the jobrunner in the experiment. */
  protected boolean m_OverrideJobRunner;

  /** the JobRunner template. */
  protected JobRunner m_JobRunner;

  /** the current experiment. */
  protected transient AbstractExperiment m_Experiment;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Executes an experiment. The jobrunner of the experiment can be overriden.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "override-jobrunner", "overrideJobRunner",
      false);

    m_OptionManager.add(
      "jobrunner", "jobRunner",
      new LocalJobRunner());
  }

  /**
   * Sets whether to override the jobrunner of the experiment.
   *
   * @param value	true if to override
   */
  public void setOverrideJobRunner(boolean value) {
    m_OverrideJobRunner = value;
    reset();
  }

  /**
   * Returns whether to override the jobrunner of the experiment.
   *
   * @return		true if to override
   */
  public boolean getOverrideJobRunner() {
    return m_OverrideJobRunner;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String overrideJobRunnerTipText() {
    return "If enabled, the jobrunner of the experiments gets replaced with the one specified here.";
  }

  /**
   * Sets the jobrunner for the experiment.
   *
   * @param value	the jobrunner
   */
  public void setJobRunner(JobRunner value) {
    m_JobRunner = value;
    reset();
  }

  /**
   * Returns the jobrunner for the experiment.
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
    return "The JobRunner to use for processing the jobs.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    if (m_OverrideJobRunner)
      return QuickInfoHelper.toString(this, "jobRunner", m_JobRunner, "jobrunner: ");
    else
      return null;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{AbstractExperiment.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{WekaExperimentContainer.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    WekaExperimentContainer	cont;

    // setup experiment
    m_Experiment = (AbstractExperiment) m_InputToken.getPayload();
    if (m_OverrideJobRunner) {
      m_Experiment = (AbstractExperiment) OptionUtils.shallowCopy(m_Experiment);
      m_Experiment.setJobRunner(m_JobRunner);
    }

    // run experiment
    result = m_Experiment.execute();
    if (result == null) {
      cont          = new WekaExperimentContainer(m_Experiment, m_Experiment.toInstances(), m_Experiment.toSpreadSheet());
      m_OutputToken = new Token(cont);
    }

    m_Experiment = null;

    return result;
  }

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    if (m_Experiment != null)
      m_Experiment.stopExecution();
    super.stopExecution();
  }
}
