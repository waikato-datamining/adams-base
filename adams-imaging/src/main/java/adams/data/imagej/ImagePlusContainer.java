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
 * ImagePlusContainer.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.imagej;

import ij.ImagePlus;

import java.awt.image.BufferedImage;

import adams.data.image.AbstractImageContainer;

/**
 * Image wrapper around an ImagePlus used by ImageJ.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ImagePlusContainer
  extends AbstractImageContainer<ImagePlus> {

  /**
   * Returns the width of the image.
   * 
   * @return		the width
   */
  @Override
  public int getWidth() {
    if (m_Content == null)
      return 0;
    else
      return m_Content.getWidth();
  }

  /**
   * Returns the height of the image.
   * 
   * @return		the height
   */
  @Override
  public int getHeight() {
    if (m_Content == null)
      return 0;
    else
      return m_Content.getHeight();
  }

  /**
   * Returns a clone of the image.
   * 
   * @return		the clone
   */
  @Override
  protected ImagePlus cloneContent() {
    return new ImagePlus(m_Content.getTitle() + "'", m_Content.getImage());
  }

  /**
   * Turns the image into a buffered image.
   * 
   * @return		the buffered image
   */
  @Override
  public BufferedImage toBufferedImage() {
    return m_Content.getBufferedImage();
  }

  /**
   * Turns the image type into a string.
   * 
   * @param type	the type to convert
   * @return		the string representation of the type
   */
  public static String imageTypeToString(int type) {
    switch (type) {
      case ImagePlus.GRAY8:
	return "Gray8";
      case ImagePlus.GRAY16:
	return "Gray16";
      case ImagePlus.GRAY32:
	return "Gray32";
      case ImagePlus.COLOR_256:
	return "Color256";
      case ImagePlus.COLOR_RGB:
	return "ColorRGB";
      default:
	return "" + type;
    }
  }
  
  /**
   * Returns a string representation of the container.
   *
   * @return		the string representation
   */
  @Override
  public String toString() {
    return "image=" + m_Content + ", type=" + imageTypeToString(m_Content.getType()) + ", report=" + m_Report + ", notes=" + m_Notes;
  }
}
