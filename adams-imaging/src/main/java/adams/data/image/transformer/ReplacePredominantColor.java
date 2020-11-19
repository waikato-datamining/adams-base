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
 * ReplacePredominantColor.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.data.image.transformer;

import adams.core.base.BaseColor;
import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;
import adams.data.statistics.StatUtils;
import gnu.trove.map.TIntIntMap;

/**
 <!-- globalinfo-start -->
 * Allows replacing the predominant color with another.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-new-color &lt;adams.core.base.BaseColor&gt; (property: newColor)
 * &nbsp;&nbsp;&nbsp;The replacement color.
 * &nbsp;&nbsp;&nbsp;default: #000000
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ReplacePredominantColor
  extends AbstractBufferedImageTransformer {

  private static final long serialVersionUID = -7828174332731436229L;

  /** the replacement color. */
  protected BaseColor m_NewColor;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows replacing the predominant color with another.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "new-color", "newColor",
      new BaseColor());
  }

  /**
   * Sets the new color.
   *
   * @param value	the color
   */
  public void setNewColor(BaseColor value) {
    m_NewColor = value;
    reset();
  }

  /**
   * Returns the new color.
   *
   * @return		the color
   */
  public BaseColor getNewColor() {
    return m_NewColor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String newColorTipText() {
    return "The replacement color.";
  }

  /**
   * Performs the actual transforming of the image.
   *
   * @param img		the image to transform (can be modified, since it is a copy)
   * @return		the generated image(s)
   */
  @Override
  protected BufferedImageContainer[] doTransform(BufferedImageContainer img) {
    BufferedImageContainer[]	result;
    int				i;
    int[]			pixels;
    TIntIntMap			counts;
    int				dominantColor;
    int				dominantCount;
    int				count;
    int				newColor;

    counts = StatUtils.uniqueCounts(BufferedImageHelper.getPixels(img.toBufferedImage()));
    if (counts.size() == 0) {
      getLogger().warning("Failed to determine color counts!");
      return new BufferedImageContainer[]{img};
    }

    newColor      = m_NewColor.toColorValue().getRGB();
    dominantColor = newColor;
    dominantCount = 0;
    for (int color: counts.keys()) {
      count = counts.get(color);
      if (count > dominantCount) {
        dominantColor = color;
        dominantCount = count;
      }
    }
    if (dominantCount == 0) {
      getLogger().warning("Failed to determine color counts!");
      return new BufferedImageContainer[]{img};
    }

    result    = new BufferedImageContainer[1];
    result[0] = (BufferedImageContainer) img.getClone();
    pixels    = result[0].getImage().getRGB(0, 0, result[0].getImage().getWidth(), result[0].getImage().getHeight(), null, 0, result[0].getImage().getWidth());
    for (i = 0; i < pixels.length; i++) {
      if (pixels[i] == dominantColor)
        pixels[i] = newColor;
    }
    result[0].getImage().setRGB(0, 0, result[0].getImage().getWidth(), result[0].getImage().getHeight(), pixels, 0, result[0].getImage().getWidth());

    return result;
  }
}
