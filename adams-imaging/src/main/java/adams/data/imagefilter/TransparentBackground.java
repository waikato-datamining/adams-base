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
 * TransparentBackground.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.imagefilter;

import java.awt.image.BufferedImage;
import java.awt.image.ImageFilter;
import java.awt.image.RGBImageFilter;

import adams.core.Index;

/**
 <!-- globalinfo-start -->
 * Uses the color at the specified location as background and turns it transparent.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-x &lt;adams.core.Index&gt; (property: X)
 * &nbsp;&nbsp;&nbsp;The X position of the pixel to get the color of the background from.
 * &nbsp;&nbsp;&nbsp;default: 
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-y &lt;adams.core.Index&gt; (property: Y)
 * &nbsp;&nbsp;&nbsp;The Y position of the pixel to get the color of the background from.
 * &nbsp;&nbsp;&nbsp;default: 
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TransparentBackground
  extends AbstractImageFilterProvider {

  /** for serialization. */
  private static final long serialVersionUID = -151175675073048859L;

  /** the X position of the background pixel. */
  protected Index m_X;

  /** the Y position of the background pixel. */
  protected Index m_Y;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Uses the color at the specified location as background and turns "
	+ "it transparent.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "x", "X",
	    new Index("1"));

    m_OptionManager.add(
	    "y", "Y",
	    new Index("1"));
  }

  /**
   * Sets the X position of the background pixel.
   *
   * @param value	the position
   */
  public void setX(Index value) {
    m_X = value;
    reset();
  }

  /**
   * Returns the Y position of the background pixel.
   *
   * @return		the position
   */
  public Index getX() {
    return m_X;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String XTipText() {
    return "The X position of the pixel to get the color of the background from.";
  }

  /**
   * Sets the Y position of the background pixel.
   *
   * @param value	the position
   */
  public void setY(Index value) {
    m_Y = value;
    reset();
  }

  /**
   * Returns the Y position of the background pixel.
   *
   * @return		the position
   */
  public Index getY() {
    return m_Y;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String YTipText() {
    return "The Y position of the pixel to get the color of the background from.";
  }

  /**
   * Generates the actor {@link ImageFilter} instance.
   * 
   * @param img		the buffered image to filter
   * @return		the image filter instance
   */
  @Override
  protected ImageFilter doGenerate(BufferedImage img) {
    final int 	marker;

    m_X.setMax(img.getWidth());
    m_Y.setMax(img.getHeight());
    
    marker = img.getRGB(m_X.getIntIndex(), m_Y.getIntIndex()) | 0xFFFFFFFF;

    return new RGBImageFilter() {
      @Override
      public final int filterRGB(int x, int y, int rgb) {
	if ((rgb | 0xFF000000) == marker)
	  return 0x00FFFFFF & rgb;
	else
	  return rgb;
      }
    };   
  }
}
