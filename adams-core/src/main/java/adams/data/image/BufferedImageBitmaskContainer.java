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

  /** the bitmask. */
  protected BufferedImage m_Bitmask;

  /**
   * Initializes the members.
   */
  protected void initialize() {
    super.initialize();

    m_Bitmask = null;
  }

  /**
   * Checks whether a bitmask is present.
   *
   * @return		true if present
   */
  @Override
  public boolean hasBitmask() {
    return (m_Bitmask != null);
  }

  /**
   * Stores the bitmask.
   *
   * @param value 	the bitmask to store, null to remove
   */
  @Override
  public void setBitmask(BufferedImage value) {
    m_Bitmask = value;
  }

  /**
   * Returns the stored bitmask.
   *
   * @return		the bitmask, null if none store
   */
  @Override
  public BufferedImage getBitmask() {
    return m_Bitmask;
  }

  /**
   * Returns the bitmask as BufferedImage object.
   *
   * @return		the buffered image, null if not bitmask stored
   */
  @Override
  public BufferedImage bitmaskToBufferedImage() {
    return m_Bitmask;
  }

  /**
   * Returns a string representation of the container.
   *
   * @return		the string representation
   */
  @Override
  public String toString() {
    return super.toString() + ", bitmask=" + m_Bitmask;
  }
}
