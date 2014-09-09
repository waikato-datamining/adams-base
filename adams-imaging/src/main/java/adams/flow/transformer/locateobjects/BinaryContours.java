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
 * BinaryContours.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.locateobjects;

import georegression.struct.point.Point2D_I32;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import adams.core.License;
import adams.core.annotation.MixedCopyright;
import boofcv.alg.filter.binary.BinaryImageOps;
import boofcv.alg.filter.binary.Contour;
import boofcv.alg.filter.binary.ThresholdImageOps;
import boofcv.alg.misc.ImageStatistics;
import boofcv.core.image.ConvertBufferedImage;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageUInt8;

/**
 <!-- globalinfo-start -->
 * Uses the BoofCV contour-finding algorithm to locate objects.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 78 $
 */
@MixedCopyright(
    author = "Peter Abeles",
    license = License.APACHE2,
    url = "http://boofcv.org/index.php?title=Example_Fit_Ellipse",
    note = "Code taken from this BoofCV example"
)
public class BinaryContours
  extends AbstractObjectLocator {

  /** for serialization. */
  private static final long serialVersionUID = 9160763275489359825L;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses the BoofCV contour-finding algorithm to locate objects.";
  }
  
  /**
   * Returns the input image as output.
   * 
   * @param image	the image with the bugs
   * @return		the original image
   */
  @Override
  protected List<LocatedObject> doLocate(BufferedImage image) {
    ArrayList<LocatedObject>	result;
    ImageFloat32 		input;
    ImageUInt8 			binary;
    ImageUInt8 			filtered;
    List<Contour> 		contours;
    int				left;
    int				right;
    int				top;
    int				bottom;
    double 			mean;

    input  = ConvertBufferedImage.convertFromSingle(image, null, ImageFloat32.class);
    binary = new ImageUInt8(input.width, input.height);
    // the mean pixel value is often a reasonable threshold when creating a binary image
    mean = ImageStatistics.mean(input);
    // create a binary image by thresholding
    ThresholdImageOps.threshold(input, binary, (float) mean, true);
    // reduce noise with some filtering
    filtered = BinaryImageOps.erode8(binary, null);
    filtered = BinaryImageOps.dilate8(filtered, null);
    // Find the contour around the shapes
    contours = BinaryImageOps.contour(filtered, 8, null);
    
    result = new ArrayList<LocatedObject>();
    for (Contour contour: contours) {
      // determine larges rectangle for contour
      left   = image.getWidth();
      right  = 0;
      top    = image.getHeight();
      bottom = 0;
      for (Point2D_I32 p: contour.external) {
	if (left > p.getX())
	  left = p.getX();
	if (right < p.getX())
	  right = p.getX();
	if (top > p.getY())
	  top = p.getY();
	if (bottom < p.getY())
	  bottom = p.getY();
      }
      result.add(
	  new LocatedObject(
	      image.getSubimage(left, top, right - left + 1, bottom - top + 1), 
	      left, top, right - left + 1, bottom - top + 1));
    }
    
    return result;
  }
}
