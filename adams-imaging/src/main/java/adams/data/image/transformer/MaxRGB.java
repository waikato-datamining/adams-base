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
public class MaxRGB extends AbstractBufferedImageTransformer {

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
	image.setRGB(x, y, calcMax(image.getRGB(x,y)));
      }
    }
    result[0] = new BufferedImageContainer();
    result[0].setImage(image);
    return result;
  }

  /**
   * Finds the Maximum value of the RGB channels and sets all other channels to 0
   * @param rgb the RGB pixel as an int
   * @return the maximized pixel as an int
   */
  private int calcMax(int rgb) {
    int result = 0;
    int[] channels = BufferedImageHelper.split(rgb);
    int[] maxedChannels = new int[4];
    int currentMax = channels[0];
    int maxIndex = 0;
    for (int i = 1; i < channels.length - 1; i++) {
      if (currentMax < channels[i]) {
	maxIndex = i;
	currentMax = channels[i];
      }
    }
    maxedChannels[maxIndex] = 255;
    result = BufferedImageHelper.combine(maxedChannels);

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
