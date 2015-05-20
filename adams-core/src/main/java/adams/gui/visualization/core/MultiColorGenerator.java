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
 * MultiColorGenerator.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.core;

import java.awt.Color;

/**
 <!-- globalinfo-start -->
 * Generates gradient colors between multiple colors.
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
 * <pre>-color &lt;java.awt.Color&gt; [-color ...] (property: colors)
 * &nbsp;&nbsp;&nbsp;The colors to use for the gradient.
 * &nbsp;&nbsp;&nbsp;default: #0000b2, #00b200, #b20000, #ffff00
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MultiColorGenerator
  extends AbstractColorGradientGenerator {

  /** for serialization. */
  private static final long serialVersionUID = 3344443413467944112L;

  /** the number of gradient colors. */
  protected int m_NumColors;

  /** the colors to create the gradient with. */
  protected Color[] m_Colors;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Generates gradient colors between multiple colors.";
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
	    "color", "colors",
	    new Color[]{
		Color.BLUE.darker(),
		Color.GREEN.darker(),
		Color.RED.darker(),
		Color.YELLOW
	    });
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
   * Sets the colors of the gradient.
   *
   * @param value	the colors
   */
  public void setColors(Color[] value) {
    m_Colors = value;
    reset();
  }

  /**
   * Returns the colors of the gradient.
   *
   * @return		the colors
   */
  public Color[] getColors() {
    return m_Colors;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorsTipText() {
    return "The colors to use for the gradient.";
  }

  /**
   * Hook method for performing checks on the setup.
   */
  protected void check() {
    int		i;

    super.check();

    if (m_Colors.length < 2)
      throw new IllegalStateException(
	  "At least two colors must be defined!");

    for (i = 1; i < m_Colors.length; i++) {
      if (m_Colors[i-1].equals(m_Colors[i]))
	throw new IllegalStateException(
	    "The consecutive colors #" + (i) + " and " + (i+1) + " are the same!");
    }
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
    int		n;
    int		count;
    double	step;
    boolean	next;

    result = new Color[m_NumColors];
    step   = (double) (m_Colors.length - 1) / m_NumColors;
    count  = 0;

    for (i = 1; i < m_Colors.length; i++) {
      red1   = m_Colors[i-1].getRed();
      green1 = m_Colors[i-1].getGreen();
      blue1  = m_Colors[i-1].getBlue();

      red2   = m_Colors[i].getRed();
      green2 = m_Colors[i].getGreen();
      blue2  = m_Colors[i].getBlue();

      n = 0;
      do {
	redNew   = (int) (red1   + ((red2   < red1)   ? -n : n) * step * Math.abs(red2   - red1));
	greenNew = (int) (green1 + ((green2 < green1) ? -n : n) * step * Math.abs(green2 - green1));
	blueNew  = (int) (blue1  + ((blue2  < blue1)  ? -n : n) * step * Math.abs(blue2  - blue1));

	result[count] = new Color(redNew, greenNew, blueNew);

	next =    ((red1 < red2) && (redNew >= red2))
	       || ((red1 > red2) && (redNew <= red2))
	       || ((green1 < green2) && (greenNew >= green2))
	       || ((green1 > green2) && (greenNew <= green2))
	       || ((blue1 < blue2) && (blueNew >= blue2))
	       || ((blue1 > blue2) && (blueNew <= blue2));

	count++;
	n++;
      }
      while ((count < m_NumColors) && !next);
    }

    return result;
  }
}
