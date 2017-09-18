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
 * FlowFileReader.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.data.io.input.DefaultFlowReader;
import adams.data.io.input.FlowReader;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import adams.flow.core.Token;

import java.io.File;

/**
 <!-- globalinfo-start -->
 * Reads the flow file and outputs the actor(s).<br>
 * A custom reader can be specified.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.io.File<br>
 * - generates:<br>
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
 * &nbsp;&nbsp;&nbsp;default: FlowFileReader
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
 * <pre>-use-custom-reader &lt;boolean&gt; (property: useCustomReader)
 * &nbsp;&nbsp;&nbsp;If enabled, the specified reader will be used instead of auto-detection.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-custom-reader &lt;adams.data.io.input.FlowReader&gt; (property: customReader)
 * &nbsp;&nbsp;&nbsp;The reader to use if a custom reader is to be used.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.input.DefaultFlowReader
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class FlowFileReader
  extends AbstractTransformer {

  private static final long serialVersionUID = -1258602500279600946L;

  /** whether to use a custom reader. */
  protected boolean m_UseCustomReader;

  /** the custom reader to use. */
  protected FlowReader m_CustomReader;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Reads the flow file and outputs the actor(s).\n"
	+ "A custom reader can be specified.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "use-custom-reader", "useCustomReader",
      false);

    m_OptionManager.add(
      "custom-reader", "customReader",
      new DefaultFlowReader());
  }

  /**
   * Sets whether to use a custom reader.
   *
   * @param value	true if to use a custom reader
   */
  public void setUseCustomReader(boolean value) {
    m_UseCustomReader = value;
    reset();
  }

  /**
   * Returns whether to use a custom reader.
   *
   * @return		true if to use a custom reader
   */
  public boolean getUseCustomReader() {
    return m_UseCustomReader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useCustomReaderTipText() {
    return "If enabled, the specified reader will be used instead of auto-detection.";
  }

  /**
   * Sets the custom reader to use.
   *
   * @param value	the reader
   */
  public void setCustomReader(FlowReader value) {
    m_CustomReader = value;
    reset();
  }

  /**
   * Returns the custom reader to use.
   *
   * @return		the reader
   */
  public FlowReader getCustomReader() {
    return m_CustomReader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String customReaderTipText() {
    return "The reader to use if a custom reader is to be used.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    if (m_UseCustomReader)
      return QuickInfoHelper.toString(this, "customReader", m_CustomReader, "custom reader: ");
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
    return new Class[]{String.class, File.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{Actor.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    PlaceholderFile	file;
    MessageCollection	errors;
    Actor		actor;

    result = null;

    file = null;
    if (m_InputToken.getPayload() instanceof String)
      file = new PlaceholderFile((String) m_InputToken.getPayload());
    else if (m_InputToken.getPayload() instanceof File)
      file = new PlaceholderFile((File) m_InputToken.getPayload());
    else
      result = "Unhandled input: " + Utils.classToString(m_InputToken.getPayload());

    actor = null;
    if (result == null) {
      errors = new MessageCollection();
      if (m_UseCustomReader) {
	actor = m_CustomReader.readActor(file);
	errors.addAll(m_CustomReader.getErrors());
      }
      else {
	actor = ActorUtils.read(file.getAbsolutePath(), errors);
      }
      if (!errors.isEmpty()) {
	result = "Failed to load actor from: " + file + "\n" + errors;
	actor  = null;
      }
    }

    if (actor != null)
      m_OutputToken = new Token(actor);

    return result;
  }
}
