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
 * ReplaceColors.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.data.image.transformer;

import adams.core.Utils;
import adams.core.base.BaseColor;
import adams.data.image.BufferedImageContainer;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

import java.awt.Color;

/**
 <!-- globalinfo-start -->
 * Allows replacing one color with another.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-old-color &lt;adams.core.base.BaseColor&gt; [-old-color ...] (property: oldColors)
 * &nbsp;&nbsp;&nbsp;The old colors to replace.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-new-color &lt;adams.core.base.BaseColor&gt; [-new-color ...] (property: newColors)
 * &nbsp;&nbsp;&nbsp;The replacement colors.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ReplaceColors
  extends AbstractBufferedImageTransformer {

  private static final long serialVersionUID = -7828174332731436229L;

  /** the old colors. */
  protected BaseColor[] m_OldColors;
  
  /** the new colors. */
  protected BaseColor[] m_NewColors;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows replacing one color with another.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "old-color", "oldColors",
      new BaseColor[0]);

    m_OptionManager.add(
      "new-color", "newColors",
      new BaseColor[0]);
  }

  /**
   * Sets the old colors to replace.
   *
   * @param value	the colors
   */
  public void setOldColors(BaseColor[] value) {
    m_OldColors = value;
    m_NewColors = (BaseColor[]) Utils.adjustArray(m_NewColors, m_OldColors.length, new BaseColor(Color.BLACK));
    reset();
  }

  /**
   * Returns the old colors to replace.
   *
   * @return		the colors
   */
  public BaseColor[] getOldColors() {
    return m_OldColors;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String oldColorsTipText() {
    return "The old colors to replace.";
  }

  /**
   * Sets the new colors.
   *
   * @param value	the colors
   */
  public void setNewColors(BaseColor[] value) {
    m_NewColors = value;
    m_OldColors = (BaseColor[]) Utils.adjustArray(m_OldColors, m_NewColors.length, new BaseColor(Color.BLACK));
    reset();
  }

  /**
   * Returns the new colors.
   *
   * @return		the colors
   */
  public BaseColor[] getNewColors() {
    return m_NewColors;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String newColorsTipText() {
    return "The replacement colors.";
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
    TIntIntMap			colors;
    int				i;
    int[]			pixels;

    colors = new TIntIntHashMap();
    for (i = 0; i < m_OldColors.length; i++)
      colors.put(m_OldColors[i].toColorValue().getRGB(), m_NewColors[i].toColorValue().getRGB());

    result    = new BufferedImageContainer[1];
    result[0] = (BufferedImageContainer) img.getClone();
    pixels    = result[0].getImage().getRGB(0, 0, result[0].getImage().getWidth(), result[0].getImage().getHeight(), null, 0, result[0].getImage().getWidth());
    for (i = 0; i < pixels.length; i++) {
      if (colors.containsKey(pixels[i]))
        pixels[i] = colors.get(pixels[i]);
    }
    result[0].getImage().setRGB(0, 0, result[0].getImage().getWidth(), result[0].getImage().getHeight(), pixels, 0, result[0].getImage().getWidth());

    return result;
  }
}
