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
 * ApplyMask.java
 * Copyright (C) 2021 University of Waikato, Hamilton, New Zealand
 */

package adams.data.image.multiimageoperation;

import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;

import java.awt.image.BufferedImage;

/**
 <!-- globalinfo-start -->
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ApplyMask
  extends AbstractBufferedImageMultiImageOperation {

  private static final long serialVersionUID = 3811234791768694013L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Uses the second image as mask that gets applied to the first image.\n"
        + "Pixels from the first image are only kept if the corresponding "
	+ "ones in the mask are non-zero.";
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
    BufferedImage 		img;
    BufferedImage 		mask;
    BufferedImage		output;
    int				x;
    int				y;
    int				m;

    result   = new BufferedImageContainer[1];
    img      = images[0].getImage();
    mask     = images[1].getImage();
    output   = BufferedImageHelper.deepCopy(img);
    for (y = 0; y < img.getHeight(); y++) {
      for (x = 0; x < img.getWidth(); x++) {
        m = mask.getRGB(x, y) & 0xFFFFFF;
        if (m == 0)
	  output.setRGB(x, y, 0);
      }
    }
    result[0] = new BufferedImageContainer();
    result[0].setImage(output);

    return result;
  }
}
