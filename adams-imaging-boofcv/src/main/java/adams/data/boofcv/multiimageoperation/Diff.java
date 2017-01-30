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
 * Copyright (C) 2016-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.data.boofcv.multiimageoperation;

import adams.data.boofcv.BoofCVHelper;
import adams.data.boofcv.BoofCVImageContainer;
import adams.data.boofcv.BoofCVImageType;
import boofcv.struct.image.ImageUInt8;

/**
 <!-- globalinfo-start -->
 * Compares two images and returns a third image with the differences between them
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-threshold &lt;int&gt; (property: threshold)
 * &nbsp;&nbsp;&nbsp;The threshold to use, based on absolute difference between pixel values.
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author sjb90
 * @version $Revision$
 */
public class Diff
  extends AbstractBoofCVMultiImageOperation {

  private static final long serialVersionUID = -4576060482553726848L;

  /** the threshold at which two pixels are considered different */
  protected int m_Threshold;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Compares two images and returns a third image with the differences between them";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add("threshold", "threshold", 0, 0, null);
  }

  /**
   * Sets the threshold to use.
   *
   * @param value	the threshold
   */
  public void setThreshold(int value) {
    if (getOptionManager().isValid("threshold", value)) {
      m_Threshold = value;
      reset();
    }
  }

  /**
   * Returns the threshold.
   *
   * @return		the threshold
   */
  public int getThreshold() {
    return m_Threshold;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String thresholdTipText() {
    return "The threshold to use, based on absolute difference between pixel values.";
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

  /**
   * Outputs -1 if the difference between the values is less than the threshold,
   * otherwise 1.
   *
   * @param value1	the two values to compare
   * @return		the difference indicator
   */
  protected int thresholdEquals(int value1, int value2) {
    return (Math.abs(value1 - value2) < m_Threshold) ? -1 : 1;
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
    for (int i = 0; i < images.length; i++)
      processed[i] = (ImageUInt8) BoofCVHelper.toBoofCVImage(images[i].getImage(), BoofCVImageType.UNSIGNED_INT_8);

    for (int y = 0; y < output.getHeight(); y++) {
      for (int x = 0; x < output.getWidth(); x++)
	output.set(x,y, thresholdEquals(processed[0].get(x,y), processed[1].get(x,y)));
    }
    result[0] = new BoofCVImageContainer();
    result[0].setImage(output);
    return result;
  }
}
