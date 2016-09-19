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
 * LocalMedianFilter.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.data.image.transformer;

import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;

import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * TODO: what class does.
 *
 * @author sjb90
 * @version $Revision$
 */
public class LocalMedianFilter extends AbstractBufferedImageTransformer {
  final int K = 4;
  /**
   * Performs the actual transforming of the image.
   *
   * @param img the image to transform (can be modified, since it is a copy)
   * @return the generated image(s)
   */
  @Override
  protected BufferedImageContainer[] doTransform(BufferedImageContainer img) {
    BufferedImageContainer[] result = new BufferedImageContainer[1];

    int 	height 	= img.getHeight();
    int 	width 	= img.getWidth();
    int[] 	pixels 	= new int[2*K+1];
    BufferedImage image = img.getImage();
    BufferedImage copy  = BufferedImageHelper.deepCopy(image);

    for (int h = 1; h < height - 1; h++) {
      for (int w = 1; w < width - 1; w++) {
	int k = 0;
	for (int x = -1; x < 2; x++) {
	  for (int y = -1; y < 2; y++) {
	    pixels[k] = copy.getRGB(w+x,h+y);
	    k++;
	  }
	}
        image.setRGB(w,h,getMedian(pixels));
      }
    }
    result[0] = new BufferedImageContainer();
    result[0].setImage(image);
    return result;
  }

  private int getMedian(int[] pixels) {
    int result;
    int[] r = new int[9];
    int[] g = new int[9];
    int[] b = new int[9];

    for (int i = 0; i < pixels.length; i++) {
      int[] channels = BufferedImageHelper.split(pixels[i]);
      r[i] = channels[0];
      g[i] = channels[1];
      b[i] = channels[2];
    }
    Arrays.sort(r);
    Arrays.sort(g);
    Arrays.sort(b);
    int[] median = new int[4];
    median[0] = r[K];
    median[1] = g[K];
    median[2] = b[K];
    result = BufferedImageHelper.combine(median);
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
