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
 * OpenCVImageContainer.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.opencv;

import adams.data.image.AbstractImageContainer;
import org.bytedeco.opencv.opencv_core.Mat;

import java.awt.image.BufferedImage;

/**
 * Encapsulates an OpenCV image.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class OpenCVImageContainer
    extends AbstractImageContainer<Mat> {

  private static final long serialVersionUID = 5927586462384340177L;

  /**
   * Returns the width of the image.
   *
   * @return the width
   */
  @Override
  public int getWidth() {
    return m_Content.cols();
  }

  /**
   * Returns the height of the image.
   *
   * @return the height
   */
  @Override
  public int getHeight() {
    return m_Content.rows();
  }

  /**
   * Turns the image into a buffered image.
   *
   * @return the buffered image
   */
  @Override
  public BufferedImage toBufferedImage() {
    return OpenCVHelper.toBufferedImage(m_Content);
  }

  /**
   * Returns a clone of the content.
   *
   * @return the clone
   */
  @Override
  protected Mat cloneContent() {
    return m_Content.clone();
  }
}
