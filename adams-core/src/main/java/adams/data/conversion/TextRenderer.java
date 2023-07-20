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
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import adams.core.ClassCrossReference;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.logging.LoggingHelper;
import adams.data.textrenderer.AbstractTextRenderer;
import adams.data.textrenderer.LimitedTextRenderer;

/**
 <!-- globalinfo-start -->
 * Turns incoming objects into their textual representation, either automatic detection based on their data type or by explicitly specifying a renderer.<br>
 * <br>
 * See also:<br>
 * adams.flow.transformer.TextRenderer
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
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
 * <pre>-unlimited-rendering &lt;boolean&gt; (property: unlimitedRendering)
 * &nbsp;&nbsp;&nbsp;If enabled (and the renderer implements adams.data.textrenderer.LimitedTextRenderer
 * &nbsp;&nbsp;&nbsp;), unlimited rendering is performed.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class TextRenderer
  extends AbstractConversionToString
  implements ClassCrossReference {

  private static final long serialVersionUID = 7863092682380867268L;

  /** whether to use a custom renderer. */
  protected boolean m_UseCustomRenderer;

  /** the custom renderer to use. */
  protected adams.data.textrenderer.TextRenderer m_CustomRenderer;

  /** whether to perform unlimited rendering. */
  protected boolean m_UnlimitedRendering;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns incoming objects into their textual representation, either "
      + "automatic detection based on their data type or by explicitly "
      + "specifying a renderer.";
  }

  /**
   * Returns the cross-referenced classes.
   *
   * @return		the classes
   */
  @Override
  public Class[] getClassCrossReferences() {
    return new Class[]{adams.flow.transformer.TextRenderer.class};
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

    m_OptionManager.add(
      "unlimited-rendering", "unlimitedRendering",
      false);
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
   * Sets whether to use unlimited rendering (if possible).
   *
   * @param value	true if unlimited
   */
  public void setUnlimitedRendering(boolean value) {
    m_UnlimitedRendering = value;
    reset();
  }

  /**
   * Returns whether to use unlimited rendering if possible.
   *
   * @return		true if unlimited
   */
  public boolean getUnlimitedRendering() {
    return m_UnlimitedRendering;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String unlimitedRenderingTipText() {
    return "If enabled (and the renderer implements " + Utils.classToString(LimitedTextRenderer.class) + "), unlimited rendering is performed.";
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
   * Returns the class that is accepted as input.
   *
   * @return the class
   */
  @Override
  public Class accepts() {
    return Object.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @throws Exception if something goes wrong with the conversion
   * @return the converted data
   */
  @Override
  protected Object doConvert() throws Exception {
    String 					result;
    String 					msg;
    Object					obj;
    adams.data.textrenderer.TextRenderer	renderer;

    result = null;
    msg    = null;
    obj    = m_Input;

    try {
      if (m_UseCustomRenderer) {
	if (m_CustomRenderer.handles(obj)) {
	  if (m_UnlimitedRendering && (m_CustomRenderer instanceof LimitedTextRenderer))
	    result = ((LimitedTextRenderer) m_CustomRenderer).renderUnlimited(obj);
	  else
	    result = m_CustomRenderer.render(obj);
	  if (result == null)
	    msg = "Renderer " + Utils.classToString(m_CustomRenderer) + " failed to render: " + Utils.classToString(obj);
	}
	else {
	  msg = "Renderer " + Utils.classToString(m_CustomRenderer) + " does not handle: " + Utils.classToString(obj);
	}
      }
      else {
	renderer = AbstractTextRenderer.getRenderer(obj);
	if (m_UnlimitedRendering && (renderer instanceof LimitedTextRenderer))
	  result = ((LimitedTextRenderer) renderer).renderUnlimited(obj);
	else
	  result = renderer.render(obj);
	if (result == null)
	  msg = "Failed to automatically render: " + Utils.classToString(obj);
      }
    }
    catch (Exception e) {
      msg = LoggingHelper.handleException(this, "Failed to render object!", e);
    }

    if (msg != null)
      throw new Exception(msg);

    return result;
  }
}
