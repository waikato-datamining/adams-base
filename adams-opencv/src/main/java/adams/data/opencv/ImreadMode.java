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
 * ImreadModes.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.opencv;

/**
 * Modes for reading images using imread.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public enum ImreadMode {

  IMREAD_UNCHANGED(org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_UNCHANGED),
  IMREAD_GRAYSCALE(org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_GRAYSCALE),
  IMREAD_COLOR(org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_COLOR),
  IMREAD_ANYDEPTH(org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_ANYDEPTH),
  IMREAD_ANYCOLOR(org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_ANYCOLOR),
  IMREAD_LOAD_GDAL(org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_LOAD_GDAL),
  IMREAD_REDUCED_GRAYSCALE_2(org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_REDUCED_GRAYSCALE_2),
  IMREAD_REDUCED_COLOR_2(org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_REDUCED_COLOR_2),
  IMREAD_REDUCED_GRAYSCALE_4(org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_REDUCED_GRAYSCALE_4),
  IMREAD_REDUCED_COLOR_4(org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_REDUCED_COLOR_4),
  IMREAD_REDUCED_GRAYSCALE_8(org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_REDUCED_GRAYSCALE_8),
  IMREAD_REDUCED_COLOR_8(org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_REDUCED_COLOR_8),
  IMREAD_IGNORE_ORIENTATION(org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_IGNORE_ORIENTATION);

  /** the OpenCV mode. */
  private int m_Mode;

  /**
   * Initializes the enum value.
   *
   * @param mode	the opencv mode
   */
  private ImreadMode(int mode) {
    m_Mode = mode;
  }

  /**
   * Returns the OpenCV mode.
   *
   * @return		the mode
   */
  public int getMode() {
    return m_Mode;
  }
}
