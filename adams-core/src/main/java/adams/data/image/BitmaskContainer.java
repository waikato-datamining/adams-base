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

/**
 * Interface for containers that store bitmasks.
 *
 * @param <T> the type of image the bitmask represents
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface BitmaskContainer<T> {

  /**
   * Checks whether bitmasks are present.
   *
   * @return		true if present
   */
  public boolean hasBitmasks();

  /**
   * Returns the number of bitmasks stored.
   *
   * @return		the number of masks
   */
  public int getNumBitmasks();

  /**
   * Stores the bitmasks.
   *
   * @param value 	the bitmasks to store, null to remove
   */
  public void setBitmasks(T[] value);

  /**
   * Returns the stored bitmasks.
   *
   * @return		the bitmasks, null if none stored
   */
  public T[] getBitmasks();
}
