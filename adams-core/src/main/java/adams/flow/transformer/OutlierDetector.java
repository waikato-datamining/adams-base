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
 * OutlierDetector.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.data.NotesHandler;
import adams.data.container.DataContainer;
import adams.data.outlier.AbstractOutlierDetector;
import adams.data.outlier.PassThrough;
import adams.flow.container.OutlierDetectorContainer;
import adams.flow.core.Token;

import java.util.List;

/**
 <!-- globalinfo-start -->
 * Applies the outlier detector and either outputs the updated data container or a flow container with the data and the detection results.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.container.DataContainer<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.container.DataContainer<br>
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
 * &nbsp;&nbsp;&nbsp;default: OutlierDetector
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
 * <pre>-detector &lt;adams.data.outlier.AbstractOutlierDetector&gt; (property: detector)
 * &nbsp;&nbsp;&nbsp;The outlier detector to use for analyzing the data.
 * &nbsp;&nbsp;&nbsp;default: adams.data.outlier.PassThrough
 * </pre>
 *
 * <pre>-only-warning &lt;boolean&gt; (property: onlyWarning)
 * &nbsp;&nbsp;&nbsp;If enabled, the detections get added merely as warnings instead of as errors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-output-container &lt;boolean&gt; (property: outputContainer)
 * &nbsp;&nbsp;&nbsp;If enabled, a flow container is output instead of just adding the detections
 * &nbsp;&nbsp;&nbsp;to the data container.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class OutlierDetector
  extends AbstractTransformer {

  private static final long serialVersionUID = 6697547899481901585L;

  /** the outlier detector to use. */
  protected AbstractOutlierDetector m_Detector;

  /** whether the detection is only added as warning instead of error. */
  protected boolean m_OnlyWarning;

  /** whether to output a container. */
  protected boolean m_OutputContainer;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Applies the outlier detector and either outputs the updated data "
	+ "container or a flow container with the data and the detection results.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "detector", "detector",
      new PassThrough());

    m_OptionManager.add(
      "only-warning", "onlyWarning",
      false);

    m_OptionManager.add(
      "output-container", "outputContainer",
      false);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "detector", m_Detector);
    result += QuickInfoHelper.toString(this, "onlyWarning", m_OnlyWarning, "only warning", ", ");
    result += QuickInfoHelper.toString(this, "outputContainer", m_OutputContainer, "output container", ", ");

    return result;
  }

  /**
   * Sets the outlier detector to use.
   *
   * @param value	the outlier detector
   */
  public void setDetector(AbstractOutlierDetector value) {
    m_Detector = value;
    reset();
  }

  /**
   * Returns the outlier detector in use.
   *
   * @return 		the outlier detector
   */
  public AbstractOutlierDetector getDetector() {
    return m_Detector;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String detectorTipText() {
    return "The outlier detector to use for analyzing the data.";
  }

  /**
   * Sets whether the detections are added as error or warning.
   *
   * @param value	if true then the detections are added as warning
   * 			instead of as error
   */
  public void setOnlyWarning(boolean value) {
    m_OnlyWarning = value;
    reset();
  }

  /**
   * Returns whether the detections are added as error or warning.
   *
   * @return 		true if the detections get added as warning instead
   * 			of as error
   */
  public boolean getOnlyWarning() {
    return m_OnlyWarning;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for displaying in
   * 			the GUI or for listing the options.
   */
  public String onlyWarningTipText() {
    return "If enabled, the detections get added merely as warnings instead of as errors.";
  }

  /**
   * Sets whether a flow container is output instead of just adding the
   * detections to the data container.
   *
   * @param value	true if a container is output
   */
  public void setOutputContainer(boolean value) {
    m_OutputContainer = value;
    reset();
  }

  /**
   * Returns whether a flow container is output instead of just adding the
   * detections to the data container.
   *
   * @return 		true if a container is output
   */
  public boolean getOutputContainer() {
    return m_OutputContainer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String outputContainerTipText() {
    return "If enabled, a flow container is output instead of just adding the detections to the data container.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{DataContainer.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    if (m_OutputContainer)
      return new Class[]{OutlierDetectorContainer.class};
    else
      return new Class[]{DataContainer.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    DataContainer		input;
    List<String>		detections;
    OutlierDetectorContainer	cont;
    NotesHandler 		handler;
    int				i;

    result = null;

    input = null;
    if (m_InputToken.hasPayload(DataContainer.class))
      input = m_InputToken.getPayload(DataContainer.class);
    else
      result = m_InputToken.unhandledData();

    try {
      detections = m_Detector.detect(input);
      if (m_OutputContainer) {
	cont          = new OutlierDetectorContainer(m_Detector, detections.toArray(new String[detections.size()]), input);
	m_OutputToken = new Token(cont);
      }
      else {
	if (input instanceof NotesHandler) {
	  handler = (NotesHandler) input.getClone();
	  for (i = 0; i < detections.size(); i++) {
	    if (m_OnlyWarning)
	      handler.getNotes().addWarning(m_Detector.getClass(), detections.get(i));
	    else
	      handler.getNotes().addError(m_Detector.getClass(), detections.get(i));
	    getLogger().info((i+1) + ". " + detections.get(i));
	  }
	  m_OutputToken = new Token(handler);
	}
	else {
	  getLogger().warning("Failed to attach detections as notes, as not a " + Utils.classToString(NotesHandler.class) + ": " + Utils.classToString(input));
	  m_OutputToken = new Token(input);
	}
      }
    }
    catch (Exception e) {
      result = handleException("Failed to process data: " + input, e);
    }

    return result;
  }
}
