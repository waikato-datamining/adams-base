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
 * ColorProviderBasedColorGradientGenerator.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.core;

import java.awt.Color;

/**
 <!-- globalinfo-start -->
 * Uses the specified color provider to generate the colors.<br>
 * Mainly used for having control over colors when generating images.
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
 * &nbsp;&nbsp;&nbsp;The color provider to use.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.core.DefaultColorProvider
 * </pre>
 *
 * <pre>-num-colors &lt;int&gt; (property: numColors)
 * &nbsp;&nbsp;&nbsp;The number of colors in the gradient to use.
 * &nbsp;&nbsp;&nbsp;default: 255
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ColorProviderBasedColorGradientGenerator
  extends AbstractColorGradientGenerator
  implements ColorProviderHandler, ColorGradientGeneratorWithFixedNumberOfColors {

  private static final long serialVersionUID = 239638637700689112L;

  /** the color provider to use. */
  protected ColorProvider m_ColorProvider;

  /** the number of gradient colors. */
  protected int m_NumColors;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses the specified color provider to generate the colors.\n"
      + "Mainly used for having control over colors when generating images.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "color-provider", "colorProvider",
      new DefaultColorProvider());

    m_OptionManager.add(
      "num-colors", "numColors",
      getDefaultNumColors(), 1, null);
  }

  /**
   * Sets the color provider to use.
   *
   * @param value	the provider
   */
  public void setColorProvider(ColorProvider value) {
    m_ColorProvider = value;
    reset();
  }

  /**
   * Returns the color provider to use.
   *
   * @return		the provider
   */
  public ColorProvider getColorProvider() {
    return m_ColorProvider;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorProviderTipText() {
    return "The color provider to use.";
  }

  /**
   * Returns the default for the number of colors.
   *
   * @return		the default
   */
  protected int getDefaultNumColors() {
    return 255;
  }

  /**
   * Sets the number of gradient colors to use.
   *
   * @param value	the number of colors
   */
  @Override
  public void setNumColors(int value) {
    if (getOptionManager().isValid("numColors", value)) {
      m_NumColors = value;
      reset();
    }
  }

  /**
   * Returns the number of gradient colors to use.
   *
   * @return		the number of colors
   */
  @Override
  public int getNumColors() {
    return m_NumColors;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String numColorsTipText() {
    return "The number of colors in the gradient to use.";
  }

  /**
   * Performs the actual generation.
   *
   * @return the generated colors
   */
  @Override
  protected Color[] doGenerate() {
    Color[]	result;
    int		i;

    m_ColorProvider.resetColors();
    result    = new Color[m_NumColors];
    for (i = 0; i < m_NumColors; i++)
      result[i] = m_ColorProvider.next();

    return result;
  }
}
