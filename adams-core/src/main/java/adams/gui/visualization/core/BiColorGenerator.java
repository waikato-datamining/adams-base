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

/**
 * BiColorGenerator.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
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
 * Valid options are: <br><br>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
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
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BiColorGenerator
  extends AbstractColorGradientGenerator {

  /** for serialization. */
  private static final long serialVersionUID = 3344443413467944112L;

  /** the number of gradient colors. */
  protected int m_NumColors;

  /** the first color. */
  protected Color m_FirstColor;

  /** the second color. */
  protected Color m_SecondColor;

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
	    255, 1, null);

    m_OptionManager.add(
	    "first-color", "firstColor",
	    Color.BLACK);

    m_OptionManager.add(
	    "second-color", "secondColor",
	    Color.WHITE);
  }

  /**
   * Sets the number of gradient colors to use.
   *
   * @param value	the number of colors
   */
  public void setNumColors(int value) {
    m_NumColors = value;
    reset();
  }

  /**
   * Returns the number of gradient colors to use.
   *
   * @return		the number of colors
   */
  public int getNumColors() {
    return m_NumColors;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numColorsTipText() {
    return "The number of colors in the gradient to use.";
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

      result[i] = new Color(redNew, greenNew, blueNew);
    }

    return result;
  }
}
