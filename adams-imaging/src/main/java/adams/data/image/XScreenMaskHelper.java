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
 * XScreenMaskHelper.java
 * Copyright (C) 2014-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.data.image;

import adams.core.logging.Logger;
import adams.data.statistics.StatUtils;

import java.awt.image.BufferedImage;

/**
 * Helper class for masking out colors (like green screening).
 *
 * @author lx51 (lx51 at students dot waikato dot ac dot nz)
 * @version $Revision$
 */
public class XScreenMaskHelper {

  /**
   * Colors that can be masked.
   * An additional switch case needs to be implemented in the generateMask() method.
   *
   * @author lx51 (lx51 at students dot waikato dot ac dot nz)
   * @version $Revision$
   * @see XScreenMaskHelper#generateMask(java.awt.image.BufferedImage, adams.data.image.XScreenMaskHelper.Color)
   */
  public enum Color {
    RED, GREEN, BLUE, YELLOW, LUMINANCE, NO_MASK
  }

  /**
   * Generate a color mask indicating how similar each pixel is to the selected color.
   * The mask is normalized to 0 and 255, with 0 being not similar and 255 being very similar.
   *
   * @param image image to be processed
   * @param color color to apply mask
   * @return a normalized mask between 0 and 255
   */
  public static int[][] generateMask(BufferedImage image, Color color) {
    float[][] mask = new float[image.getHeight()][image.getWidth()];
    float min = Float.POSITIVE_INFINITY;
    float max = Float.NEGATIVE_INFINITY;

    // Apply screening and generate histogram
    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        int rgb = image.getRGB(x, y);
        int r = rgb >> 16 & 0xff;
        int g = rgb >> 8 & 0xff;
        int b = rgb & 0xff;

        // Implemented algorithms should output smaller values as being dissimilar, and higher values being similar.
        int value;
        switch (color) {
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
          case LUMINANCE:
              value = (int) Math.round(0.2126 * r + 0.7152 * g + 0.0722 * b);
              break;
          case NO_MASK:
              value = rgb;
              break;
          default:
            throw new IllegalStateException("Color not implemented: " + color.name());
        }
        mask[y][x] = value;
        if (value > max) max = value;
        if (value < min) min = value;
      }
    }

    // Normalize map and calculate histogram
    int[][] normalized = new int[image.getHeight()][image.getWidth()];
    float ratio = 255 / (max - min);
    for (int y = 0; y < mask.length; y++) {
      for (int x = 0; x < mask[0].length; x++)
        normalized[y][x] = Math.round((mask[y][x] - min) * ratio);
    }

    return normalized;
  }

  /**
   * Given a previously generated mask, this uses the data to mask an image.
   *
   * @param image     image to mask
   * @param mask      generated mask
   * @param threshold threshold to binarize image, specify a negative value to automatically determine a threshold
   * @param down      if true, then pixels <= threshold are not masked and the others' alpha channel are set to 0 (made transparent)
   * @param log       logger to be used to log the automatically determined threshold, can be null
   * @return masked image, type ARGB
   */
  public static BufferedImage applyMask(BufferedImage image, int[][] mask, int threshold, boolean down, Logger log) {
    BufferedImage output = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

    if (image.getWidth() != mask[0].length || image.getHeight() != mask.length)
      throw new IllegalStateException("Image and mask dimensions mismatch!");

    // Auto-thresholding
    if (threshold < 0)
      threshold = determineThreshold(mask, log);

    // Apply mask
    for (int y = 0; y < mask.length; y++) {
      for (int x = 0; x < mask[0].length; x++) {
        int rgb = image.getRGB(x, y);
        if (down)
          output.setRGB(x, y, mask[y][x] <= threshold ? rgb : rgb & 0x00ffffff);
        else
          output.setRGB(x, y, mask[y][x] >= threshold ? rgb : rgb & 0x00ffffff);
      }
    }

    return output;
  }

  /**
   * Given a previously generated mask, this binarizes the mask given a threshold.
   *
   * @param mask      generated mask
   * @param threshold threshold to binarize image, specify a negative value to automatically determine a threshold
   * @param down      if true, then pixels <= threshold are not masked and the others' alpha channel are set to 0 (made transparent)
   * @param log       logger to be used to log the automatically determined threshold, can be null
   */
  public static void binarizeMask(int[][] mask, int threshold, boolean down, Logger log) {
    // Auto-thresholding
    if (threshold < 0)
      threshold = determineThreshold(mask, log);

    // Apply mask
    for (int y = 0; y < mask.length; y++) {
      for (int x = 0; x < mask[0].length; x++) {
        if (down)
          mask[y][x] = mask[y][x] <= threshold ? 1 : 0;
        else
          mask[y][x] = mask[y][x] >= threshold ? 1 : 0;
      }
    }
  }

  /**
   * Finds the threshold in the normalized histogram.
   *
   * @param histogram	the histogram to process
   * @param limit	the limit to use
   * @return		the index in the histogram
   */
  protected static int findThreshold(int[] histogram, int limit) {
    int threshold = histogram.length / 2;
    if (histogram[threshold] <= limit)
      for (; threshold < 256 && histogram[threshold] <= limit; threshold++) ;
    else
      for (; threshold > 0 && histogram[threshold] > limit; threshold--) ;
    return threshold;
  }

  /**
   * Determine an optimal threshold value.
   * A normalized histogram is calculated, assuming two peaks, the threshold begins in the middle, walking right until
   * it hits the right peak, or left until it is walks off the peak.
   *
   * @param mask generated mask
   * @param log  logger to be used to log the automatically determined threshold, can be null
   * @return threshold value
   */
  public static int determineThreshold(int[][] mask, Logger log) {
    int min = Integer.MAX_VALUE;
    int max = Integer.MIN_VALUE;
    int[] histogram = new int[256];

    // Calculate histogram
    for (int y = 0; y < mask.length; y++) {
      for (int x = 0; x < mask[0].length; x++) {
        int value = histogram[mask[y][x]]++;
        if (value > max) max = value;
        if (value < min) min = value;
      }
    }

    // Normalize histogram
    float ratio = 255f / (max - min);
    for (int i = 0; i < histogram.length; i++)
      histogram[i] = Math.round((histogram[i] - min) * ratio);

    // Determine threshold
    int threshold = findThreshold(histogram, 1);

    // skewed threshold?
    if ((threshold < 10) || (threshold > 245)) {
      int limit = (int) (StatUtils.median(histogram) / 4);
      if (log != null)
	log.warning("Skewed threshold index (" + threshold + ") found, retrying with new limit: " + limit);
      threshold = findThreshold(histogram, limit);
    }

    // Log threshold
    if (log != null)
      log.info("Threshold index: " + threshold);

    return threshold;
  }
}
