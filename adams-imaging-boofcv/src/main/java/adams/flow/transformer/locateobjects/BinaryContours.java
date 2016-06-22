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
 * Copyright (C) 2014-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.locateobjects;

import adams.core.License;
import adams.core.QuickInfoHelper;
import adams.core.annotation.MixedCopyright;
import boofcv.alg.filter.binary.BinaryImageOps;
import boofcv.alg.filter.binary.Contour;
import boofcv.alg.filter.binary.ThresholdImageOps;
import boofcv.alg.misc.ImageStatistics;
import boofcv.core.image.ConvertBufferedImage;
import boofcv.struct.ConnectRule;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageUInt8;
import georegression.struct.point.Point2D_I32;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Uses the BoofCV contour-finding algorithm to locate objects.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-center-on-canvas &lt;boolean&gt; (property: centerOnCanvas)
 * &nbsp;&nbsp;&nbsp;If enabled, the located objects get centered on a canvas of fixed size.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-canvas-width &lt;int&gt; (property: canvasWidth)
 * &nbsp;&nbsp;&nbsp;The width of the canvas in pixels.
 * &nbsp;&nbsp;&nbsp;default: 100
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-canvas-height &lt;int&gt; (property: canvasHeight)
 * &nbsp;&nbsp;&nbsp;The height of the canvas in pixels.
 * &nbsp;&nbsp;&nbsp;default: 100
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-canvas-color &lt;java.awt.Color&gt; (property: canvasColor)
 * &nbsp;&nbsp;&nbsp;The color to use for filling the canvas.
 * &nbsp;&nbsp;&nbsp;default: #ffffff
 * </pre>
 * 
 * <pre>-remove-small-blobs &lt;boolean&gt; (property: removeSmallBlobs)
 * &nbsp;&nbsp;&nbsp;If enabled, small blobs are removed using erode8&#47;dilate8.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-connect-rule &lt;FOUR|EIGHT&gt; (property: connectRule)
 * &nbsp;&nbsp;&nbsp;The connect rule to apply.
 * &nbsp;&nbsp;&nbsp;default: EIGHT
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
  extends AbstractObjectLocator {

  /** for serialization. */
  private static final long serialVersionUID = 9160763275489359825L;
  
  /** whether to remove small blobs. */
  protected boolean m_RemoveSmallBlobs;

  /** the connect rule. */
  protected ConnectRule m_ConnectRule;

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
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "remove-small-blobs", "removeSmallBlobs",
      false);

    m_OptionManager.add(
      "connect-rule", "connectRule",
      ConnectRule.EIGHT);
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
   * Sets the connect rule to apply.
   *
   * @param value	the rule
   */
  public void setConnectRule(ConnectRule value) {
    m_ConnectRule = value;
    reset();
  }

  /**
   * Returns the connect rule to apply.
   *
   * @return		the rule
   */
  public ConnectRule getConnectRule() {
    return m_ConnectRule;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String connectRuleTipText() {
    return "The connect rule to apply.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	value;

    result = QuickInfoHelper.toString(this, "connectRule", m_ConnectRule, "rule: ");
    value = QuickInfoHelper.toString(this, "removeSmallBlobs", m_RemoveSmallBlobs, "remove small blobs", ", ");
    if (value != null)
      result += value;

    return result;
  }

  /**
   * Returns the input image as output.
   * 
   * @param image	  the image to process
   * @param annotateOnly  whether to annotate only
   * @return		  the containers of located objects
   */
  protected LocatedObjects doLocate(BufferedImage image, boolean annotateOnly) {
    LocatedObjects	result;
    ImageFloat32 	input;
    ImageUInt8 		binary;
    ImageUInt8 		filtered;
    List<Contour> 	contours;
    int			left;
    int			right;
    int			top;
    int			bottom;
    double 		mean;

    input  = ConvertBufferedImage.convertFromSingle(image, null, ImageFloat32.class);
    binary = new ImageUInt8(input.width, input.height);
    // the mean pixel value is often a reasonable threshold when creating a binary image
    mean = ImageStatistics.mean(input);
    // create a binary image by thresholding
    ThresholdImageOps.threshold(input, binary, (float) mean, true);
    // reduce noise with some filtering?
    if (m_RemoveSmallBlobs) {
      filtered = BinaryImageOps.erode8(binary, 1, null);
      filtered = BinaryImageOps.dilate8(filtered, 1, null);
    }
    else {
      filtered = binary;
    }
    // Find the contour around the shapes
    contours = BinaryImageOps.contour(filtered, m_ConnectRule, null);
    
    result = new LocatedObjects();
    for (Contour contour: contours) {
      // determine largest rectangle for contour
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
	      annotateOnly ? null : image.getSubimage(left, top, right - left + 1, bottom - top + 1),
	      left, top, right - left + 1, bottom - top + 1));
    }
    
    return result;
  }
}
