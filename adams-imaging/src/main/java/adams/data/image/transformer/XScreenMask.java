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
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.data.image.transformer;

import adams.data.image.BufferedImageContainer;

import java.awt.image.BufferedImage;

/**
 * <!-- globalinfo-start -->
 * * Masks out a color by making it transparent.
 * * <p/>
 * <!-- globalinfo-end -->
 * <p/>
 * <!-- options-start -->
 * * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * * &nbsp;&nbsp;&nbsp;default: WARNING
 * * </pre>
 * * 
 * * <pre>-color &lt;RED|GREEN|BLUE|YELLOW&gt; (property: color)
 * * &nbsp;&nbsp;&nbsp;Color to be masked.
 * * &nbsp;&nbsp;&nbsp;default: RED
 * * </pre>
 * * 
 * * <pre>-bias &lt;int&gt; (property: bias)
 * * &nbsp;&nbsp;&nbsp;Bias to be applied after finding the histogram peak.
 * * &nbsp;&nbsp;&nbsp;default: 0
 * * &nbsp;&nbsp;&nbsp;minimum: -255
 * * &nbsp;&nbsp;&nbsp;maximum: 255
 * * </pre>
 * * 
 * * <pre>-down &lt;boolean&gt; (property: down)
 * * &nbsp;&nbsp;&nbsp;If true, then pixels &lt;= threshold are not masked and the others' alpha channel 
 * * &nbsp;&nbsp;&nbsp;are set to 0 (made transparent).
 * * &nbsp;&nbsp;&nbsp;default: true
 * * </pre>
 * * 
 * * <pre>-auto-threshold &lt;boolean&gt; (property: autoThreshold)
 * * &nbsp;&nbsp;&nbsp;If true, it will automatically select the threshold value.
 * * &nbsp;&nbsp;&nbsp;default: true
 * * </pre>
 * * 
 * * <pre>-threshold &lt;int&gt; (property: threshold)
 * * &nbsp;&nbsp;&nbsp;If auto-threshold is disabled, this will be used as the threshold value.
 * * &nbsp;&nbsp;&nbsp;default: 127
 * * &nbsp;&nbsp;&nbsp;minimum: 0
 * * &nbsp;&nbsp;&nbsp;maximum: 255
 * * </pre>
 * * 
 * <!-- options-end -->
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
   * Colors that can be masked.
   * An additional switch case needs to be implemented in the doTransform() method.
   *
   * @author lx51 (lx51 at students dot waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum Color {
    RED, GREEN, BLUE, YELLOW
  }

  /**
   * Color to be masked out.
   */
  protected Color m_Color;

  /**
   * Bias to be applied to the auto-threshold value.
   */
  protected int m_Bias;

  /**
   * If true, then pixels <= threshold are not masked and the others' alpha channel are set to 0 (made transparent).
   */
  protected boolean m_Down;

  /**
   * If true, it will automatically select the threshold value.
   */
  protected boolean m_AutoThreshold;

  /**
   * If auto-threshold is disabled, this will be used as the threshold value..
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
    m_OptionManager.add("bias", "bias", 0, -255, 255);
    m_OptionManager.add("down", "down", true);
    m_OptionManager.add("auto-threshold", "autoThreshold", true);
    m_OptionManager.add("threshold", "threshold", 127, 0, 255);
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
   * Gets bias to be applied to auto-threshold value.
   *
   * @return bias
   */
  public int getBias() {
    return m_Bias;
  }

  /**
   * Sets bias to be applied to auto-threshold value.
   *
   * @param value bias
   */
  public void setBias(int value) {
    if (value >= -255 && value <= 255) {
      m_Bias = value;
      reset();
    } else
      getLogger().severe("Bias must be -255 >= value <= 255, provided: " + value);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or for listing the options.
   */
  public String biasTipText() {
    return "Bias to be applied after finding the histogram peak.";
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
   * Get whether it shall automatically select the threshold value.
   *
   * @return automatically detect threshold?
   */
  public boolean getAutoThreshold() {
    return m_AutoThreshold;
  }

  /**
   * Enable or disable automatically selecting the threshold value.
   *
   * @param value automatically detect threshold?
   */
  public void setAutoThreshold(boolean value) {
    m_AutoThreshold = value;
    reset();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or for listing the options.
   */
  public String autoThresholdTipText() {
    return "If true, it will automatically select the threshold value.";
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
    if (value >= 0 && value <= 255) {
      m_Threshold = value;
      reset();
    } else
      getLogger().severe("Threshold must be 0 >= value <= 255, provided: " + value);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or for listing the options.
   */
  public String thresholdTipText() {
    return "If auto-threshold is disabled, this will be used as the threshold value.";
  }

  /**
   * Normalize a histogram to 0-255.
   *
   * @param histogram histogram as integer array
   */
  protected void normalizeHistogram(int[] histogram) {
    int min = Integer.MAX_VALUE;
    int max = Integer.MIN_VALUE;
    for (int c : histogram) {
      if (c > max) max = c;
      if (c < min) min = c;
    }
    int range = max - min;
    for (int i = 0; i < histogram.length; i++)
      histogram[i] = (int) ((histogram[i] - min) / (float) range * 255);
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
    BufferedImage output = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
    float[][] map = new float[image.getHeight()][image.getWidth()];
    float min = Float.POSITIVE_INFINITY;
    float max = Float.NEGATIVE_INFINITY;
    float range;

    // Apply screening and generate histogram
    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        int color = image.getRGB(x, y);
        int r = color >> 16 & 0xff;
        int g = color >> 8 & 0xff;
        int b = color & 0xff;

        int value;
        switch (m_Color) {
          case RED:
            value = r * (r - b) * (r - g);
            break;
          case GREEN:
            value = g * (g - r) * (g - b);
            break;
          case BLUE:
            value = b * (b - r) * (b - g);
            break;
          case YELLOW:
            value = (r - b) * (g - b);
            break;
          default:
            throw new IllegalStateException("Color not implemented: " + m_Color);
        }
        map[y][x] = value;
        if (value > max) max = value;
        if (value < min) min = value;
      }
    }
    range = max - min;

    // Normalize map and calculate histogram
    int[] histogram = new int[256];
    BufferedImage tmp = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
    for (int y = 0; y < map.length; y++) {
      for (int x = 0; x < map[0].length; x++) {
        map[y][x] = (map[y][x] - min) / range * 255;
        histogram[(int) map[y][x]]++;
        int c = (int) map[y][x];
        tmp.setRGB(x, y, (0xff << 24) + (c << 16) + (c << 8) + c);
      }
    }

    // Normalize histogram
    normalizeHistogram(histogram);

    // Auto-threshold
    int threshold = m_Threshold;
    if (m_AutoThreshold) {
      threshold = histogram.length / 2;
      if (histogram[threshold] <= 1)
        for (; threshold < 256 && histogram[threshold] <= 1; threshold++) ;
      else
        for (; threshold > 0 && histogram[threshold] > 1; threshold--) ;
      if (isLoggingEnabled())
	getLogger().info("Threshold: " + threshold);
    }

    // Create masked image
    for (int y = 0; y < map.length; y++) {
      for (int x = 0; x < map[0].length; x++) {
        int color = image.getRGB(x, y);
        if (m_Down)
          output.setRGB(x, y, map[y][x] <= threshold ? color : color & 0x00ffffff);
        else
          output.setRGB(x, y, map[y][x] >= threshold ? color : color & 0x00ffffff);
      }
    }

    BufferedImageContainer outputContainer = (BufferedImageContainer) container.getHeader();
    outputContainer.setImage(output);
    return new BufferedImageContainer[]{outputContainer};
  }
}
