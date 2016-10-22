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

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * TODO: what class does.
 *
 * @author sjb90
 * @version $Revision$
 */
public abstract class AbstractBufferedMoment extends AbstractMoment<BufferedImageContainer> {

  /** the value of the background colour **/
  protected Color m_BackgroundValue;

  public Color getBackgroundValue() {
    return m_BackgroundValue;
  }

  public void setBackgroundValue(Color m_Background) {
    this.m_BackgroundValue = m_Background;
    reset();
  }

  /**
   * Adds options to the internal list of options. Derived classes must
   * override this method to add additional options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add("background-value", "backgroundValue", Color.WHITE);
  }

  /**
   * Takes an image of type T and returns a boolean matrix that can be used for moments
   *
   * @param img
   * @return the boolean matrix representing the image
   */
  @Override
  public boolean[][] imageToMatrix(BufferedImageContainer img) {
    BufferedImage image = img.toBufferedImage();
    int bg = m_BackgroundValue.getRGB();
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

  @Override
  protected boolean check(BufferedImageContainer img) {
    return true;
  }

}
