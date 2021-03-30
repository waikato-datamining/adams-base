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
 * ImageSegmentationFileWriter.java
 * Copyright (C) 2020 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.core.QuickInfoHelper;
import adams.data.io.output.ImageSegmentationAnnotationWriter;
import adams.data.io.output.IndividualImageSegmentationLayerWriter;
import adams.flow.container.ImageSegmentationContainer;

/**
 <!-- globalinfo-start -->
 * Write image segmentation containers using the specified writer.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.ImageSegmentationContainer<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.ImageSegmentationContainer: name, base, layers
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
 * &nbsp;&nbsp;&nbsp;default: ImageSegmentationFileWriter
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
 * <pre>-output &lt;adams.core.io.PlaceholderFile&gt; (property: outputFile)
 * &nbsp;&nbsp;&nbsp;The file to write to (other files may get generated based on writer).
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 * <pre>-writer &lt;adams.data.io.output.AbstractImageSegmentationAnnotationWriter&gt; (property: writer)
 * &nbsp;&nbsp;&nbsp;The image segmentation writer to use.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.output.IndividualImageSegmentationLayerWriter
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ImageSegmentationFileWriter
  extends AbstractFileWriter {

  /** for serialization. */
  private static final long serialVersionUID = 7466006970025235243L;

  /** the image writer to use. */
  protected ImageSegmentationAnnotationWriter m_Writer;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Write image segmentation containers using the specified writer.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "writer", "writer",
      new IndividualImageSegmentationLayerWriter());
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputFileTipText() {
    return "The file to write to (other files may get generated based on writer).";
  }

  /**
   * Sets the writer to use.
   *
   * @param value 	the writer
   */
  public void setWriter(ImageSegmentationAnnotationWriter value) {
    m_Writer = value;
    reset();
  }

  /**
   * Returns the writer to use.
   *
   * @return 		the writer
   */
  public ImageSegmentationAnnotationWriter getWriter() {
    return m_Writer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String writerTipText() {
    return "The image segmentation writer to use.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  public Class[] accepts() {
    return new Class[]{ImageSegmentationContainer.class};
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "writer", m_Writer);
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    ImageSegmentationContainer	cont;

    cont = m_InputToken.getPayload(ImageSegmentationContainer.class);

    try {
      result = m_Writer.write(m_OutputFile, cont);
    }
    catch (Exception e) {
      result = handleException("Failed to write image segmentation data to file: " + m_OutputFile, e);
    }

    return result;
  }
}
