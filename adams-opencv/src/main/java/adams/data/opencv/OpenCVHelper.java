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
 * OpenCVHelper.java
 * Copyright (C) 2022-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.data.opencv;

import adams.data.Notes;
import adams.data.image.AbstractImageContainer;
import adams.data.report.Report;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter.ToMat;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;

import java.awt.image.BufferedImage;

/**
 * Helper methods for OpenCV.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class OpenCVHelper {

  /** whether OpenCV is available. */
  protected static Boolean m_Available;

  /**
   * Checks whether OpenCV is available.
   *
   * @return		true if available
   */
  public static synchronized boolean isAvailable() {
    if (m_Available == null) {
      try {
        new opencv_imgcodecs();
	m_Available = true;
      }
      catch (Throwable e) {
	m_Available = false;
      }
    }

    return m_Available;
  }

  /**
   * Converts the Mat object into a BufferedImage one.
   *
   * @param mat		the matrix to convert
   * @return		the generated image
   */
  public static BufferedImage toBufferedImage(Mat mat) {
    BufferedImage 		result;
    Java2DFrameConverter 	jConv;
    ToMat			toMat;
    Frame 			frame;

    toMat  = new OpenCVFrameConverter.ToMat();
    frame  = toMat.convert(mat);
    jConv  = new Java2DFrameConverter();
    result = jConv.convert(frame);
    // toMat/jConv results in NULL pointers??

    return result;
  }

  /**
   * Converts the BufferedImage into a Mat object.
   *
   * @param image	the image to convert
   * @return		the generated object
   */
  public static Mat toMat(BufferedImage image) {
    Mat		 		result;
    Java2DFrameConverter 	jConv;
    ToMat			toMat;
    Frame 			frame;

    jConv = new Java2DFrameConverter();
    frame  = jConv.convert(image);
    toMat  = new OpenCVFrameConverter.ToMat();
    result = toMat.convert(frame);
    // toMat/jConv results in NULL pointers??

    return result;
  }

  /**
   * Creates an {@link OpenCVImageContainer} container if necessary, otherwise
   * it just casts the object.
   *
   * @param cont	the cont to cast/convert
   * @return		the casted/converted container
   */
  public static OpenCVImageContainer toOpenCVImageContainer(AbstractImageContainer cont) {
    OpenCVImageContainer	result;
    Report 			report;
    Notes 			notes;

    if (cont instanceof OpenCVImageContainer)
      return (OpenCVImageContainer) cont;

    report = cont.getReport().getClone();
    notes  = cont.getNotes().getClone();
    result = new OpenCVImageContainer();
    result.setImage(toMat(cont.toBufferedImage()));
    result.setReport(report);
    result.setNotes(notes);

    return result;
  }
}
