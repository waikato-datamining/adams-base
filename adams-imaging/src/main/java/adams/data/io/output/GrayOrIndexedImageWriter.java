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
 * GrayOrIndexedImageWriter.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.data.io.output;

import adams.core.ClassCrossReference;
import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.data.image.BufferedImageContainer;
import adams.data.image.transformer.GrayOrIndexedColorizer;
import adams.data.io.input.AbstractImageReader;
import adams.gui.visualization.core.ColorProvider;
import adams.gui.visualization.core.CustomColorProvider;

import java.awt.Color;

/**
 <!-- globalinfo-start -->
 * Changes the unique colors to the ones specified by the color provider.<br>
 * Useful for generating human-viewable images from image segmentation annotations.<br>
 * Uses adams.data.io.output.ApacheCommonsImageWriter for performing the actual writing after the conversion.<br>
 * <br>
 * See also:<br>
 * adams.data.io.output.ApacheCommonsImageWriter
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
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class GrayOrIndexedImageWriter
  extends AbstractImageWriter<BufferedImageContainer>
  implements ClassCrossReference {

  private static final long serialVersionUID = 8155769915641682158L;

  /** the color provider for generating the colors. */
  protected ColorProvider m_ColorProvider;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Changes the unique colors to the ones specified by the color provider.\n"
      + "Useful for generating human-viewable images from image segmentation annotations.\n"
      + "Uses " + Utils.classToString(ApacheCommonsImageWriter.class) + " for performing the actual writing after the conversion.";
  }

  /**
   * Returns the cross-referenced classes.
   *
   * @return		the classes
   */
  public Class[] getClassCrossReferences() {
    return new Class[]{ApacheCommonsImageWriter.class};
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
    CustomColorProvider 	result;

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
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Gray/indexed image writer";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new ApacheCommonsImageWriter().getFormatExtensions();
  }

  /**
   * Returns, if available, the corresponding reader.
   *
   * @return		the reader, null if none available
   */
  @Override
  public AbstractImageReader getCorrespondingReader() {
    return null;
  }

  /**
   * Performs the actual writing of the image file.
   *
   * @param file	the file to write to
   * @param cont	the image container to write
   * @return		null if successfully written, otherwise error message
   */
  @Override
  protected String doWrite(PlaceholderFile file, BufferedImageContainer cont) {
    GrayOrIndexedColorizer 	colorizer;

    colorizer = new GrayOrIndexedColorizer();
    colorizer.setLoggingLevel(m_LoggingLevel);
    colorizer.setColorProvider(m_ColorProvider.shallowCopy());
    cont      = colorizer.transform(cont)[0];
    colorizer.cleanUp();

    return new ApacheCommonsImageWriter().write(file, cont);
  }
}
