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
 * BufferedImageBitmaskContainer.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.data.image;

import java.awt.image.BufferedImage;

/**
 * Container for storing a BufferedImage and its associated bitmask image.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class BufferedImageBitmaskContainer
  extends BufferedImageContainer
  implements BitmaskContainer<BufferedImage> {

  private static final long serialVersionUID = -3729935584834380099L;

  /** the bitmasks. */
  protected BufferedImage[] m_Bitmasks;

  /**
   * Initializes the members.
   */
  protected void initialize() {
    super.initialize();

    m_Bitmasks = null;
  }

  /**
   * Checks whether a bitmask is present.
   *
   * @return		true if present
   */
  @Override
  public boolean hasBitmasks() {
    return (m_Bitmasks != null);
  }

  /**
   * Returns the number of bitmasks stored.
   *
   * @return		the number of masks
   */
  public int getNumBitmasks() {
    if (m_Bitmasks == null)
      return 0;
    else
      return m_Bitmasks.length;
  }

  /**
   * Stores the bitmasks.
   *
   * @param value 	the bitmasks to store, null to remove
   */
  public void setBitmasks(BufferedImage[] value) {
    m_Bitmasks = value;
  }

  /**
   * Returns the stored bitmasks.
   *
   * @return		the bitmasks, null if none store
   */
  @Override
  public BufferedImage[] getBitmasks() {
    return m_Bitmasks;
  }

  /**
   * Returns a string representation of the container.
   *
   * @return		the string representation
   */
  @Override
  public String toString() {
    return super.toString() + ", bitmask=" + (m_Bitmasks == null ? "-none-" : m_Bitmasks.length);
  }
}
