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
 * GrayOrIndexedImageHandler.java
 * Copyright (C) 2020-2022 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.previewbrowser;

import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.data.image.BufferedImageContainer;
import adams.data.image.transformer.GrayOrIndexedColorizer;
import adams.data.io.input.ApacheCommonsImageReader;
import adams.gui.visualization.core.ColorProvider;
import adams.gui.visualization.core.CustomColorProvider;
import adams.gui.visualization.image.ImagePanel;

import java.awt.Color;
import java.io.File;

/**
 <!-- globalinfo-start -->
 * Changes the unique colors to the ones specified by the color provider for the following image types: tif,jpg,tiff,bmp,gif,png,wbmp,jpeg
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-color-provider &lt;adams.gui.visualization.core.ColorProvider&gt; (property: colorProvider)
 * &nbsp;&nbsp;&nbsp;The color provider to use for coloring in the grayscale&#47;indexed image.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.core.CustomColorProvider -color #ffff00 -color #0000ff -color #ff0000
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class GrayOrIndexedImageHandler
  extends AbstractContentHandler {

  /** for serialization. */
  private static final long serialVersionUID = -3962259305718630395L;

  /** the color provider for generating the colors. */
  protected ColorProvider m_ColorProvider;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Changes the unique colors to the ones specified by the color provider for the following image types: " + Utils.arrayToString(getExtensions());
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "color-provider", "colorProvider",
      getDefaultColorProvider());
  }

  /**
   * Returns the default color provider.
   *
   * @return		the default
   */
  protected ColorProvider getDefaultColorProvider() {
    CustomColorProvider result;
    result = new CustomColorProvider();
    result.setColors(new Color[]{Color.YELLOW, Color.BLUE, Color.RED});
    return result;
  }

  /**
   * Sets the color provider to use.
   *
   * @param value	the color provider
   */
  public void setColorProvider(ColorProvider value) {
    m_ColorProvider = value;
    reset();
  }

  /**
   * Returns the color provider to use.
   *
   * @return		the color provider
   */
  public ColorProvider getColorProvider() {
    return m_ColorProvider;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String colorProviderTipText() {
    return "The color provider to use for coloring in the grayscale/indexed image.";
  }

  /**
   * Returns the list of extensions (without dot) that this handler can
   * take care of.
   *
   * @return		the list of extensions (no dot)
   */
  @Override
  public String[] getExtensions() {
    return new ApacheCommonsImageReader().getFormatExtensions();
  }

  /**
   * Creates the actual view.
   *
   * @param file	the file to create the view for
   * @return		the view
   */
  @Override
  protected PreviewPanel createPreview(File file) {
    ImagePanel				panel;
    ApacheCommonsImageReader 		reader;
    BufferedImageContainer		cont;
    GrayOrIndexedColorizer		colorizer;

    reader = new ApacheCommonsImageReader();
    cont   = reader.read(new PlaceholderFile(file));
    if (cont == null)
      return new NoPreviewAvailablePanel();

    colorizer = new GrayOrIndexedColorizer();
    colorizer.setLoggingLevel(m_LoggingLevel);
    colorizer.setColorProvider(m_ColorProvider.shallowCopy());
    cont      = colorizer.transform(cont)[0];
    colorizer.cleanUp();

    panel = new ImagePanel();
    panel.getUndo().setEnabled(false);
    panel.setCurrentImage(cont);

    return new PreviewPanel(panel, panel.getPaintPanel());
  }
}
