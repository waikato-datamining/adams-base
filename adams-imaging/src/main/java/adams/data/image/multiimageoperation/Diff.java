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
 * Diff.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.data.image.multiimageoperation;

import adams.core.io.PlaceholderFile;
import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;
import adams.data.io.output.AbstractImageWriter;
import adams.data.io.output.JAIImageWriter;
import adams.flow.sink.ImageWriter;


import java.awt.image.BufferedImage;


/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author sjb90
 * @version $Revision$
 */
public class Diff extends AbstractBufferedImageMultiImageOperation {

  /** the threshold at which two pixels are considered different */
  protected int m_Threshold;

  public int getThreshold() {
    return m_Threshold;
  }

  public void setThreshold(int m_Threshold) {
    this.m_Threshold = m_Threshold;
  }

  /**
   * Returns the minimum number of images that are required for the operation.
   *
   * @return the number of images that are required, <= 0 means no lower limit
   */

  @Override
  public int minNumImagesRequired() {
    return 2;
  }

  /**
   * Returns the maximum number of images that are required for the operation.
   *
   * @return the number of images that are required, <= 0 means no upper limit
   */
  @Override
  public int maxNumImagesRequired() {
    return 2;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();
    m_OptionManager.add("threshold", "threshold", 0);
  }

  /**
   * Checks the images.
   *
   * @param images	the images to check
   */
  @Override
  protected void check(BufferedImageContainer[] images) {
    String	msg;

    super.check(images);

    msg = checkSameDimensions(images);
    if (msg != null)
      throw new IllegalStateException(msg);
  }

  /**
   * Performs the actual processing of the images.
   *
   * @param images the images to process
   * @return the generated image(s)
   */
  @Override
  protected BufferedImageContainer[] doProcess(BufferedImageContainer[] images) {
    BufferedImageContainer[]	result;
    BufferedImage output;
    int				x;
    int				y;
    int				i;
    int[][] 			channels;
    int[]			split;
    result   = new BufferedImageContainer[1];
    output   = BufferedImageHelper.deepCopy(images[0].toBufferedImage());
    channels = new int[images.length][];
    // Walk through the pixels of the images
    for (y = 0; y < images[0].getHeight(); y++) {
      for (x = 0; x < images[0].getWidth(); x++) {
	// For each image split the pixel at the current X and Y and store the resulting
	// RGBA values in the channels array
	// this gives one RGBA array for each image, representing the current pixel
	for (i = 0; i < images.length; i++)
	  channels[i] = BufferedImageHelper.split(images[i].toBufferedImage().getRGB(x, y));
	// Step through each RGB value and compare to see if they are equal
	split = thresholdEquals(channels);
	output.setRGB(x, y, BufferedImageHelper.combine(split));
      }
    }
    result[0] = new BufferedImageContainer();
    result[0].setReport(images[0].getReport().getClone());
    result[0].getNotes().mergeWith(images[0].getNotes());
    result[0].setImage(output);
    JAIImageWriter writer = new JAIImageWriter();
    writer.write(new PlaceholderFile("/home/sjb90/Pictures/Binary test/diffout.png"), result[0]);
    return result;
  }

  private int[] thresholdEquals(int[][] channels) {
    int[] result = new int[4];
    result[3] = channels[0][3];
    for (int i = 0; i < 3; i++) {
      result[i] = (Math.abs(channels[0][i] - channels[1][i]) < m_Threshold) ? 0 : 255;
    }
    return result;
  }

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Compares two images and returns a third image with the differences between them";
  }
}
