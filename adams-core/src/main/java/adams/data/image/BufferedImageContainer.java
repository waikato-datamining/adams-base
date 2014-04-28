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
 * BufferedImageContainer.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.image;

import java.awt.image.BufferedImage;

/**
 * Image wrapper around a BufferedImage used by JAI.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BufferedImageContainer
  extends AbstractImage<BufferedImage> {

  /**
   * Returns the width of the image.
   * 
   * @return		the width
   */
  @Override
  public int getWidth() {
    if (m_Image == null)
      return 0;
    else
      return m_Image.getWidth();
  }

  /**
   * Returns the height of the image.
   * 
   * @return		the height
   */
  @Override
  public int getHeight() {
    if (m_Image == null)
      return 0;
    else
      return m_Image.getHeight();
  }
  
  /**
   * Returns a clone of the image.
   * 
   * @return		the clone
   */
  @Override
  protected BufferedImage cloneImage() {
    return BufferedImageHelper.deepCopy(m_Image);
  }

  /**
   * Turns the image into a buffered image.
   * 
   * @return		the buffered image
   */
  @Override
  public BufferedImage toBufferedImage() {
    return m_Image;
  }
  
  /**
   * Returns a string representation of the container.
   *
   * @return		the string representation
   */
  @Override
  public String toString() {
    return "image=" + m_Image + ", colormodel=" + m_Image.getColorModel() + ", samplemodel=" + m_Image.getSampleModel() + ", report=" + m_Report + ", notes=" + m_Notes;
  }
}
