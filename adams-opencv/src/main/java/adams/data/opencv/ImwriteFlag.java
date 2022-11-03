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
 * ImwriteFlags.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.opencv;

/**
 * The flags that imwrite supports.
 *
 * https://docs.opencv.org/4.6.0/d8/d6a/group__imgcodecs__flags.html#ga292d81be8d76901bff7988d18d2b42ac
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public enum ImwriteFlag {

  IMWRITE_JPEG_QUALITY(org.bytedeco.opencv.global.opencv_imgcodecs.IMWRITE_JPEG_QUALITY),
  IMWRITE_JPEG_PROGRESSIVE(org.bytedeco.opencv.global.opencv_imgcodecs.IMWRITE_JPEG_PROGRESSIVE),
  IMWRITE_JPEG_OPTIMIZE(org.bytedeco.opencv.global.opencv_imgcodecs.IMWRITE_JPEG_OPTIMIZE),
  IMWRITE_JPEG_RST_INTERVAL(org.bytedeco.opencv.global.opencv_imgcodecs.IMWRITE_JPEG_RST_INTERVAL),
  IMWRITE_JPEG_LUMA_QUALITY(org.bytedeco.opencv.global.opencv_imgcodecs.IMWRITE_JPEG_LUMA_QUALITY),
  IMWRITE_JPEG_CHROMA_QUALITY(org.bytedeco.opencv.global.opencv_imgcodecs.IMWRITE_JPEG_CHROMA_QUALITY),
  IMWRITE_PNG_COMPRESSION(org.bytedeco.opencv.global.opencv_imgcodecs.IMWRITE_PNG_COMPRESSION),
  IMWRITE_PNG_STRATEGY(org.bytedeco.opencv.global.opencv_imgcodecs.IMWRITE_PNG_STRATEGY),
  IMWRITE_PNG_BILEVEL(org.bytedeco.opencv.global.opencv_imgcodecs.IMWRITE_PNG_BILEVEL),
  IMWRITE_PXM_BINARY(org.bytedeco.opencv.global.opencv_imgcodecs.IMWRITE_PXM_BINARY),
  IMWRITE_EXR_TYPE(org.bytedeco.opencv.global.opencv_imgcodecs.IMWRITE_EXR_TYPE),
  IMWRITE_EXR_COMPRESSION(org.bytedeco.opencv.global.opencv_imgcodecs.IMWRITE_EXR_COMPRESSION),
  IMWRITE_WEBP_QUALITY(org.bytedeco.opencv.global.opencv_imgcodecs.IMWRITE_WEBP_QUALITY),
  IMWRITE_PAM_TUPLETYPE(org.bytedeco.opencv.global.opencv_imgcodecs.IMWRITE_PAM_TUPLETYPE),
  IMWRITE_TIFF_RESUNIT(org.bytedeco.opencv.global.opencv_imgcodecs.IMWRITE_TIFF_RESUNIT),
  IMWRITE_TIFF_XDPI(org.bytedeco.opencv.global.opencv_imgcodecs.IMWRITE_TIFF_XDPI),
  IMWRITE_TIFF_YDPI(org.bytedeco.opencv.global.opencv_imgcodecs.IMWRITE_TIFF_YDPI),
  IMWRITE_TIFF_COMPRESSION(org.bytedeco.opencv.global.opencv_imgcodecs.IMWRITE_TIFF_COMPRESSION),
  IMWRITE_JPEG2000_COMPRESSION_X1000(org.bytedeco.opencv.global.opencv_imgcodecs.IMWRITE_JPEG2000_COMPRESSION_X1000);

  /** the opencv flag. */
  private int m_Flag;

  /**
   * Initializes the enum value.
   *
   * @param flag	the OpenCV flag
   */
  private ImwriteFlag(int flag) {
    m_Flag = flag;
  }

  /**
   * Returns the OpenCV flag.
   *
   * @return		the flag
   */
  public int getFlag() {
    return m_Flag;
  }
}
