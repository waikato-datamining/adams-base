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
 * FlowFileWriter.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.data.io.output.DefaultFlowWriter;
import adams.data.io.output.FlowWriter;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;

/**
 <!-- globalinfo-start -->
 * Writes the incoming actor(s) to a file.<br>
 * A custom flow writer can be specified.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Actor<br>
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
 * &nbsp;&nbsp;&nbsp;default: FlowFileWriter
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
 * &nbsp;&nbsp;&nbsp;The file to write the actor to.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 * <pre>-use-custom-writer &lt;boolean&gt; (property: useCustomWriter)
 * &nbsp;&nbsp;&nbsp;If enabled, the specified writer will be used instead of auto-detection.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-custom-writer &lt;adams.data.io.output.FlowWriter&gt; (property: customWriter)
 * &nbsp;&nbsp;&nbsp;The writer to use if a custom writer is to be used.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.output.DefaultFlowWriter
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FlowFileWriter
  extends AbstractFileWriter {

  private static final long serialVersionUID = 2859778244702202261L;

  /** whether to use a custom writer. */
  protected boolean m_UseCustomWriter;

  /** the custom writer to use. */
  protected FlowWriter m_CustomWriter;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Writes the incoming actor(s) to a file.\n"
        + "A custom flow writer can be specified.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "use-custom-writer", "useCustomWriter",
      false);

    m_OptionManager.add(
      "custom-writer", "customWriter",
      new DefaultFlowWriter());
  }

  @Override
  public String outputFileTipText() {
    return "The file to write the actor to.";
  }

  /**
   * Sets whether to use a custom writer.
   *
   * @param value	true if to use a custom writer
   */
  public void setUseCustomWriter(boolean value) {
    m_UseCustomWriter = value;
    reset();
  }

  /**
   * Returns whether to use a custom writer.
   *
   * @return		true if to use a custom writer
   */
  public boolean getUseCustomWriter() {
    return m_UseCustomWriter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useCustomWriterTipText() {
    return "If enabled, the specified writer will be used instead of auto-detection.";
  }

  /**
   * Sets the custom writer to use.
   *
   * @param value	the writer
   */
  public void setCustomWriter(FlowWriter value) {
    m_CustomWriter = value;
    reset();
  }

  /**
   * Returns the custom writer to use.
   *
   * @return		the writer
   */
  public FlowWriter getCustomWriter() {
    return m_CustomWriter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String customWriterTipText() {
    return "The writer to use if a custom writer is to be used.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    if (m_UseCustomWriter)
      return QuickInfoHelper.toString(this, "customWriter", m_CustomWriter, "custom writer: ");
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
    return new Class[]{Actor.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Actor	actor;

    result = null;

    actor = null;
    if (m_InputToken.getPayload() instanceof Actor)
      actor = (Actor) m_InputToken.getPayload();
    else
      result = "Unhandled input: " + Utils.classToString(m_InputToken.getPayload());

    if (result == null) {
      if (m_UseCustomWriter) {
	if (!m_CustomWriter.write(actor, m_OutputFile))
	  result = "Failed to write actor to: " + m_OutputFile;
      }
      else {
	if (!ActorUtils.write(m_OutputFile.getAbsolutePath(), actor))
	  result = "Failed to write actor to: " + m_OutputFile;
      }
    }

    return result;
  }
}
