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
 * BiColorGenerator.java
 * Copyright (C) 2011-2018 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.core;

import java.awt.Color;

/**
 <!-- globalinfo-start -->
 * Generates gradient colors between two colors.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-num-colors &lt;int&gt; (property: numColors)
 * &nbsp;&nbsp;&nbsp;The number of colors in the gradient to use.
 * &nbsp;&nbsp;&nbsp;default: 255
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-first-color &lt;java.awt.Color&gt; (property: firstColor)
 * &nbsp;&nbsp;&nbsp;The first color of the gradient.
 * &nbsp;&nbsp;&nbsp;default: #000000
 * </pre>
 *
 * <pre>-second-color &lt;java.awt.Color&gt; (property: secondColor)
 * &nbsp;&nbsp;&nbsp;The second color of the gradient.
 * &nbsp;&nbsp;&nbsp;default: #ffffff
 * </pre>
 *
 * <pre>-alpha &lt;int&gt; (property: alpha)
 * &nbsp;&nbsp;&nbsp;The alpha value to use (0=transparent, 255=opaque); ignored if 255.
 * &nbsp;&nbsp;&nbsp;default: 255
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * &nbsp;&nbsp;&nbsp;maximum: 255
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class BiColorGenerator
  extends AbstractColorGradientGenerator
  implements ColorGradientGeneratorWithFixedNumberOfColors {

  /** for serialization. */
  private static final long serialVersionUID = 3344443413467944112L;

  /** the number of gradient colors. */
  protected int m_NumColors;

  /** the first color. */
  protected Color m_FirstColor;

  /** the second color. */
  protected Color m_SecondColor;

  /** the alpha value to use (ignored if 255). */
  protected int m_Alpha;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Generates gradient colors between two colors.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "num-colors", "numColors",
      getDefaultNumColors(), 1, null);

    m_OptionManager.add(
      "first-color", "firstColor",
      getDefaultFirstColor());

    m_OptionManager.add(
      "second-color", "secondColor",
      getDefaultSecondColor());

    m_OptionManager.add(
      "alpha", "alpha",
      getDefaultAlpha(), 0, 255);
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
   * Returns the default for the first color.
   *
   * @return		the default
   */
  protected Color getDefaultFirstColor() {
    return Color.BLACK;
  }

  /**
   * Sets the first color of the gradient.
   *
   * @param value	the first color
   */
  public void setFirstColor(Color value) {
    m_FirstColor = value;
    reset();
  }

  /**
   * Returns the first color of the gradient.
   *
   * @return		the first color
   */
  public Color getFirstColor() {
    return m_FirstColor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String firstColorTipText() {
    return "The first color of the gradient.";
  }

  /**
   * Returns the default for the second color.
   *
   * @return		the default
   */
  protected Color getDefaultSecondColor() {
    return Color.WHITE;
  }

  /**
   * Sets the second color of the gradient.
   *
   * @param value	the second color
   */
  public void setSecondColor(Color value) {
    m_SecondColor = value;
    reset();
  }

  /**
   * Returns the second color of the gradient.
   *
   * @return		the second color
   */
  public Color getSecondColor() {
    return m_SecondColor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String secondColorTipText() {
    return "The second color of the gradient.";
  }

  /**
   * Returns the default for the alpha value.
   *
   * @return		the default
   */
  protected int getDefaultAlpha() {
    return 255;
  }

  /**
   * Sets the alpha value to use (0=transparent, 255=opaque); ignored if 255.
   *
   * @param value	the alpha value
   */
  public void setAlpha(int value) {
    if (getOptionManager().isValid("alpha", value)) {
      m_Alpha = value;
      reset();
    }
  }

  /**
   * Returns the alpha value to use (0=transparent, 255=opaque); ignored if 255.
   *
   * @return		the alpha value
   */
  public int getAlpha() {
    return m_Alpha;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String alphaTipText() {
    return "The alpha value to use (0=transparent, 255=opaque); ignored if 255.";
  }

  /**
   * Hook method for performing checks on the setup.
   */
  protected void check() {
    super.check();

    if (m_FirstColor.equals(m_SecondColor))
      throw new IllegalStateException("The two colors must be different!");
  }

  /**
   * Performs the actual generation.
   *
   * @return		the generated colors
   */
  protected Color[] doGenerate() {
    Color[]	result;
    int		red1;
    int		red2;
    int		redNew;
    int		green1;
    int		green2;
    int		greenNew;
    int		blue1;
    int		blue2;
    int		blueNew;
    int		i;
    double	step;

    result = new Color[m_NumColors];
    red1   = m_FirstColor.getRed();
    green1 = m_FirstColor.getGreen();
    blue1  = m_FirstColor.getBlue();

    red2   = m_SecondColor.getRed();
    green2 = m_SecondColor.getGreen();
    blue2  = m_SecondColor.getBlue();

    step   = 1.0 / m_NumColors;

    for (i = 0; i < m_NumColors; i++) {
      redNew   = (int) (red1   + ((red2   < red1)   ? -i : i) * step * Math.abs(red2   - red1));
      greenNew = (int) (green1 + ((green2 < green1) ? -i : i) * step * Math.abs(green2 - green1));
      blueNew  = (int) (blue1  + ((blue2  < blue1)  ? -i : i) * step * Math.abs(blue2  - blue1));

      if (m_Alpha == 255)
	result[i] = new Color(redNew, greenNew, blueNew);
      else
        result[i] = new Color(redNew, greenNew, blueNew, m_Alpha);
    }

    return result;
  }
}
