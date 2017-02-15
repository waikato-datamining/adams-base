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
 * WekaNewExperiment.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.core.option.OptionUtils;
import adams.flow.core.Token;
import adams.gui.tools.wekamultiexperimenter.experiment.AbstractExperiment;
import adams.gui.tools.wekamultiexperimenter.experiment.CrossValidationExperiment;

/**
 <!-- globalinfo-start -->
 * Generates a new ADAMS experiment setup.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.gui.tools.wekamultiexperimenter.experiment.AbstractExperiment<br>
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
 * &nbsp;&nbsp;&nbsp;default: WekaNewExperiment
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
 * <pre>-experiment &lt;adams.gui.tools.wekamultiexperimenter.experiment.AbstractExperiment&gt; (property: experiment)
 * &nbsp;&nbsp;&nbsp;The experiment setup to output.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.tools.wekamultiexperimenter.experiment.CrossValidationExperiment -results-handler \"adams.gui.tools.wekamultiexperimenter.experiment.FileResultsHandler -reader \\\"adams.data.io.input.ArffSpreadSheetReader -data-row-type adams.data.spreadsheet.DenseDataRow -spreadsheet-type adams.data.spreadsheet.DefaultSpreadSheet\\\" -writer adams.data.io.output.ArffSpreadSheetWriter\" -class-attribute adams.data.weka.classattribute.LastAttribute -jobrunner adams.multiprocess.LocalJobRunner
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaNewExperiment
  extends AbstractSimpleSource {

  private static final long serialVersionUID = 6104313581716027684L;

  /** the experiment. */
  protected AbstractExperiment m_Experiment;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a new ADAMS experiment setup.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "experiment", "experiment",
      getDefaultExperiment());
  }

  /**
   * Returns the default experiment.
   *
   * @return		the experiment
   */
  protected AbstractExperiment getDefaultExperiment() {
    return new CrossValidationExperiment();
  }

  /**
   * Sets the experiment setup.
   *
   * @param value	the setup
   */
  public void setExperiment(AbstractExperiment value) {
    m_Experiment = value;
    reset();
  }

  /**
   * Returns the experiment setup.
   *
   * @return		the setup
   */
  public AbstractExperiment getExperiment() {
    return m_Experiment;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String experimentTipText() {
    return "The experiment setup to output.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "experiment", m_Experiment, "experiment: ");
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{AbstractExperiment.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    m_OutputToken = new Token( OptionUtils.shallowCopy(m_Experiment));
    return null;
  }
}
