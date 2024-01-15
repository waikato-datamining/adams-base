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
 * BoofCVImageType.java
 * Copyright (C) 2013-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.data.boofcv;

import boofcv.struct.image.GrayF32;
import boofcv.struct.image.GrayF64;
import boofcv.struct.image.GrayS16;
import boofcv.struct.image.GrayS32;
import boofcv.struct.image.GrayS64;
import boofcv.struct.image.GrayS8;
import boofcv.struct.image.GrayU16;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.InterleavedF32;
import boofcv.struct.image.InterleavedF64;
import boofcv.struct.image.InterleavedS16;
import boofcv.struct.image.InterleavedS32;
import boofcv.struct.image.InterleavedS64;
import boofcv.struct.image.InterleavedS8;
import boofcv.struct.image.InterleavedU16;
import boofcv.struct.image.InterleavedU8;

/**
 * The different image types that are available.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public enum BoofCVImageType {
  /** float 32bit. */
  GRAYF32(GrayF32.class),
  /** float 64bit. */
  GRAYF64(GrayF64.class),
  /** signed int 8 bit. */
  GRAYS8(GrayS8.class),
  /** unsigned int 8 bit. */
  GRAYU8(GrayU8.class),
  /** signed int 16 bit. */
  GRAYS16(GrayS16.class),
  /** unsigned int 16 bit. */
  GRAYU16(GrayU16.class),
  /** signed int 32 bit. */
  GRAYS32(GrayS32.class),
  /** signed int 64 bit. */
  GRAYS64(GrayS64.class),
  /** interleaved/multiband float 32 bit */
  INTERLEAVEDF32(InterleavedF32.class),
  /** interleaved/multiband float 32 bit */
  INTERLEAVEDF64(InterleavedF64.class),
  /** interleaved/multiband signed 8 bit */
  INTERLEAVEDS8(InterleavedS8.class),
  /** interleaved/multiband unsigned 8 bit */
  INTERLEAVEDU8(InterleavedU8.class),
  /** interleaved/multiband signed 16 bit */
  INTERLEAVEDS16(InterleavedS16.class),
  /** interleaved/multiband unsigned 16 bit */
  INTERLEAVEDU16(InterleavedU16.class),
  /** interleaved/multiband signed 32 bit */
  INTERLEAVEDS32(InterleavedS32.class),
  /** interleaved/multiband signed 64 bit */
  INTERLEAVEDS64(InterleavedS64.class);
  
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
