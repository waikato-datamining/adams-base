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
import com.github.fracpete.javautils.struct.Struct2;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import org.bytedeco.javacpp.indexer.IntRawIndexer;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter.ToMat;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.Scalar;

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

  /**
   * Finds the contours in the image. Automatically converts it to binary.
   *
   * @param img		the image to find the contours in
   * @param threshold 	the threshold for generating the binary image, eg 127
   * @param inverse 	whether to generate the inverse binary ie black instead of white
   * @return		the list of contours, must be closed by caller
   */
  public static MatVector findContours(BufferedImage img, int threshold, boolean inverse) {
    int		conversion;

    switch (img.getType()) {
      case BufferedImage.TYPE_INT_ARGB:
	conversion = opencv_imgproc.COLOR_RGBA2GRAY;
	break;
      case BufferedImage.TYPE_INT_RGB:
	conversion = opencv_imgproc.COLOR_RGB2GRAY;
	break;
      case BufferedImage.TYPE_INT_BGR:
	conversion = opencv_imgproc.COLOR_BGR2GRAY;
	break;
      default:
	throw new IllegalArgumentException("Unhandled image type: " + img.getType());
    }
    return findContours(toMat(img), conversion, threshold, inverse);
  }

  /**
   * Finds the contours in the image. Automatically converts it to binary.
   *
   * @param mat		the image to find the contours in
   * @param conversion 	the color conversion to perform, eg COLOR_RGBA2GRAY
   * @param threshold 	the threshold for generating the binary image, eg 127
   * @param inverse 	whether to generate the inverse binary ie black instead of white
   * @return		the list of contours, must be closed by caller
   */
  public static MatVector findContours(Mat mat, int conversion, int threshold, boolean inverse) {
    MatVector 		result;
    Mat 		gray;
    Mat 		binary;

    // convert to grayscale
    gray = new Mat(mat.rows(), mat.cols(), mat.type());
    opencv_imgproc.cvtColor(mat, gray, conversion);

    // generate binary
    binary = new Mat(mat.rows(), mat.cols(), mat.type(), new Scalar(0));
    opencv_imgproc.threshold(gray, binary, threshold, 255, inverse ? opencv_imgproc.THRESH_BINARY_INV : opencv_imgproc.THRESH_BINARY);

    // find contours
    result = new MatVector();
    opencv_imgproc.findContours(binary, result, opencv_imgproc.RETR_TREE, opencv_imgproc.CHAIN_APPROX_SIMPLE);

    // clean up
    gray.close();
    binary.close();

    return result;
  }

  /**
   * Turns the contour matrix into x/y int arrays of coordinates.
   *
   * @param contour	the contour to convert
   * @return		the x/y coordinates
   */
  public static Struct2<int[], int[]> contourToCoordinates(Mat contour) {
    TIntList	x;
    TIntList	y;
    int		i;

    if (contour.cols() != 1)
      throw new IllegalArgumentException("Contour matrix must have a width of 1, but found: " + contour.cols());

    x = new TIntArrayList();
    y = new TIntArrayList();

    IntRawIndexer indexer = contour.createIndexer();
    for (i = 0; i < contour.rows(); i++) {
      x.add(indexer.get(i, 0, 0));
      y.add(indexer.get(i, 0, 1));
    }

    return new Struct2<>(x.toArray(), y.toArray());
  }
}
