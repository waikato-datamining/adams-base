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
 * BoofCVTransformer.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.data.boofcv.BoofCVHelper;
import adams.data.boofcv.BoofCVImageContainer;
import adams.data.boofcv.transformer.AbstractBoofCVTransformer;
import adams.data.image.AbstractImageContainer;

import java.util.Arrays;

/**
 <!-- globalinfo-start -->
 * Applies a BoofCV transformation to the incoming image and outputs the generated image(s).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.image.AbstractImageContainer<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;BoofCVImageContainer<br>
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
 * &nbsp;&nbsp;&nbsp;default: BoofCVTransformer
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-output-array &lt;boolean&gt; (property: outputArray)
 * &nbsp;&nbsp;&nbsp;Whether to output the images as an array or one-by-one.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-transformer &lt;AbstractBoofCVTransformer&gt; (property: transformAlgorithm)
 * &nbsp;&nbsp;&nbsp;The transformer to use for transformering the image.
 * &nbsp;&nbsp;&nbsp;default: PassThrough
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class BoofCVTransformer
  extends AbstractArrayProvider {

  /** for serialization. */
  private static final long serialVersionUID = 3690378527551302472L;

  /** the transformer to apply to the image. */
  protected AbstractBoofCVTransformer m_TransformAlgorithm;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Applies a BoofCV transformation to the incoming image and outputs "
      + "the generated image(s).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "transformer", "transformAlgorithm",
      new adams.data.boofcv.transformer.PassThrough());
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputArrayTipText() {
    return "Whether to output the images as an array or one-by-one.";
  }

  /**
   * Sets the transformer to use.
   *
   * @param value	the transformer
   */
  public void setTransformAlgorithm(AbstractBoofCVTransformer value) {
    m_TransformAlgorithm = value;
    reset();
  }

  /**
   * Returns the transformer in use.
   *
   * @return		the transformer
   */
  public AbstractBoofCVTransformer getTransformAlgorithm() {
    return m_TransformAlgorithm;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String transformAlgorithmTipText() {
    return "The transformer to use for transformering the image.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String      result;
    String      value;

    result = QuickInfoHelper.toString(this, "transformAlgorithm", m_TransformAlgorithm);
    value  = QuickInfoHelper.toString(this, "outputArray", m_OutputArray, (m_OutputArray ? "as array" : ""), ", ");
    if (value != null)
      result += value;

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  public Class[] accepts() {
    return new Class[]{AbstractImageContainer.class};
  }

  /**
   * Returns the base class of the items.
   *
   * @return		the class
   */
  @Override
  protected Class getItemClass() {
    return BoofCVImageContainer.class;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    BoofCVImageContainer	img;

    result = null;

    try {
      m_TransformAlgorithm.setFlowContext(this);
      img = BoofCVHelper.toBoofCVImageContainer((AbstractImageContainer) m_InputToken.getPayload());
      m_Queue.clear();
      m_Queue.addAll(Arrays.asList(m_TransformAlgorithm.transform(img)));
      m_TransformAlgorithm.setFlowContext(null);
    }
    catch (Exception e) {
      result = handleException("Failed to transform image: ", e);
    }

    return result;
  }
}
