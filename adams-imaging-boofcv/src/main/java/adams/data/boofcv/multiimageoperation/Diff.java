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

package adams.data.boofcv.multiimageoperation;

import adams.data.boofcv.BoofCVHelper;
import adams.data.boofcv.BoofCVImageContainer;
import adams.data.boofcv.BoofCVImageType;
import boofcv.struct.image.ImageUInt8;

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
public class Diff extends AbstractBoofCVMultiImageOperation {

  /** the threshold at which two pixels are considered different */
  protected int m_Threshold;

  /**
   * Returns the threshold
   * @return
   */
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
   * Performs the actual processing of the images.
   *
   * @param images the images to process
   * @return the generated image(s)
   */
  @Override
  protected BoofCVImageContainer[] doProcess(BoofCVImageContainer[] images) {
    BoofCVImageContainer[]	result;
    ImageUInt8[]		processed;
    ImageUInt8 			output;

    output = new ImageUInt8(images[0].getWidth(),images[0].getHeight());
    result = new BoofCVImageContainer[1];
    processed = new ImageUInt8[images.length];
    for (int i = 0; i < images.length; i++) {
      processed[i] = (ImageUInt8) BoofCVHelper.toBoofCVImage(images[i].getImage(), BoofCVImageType.UNSIGNED_INT_8);
    }

    for (int y = 0; y < output.getHeight(); y++) {
      for (int x = 0; x < output.getWidth(); x++) {
	int[] values = new int[2];
	values[0] = processed[0].get(x,y);
	values[1] = processed[1].get(x,y);
	output.set(x,y, thresholdEquals(values));
      }
    }
    result[0] = new BoofCVImageContainer();
    result[0].setImage(output);
    return result;
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
  protected void check(BoofCVImageContainer[] images) {
    String	msg;

    super.check(images);

    msg = checkSameDimensions(images);
    if (msg != null)
      throw new IllegalStateException(msg);
  }

  private int thresholdEquals(int[] values) {
    int result = 0;
    result = (Math.abs(values[0] - values[1]) < m_Threshold) ? -1 : 1;
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
