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
 * XOr.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.data.boofcv.multiimageoperation;

import adams.data.boofcv.BoofCVHelper;
import adams.data.boofcv.BoofCVImageContainer;
import adams.data.boofcv.BoofCVImageType;
import boofcv.struct.image.ImageUInt8;

/**
 <!-- globalinfo-start -->
 * Performs a logical XOR on the binary pixels of the images.<br>
 * Converts images automatically to type UNSIGNED_INT_8.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 10552 $
 */
public class XOr
  extends AbstractBoofCVMultiImageOperation {

  private static final long serialVersionUID = 8245723009628774167L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Performs a logical XOR on the binary pixels of the images.\n"
	+ "Converts images automatically to type " + BoofCVImageType.UNSIGNED_INT_8 + ".";
  }

  /**
   * Returns the minimum number of images that are required for the operation.
   *
   * @return		the number of images that are required, <= 0 means no lower limit
   */
  @Override
  public int minNumImagesRequired() {
    return 2;
  }

  /**
   * Returns the maximum number of images that are required for the operation.
   *
   * @return		the number of images that are required, <= 0 means no upper limit
   */
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
   * Performs the actual processing of the images.
   *
   * @param images	the images to process
   * @return		the generated image(s)
   */
  @Override
  protected BoofCVImageContainer[] doProcess(BoofCVImageContainer[] images) {
    BoofCVImageContainer[]	result;
    int				x;
    int				y;
    int 			xor;
    ImageUInt8			img0;
    ImageUInt8			img1;
    ImageUInt8			output;

    result    = new BoofCVImageContainer[1];
    img0      = (ImageUInt8) BoofCVHelper.toBoofCVImage(images[0], BoofCVImageType.UNSIGNED_INT_8);
    img1      = (ImageUInt8) BoofCVHelper.toBoofCVImage(images[1], BoofCVImageType.UNSIGNED_INT_8);
    output    = (ImageUInt8) BoofCVHelper.clone(img0);
    for (y = 0; y < images[0].getHeight(); y++) {
      for (x = 0; x < images[0].getWidth(); x++) {
	xor = ((img0.get(x, y) == 0) || (img1.get(x, y) == 0)) && (img0.get(x, y) != img1.get(x, y)) ? 0 : 1;
	output.set(x, y, xor);
      }
    }
    result[0] = new BoofCVImageContainer();
    result[0].setImage(output);

    return result;
  }
}
