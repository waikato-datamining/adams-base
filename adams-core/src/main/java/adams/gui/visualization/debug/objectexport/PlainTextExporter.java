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
 * PlainTextExporter.java
 * Copyright (C) 2015-2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.debug.objectexport;

import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.data.textrenderer.AbstractTextRenderer;
import adams.data.textrenderer.LimitedTextRenderer;
import adams.data.textrenderer.TextRenderer;

import java.io.File;

/**
 * Uses text renderers (auto or custom) to turn the object into text.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PlainTextExporter
  extends AbstractObjectExporter {

  private static final long serialVersionUID = 4899389310274830738L;

  /** whether to use a custom renderer. */
  protected boolean m_UseCustomRenderer;

  /** the custom renderer to use. */
  protected adams.data.textrenderer.TextRenderer m_CustomRenderer;

  /** whether to perform unlimited rendering. */
  protected boolean m_UnlimitedRendering;

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  public String getFormatDescription() {
    return "Plain text (dump)";
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
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  public String[] getFormatExtensions() {
    return new String[]{"txt"};
  }

  /**
   * Checks whether the exporter can handle the specified class.
   *
   * @param cls		the class to check
   * @return		true if the exporter can handle this type of object
   */
  @Override
  public boolean handles(Class cls) {
    return true;
  }

  /**
   * Performs the actual export.
   *
   * @param obj		the object to export
   * @param file	the file to export to
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doExport(Object obj, File file) {
    String		result;
    String		text;
    TextRenderer 	renderer;

    result = null;
    text   = null;

    if (m_UseCustomRenderer) {
      if (m_CustomRenderer.handles(obj)) {
	if (m_UnlimitedRendering && (m_CustomRenderer instanceof LimitedTextRenderer))
	  text = ((LimitedTextRenderer) m_CustomRenderer).renderUnlimited(obj);
	else
	  text = m_CustomRenderer.render(obj);
	if (text == null)
	  result = "Renderer " + Utils.classToString(m_CustomRenderer) + " failed to render: " + Utils.classToString(obj);
      }
      else {
	result = "Renderer " + Utils.classToString(m_CustomRenderer) + " does not handle: " + Utils.classToString(obj);
      }
    }
    else {
      renderer = AbstractTextRenderer.getRenderer(obj);
      if (m_UnlimitedRendering && (renderer instanceof LimitedTextRenderer))
	text = ((LimitedTextRenderer) renderer).renderUnlimited(obj);
      else
	text = renderer.render(obj);
      if (text == null)
	result = "Failed to automatically render: " + Utils.classToString(obj);
    }

    if (text != null)
      result = FileUtils.writeToFileMsg(file.getAbsolutePath(), text, false, null);

    return result;
  }
}
