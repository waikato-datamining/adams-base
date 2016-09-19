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
 * MaxRGB.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.data.image.transformer;

import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;

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
public class MinRGB extends AbstractBufferedImageTransformer {

  /**

   * Performs the actual transforming of the image.
   *
   * @param img the image to transform (can be modified, since it is a copy)
   * @return the generated image(s)
   */
  @Override
  protected BufferedImageContainer[] doTransform(BufferedImageContainer img) {
    BufferedImageContainer[] result = new BufferedImageContainer[1];
    BufferedImage image = BufferedImageHelper.deepCopy(img.toBufferedImage());

    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
	image.setRGB(x, y, calcMin(image.getRGB(x, y)));
      }
    }
    result[0] = new BufferedImageContainer();
    result[0].setImage(image);
    return result;
  }

  /**
   * Finds the Minimum value of the RGB channels and sets all other channels to 0
   * @param rgb the RGB pixel as an int
   * @return the minimized pixel as an int
   */
  private int calcMin(int rgb) {
    int result = 0;
    int[] channels = BufferedImageHelper.split(rgb);
    int[] minnedChannels = {255, 255, 255, 0};
    int currentMin = channels[0];
    int minIndex = 0;
    for (int i = 1; i < channels.length - 1; i++) {
      if (currentMin > channels[i]) {
	minnedChannels[minIndex] = 0;
	minIndex = i;
	currentMin = channels[i];
      }
      else {
	minnedChannels[i] = 0;
      }
    }
    result = BufferedImageHelper.combine(minnedChannels);
    return result;
  }

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return null;
  }
}
