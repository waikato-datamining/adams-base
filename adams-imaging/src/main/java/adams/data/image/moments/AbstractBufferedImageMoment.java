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
 * AbstractBufferedMoment.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.data.image.moments;

import adams.data.image.BufferedImageContainer;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Ancestor for moments for BufferedImage containers.
 *
 * @author sjb90
 * @version $Revision$
 */
public abstract class AbstractBufferedImageMoment
  extends AbstractMoment<BufferedImageContainer> {

  private static final long serialVersionUID = 1665590865743934991L;

  /** the value of the background colour. **/
  protected Color m_BackgroundValue;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add("background-value", "backgroundValue", Color.WHITE);
  }

  /**
   * Sets the background color.
   *
   * @param value	the color
   */
  public void setBackgroundValue(Color value) {
    m_BackgroundValue = value;
    reset();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String backgroundValueTipText() {
    return "The background color to use.";
  }

  /**
   * Returns the background color.
   *
   * @return		the color
   */
  public Color getBackgroundValue() {
    return m_BackgroundValue;
  }

  /**
   * Hook method for performing checks on the image.
   *
   * @param img		the image to check
   * @return		true if successful
   */
  @Override
  protected boolean check(BufferedImageContainer img) {
    return true;
  }

  /**
   * Takes an image of type T and returns a boolean matrix that can be used for moments
   *
   * @param img		the image
   * @return 		the boolean matrix representing the image
   */
  @Override
  public boolean[][] imageToMatrix(BufferedImageContainer img) {
    return imageToMatrix(img, m_BackgroundValue);
  }

  /**
   * Takes an image of type T and returns a boolean matrix that can be used for moments
   *
   * @param img		the image
   * @param background	the background color
   * @return 		the boolean matrix representing the image
   */
  public static boolean[][] imageToMatrix(BufferedImageContainer img, Color background) {
    BufferedImage image = img.toBufferedImage();
    int bg = background.getRGB();
    boolean[][] result = new boolean[image.getHeight()][image.getWidth()];
    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
	if(image.getRGB(x, y) == bg)
	  result[y][x] = false;
	else
	  result[y][x] = true;
      }
    }
    return result;
  }
}
