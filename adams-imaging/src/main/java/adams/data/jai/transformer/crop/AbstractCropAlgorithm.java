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
 * AbstractCropAlgorithm.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.jai.transformer.crop;

import java.awt.Point;
import java.awt.image.BufferedImage;

import adams.core.option.AbstractOptionHandler;
import adams.data.image.CropAlgorithm;

/**
 * Ancestor to cropping algorithms.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 6675 $
 */
public abstract class AbstractCropAlgorithm
  extends AbstractOptionHandler 
  implements CropAlgorithm<BufferedImage> {

  /** for serialization. */
  private static final long serialVersionUID = 776508705083560079L;

  /** the top-left corner. */
  protected Point m_TopLeft;

  /** the bottom-right corner. */
  protected Point m_BottomRight;

  /**
   * Checks whether the image can be cropped.
   * <p/>
   * Default implementation only checks whether an image was supplied.
   * 
   * @param img				the image to check
   * @throws IllegalStateException 	if image fails check
   */
  protected void check(BufferedImage img) {
    if (img == null)
      throw new IllegalStateException("No image supplied!");
  }

  /**
   * Hook method before the crop happens.
   * <p/>
   * Default method initializes the top-left and bottom-right corners to 
   * image dimensions.
   * 
   * @param img		the image to crop
   */
  protected void preCrop(BufferedImage img) {
    m_TopLeft     = new Point(0, 0);
    m_BottomRight = new Point(img.getWidth(), img.getHeight());
  }

  /**
   * Performs the actual crop.
   * 
   * @param img		the image to crop
   * @return		the (potentially) cropped image
   */
  protected abstract BufferedImage doCrop(BufferedImage img);

  /**
   * Hook method after the crop happened.
   * <p/>
   * Default method does nothing.
   * 
   * @param img		the cropped
   */
  protected void postCrop(BufferedImage img) {
  }

  /**
   * Crops the image.
   * 
   * @param img		the image to crop
   * @return		the (potentially) cropped image
   */
  public BufferedImage crop(BufferedImage img) {
    BufferedImage	result;
    
    check(img);
    preCrop(img);
    result = doCrop(img);
    postCrop(result);
    
    return result;
  }
  
  /**
   * Returns the top-left coordinates of the cropped image in the original
   * image.
   * 
   * @return		the top-left corner
   */
  public Point getTopLeft() {
    return m_TopLeft;
  }
  
  /**
   * Returns the bottom-right coordinates of the cropped image in the original
   * image.
   * 
   * @return		the bottom-right corner
   */
  public Point getBottomRight() {
    return m_BottomRight;
  }
}
