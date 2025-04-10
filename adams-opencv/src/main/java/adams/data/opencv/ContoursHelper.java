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
 * ContoursHelper.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.data.opencv;

import com.github.fracpete.javautils.struct.Struct2;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import org.bytedeco.javacpp.indexer.IntRawIndexer;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.Scalar;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods around object contours.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ContoursHelper {

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
    return findContours(OpenCVHelper.toMat(img), conversion, threshold, inverse);
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
    TIntList 	x;
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

  /**
   * Turns the contours into polygons.
   * Caller must close the contours object.
   *
   * @param contours	the contours to convert
   * @return		the polygons
   */
  public static List<Polygon> contoursToPolygons(MatVector contours) {
    return contoursToPolygons(contours, -1, -1);
  }

  /**
   * Turns the contours into polygons that fit the min/max restrictions.
   * Caller must close the contours object.
   *
   * @param contours	the contours to convert
   * @param min 	the minimum size (width and height), ignored if <= 0
   * @param max 	the maximum size (width and height), ignored if <= 0
   * @return		the polygons
   */
  public static List<Polygon> contoursToPolygons(MatVector contours, int min, int max) {
    List<Polygon>		result;
    int				i;
    Struct2<int[], int[]>	coords;
    Polygon			poly;
    Rectangle			rect;

    result = new ArrayList<>();
    
    for (i = 0; i < contours.size(); i++) {
      coords = ContoursHelper.contourToCoordinates(contours.get(i));
      poly   = new Polygon(coords.value1, coords.value2, coords.value1.length);
      rect   = poly.getBounds();
      // check restrictions
      if (min > 0) {
	if ((rect.width < min) || (rect.height < min))
	  continue;
      }
      if (max > 0) {
	if ((rect.width > max) || (rect.height > max))
	  continue;
      }
      result.add(poly);
    }

    return result;
  }
}
