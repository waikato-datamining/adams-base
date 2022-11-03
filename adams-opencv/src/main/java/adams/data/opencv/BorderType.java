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
 * BorderType.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.opencv;

/**
 * Border types used by OpenCV.
 *
 * https://docs.opencv.org/4.6.0/d2/de8/group__core__array.html#ga209f2f4869e304c82d07739337eae7c5
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public enum BorderType {

  BORDER_CONSTANT(org.bytedeco.opencv.global.opencv_core.BORDER_CONSTANT),
  BORDER_REPLICATE(org.bytedeco.opencv.global.opencv_core.BORDER_REPLICATE),
  BORDER_REFLECT(org.bytedeco.opencv.global.opencv_core.BORDER_REFLECT),
  BORDER_WRAP(org.bytedeco.opencv.global.opencv_core.BORDER_WRAP),
  BORDER_REFLECT_101(org.bytedeco.opencv.global.opencv_core.BORDER_REFLECT_101),
  BORDER_TRANSPARENT(org.bytedeco.opencv.global.opencv_core.BORDER_TRANSPARENT),
  BORDER_REFLECT101(org.bytedeco.opencv.global.opencv_core.BORDER_REFLECT101),
  BORDER_DEFAULT(org.bytedeco.opencv.global.opencv_core.BORDER_DEFAULT),
  BORDER_ISOLATED(org.bytedeco.opencv.global.opencv_core.BORDER_ISOLATED);

  private int m_Type;

  /**
   * Initializes the enum with the OpenCV border type.
   *
   * @param type	the type
   */
  private BorderType(int type) {
    m_Type = type;
  }

  /**
   * Returns the border type.
   *
   * @return		the type
   */
  public int getType() {
    return m_Type;
  }
}
