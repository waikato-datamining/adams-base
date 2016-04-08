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
 * XScreenMask.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.data.image.transformer;

import adams.data.image.BufferedImageContainer;
import adams.data.image.XScreenMaskHelper;
import adams.data.image.XScreenMaskHelper.Color;

import java.awt.image.BufferedImage;

/**
 <!-- globalinfo-start -->
 * Masks out a color by making it transparent.
 * <br><br>
 <!-- globalinfo-end -->
 <br><br>
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-color &lt;RED|GREEN|BLUE|YELLOW&gt; (property: color)
 * &nbsp;&nbsp;&nbsp;Color to be masked.
 * &nbsp;&nbsp;&nbsp;default: RED
 * </pre>
 *
 * <pre>-down &lt;boolean&gt; (property: down)
 * &nbsp;&nbsp;&nbsp;If true, then pixels &lt;= threshold are not masked and the others' alpha channel 
 * &nbsp;&nbsp;&nbsp;are set to 0 (made transparent).
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 * <pre>-threshold &lt;int&gt; (property: threshold)
 * &nbsp;&nbsp;&nbsp;Threshold value used for binarization, specify -1 to automatically determine 
 * &nbsp;&nbsp;&nbsp;a threshold.
 * &nbsp;&nbsp;&nbsp;default: 127
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * &nbsp;&nbsp;&nbsp;maximum: 255
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author lx51 (lx51 at students dot waikato dot ac dot nz)
 * @version $Revision$
 */
public class XScreenMask extends AbstractBufferedImageTransformer {

  /**
   * For serialization.
   */
  private static final long serialVersionUID = -922292531841315436L;

  /**
   * Color to be masked out.
   */
  protected Color m_Color;

  /**
   * If true, then pixels <= threshold are not masked and the others' alpha channel are set to 0 (made transparent).
   */
  protected boolean m_Down;

  /**
   * Threshold value used for binarization, specify -1 to automatically determine a threshold.
   */
  protected int m_Threshold;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Masks out a color by making it transparent.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add("color", "color", Color.RED);
    m_OptionManager.add("down", "down", true);
    m_OptionManager.add("threshold", "threshold", 127, -1, 255);
  }

  /**
   * Gets color to be masked out.
   *
   * @return color to be masked out
   */
  public Color getColor() {
    return m_Color;
  }

  /**
   * Sets color to be masked out.
   *
   * @param value bias
   */
  public void setColor(Color value) {
    if (value != null) {
      m_Color = value;
      reset();
    } else
      getLogger().severe("Color must not be null.");
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or for listing the options.
   */
  public String colorTipText() {
    return "Color to be masked.";
  }

  /**
   * Get whether mask is applied below or above the threshold.
   *
   * @return threshold direction
   */
  public boolean getDown() {
    return m_Down;
  }

  /**
   * Set whether mask is applied below or above the threshold.
   *
   * @param value threshold direction
   */
  public void setDown(boolean value) {
    m_Down = value;
    reset();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or for listing the options.
   */
  public String downTipText() {
    return "If true, then pixels <= threshold are not masked and the others' alpha channel are set to 0 (made transparent).";
  }

  /**
   * Get the manual threshold value.
   *
   * @return manual threshold value
   */
  public int getThreshold() {
    return m_Threshold;
  }

  /**
   * Set the manual threshold value.
   *
   * @param value manual threshold value
   */
  public void setThreshold(int value) {
    if (value >= -1 && value <= 255) {
      m_Threshold = value;
      reset();
    } else
      getLogger().severe("Threshold must be 0 >= value <= 255, or -1 for auto-thresholding, provided: " + value);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or for listing the options.
   */
  public String thresholdTipText() {
    return "Threshold value used for binarization, specify -1 to automatically determine a threshold.";
  }

  /**
   * Masks the image.
   *
   * @param container the image to be processed
   * @return the masked image
   */
  @Override
  protected BufferedImageContainer[] doTransform(BufferedImageContainer container) {
    BufferedImage image = container.getImage();

    int[][] mask = XScreenMaskHelper.generateMask(image, m_Color);
    image = XScreenMaskHelper.applyMask(image, mask, m_Threshold, m_Down, getLogger());

    container.setImage(image);
    return new BufferedImageContainer[]{container};
  }
}
