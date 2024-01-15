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
 * CannyBinary.java
 * Copyright (C) 2014-2017 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.locateobjects;

import adams.core.License;
import adams.core.QuickInfoHelper;
import adams.core.annotation.MixedCopyright;
import boofcv.alg.feature.detect.edge.CannyEdge;
import boofcv.alg.filter.binary.BinaryImageOps;
import boofcv.alg.filter.binary.Contour;
import boofcv.factory.feature.detect.edge.FactoryEdgeDetectors;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.ConnectRule;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.GrayU8;
import georegression.struct.point.Point2D_I32;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Uses the BoofCV canny binary algorithm to locate objects.
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
    url = "https://github.com/lessthanoptimal/BoofCV/blob/v0.15/examples/src/boofcv/examples/ExampleFitPolygon.java",
    note = "Code taken from this BoofCV example"
)
public class CannyBinary
  extends AbstractObjectLocator {

  /** for serialization. */
  private static final long serialVersionUID = 9160763275489359825L;
  
  /** the blur radius to use. */
  protected int m_BlurRadius;
  
  /** the low threshold. */
  protected float m_ThresholdLow;
  
  /** the high threshold. */
  protected float m_ThresholdHigh;

  /** the connect rule. */
  protected ConnectRule m_ConnectRule;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses the BoofCV canny binary algorithm to locate objects.";
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

    m_OptionManager.add(
      "connect-rule", "connectRule",
      ConnectRule.EIGHT);
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
    
    result  = QuickInfoHelper.toString(this, "blurRadius", m_BlurRadius, "radius: ");
    result += QuickInfoHelper.toString(this, "thresholdLow", m_ThresholdLow, ", low: ");
    result += QuickInfoHelper.toString(this, "thresholdHigh", m_ThresholdHigh, ", high: ");
    result += QuickInfoHelper.toString(this, "connectRule", m_ConnectRule, ", rule: ");

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
    GrayF32 				input;
    GrayU8 					binary;
    CannyEdge<GrayF32,GrayF32> 	canny;
    List<Contour> 				contours;
    int						left;
    int						right;
    int						top;
    int						bottom;

    input  = ConvertBufferedImage.convertFromSingle(image, null, GrayF32.class);
    binary = new GrayU8(input.width, input.height);
    // Finds edges inside the image
    canny = FactoryEdgeDetectors.canny(m_BlurRadius, true, true, GrayF32.class, GrayF32.class);
    canny.process(input, m_ThresholdLow, m_ThresholdHigh, binary);
    contours = BinaryImageOps.contour(binary, m_ConnectRule, null);
    
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
