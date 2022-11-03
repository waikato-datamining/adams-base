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
 * InterpolationType.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.opencv;

import org.bytedeco.opencv.global.opencv_imgproc;

/**
 * Defines interpolation types.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public enum InterpolationType {
  CV_INTER_NN(opencv_imgproc.CV_INTER_NN),
  CV_INTER_LINEAR(opencv_imgproc.CV_INTER_LINEAR),
  CV_INTER_CUBIC(opencv_imgproc.CV_INTER_CUBIC),
  CV_INTER_AREA(opencv_imgproc.CV_INTER_AREA),
  CV_INTER_LANCZOS4(opencv_imgproc.CV_INTER_LANCZOS4);

  /** the OpenCV type. */
  private int m_Type;

  /**
   * Initializes the enum value.
   *
   * @param type	the OpenCV type
   */
  private InterpolationType(int type) {
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
