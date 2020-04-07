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
 * BitmaskContainer.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.data.image;

import java.awt.image.BufferedImage;

/**
 * Interface for containers that store bitmasks.
 *
 * @param <T> the type of image the bitmask represents
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface BitmaskContainer<T> {

  /**
   * Checks whether a bitmask is present.
   *
   * @return		true if present
   */
  public boolean hasBitmask();

  /**
   * Stores the bitmask.
   *
   * @param value 	the bitmask to store, null to remove
   */
  public void setBitmask(T value);

  /**
   * Returns the stored bitmask.
   *
   * @return		the bitmask, null if none store
   */
  public T getBitmask();

  /**
   * Returns the bitmask as BufferedImage object.
   *
   * @return		the buffered image, null if not bitmask stored
   */
  public BufferedImage bitmaskToBufferedImage();
}
