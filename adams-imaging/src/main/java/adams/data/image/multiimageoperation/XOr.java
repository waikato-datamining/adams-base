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
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.data.image.multiimageoperation;

import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 <!-- globalinfo-start -->
 * Performs a logical XOR on the binary pixels of the images.<br>
 * Converts images automatically to type BufferedImage.TYPE_BYTE_BINARY.
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
 * @version $Revision: 10568 $
 */
public class XOr
  extends AbstractBufferedImageMultiImageOperation {

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Performs a logical XOR on the binary pixels of the images.\n"
	+ "Converts images automatically to type BufferedImage.TYPE_BYTE_BINARY.";
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
   * @param images	the images to process
   * @return		the generated image(s)
   */
  @Override
  protected BufferedImageContainer[] doProcess(BufferedImageContainer[] images) {
    BufferedImageContainer[]	result;
    BufferedImage 		img0;
    BufferedImage		img1;
    BufferedImage		output;
    int				x;
    int				y;
    int 			xor;
    int				match;
    int				mismatch;

    result   = new BufferedImageContainer[1];
    img0     = BufferedImageHelper.convert(images[0].getImage(), BufferedImage.TYPE_BYTE_BINARY);
    img1     = BufferedImageHelper.convert(images[1].getImage(), BufferedImage.TYPE_BYTE_BINARY);
    output   = BufferedImageHelper.deepCopy(img0);
    match    = Color.BLACK.getRGB();
    mismatch = Color.WHITE.getRGB();
    for (y = 0; y < images[0].getHeight(); y++) {
      for (x = 0; x < images[0].getWidth(); x++) {
	xor = ((img0.getRGB(x, y) == match) || (img1.getRGB(x, y) == match)) && (img0.getRGB(x, y) != img1.getRGB(x, y)) ? match : mismatch;
	output.setRGB(x, y, xor);
      }
    }
    result[0] = new BufferedImageContainer();
    result[0].setImage(output);

    return result;
  }
}
