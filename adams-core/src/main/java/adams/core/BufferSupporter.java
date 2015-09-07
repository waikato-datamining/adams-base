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
 * BufferSupporter.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.core;

/**
 * Interface for classes that support a buffer size.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface BufferSupporter {

  /**
   * Sets the size of the buffer.
   *
   * @param value	the size
   */
  public void setBufferSize(int value);

  /**
   * Returns the size of the buffer.
   *
   * @return		the size
   */
  public int getBufferSize();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String bufferSizeTipText();
}
