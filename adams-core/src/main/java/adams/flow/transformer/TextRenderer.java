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
 * TextRenderer.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.data.textrenderer.AbstractTextRenderer;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Turns incoming objects into their textual representation, either automatic detection based on their data type or by explicitly specifying a renderer.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Object<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
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
 * &nbsp;&nbsp;&nbsp;default: TextRenderer
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
 * <pre>-use-custom-renderer &lt;boolean&gt; (property: useCustomRenderer)
 * &nbsp;&nbsp;&nbsp;If enabled, uses the specified custom renderer.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-custom-renderer &lt;adams.data.textrenderer.TextRenderer&gt; (property: customRenderer)
 * &nbsp;&nbsp;&nbsp;The custom renderer to use (if enabled).
 * &nbsp;&nbsp;&nbsp;default: adams.data.textrenderer.DefaultTextRenderer
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class TextRenderer
  extends AbstractTransformer {

  private static final long serialVersionUID = 965553206055540848L;

  /** whether to use a custom renderer. */
  protected boolean m_UseCustomRenderer;

  /** the custom renderer to use. */
  protected adams.data.textrenderer.TextRenderer m_CustomRenderer;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns incoming objects into their textual representation, either "
      + "automatic detection based on their data type or by explicitly "
      + "specifying a renderer.";
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
      AbstractTextRenderer.getDefaultRenderer());
  }

  /**
   * Sets whether to use a custom renderer.
   *
   * @param value	true if custom
   */
  public void setUseCustomRenderer(boolean value) {
    m_UseCustomRenderer = value;
    reset();
  }

  /**
   * Returns whether to use a custom renderer.
   *
   * @return		true if custom
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
    return "If enabled, uses the specified custom renderer.";
  }

  /**
   * Sets the custom renderer to use.
   *
   * @param value	the renderer
   */
  public void setCustomRenderer(adams.data.textrenderer.TextRenderer value) {
    m_CustomRenderer = value;
    reset();
  }

  /**
   * Returns the custom renderer to use.
   *
   * @return		the renderer
   */
  public adams.data.textrenderer.TextRenderer getCustomRenderer() {
    return m_CustomRenderer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String customRendererTipText() {
    return "The custom renderer to use (if enabled).";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "useCustomRenderer", (m_UseCustomRenderer ? "custom" : "automatic"));
    if (m_UseCustomRenderer)
      result += QuickInfoHelper.toString(this, "customRenderer", m_CustomRenderer, ", renderer: ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Object.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{String.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Object	obj;
    String	text;

    result = null;
    obj    = m_InputToken.getPayload();
    text   = null;

    try {
      if (m_UseCustomRenderer) {
        if (m_CustomRenderer.handles(obj)) {
	  text = m_CustomRenderer.render(obj);
	  if (text == null)
	    result = "Renderer " + Utils.classToString(m_CustomRenderer) + " failed to render: " + Utils.classToString(obj);
	}
        else {
	  result = "Renderer " + Utils.classToString(m_CustomRenderer) + " does not handle: " + Utils.classToString(obj);
	}
      }
      else {
        text = AbstractTextRenderer.renderObject(obj);
	if (text == null)
	  result = "Failed to automatically render: " + Utils.classToString(obj);
      }

      if (text != null)
        m_OutputToken = new Token(text);
    }
    catch (Exception e) {
      result = handleException("Failed to render object!", e);
    }

    return result;
  }
}
