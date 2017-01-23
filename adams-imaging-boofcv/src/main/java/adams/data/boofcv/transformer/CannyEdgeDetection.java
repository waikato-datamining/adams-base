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
 * CannyEdgeDetection.java
 * Copyright (C) 2014-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.data.boofcv.transformer;

import adams.core.License;
import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import adams.core.TechnicalInformationHandler;
import adams.core.annotation.MixedCopyright;
import adams.data.boofcv.BoofCVHelper;
import adams.data.boofcv.BoofCVImageContainer;
import adams.data.boofcv.BoofCVImageType;
import boofcv.alg.feature.detect.edge.CannyEdge;
import boofcv.alg.feature.detect.edge.EdgeContour;
import boofcv.alg.filter.binary.BinaryImageOps;
import boofcv.alg.filter.binary.Contour;
import boofcv.factory.feature.detect.edge.FactoryEdgeDetectors;
import boofcv.gui.binary.VisualizeBinaryData;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.ConnectRule;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.GrayS16;
import boofcv.struct.image.GrayU8;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Performs edge detection using the Canny Edge detection algorithm.<br>
 * For more information on the algorithm, see:<br>
 * WikiPedia. Canny edge detector.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * <pre>
 * &#64;misc{missing_id,
 *    author = {WikiPedia},
 *    title = {Canny edge detector},
 *    HTTP = {http:&#47;&#47;en.wikipedia.org&#47;wiki&#47;Canny_edge_detector}
 * }
 * </pre>
 * <br><br>
 <!-- technical-bibtex-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-type &lt;BINARY_EDGES|TRACE_GRAPH|CONTOUR&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of output to generate.
 * &nbsp;&nbsp;&nbsp;default: BINARY_EDGES
 * </pre>
 * 
 * <pre>-low-threshold &lt;float&gt; (property: lowThreshold)
 * &nbsp;&nbsp;&nbsp;The low threshold to use.
 * &nbsp;&nbsp;&nbsp;default: 0.1
 * </pre>
 * 
 * <pre>-connect-rule &lt;FOUR|EIGHT&gt; (property: connectRule)
 * &nbsp;&nbsp;&nbsp;The connect rule to apply.
 * &nbsp;&nbsp;&nbsp;default: EIGHT
 * </pre>
 * 
 * <pre>-high-threshold &lt;float&gt; (property: highThreshold)
 * &nbsp;&nbsp;&nbsp;The high threshold to use.
 * &nbsp;&nbsp;&nbsp;default: 0.3
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@MixedCopyright(
    author = "Peter Abeles",
    license = License.APACHE2,
    url = "http://boofcv.org/index.php?title=Example_Canny_Edge",
    note = "Example code taken from this URL"
)
public class CannyEdgeDetection
  extends AbstractBoofCVTransformer
  implements TechnicalInformationHandler {

  /** for serialization. */
  private static final long serialVersionUID = -465068613851000709L;
  
  /**
   * The type of output to generate.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum OutputType {
    BINARY_EDGES,
    TRACE_GRAPH,
    CONTOUR
  }
  
  /** the low threshold to use. */
  protected float m_LowThreshold;
  
  /** the high threshold to use. */
  protected float m_HighThreshold;

  /** the connect rule. */
  protected ConnectRule m_ConnectRule;

  /** the type of output to generate. */
  protected OutputType m_Type;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Performs edge detection using the Canny Edge detection algorithm.\n"
	+ "For more information on the algorithm, see:\n"
	+ getTechnicalInformation().toString();
  }

  /**
   * Returns an instance of a TechnicalInformation object, containing
   * detailed information about the technical background of this class,
   * e.g., paper reference or book this class is based on.
   *
   * @return 		the technical information about this class
   */
  public TechnicalInformation getTechnicalInformation() {
    TechnicalInformation 	result;

    result = new TechnicalInformation(Type.MISC);
    result.setValue(Field.AUTHOR, "WikiPedia");
    result.setValue(Field.TITLE, "Canny edge detector");
    result.setValue(Field.HTTP, "http://en.wikipedia.org/wiki/Canny_edge_detector");

    return result;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "type", "type",
      OutputType.BINARY_EDGES);

    m_OptionManager.add(
      "low-threshold", "lowThreshold",
      0.1f);

    m_OptionManager.add(
      "connect-rule", "connectRule",
      ConnectRule.EIGHT);

    m_OptionManager.add(
      "high-threshold", "highThreshold",
      0.3f);
  }

  /**
   * Sets the type of output to generate.
   *
   * @param value	the type
   */
  public void setType(OutputType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of output to generate.
   *
   * @return		the type
   */
  public OutputType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String typeTipText() {
    return "The type of output to generate.";
  }

  /**
   * Sets the low threshold to use.
   *
   * @param value	the low threshold to use
   */
  public void setLowThreshold(float value) {
    m_LowThreshold = value;
    reset();
  }

  /**
   * Returns the low threshold to use.
   *
   * @return		the low threshold in use
   */
  public float getLowThreshold() {
    return m_LowThreshold;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String lowThresholdTipText() {
    return "The low threshold to use.";
  }

  /**
   * Sets the high threshold to use.
   *
   * @param value	the high threshold to use
   */
  public void setHighThreshold(float value) {
    m_HighThreshold = value;
    reset();
  }

  /**
   * Returns the high threshold to use.
   *
   * @return		the high threshold in use
   */
  public float getHighThreshold() {
    return m_HighThreshold;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String highThresholdTipText() {
    return "The high threshold to use.";
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
   * Performs no transformation at all, just returns the input.
   *
   * @param img		the image to process (can be modified, since it is a copy)
   * @return		the copy of the image
   */
  @Override
  protected BoofCVImageContainer[] doTransform(BoofCVImageContainer img) {
    BoofCVImageContainer[]		result;
    GrayU8 				gray;
    GrayU8 				edgeImage;
    CannyEdge<GrayU8,GrayS16> 	canny;
    List<EdgeContour> 			edgeContours;
    List<Contour> 			contours;
    BufferedImage			rendered;
    
    gray      = (GrayU8) BoofCVHelper.toBoofCVImage(img.getImage(), BoofCVImageType.UNSIGNED_INT_8);
    edgeImage = new GrayU8(gray.width, gray.height);

    // Create a canny edge detector which will dynamically compute the threshold based on maximum edge intensity
    // It has also been configured to save the trace as a graph.  This is the graph created while performing
    // hysteresis thresholding.
    canny = FactoryEdgeDetectors.canny(2, true, true, GrayU8.class, GrayS16.class);

    // The edge image is actually an optional parameter.  If you don't need it just pass in null
    canny.process(gray, m_LowThreshold, m_HighThreshold, edgeImage);

    // First get the contour created by canny
    edgeContours = canny.getContours();
    
    // The 'edgeContours' is a tree graph that can be difficult to process.  An alternative is to extract
    // the contours from the binary image, which will produce a single loop for each connected cluster of pixels.
    // Note that you are only interested in external contours.
    contours = BinaryImageOps.contour(edgeImage, m_ConnectRule, null);

    // render the result
    switch (m_Type) {
      case BINARY_EDGES:
	rendered = VisualizeBinaryData.renderBinary(edgeImage, false, null);
	break;
      case CONTOUR:
	rendered = VisualizeBinaryData.renderContours(edgeContours, null, gray.width, gray.height, null);
	break;
      case TRACE_GRAPH:
        rendered = new BufferedImage(gray.width, gray.height,BufferedImage.TYPE_INT_RGB);
	VisualizeBinaryData.render(contours, (int[]) null, rendered);
	break;
      default:
	throw new IllegalStateException("Unhandled output type: " + m_Type);
    }

    result    = new BoofCVImageContainer[1];
    result[0] = (BoofCVImageContainer) img.getHeader();
    result[0].setImage(ConvertBufferedImage.convertFrom(rendered, (GrayF32) null));
    
    return result;
  }
}
