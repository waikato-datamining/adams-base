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
 * AbstractMultiImageOperation.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.data.image;

import adams.core.option.AbstractOptionHandler;

/**
 * Abstract base class for operations that require multiple images.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of image to process
 */
public abstract class AbstractMultiImageOperation<T extends AbstractImageContainer>
  extends AbstractOptionHandler {

  /**
   * Returns the number of images that are required for the operation.
   *
   * @return		the number of images that are required, <= 0 means any number accepted
   */
  public abstract int numImagesRequired();

  /**
   * Checks whether the two images have the same dimensions.
   *
   * @param image1	the first image
   * @param image2	the second image
   * @return		true if the same dimensions
   */
  protected boolean checkSameDimensions(T image1, T image2) {
    return
      (image1.getWidth() == image2.getWidth())
      && (image1.getHeight() == image2.getHeight());
  }

  /**
   * Checks the images.
   * <br><br>
   * Default implementation only ensures that images are present.
   *
   * @param images	the images to check
   */
  protected void check(T[] images) {
    if ((images == null) || (images.length == 0))
      throw new IllegalStateException("No images provided!");

    if (numImagesRequired() > 0) {
      if (images.length != numImagesRequired())
	throw new IllegalStateException(
	  "Required number of images differs from supplied number: " + numImagesRequired() + " != " + images.length);
    }
  }

  /**
   * Performs the actual processing of the images.
   *
   * @param images	the images to process
   * @return		the generated image(s)
   */
  protected abstract T[] doProcess(T[] images);

  /**
   * Processes the images.
   *
   * @param images	the images to process
   * @return		the generated image(s)
   */
  public T[] process(T[] images) {
    check(images);
    return doProcess(images);
  }
}
