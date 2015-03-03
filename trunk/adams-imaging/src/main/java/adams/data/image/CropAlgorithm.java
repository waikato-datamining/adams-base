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
 * CropAlgorithm.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.image;

import java.awt.Point;

/**
 * Interface for crop algorithms.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface CropAlgorithm<T> {
  
  /** the constant for the top. */
  public final static String CROP_TOP = "Crop.Top";
  
  /** the constant for the left. */
  public final static String CROP_LEFT = "Crop.Left";
  
  /** the constant for the bottom. */
  public final static String CROP_BOTTOM = "Crop.Bottom";
  
  /** the constant for the right. */
  public final static String CROP_RIGHT = "Crop.Right";
  
  /**
   * Crops the image.
   * 
   * @param img		the image to crop
   * @return		the (potentially) cropped image
   */
  public T crop(T img);
  
  /**
   * Returns the top-left coordinates of the cropped image in the original
   * image.
   * 
   * @return		the top-left corner
   */
  public Point getTopLeft();
  
  /**
   * Returns the bottom-right coordinates of the cropped image in the original
   * image.
   * 
   * @return		the bottom-right corner
   */
  public Point getBottomRight();
}
