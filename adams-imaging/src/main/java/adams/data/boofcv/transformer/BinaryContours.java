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
package adams.data.boofcv.transformer;

import java.awt.image.BufferedImage;
import java.util.List;

import adams.core.BoofCVHelper;
import adams.core.License;
import adams.core.annotation.MixedCopyright;
import adams.data.boofcv.BoofCVImageContainer;
import adams.data.boofcv.BoofCVImageType;
import boofcv.alg.filter.binary.BinaryImageOps;
import boofcv.alg.filter.binary.Contour;
import boofcv.alg.filter.binary.ThresholdImageOps;
import boofcv.alg.misc.ImageStatistics;
import boofcv.core.image.ConvertBufferedImage;
import boofcv.gui.binary.VisualizeBinaryData;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageUInt8;

/**
 <!-- globalinfo-start -->
 * Uses the BoofCV binary contours algorithm to detect edges.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-remove-small-blobs &lt;boolean&gt; (property: removeSmallBlobs)
 * &nbsp;&nbsp;&nbsp;If enabled, small blobs are removed using erode8&#47;dilate8.
 * &nbsp;&nbsp;&nbsp;default: false
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
    url = "https://github.com/lessthanoptimal/BoofCV/blob/v0.15/examples/src/boofcv/examples/ExampleFitEllipse.java",
    note = "Code taken from this BoofCV example"
)
public class BinaryContours
  extends AbstractBoofCVTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -95759083210389706L;
  
  /** whether to remove small blobs. */
  protected boolean m_RemoveSmallBlobs;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses the BoofCV binary contours algorithm to detect edges.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "remove-small-blobs", "removeSmallBlobs",
	    false);
  }

  /**
   * Sets whether to remove small blobs using erode8/dilate8.
   *
   * @param value	true if to remove blobs
   */
  public void setRemoveSmallBlobs(boolean value) {
    m_RemoveSmallBlobs = value;
    reset();
  }

  /**
   * Returns whether to remove small blobs using erode8/dilate8.
   *
   * @return		true if blobs removed
   */
  public boolean getRemoveSmallBlobs() {
    return m_RemoveSmallBlobs;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String removeSmallBlobsTipText() {
    return "If enabled, small blobs are removed using erode8/dilate8.";
  }

  /**
   * Performs no transformation at all, just returns the input.
   *
   * @param img		the image to process (can be modified, since it is a copy)
   * @return		the copy of the image
   */
  @Override
  protected BoofCVImageContainer[] doTransform(BoofCVImageContainer img) {
    BoofCVImageContainer[]	result;
    ImageFloat32 		input;
    ImageUInt8 			binary;
    ImageUInt8 			filtered;
    List<Contour> 		contours;
    BufferedImage		rendered;
    double 			mean;
    
    input  = (ImageFloat32) BoofCVHelper.toBoofCVImage(img.getImage(), BoofCVImageType.FLOAT_32);
    binary = new ImageUInt8(input.width, input.height);
    // the mean pixel value is often a reasonable threshold when creating a binary image
    mean = ImageStatistics.mean(input);
    // create a binary image by thresholding
    ThresholdImageOps.threshold(input, binary, (float) mean, true);
    // reduce noise with some filtering?
    if (m_RemoveSmallBlobs) {
      filtered = BinaryImageOps.erode8(binary, null);
      filtered = BinaryImageOps.dilate8(filtered, null);
    }
    else {
      filtered = binary;
    }
    // Find the contour around the shapes
    contours = BinaryImageOps.contour(filtered, 8, null);
    rendered = VisualizeBinaryData.renderExternal(contours, null, binary.width, binary.height, null);

    result = new BoofCVImageContainer[1];
    result[0] = (BoofCVImageContainer) img.getHeader();
    result[0].setImage(ConvertBufferedImage.convertFromSingle(rendered, null, ImageUInt8.class));
    
    return result;
  }
}
