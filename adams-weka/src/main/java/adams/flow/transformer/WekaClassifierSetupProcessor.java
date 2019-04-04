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
 * WekaClassifierSetupProcessor.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.flow.core.Token;
import adams.flow.transformer.wekaclassifiersetupprocessor.AbstractClassifierSetupProcessor;
import adams.flow.transformer.wekaclassifiersetupprocessor.PassThrough;

/**
 <!-- globalinfo-start -->
 * Applies the specified processor to the incoming array of classifiers, e.g., for generating new or filtered setups.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.classifiers.Classifier[]<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;weka.classifiers.Classifier[]<br>
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
 * &nbsp;&nbsp;&nbsp;default: WekaClassifierProcessor
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
 * <pre>-processor &lt;adams.flow.transformer.wekaclassifierprocessor.AbstractClassifierProcessor&gt; (property: processor)
 * &nbsp;&nbsp;&nbsp;The processor to apply to the incoming classifier arrays.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.transformer.wekaclassifierprocessor.PassThrough
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class WekaClassifierSetupProcessor
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -3019442578354930841L;

  /** the processor to use. */
  protected AbstractClassifierSetupProcessor m_Processor;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies the specified processor to the incoming array of classifiers, "
      + "e.g., for generating new or filtered setups.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "processor", "processor",
      new PassThrough());
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "processor", m_Processor, "processor: ");
  }

  /**
   * Sets the processor the incoming classifier arrays.
   *
   * @param value	the processor
   */
  public void setProcessor(AbstractClassifierSetupProcessor value) {
    m_Processor = value;
    reset();
  }

  /**
   * Returns the processor for the incoming classifier arrays.
   *
   * @return		the processor
   */
  public AbstractClassifierSetupProcessor getProcessor() {
    return m_Processor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String processorTipText() {
    return "The processor to apply to the incoming classifier arrays.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->weka.classifiers.Classifier[].class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{weka.classifiers.Classifier[].class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->weka.classifiers.Classifier[].class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{weka.classifiers.Classifier[].class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String				result;
    weka.classifiers.Classifier[]	cls;

    result = null;

    try {
      cls = (weka.classifiers.Classifier[]) m_InputToken.getPayload();
      cls = m_Processor.process(cls);
      m_OutputToken = new Token(cls);
    }
    catch (Exception e) {
      m_OutputToken = null;
      result = handleException("Failed to process classifiers: ", e);
    }

    return result;
  }
}
