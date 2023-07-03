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
 * SimpleLogging.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.core.QuickInfoHelper;
import adams.data.textrenderer.AbstractTextRenderer;
import adams.data.textrenderer.DefaultTextRenderer;
import adams.data.textrenderer.TextRenderer;
import adams.flow.core.Unknown;
import adams.flow.sink.simplelogging.format.NoFormat;
import adams.flow.sink.simplelogging.format.SimpleFormat;
import adams.flow.sink.simplelogging.output.ConsoleOutput;
import adams.flow.sink.simplelogging.output.SimpleOutput;

/**
 <!-- globalinfo-start -->
 * Turns the incoming objects into strings, formats them with the selected formatter and then outputs them with the specified output(s).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: SimpleLogging
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
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-use-custom-renderer &lt;boolean&gt; (property: useCustomRenderer)
 * &nbsp;&nbsp;&nbsp;If enabled, the specified custom renderer is used for turning the object
 * &nbsp;&nbsp;&nbsp;into a string rather than automatically determining a renderer.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-custom-renderer &lt;adams.data.textrenderer.TextRenderer&gt; (property: customRenderer)
 * &nbsp;&nbsp;&nbsp;The custom renderer to use.
 * &nbsp;&nbsp;&nbsp;default: adams.data.textrenderer.DefaultTextRenderer
 * </pre>
 *
 * <pre>-format &lt;adams.flow.sink.simplelogging.format.SimpleFormat&gt; (property: format)
 * &nbsp;&nbsp;&nbsp;The formatting scheme to use.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.sink.simplelogging.format.NoFormat
 * </pre>
 *
 * <pre>-output &lt;adams.flow.sink.simplelogging.output.SimpleOutput&gt; (property: output)
 * &nbsp;&nbsp;&nbsp;The type of output to use for the formatted message.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.sink.simplelogging.output.ConsoleOutput
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class SimpleLogging
  extends AbstractSink {

  private static final long serialVersionUID = -3070359929264727637L;

  /** whether to use a custom text renderer. */
  protected boolean m_UseCustomRenderer;

  /** the custom renderer to use. */
  protected TextRenderer m_CustomRenderer;

  /** the formatter to use. */
  protected SimpleFormat m_Format;

  /** the output to use. */
  protected SimpleOutput m_Output;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns the incoming objects into strings, formats them with the "
      + "selected formatter and then outputs them with the specified output(s).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "use-custom-renderer", "useCustomRenderer",
      false);

    m_OptionManager.add(
      "custom-renderer", "customRenderer",
      new DefaultTextRenderer());

    m_OptionManager.add(
      "format", "format",
      new NoFormat());

    m_OptionManager.add(
      "output", "output",
      new ConsoleOutput());
  }

  /**
   * Sets whether to use a custom renderer instead of automatically determining it.
   *
   * @param value 	true if to use custom renderer
   */
  public void setUseCustomRenderer(boolean value) {
    m_UseCustomRenderer = value;
    reset();
  }

  /**
   * Returns whether to use a custom renderer instead of automatically determining it.
   *
   * @return 		true if to use custom renderer
   */
  public boolean getUseCustomRenderer() {
    return m_UseCustomRenderer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useCustomRendererTipText() {
    return "If enabled, the specified custom renderer is used for turning the "
      + "object into a string rather than automatically determining a renderer.";
  }

  /**
   * Sets the custom renderer to use.
   *
   * @param value 	the renderer
   */
  public void setCustomRenderer(TextRenderer value) {
    m_CustomRenderer = value;
    reset();
  }

  /**
   * Returns the custom renderer to use.
   *
   * @return 		the renderer
   */
  public TextRenderer getCustomRenderer() {
    return m_CustomRenderer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String customRendererTipText() {
    return "The custom renderer to use.";
  }

  /**
   * Sets the formatting scheme to use.
   *
   * @param value 	the format
   */
  public void setFormat(SimpleFormat value) {
    m_Format = value;
    reset();
  }

  /**
   * Returns the formatting scheme to use.
   *
   * @return 		the format
   */
  public SimpleFormat getFormat() {
    return m_Format;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String formatTipText() {
    return "The formatting scheme to use.";
  }

  /**
   * Sets the type of output to use.
   *
   * @param value 	the output
   */
  public void setOutput(SimpleOutput value) {
    m_Output = value;
    reset();
  }

  /**
   * Returns the type of output to use.
   *
   * @return 		the output
   */
  public SimpleOutput getOutput() {
    return m_Output;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputTipText() {
    return "The type of output to use for the formatted message.";
  }

  @Override
  public String getQuickInfo() {
    String	result;

    if (m_UseCustomRenderer)
      result = QuickInfoHelper.toString(this, "customRenderer", m_CustomRenderer, "renderer: ") + ", ";
    else
      result = "";

    result += QuickInfoHelper.toString(this, "format", m_Format, "format: ");
    result += QuickInfoHelper.toString(this, "output", m_Output, ", output: ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Unknown.class};
  }

  /**
   * Executes the flow item.
   *
   * @return null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    Object		input;
    TextRenderer 	renderer;
    String		msg;

    input = m_InputToken.getPayload();
    if (m_UseCustomRenderer)
      renderer = m_CustomRenderer;
    else
      renderer = AbstractTextRenderer.getRenderer(input);
    m_Format.setFlowContext(this);
    m_Output.setFlowContext(this);
    try {
      msg = renderer.render(input);
      msg = m_Format.formatMessage(msg);
      result = m_Output.logMessage(msg);
    }
    catch (Exception e) {
      result = handleException("Failed to render/format/output incoming object!", e);
    }

    return result;
  }
}
