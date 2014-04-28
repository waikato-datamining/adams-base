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
import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor to cropping algorithms.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractCropAlgorithm
  extends AbstractOptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = 776508705083560079L;

  /**
   * Checks whether the image can be cropped.
   * <p/>
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
   * Performs the actual crop.
   * 
   * @param img		the image to crop
   * @return		the (potentially) cropped image
   */
  protected abstract ImagePlus doCrop(ImagePlus img);
  
  /**
   * Crops the image.
   * 
   * @param img		the image to crop
   * @return		the (potentially) cropped image
   */
  public ImagePlus crop(ImagePlus img) {
    check(img);
    return doCrop(img);
  }
  
  /**
   * It is needed for the FrameCropAlgorithm 
   * 
   * @return the difference of the x value to the original image
   */
  public int getXValue(){
    return 0;
  }
  
  /**
   * It is needed for the FrameCropAlgorithm
   * 
   * @return the difference of the y value to the original image
   */
  public int getYValue(){
    return 0;
  }
}
