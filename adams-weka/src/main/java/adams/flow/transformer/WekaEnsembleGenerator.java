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
 * WekaEnsembleGenerator.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.flow.core.Token;
import adams.flow.transformer.wekaensemblegenerator.AbstractWekaEnsembleGenerator;
import adams.flow.transformer.wekaensemblegenerator.VotedModels;

/**
 <!-- globalinfo-start -->
 * Uses the specified generator to create ensembles from the incoming data.
 * <br><br>
 <!-- globalinfo-end -->
 * <p>
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.classifiers.Classifier[]<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;weka.classifiers.meta.Vote<br>
 * <br><br>
 <!-- flow-summary-end -->
 * <p>
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: WekaEnsembleGenerator
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
 * <pre>-generator &lt;adams.flow.transformer.wekaensemblegenerator.AbstractWekaEnsembleGenerator&gt; (property: generator)
 * &nbsp;&nbsp;&nbsp;The ensemble generator to use.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.transformer.wekaensemblegenerator.VotedModels -template \"weka.classifiers.meta.Vote -S 1 -B \\\"weka.classifiers.rules.ZeroR \\\" -R AVG\"
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class WekaEnsembleGenerator
  extends AbstractTransformer {

  private static final long serialVersionUID = 6016827763901994488L;

  /** the generator to use. */
  protected AbstractWekaEnsembleGenerator m_Generator;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses the specified generator to create ensembles from the incoming data.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "generator", "generator",
      new VotedModels());
  }

  /**
   * Sets the ensemble generator to use.
   *
   * @param value	the generator
   */
  public void setGenerator(AbstractWekaEnsembleGenerator value) {
    m_Generator = value;
    reset();
  }

  /**
   * Returns the ensemble generator to use.
   *
   * @return		the generator
   */
  public AbstractWekaEnsembleGenerator getGenerator() {
    return m_Generator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String generatorTipText() {
    return "The ensemble generator to use.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "generator", m_Generator, "generator: ");
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return m_Generator.accepts();
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return m_Generator.generates();
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;

    result = null;

    try {
      m_OutputToken = new Token(m_Generator.generate(m_InputToken.getPayload()));
    }
    catch (Exception e) {
      result = handleException("Failed to generate ensemble using: " + m_Generator.toCommandLine(), e);
    }

    return result;
  }
}
