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
 * ThresholdType.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.opencv;

/**
 * The threshold types.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public enum ThresholdType {

  THRESH_BINARY(org.bytedeco.opencv.global.opencv_imgproc.THRESH_BINARY),
  THRESH_BINARY_INV(org.bytedeco.opencv.global.opencv_imgproc.THRESH_BINARY_INV),
  THRESH_TRUNC(org.bytedeco.opencv.global.opencv_imgproc.THRESH_TRUNC),
  THRESH_TOZERO(org.bytedeco.opencv.global.opencv_imgproc.THRESH_TOZERO),
  THRESH_TOZERO_INV(org.bytedeco.opencv.global.opencv_imgproc.THRESH_TOZERO_INV),
  THRESH_MASK(org.bytedeco.opencv.global.opencv_imgproc.THRESH_MASK),
  THRESH_OTSU(org.bytedeco.opencv.global.opencv_imgproc.THRESH_OTSU),
  THRESH_TRIANGLE(org.bytedeco.opencv.global.opencv_imgproc.THRESH_TRIANGLE);

  /** the type. */
  private int m_Type;

  /**
   * Initializes the enum value.
   *
   * @param type	the OpenCV type
   */
  private ThresholdType(int type) {
    m_Type = type;
  }

  /**
   * Returns the OpenCV type.
   *
   * @return		the type
   */
  public int getType() {
    return m_Type;
  }
}
