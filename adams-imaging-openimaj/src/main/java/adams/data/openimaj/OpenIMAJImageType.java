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
 * OpenIMAJImageType.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.openimaj;

import org.openimaj.image.FImage;
import org.openimaj.image.MBFImage;

/**
 * The different image types that are available.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public enum OpenIMAJImageType {
  /** single-band float. */
  FIMAGE(FImage.class),
  /** multi-band float. */
  MBFIMAGE(MBFImage.class);

  /** the associated image class. */
  private Class m_ImageClass;

  /**
   * Initializes the enum.
   *
   * @param imageClass	the associated image class
   */
  private OpenIMAJImageType(Class imageClass) {
    m_ImageClass = imageClass;
  }
  
  /**
   * Returns the associated image class.
   * 
   * @return		the class
   */
  public Class getImageClass() {
    return m_ImageClass;
  }
}
