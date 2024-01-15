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
 * Copyright (C) 2015-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.data.boofcv.multiimageoperation;

import adams.data.boofcv.BoofCVHelper;
import adams.data.boofcv.BoofCVImageContainer;
import adams.data.boofcv.BoofCVImageType;
import boofcv.struct.image.GrayU8;

/**
 <!-- globalinfo-start -->
 * Performs a logical AND on the  binary pixels of the images.<br>
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
 * @version $Revision$
 */
public class And
  extends AbstractBoofCVMultiImageOperation {

  private static final long serialVersionUID = -3399149520525668500L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Performs a logical AND on the  binary pixels of the images.\n"
	+ "Converts images automatically to type " + BoofCVImageType.GRAYU8 + ".";
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
    GrayU8[]		img;
    int				x;
    int				y;
    GrayU8			output;
    int				i;
    int				val;
    boolean			same;

    result    = new BoofCVImageContainer[1];
    img       = new GrayU8[images.length];
    for (i = 0; i < images.length; i++)
      img[i] = (GrayU8) BoofCVHelper.toBoofCVImage(images[i], BoofCVImageType.GRAYU8);
    output    = (GrayU8) BoofCVHelper.clone(img[0]);
    for (y = 0; y < images[0].getHeight(); y++) {
      for (x = 0; x < images[0].getWidth(); x++) {
	val  = img[0].get(x, y);
	same = true;
	for (i = 1; i < img.length; i++) {
	  if (val != img[i].get(x, y))
	    same = false;
	}
	output.set(x, y, same ? 0 : 1);
      }
    }
    result[0] = new BoofCVImageContainer();
    result[0].setImage(output);

    return result;
  }
}
