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
 * And.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.data.image.multiimageoperation;

import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 <!-- globalinfo-start -->
 * Performs a logical AND on the binary pixels of the images.<br>
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
 * @version $Revision$
 */
public class And
  extends AbstractBufferedImageMultiImageOperation {

  private static final long serialVersionUID = 7381673951864996785L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Performs a logical AND on the binary pixels of the images.\n"
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
    return -1;
  }

  /**
   * Checks the images.
   *
   * @param images	the images to check
   */
  @Override
  protected void check(BufferedImageContainer[] images) {
    int		i;

    super.check(images);

    for (i = 1; i < images.length; i++) {
      if (!checkSameDimensions(images[0], images[i]))
	throw new IllegalStateException(
	  "All images need to have the same dimensions: "
	    + images[0].getWidth() + "x" + images[0].getHeight() + " (#1)"
	    + " != "
	    + images[i].getWidth() + "x" + images[i].getHeight() + "(#" + (i+1) +")");
    }
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
    BufferedImage[] 		img;
    BufferedImage		output;
    int				x;
    int				y;
    int				match;
    int				mismatch;
    int				i;
    int				val;
    boolean 			same;

    result   = new BufferedImageContainer[1];
    img      = new BufferedImage[images.length];
    for (i = 0; i < images.length; i++)
      img[i] = BufferedImageHelper.convert(images[i].getImage(), BufferedImage.TYPE_BYTE_BINARY);
    output   = BufferedImageHelper.deepCopy(img[0]);
    match    = Color.BLACK.getRGB();
    mismatch = Color.WHITE.getRGB();
    for (y = 0; y < images[0].getHeight(); y++) {
      for (x = 0; x < images[0].getWidth(); x++) {
	val  = img[0].getRGB(x, y);
	same = true;
	for (i = 1; i < img.length; i++) {
	  if (val != img[i].getRGB(x, y))
	    same = false;
	}
	output.setRGB(x, y, same ? match : mismatch);
      }
    }
    result[0] = new BufferedImageContainer();
    result[0].setImage(output);

    return result;
  }
}
