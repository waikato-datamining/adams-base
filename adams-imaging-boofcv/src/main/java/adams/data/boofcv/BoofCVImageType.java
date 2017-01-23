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
 * BoofCVImageType.java
 * Copyright (C) 2013-2017 University of Waikato, Hamilton, New Zealand
 */
package adams.data.boofcv;

import boofcv.struct.image.*;

/**
 * The different image types that are available.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public enum BoofCVImageType {
  /** float 32bit. */
  FLOAT_32(GrayF32.class),
  /** float 64bit. */
  FLOAT_64(GrayF64.class),
  /** signed int 8 bit. */
  SIGNED_INT_8(GrayS8.class),
  /** unsigned int 8 bit. */
  UNSIGNED_INT_8(GrayU8.class),
  /** signed int 16 bit. */
  SIGNED_INT_16(GrayS16.class),
  /** unsigned int 16 bit. */
  UNSIGNED_INT_16(GrayU16.class),
  /** signed int 32 bit. */
  SIGNED_INT_32(GrayS32.class),
  /** signed int 64 bit. */
  SIGNED_INT_64(GrayS64.class),
  /** multiband image */
  MULTIBAND(ImageMultiBand.class);
  
  /** the associated image class. */
  private Class m_ImageClass;
  
  /**
   * Initializes the enum.
   * 
   * @param imageClass	the associated image class
   */
  private BoofCVImageType(Class imageClass) {
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
