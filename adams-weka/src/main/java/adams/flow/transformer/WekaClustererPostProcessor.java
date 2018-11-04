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
 * WekaClustererPostProcessor.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.ClassCrossReference;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.flow.container.WekaModelContainer;
import adams.flow.core.Token;
import adams.flow.transformer.wekaclusterer.AbstractClustererPostProcessor;
import adams.flow.transformer.wekaclusterer.PassThrough;

/**
 <!-- globalinfo-start -->
 * Applies the specified post-processor to the cluster container (adams.flow.container.WekaModelContainer)<br>
 * <br>
 * See also:<br>
 * adams.flow.transformer.WekaTrainClusterer
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaModelContainer<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaModelContainer<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.WekaModelContainer: Model, Header, Dataset
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
 * &nbsp;&nbsp;&nbsp;default: WekaClustererPostProcessor
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
 * <pre>-post-processor &lt;adams.flow.transformer.wekaclusterer.AbstractClustererPostProcessor&gt; (property: postProcessor)
 * &nbsp;&nbsp;&nbsp;The post-processor to use.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.transformer.wekaclusterer.PassThrough
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class WekaClustererPostProcessor
  extends AbstractTransformer
  implements ClassCrossReference {

  /** for serialization. */
  private static final long serialVersionUID = -3019442578354930841L;

  /** the post-processor. */
  protected AbstractClustererPostProcessor m_PostProcessor;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Applies the specified post-processor to the cluster "
	+ "container (" + Utils.classToString(WekaModelContainer.class) + ")";
  }

  /**
   * Returns the cross-referenced classes.
   *
   * @return		the classes
   */
  public Class[] getClassCrossReferences() {
    return new Class[]{WekaTrainClusterer.class};
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "post-processor", "postProcessor",
      new PassThrough());
  }

  /**
   * Sets the post-processor to use.
   *
   * @param value	the post-processor
   */
  public void setPostProcessor(AbstractClustererPostProcessor value) {
    m_PostProcessor = value;
    reset();
  }

  /**
   * Returns the post-processor in use.
   *
   * @return		the post-processor
   */
  public AbstractClustererPostProcessor getPostProcessor() {
    return m_PostProcessor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String postProcessorTipText() {
    return "The post-processor to use.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "postProcessor", m_PostProcessor, "post-processor: ");
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the accepted classes
   */
  public Class[] accepts() {
    return new Class[]{WekaModelContainer.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the generated classes
   */
  public Class[] generates() {
    return new Class[]{WekaModelContainer.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    WekaModelContainer		cont;

    result = null;

    try {
      cont = m_InputToken.getPayload(WekaModelContainer.class);
      cont = m_PostProcessor.postProcess(cont);
      m_OutputToken = new Token(cont);
    }
    catch (Exception e) {
      m_OutputToken = null;
      result = handleException("Failed to process cluster container: " + m_InputToken.getPayload(), e);
    }

    return result;
  }
}
