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
 * BoofCVImageContainer.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.boofcv;

import java.awt.image.BufferedImage;

import adams.core.BoofCVHelper;
import adams.data.image.AbstractImage;
import boofcv.struct.image.ImageBase;
import boofcv.struct.image.ImageSingleBand;

/**
 * Image wrapper around a {@link ImageBase} used by BoofCV.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BoofCVImageContainer
  extends AbstractImage<ImageBase> {

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
   * Returns a clone of the image. Actually, only for {@link ImageSingleBand}
   * a clone is returned, all other types are a "subimage" with the same
   * size as the original.
   * 
   * @return		the clone/subimage
   */
  @Override
  protected ImageBase cloneContent() {
    if (m_Content instanceof ImageSingleBand)
      return ((ImageSingleBand) m_Content).clone();
    else
      return m_Content.subimage(0, 0, m_Content.getWidth() - 1, m_Content.getHeight() - 1, null);
  }

  /**
   * Turns the image into a buffered image.
   * 
   * @return		the buffered image
   */
  @Override
  public BufferedImage toBufferedImage() {
    return BoofCVHelper.toBufferedImage(m_Content);
  }
}
