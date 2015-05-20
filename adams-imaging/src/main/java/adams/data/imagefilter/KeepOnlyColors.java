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
 * KeepOnlyColors.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.imagefilter;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ImageFilter;
import java.awt.image.RGBImageFilter;

import adams.core.base.BaseColor;

/**
 <!-- globalinfo-start -->
 * Keeps only the specified colors and turns all other pixels to transparent.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-color &lt;adams.core.base.BaseColor&gt; [-color ...] (property: colors)
 * &nbsp;&nbsp;&nbsp;The colors to keep.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-replacement &lt;java.awt.Color&gt; (property: replacement)
 * &nbsp;&nbsp;&nbsp;The color to replace the unwanted colors with.
 * &nbsp;&nbsp;&nbsp;default: #ffffff
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class KeepOnlyColors
  extends AbstractImageFilterProvider {

  /** for serialization. */
  private static final long serialVersionUID = -151175675073048859L;

  /** the colors to keep. */
  protected BaseColor[] m_Colors;
  
  /** the replacement color. */
  protected Color m_Replacement;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Keeps only the specified colors and turns all other pixels to transparent.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "color", "colors",
	    new BaseColor[0]);

    m_OptionManager.add(
	    "replacement", "replacement",
	    Color.WHITE);
  }

  /**
   * Sets the colors to keep.
   *
   * @param value	the colors
   */
  public void setColors(BaseColor[] value) {
    m_Colors = value;
    reset();
  }

  /**
   * Returns the colors to keep.
   *
   * @return		the colors
   */
  public BaseColor[] getColors() {
    return m_Colors;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String colorsTipText() {
    return "The colors to keep.";
  }

  /**
   * Sets the color to replace the unwanted ones with.
   *
   * @param value	the replacement color
   */
  public void setReplacement(Color value) {
    m_Replacement = value;
    reset();
  }

  /**
   * Returns the color to replace the unwanted ones with.
   *
   * @return		the replacement color
   */
  public Color getReplacement() {
    return m_Replacement;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String replacementTipText() {
    return "The color to replace the unwanted colors with.";
  }

  /**
   * Hook method for performing checks.
   */
  @Override
  protected void check() {
    super.check();
    
    if ((m_Colors == null) || (m_Colors.length == 0))
      throw new IllegalStateException("No colors provided that should be kept!");
  }
  
  /**
   * Generates the actor {@link ImageFilter} instance.
   * 
   * @param img		the buffered image to filter
   * @return		the image filter instance
   */
  @Override
  protected ImageFilter doGenerate(BufferedImage img) {
    int			i;
    final int[] 	markers;
    final int		replacement;
    
    markers = new int[m_Colors.length];
    for (i = 0; i < m_Colors.length; i++)
      markers[i] = m_Colors[i].toColorValue().getRGB();
    replacement = m_Replacement.getRGB();

    return new RGBImageFilter() {
      @Override
      public final int filterRGB(int x, int y, int rgb) {
	for (int i = 0; i < markers.length; i++) {
	  if (rgb == markers[i])
	    return rgb;
	}
	return replacement;
      }
    };    
  }
}
