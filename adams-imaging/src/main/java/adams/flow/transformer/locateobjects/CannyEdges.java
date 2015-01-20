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
 * CannyEdges.java
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.locateobjects;

import adams.core.License;
import adams.core.QuickInfoHelper;
import adams.core.annotation.MixedCopyright;
import boofcv.alg.feature.detect.edge.CannyEdge;
import boofcv.alg.feature.detect.edge.EdgeContour;
import boofcv.alg.feature.detect.edge.EdgeSegment;
import boofcv.core.image.ConvertBufferedImage;
import boofcv.factory.feature.detect.edge.FactoryEdgeDetectors;
import boofcv.struct.image.ImageFloat32;
import georegression.struct.point.Point2D_I32;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Uses the BoofCV canny edges algorithm to locate objects.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-blur-radius &lt;int&gt; (property: blurRadius)
 * &nbsp;&nbsp;&nbsp;The blur radius.
 * &nbsp;&nbsp;&nbsp;default: 2
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-threshold-low &lt;float&gt; (property: thresholdLow)
 * &nbsp;&nbsp;&nbsp;The low threshold.
 * &nbsp;&nbsp;&nbsp;default: 0.1
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 * 
 * <pre>-threshold-high &lt;float&gt; (property: thresholdHigh)
 * &nbsp;&nbsp;&nbsp;The high threshold.
 * &nbsp;&nbsp;&nbsp;default: 0.3
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
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
    url = "https://github.com/lessthanoptimal/BoofCV/blob/v0.15/examples/src/boofcv/examples/ExampleFitPolygon.java",
    note = "Code taken from this BoofCV example"
)
public class CannyEdges
  extends AbstractObjectLocator {

  /** for serialization. */
  private static final long serialVersionUID = 9160763275489359825L;
  
  /** the blur radius to use. */
  protected int m_BlurRadius;
  
  /** the low threshold. */
  protected float m_ThresholdLow;
  
  /** the high threshold. */
  protected float m_ThresholdHigh;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses the BoofCV canny edges algorithm to locate objects.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "blur-radius", "blurRadius",
	    2, 0, null);

    m_OptionManager.add(
	    "threshold-low", "thresholdLow",
	    0.1f, 0.0f, null);

    m_OptionManager.add(
	    "threshold-high", "thresholdHigh",
	    0.3f, 0.0f, null);
  }

  /**
   * Sets the blur radius.
   *
   * @param value	the radius
   */
  public void setBlurRadius(int value) {
    if (value >= 0) {
      m_BlurRadius = value;
      reset();
    }
    else {
      getLogger().warning("Blur radius must be at least 0, provided: " + value);
    }
  }

  /**
   * Returns the blur radius.
   *
   * @return		the radius
   */
  public int getBlurRadius() {
    return m_BlurRadius;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String blurRadiusTipText() {
    return "The blur radius.";
  }

  /**
   * Sets the low threshold.
   *
   * @param value	the threshold
   */
  public void setThresholdLow(float value) {
    if (value >= 0) {
      m_ThresholdLow = value;
      reset();
    }
    else {
      getLogger().warning("Low threshold must be at least 0, provided: " + value);
    }
  }

  /**
   * Returns the low threshold.
   *
   * @return		the threshold
   */
  public float getThresholdLow() {
    return m_ThresholdLow;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String thresholdLowTipText() {
    return "The low threshold.";
  }

  /**
   * Sets the high threshold.
   *
   * @param value	the threshold
   */
  public void setThresholdHigh(float value) {
    if (value >= 0) {
      m_ThresholdHigh = value;
      reset();
    }
    else {
      getLogger().warning("High threshold must be at least 0, provided: " + value);
    }
  }

  /**
   * Returns the high threshold.
   *
   * @return		the threshold
   */
  public float getThresholdHigh() {
    return m_ThresholdHigh;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String thresholdHighTipText() {
    return "The high threshold.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "blurRadius", m_BlurRadius, "radius: ");
    result += QuickInfoHelper.toString(this, "thresholdLow", m_ThresholdLow, ", low: ");
    result += QuickInfoHelper.toString(this, "thresholdHigh", m_ThresholdHigh, ", high: ");
    
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
    LocatedObjects				result;
    ImageFloat32 				input;
    CannyEdge<ImageFloat32,ImageFloat32> 	canny;
    List<EdgeContour> 				contours;
    int						left;
    int						right;
    int						top;
    int						bottom;

    input  = ConvertBufferedImage.convertFromSingle(image, null, ImageFloat32.class);
    // Finds edges inside the image
    canny = FactoryEdgeDetectors.canny(m_BlurRadius, true, true, ImageFloat32.class, ImageFloat32.class);
    canny.process(input, m_ThresholdLow, m_ThresholdHigh, null);
    contours = canny.getContours();
    
    result = new LocatedObjects();
    for (EdgeContour contour: contours) {
      // determine largest rectangle for contour
      left   = image.getWidth();
      right  = 0;
      top    = image.getHeight();
      bottom = 0;
      for (EdgeSegment seg: contour.segments) {
	for (Point2D_I32 p: seg.points) {
	  if (left > p.getX())
	    left = p.getX();
	  if (right < p.getX())
	    right = p.getX();
	  if (top > p.getY())
	    top = p.getY();
	  if (bottom < p.getY())
	    bottom = p.getY();
	}
      }
      result.add(
	  new LocatedObject(
	      annotateOnly ? null : image.getSubimage(left, top, right - left + 1, bottom - top + 1),
	      left, top, right - left + 1, bottom - top + 1));
    }
    
    return result;
  }
}
