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
 * BoofCVImageContainer.java
 * Copyright (C) 2013-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.data.boofcv;

import adams.data.image.AbstractImageContainer;
import boofcv.struct.image.ImageBase;
import boofcv.struct.image.ImageGray;

import java.awt.image.BufferedImage;

/**
 * Image wrapper around a {@link ImageBase} used by BoofCV.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class BoofCVImageContainer
  extends AbstractImageContainer<ImageBase> {

  /** for serialization. */
  private static final long serialVersionUID = 6674669743000941777L;

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
   * Returns a clone of the image. Actually, only for {@link ImageGray}
   * a clone is returned, all other types are a "subimage" with the same
   * size as the original.
   * 
   * @return		the clone/subimage
   */
  @Override
  protected ImageBase cloneContent() {
    return BoofCVHelper.clone(m_Content);
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
