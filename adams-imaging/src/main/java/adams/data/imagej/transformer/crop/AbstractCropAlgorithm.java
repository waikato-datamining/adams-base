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
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.imagej.transformer.crop;

import ij.ImagePlus;

import java.awt.Point;

import adams.core.option.AbstractOptionHandler;
import adams.data.image.CropAlgorithm;

/**
 * Ancestor to cropping algorithms.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractCropAlgorithm
  extends AbstractOptionHandler
  implements CropAlgorithm<ImagePlus> {

  /** for serialization. */
  private static final long serialVersionUID = 776508705083560079L;

  /** the top-left corner. */
  protected Point m_TopLeft;

  /** the bottom-right corner. */
  protected Point m_BottomRight;
  
  /**
   * Checks whether the image can be cropped.
   * <br><br>
   * Default implementation only checks whether an image was supplied.
   * 
   * @param img				the image to check
   * @throws IllegalStateException 	if image fails check
   */
  protected void check(ImagePlus img) {
    if (img == null)
      throw new IllegalStateException("No image supplied!");
  }

  /**
   * Hook method before the crop happens.
   * <br><br>
   * Default method initializes the top-left and bottom-right corners to 
   * image dimensions.
   * 
   * @param img		the image to crop
   */
  protected void preCrop(ImagePlus img) {
    m_TopLeft     = new Point(0, 0);
    m_BottomRight = new Point(img.getWidth(), img.getHeight());
  }

  /**
   * Performs the actual crop.
   * 
   * @param img		the image to crop
   * @return		the (potentially) cropped image
   */
  protected abstract ImagePlus doCrop(ImagePlus img);

  /**
   * Hook method after the crop happened.
   * <br><br>
   * Default method does nothing.
   * 
   * @param img		the cropped
   */
  protected void postCrop(ImagePlus img) {
  }
  
  /**
   * Crops the image.
   * 
   * @param img		the image to crop
   * @return		the (potentially) cropped image
   */
  public ImagePlus crop(ImagePlus img) {
    ImagePlus	result;
    
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
