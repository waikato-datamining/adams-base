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
 * BoofCVDetectLineSegments.java
 * Copyright (C) 2014-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import adams.core.License;
import adams.core.annotation.MixedCopyright;
import adams.data.boofcv.BoofCVHelper;
import adams.data.boofcv.BoofCVImageType;
import adams.data.image.AbstractImageContainer;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.core.Token;
import boofcv.abst.feature.detect.line.DetectLineSegmentsGridRansac;
import boofcv.factory.feature.detect.line.FactoryDetectLineAlgs;
import boofcv.struct.image.ImageFloat32;
import georegression.struct.line.LineSegment2D_F32;

import java.util.List;

/**
 <!-- globalinfo-start -->
 * Detects line segments in images (line RANSAC).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;BoofCVImageContainer<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: BoofCVDetectLineSegments
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-region-size &lt;int&gt; (property: regionSize)
 * &nbsp;&nbsp;&nbsp;The size of the region.
 * &nbsp;&nbsp;&nbsp;default: 40
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-threshold-edge &lt;double&gt; (property: thresholdEdge)
 * &nbsp;&nbsp;&nbsp;The edge threshold to use.
 * &nbsp;&nbsp;&nbsp;default: 30.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 * 
 * <pre>-threshold-angle &lt;double&gt; (property: thresholdAngle)
 * &nbsp;&nbsp;&nbsp;The angle threshold in radians to use.
 * &nbsp;&nbsp;&nbsp;default: 2.36
 * </pre>
 * 
 * <pre>-connect-lines &lt;boolean&gt; (property: connectLines)
 * &nbsp;&nbsp;&nbsp;Whether lines should be connected and optimized.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7819 $
 */
@MixedCopyright(
    copyright = "2011-2012 Peter Abeles",
    license = License.APACHE2,
    note = "Example code taken from here http://boofcv.org/index.php?title=Example_Detect_Lines"
)
public class BoofCVDetectLineSegments
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 2784091483353536513L;

  /** region size. */
  protected int m_RegionSize;
  
  /** the edge threshold to use. */
  protected double m_ThresholdEdge;
  
  /** the angle threshold to use. */
  protected double m_ThresholdAngle;

  /** whether to connect the segments. */
  protected boolean m_ConnectLines;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Detects line segments in images (line RANSAC).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "region-size", "regionSize",
	    40, 0, null);

    m_OptionManager.add(
	    "threshold-edge", "thresholdEdge",
	    30.0, 0.0, null);

    m_OptionManager.add(
	    "threshold-angle", "thresholdAngle",
	    2.36);

    m_OptionManager.add(
	    "connect-lines", "connectLines",
	    false);
  }

  /**
   * Sets the Radius for local maximum suppression.
   *
   * @param value	the radius
   */
  public void setRegionSize(int value) {
    if (value >= 0) {
      m_RegionSize = value;
      reset();
    }
    else {
      getLogger().warning("Local max radius must be at least 0, provided: " + value);
    }
  }

  /**
   * Returns the Radius for local maximum suppression.
   *
   * @return		the radius
   */
  public int getRegionSize() {
    return m_RegionSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regionSizeTipText() {
    return "The size of the region.";
  }

  /**
   * Sets the edge threshold to use.
   *
   * @param value	the threshold
   */
  public void setThresholdEdge(double value) {
    if (value >= 0.0) {
      m_ThresholdEdge = value;
      reset();
    }
    else {
      getLogger().warning("Edge threshold must be at least 0, provided: " + value);
    }
  }

  /**
   * Returns the edge threshold to use.
   *
   * @return		the threshold
   */
  public double getThresholdEdge() {
    return m_ThresholdEdge;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String thresholdEdgeTipText() {
    return "The edge threshold to use.";
  }

  /**
   * Sets the angle threshold to use.
   *
   * @param value	the threshold
   */
  public void setThresholdAngle(double value) {
    m_ThresholdAngle = value;
    reset();
  }

  /**
   * Returns the angle threshold to use.
   *
   * @return		the threshold
   */
  public double getThresholdAngle() {
    return m_ThresholdAngle;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String thresholdAngleTipText() {
    return "The angle threshold in radians to use.";
  }

  /**
   * Sets whether to connect/optimize the segments.
   *
   * @param value	true if to connect/optimize
   */
  public void setConnectLines(boolean value) {
    m_ConnectLines = value;
    reset();
  }

  /**
   * Returns whether to connect/optimize the segments.
   *
   * @return		true if to connect/optimize
   */
  public boolean getConnectLines() {
    return m_ConnectLines;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String connectLinesTipText() {
    return "Whether lines should be connected and optimized.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  public Class[] accepts() {
    return new Class[]{AbstractImageContainer.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{SpreadSheet.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String				result;
    AbstractImageContainer	 		cont;
    ImageFloat32 			input;
    DetectLineSegmentsGridRansac 	detector;
    List<LineSegment2D_F32> 		found;
    SpreadSheet  			sheet;
    Row					row;
    
    result = null;
    
    try {
      cont     = (AbstractImageContainer) m_InputToken.getPayload();
      input    = (ImageFloat32) BoofCVHelper.toBoofCVImage(cont, BoofCVImageType.FLOAT_32);
      detector = FactoryDetectLineAlgs.lineRansac(m_RegionSize, m_ThresholdEdge, m_ThresholdAngle, m_ConnectLines, ImageFloat32.class, ImageFloat32.class);
      found    = detector.detect(input);
      
      sheet = new DefaultSpreadSheet();
      row   = sheet.getHeaderRow();
      row.addCell("I").setContent("Index");
      row.addCell("AX").setContent("A.X");
      row.addCell("AY").setContent("A.Y");
      row.addCell("BX").setContent("B.X");
      row.addCell("BY").setContent("B.Y");
      for (LineSegment2D_F32 line: found) {
	row = sheet.addRow();
	row.addCell("I").setContent(sheet.getRowCount());
	row.addCell("AX").setContent(line.a.x);
	row.addCell("AY").setContent(line.a.y);
	row.addCell("BX").setContent(line.b.x);
	row.addCell("BY").setContent(line.b.y);
      }
      m_OutputToken = new Token(sheet);
    }
    catch (Exception e) {
      result = handleException("Failed to detect line segments", e);
    }
    
    return result;
  }
}
