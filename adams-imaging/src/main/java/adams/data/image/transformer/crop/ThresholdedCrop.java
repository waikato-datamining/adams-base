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
 * ThresholdedCrop.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.image.transformer.crop;

import java.awt.Point;
import java.awt.image.BufferedImage;

import adams.core.Utils;
import adams.data.image.BufferedImageHelper;
import adams.data.statistics.StatUtils;

/**
 <!-- globalinfo-start -->
 * Simple cropping algorithm that assumes a good contrast between background and foreground.<br>
 * Algorithm:<br>
 * - create histogram of grayscale image<br>
 * - remove counts from histogram that fall below noise-level<br>
 * - determine left-most (L) and right-most (R) non-zero count<br>
 * - divide region between L and R into two and determine highest peak in each (LP and RP)<br>
 * - 8-bit threshold is halfway between LP and RP<br>
 * - determine first pixel that is above threshold from top, bottom, left and right, which is used for the crop
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-noise-level &lt;double&gt; (property: noiseLevel)
 * &nbsp;&nbsp;&nbsp;The noise level in percent (0-1).
 * &nbsp;&nbsp;&nbsp;default: 0.05
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * &nbsp;&nbsp;&nbsp;maximum: 1.0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8487 $
 */
public class ThresholdedCrop
  extends AbstractCropAlgorithm {

  /** for serialization. */
  private static final long serialVersionUID = -696539737461589970L;

  /** the noise level in percent (0-1). */
  protected double m_NoiseLevel;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Simple cropping algorithm that assumes a good contrast between background and foreground.\n"
	+ "Algorithm:\n"
	+ "- create histogram of grayscale image\n"
	+ "- remove counts from histogram that fall below noise-level\n"
	+ "- determine left-most (L) and right-most (R) non-zero count\n"
	+ "- divide region between L and R into two and determine highest peak in each (LP and RP)\n"
	+ "- 8-bit threshold is halfway between LP and RP\n"
	+ "- determine first pixel that is above threshold from top, bottom, left and right, which is used for the crop";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "noise-level", "noiseLevel",
	    0.05, 0.0, 1.0);
  }

  /**
   * Sets the noise level.
   *
   * @param value 	the noise level (0-1)
   */
  public void setNoiseLevel(double value) {
    if ((value >= 0) && (value <= 1.0)) {
      m_NoiseLevel = value;
      reset();
    }
    else {
      getLogger().warning("Noise level must satisfy 0 <= x <= 1, provided: " + value);
    }
  }

  /**
   * Returns the noise level.
   *
   * @return 		the noise level (0-1)
   */
  public double getNoiseLevel() {
    return m_NoiseLevel;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String noiseLevelTipText() {
    return "The noise level in percent (0-1).";
  }

  /**
   * Calculates the threshold 8-bit value to distinguish between background 
   * and foreground.
   * 
   * @param img		the image to use
   * @return		the threshold byte
   */
  protected int findThreshold(BufferedImage img) {
    int			result;
    int[][]		histo;
    int			max;
    double		noise;
    int			i;
    int			left;
    int			right;
    int			leftPeak;
    int			rightPeak;
    int[]		subset;

    // generate histogram
    histo = BufferedImageHelper.histogram(img, true);
    if (isLoggingEnabled())
      getLogger().finer("Histogram: " + Utils.arrayToString(histo[0]));
    
    // remove counts below noise level
    max   = StatUtils.max(histo[0]);
    noise = max * m_NoiseLevel;
    if (isLoggingEnabled())
      getLogger().fine("Noise level at " + m_NoiseLevel + ": " + noise);
    for (i = 0; i < histo[0].length; i++) {
      if (histo[0][i] < noise)
	histo[0][i] = 0;
    }
    if (isLoggingEnabled())
      getLogger().finer("Denoised histogram: " + Utils.arrayToString(histo[0]));
    
    // find left-most non-zero point
    left = 0;
    for (i = 0; i < histo[0].length; i++) {
      if (histo[0][i] > 0) {
	left = i;
	break;
      }
    }
    if (isLoggingEnabled())
      getLogger().fine("left non-zero: " + left);
    
    // find right-most non-zero point
    right = 255;
    for (i = histo[0].length - 1; i >= 0; i--) {
      if (histo[0][i] > 0) {
	right = i;
	break;
      }
    }
    if (isLoggingEnabled())
      getLogger().fine("right non-zero: " + right);
    
    // find left peak
    subset = new int[(right - left + 1) / 2];
    System.arraycopy(histo[0], left, subset, 0, subset.length);
    if (isLoggingEnabled())
      getLogger().finer("left subset: " + Utils.arrayToString(subset));
    leftPeak = left + StatUtils.maxIndex(subset);
    if (isLoggingEnabled())
      getLogger().fine("left peak: " + leftPeak);
    
    // find right peak
    subset = new int[(right - left + 1) / 2];
    System.arraycopy(histo[0], right - subset.length + 1, subset, 0, subset.length);
    if (isLoggingEnabled())
      getLogger().finer("right subset: " + Utils.arrayToString(subset));
    rightPeak = right - subset.length + 1 + StatUtils.maxIndex(subset);
    if (isLoggingEnabled())
      getLogger().fine("right peak: " + rightPeak);
    
    // calculate threshold
    result = rightPeak - leftPeak + 1;
    if (isLoggingEnabled())
      getLogger().fine("threshold: " + result);
    
    return result;
  }
  
  /**
   * Performs the actual cropping.
   * 
   * @param img		the image to crop
   * @return		the (potentially) cropped image
   */
  @Override
  protected BufferedImage doCrop(BufferedImage img) {
    BufferedImage	image;
    BufferedImage	gray;
    int			threshold;
    int			width;
    int			height;
    int			i;
    int			xMiddle;
    int			yMiddle;
    int			top;
    int			bottom;
    int			left;
    int			right;

    gray      = BufferedImageHelper.convert(img, BufferedImage.TYPE_BYTE_GRAY);
    threshold = findThreshold(gray);
    width     = img.getWidth();
    height    = img.getHeight();
    xMiddle   = width / 2;
    yMiddle   = height / 2;
    
    // from top
    top = 0;
    for (i = 0; i < yMiddle; i++) {
      if (((gray.getRGB(xMiddle, i) >> 0) & 0xFF) > threshold) {
	top = i;
	break;
      }
    }
    if (isLoggingEnabled())
      getLogger().fine("top: " + top);
    
    // from bottom
    bottom = height - 1;
    for (i = height - 1; i >= yMiddle; i--) {
      if (((gray.getRGB(xMiddle, i) >> 0) & 0xFF) > threshold) {
	bottom = i;
	break;
      }
    }
    if (isLoggingEnabled())
      getLogger().fine("bottom: " + bottom);
    
    // from left
    left = 0;
    for (i = 0; i < xMiddle; i++) {
      if (((gray.getRGB(i, yMiddle) >> 0) & 0xFF) > threshold) {
	left = i;
	break;
      }
    }
    if (isLoggingEnabled())
      getLogger().fine("left: " + left);
    
    // from right
    right = width - 1;
    for (i = width - 1; i >= xMiddle; i--) {
      if (((gray.getRGB(i, yMiddle) >> 0) & 0xFF) > threshold) {
	right = i;
	break;
      }
    }
    if (isLoggingEnabled())
      getLogger().fine("right: " + right);

    m_TopLeft     = new Point(left, top);
    m_BottomRight = new Point(right, bottom);

    // crop original
    image = img.getSubimage(left, top, right - left + 1, bottom - top + 1);

    return image;
  }
}
