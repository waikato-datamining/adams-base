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
 * BoofCVDetectLines.java
 * Copyright (C) 2013-2017 University of Waikato, Hamilton, New Zealand
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
import boofcv.abst.feature.detect.line.DetectLineHoughPolar;
import boofcv.factory.feature.detect.line.ConfigHoughPolar;
import boofcv.factory.feature.detect.line.FactoryDetectLineAlgs;
import boofcv.struct.image.GrayS16;
import georegression.struct.line.LineParametric2D_F32;

import java.util.List;

/**
 <!-- globalinfo-start -->
 * Detects lines in images (Hough line detector based on polar parametrization).
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
 * Valid options are: <br><br>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: BoofCVDetectLines
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
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
 * <pre>-local-max-radius &lt;int&gt; (property: localMaxRadius)
 * &nbsp;&nbsp;&nbsp;The Radius for local maximum suppression.
 * &nbsp;&nbsp;&nbsp;default: 3
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-min-counts &lt;int&gt; (property: minCounts)
 * &nbsp;&nbsp;&nbsp;The Minimum number of counts for detected line.
 * &nbsp;&nbsp;&nbsp;default: 30
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-resolution-range &lt;double&gt; (property: resolutionRange)
 * &nbsp;&nbsp;&nbsp;The Resolution of line range in pixels.
 * &nbsp;&nbsp;&nbsp;default: 2.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 * 
 * <pre>-resolution-angle &lt;double&gt; (property: resolutionAngle)
 * &nbsp;&nbsp;&nbsp;The Resolution of line angle in radius.
 * &nbsp;&nbsp;&nbsp;default: 0.017453292519943295
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 * 
 * <pre>-edge-threshold &lt;float&gt; (property: edgeThreshold)
 * &nbsp;&nbsp;&nbsp;The edge threshold to use.
 * &nbsp;&nbsp;&nbsp;default: 25.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 * 
 * <pre>-max-lines &lt;int&gt; (property: maxLines)
 * &nbsp;&nbsp;&nbsp;The maximum number of lines to detect.
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@MixedCopyright(
    copyright = "2011-2012 Peter Abeles",
    license = License.APACHE2,
    note = "Example code taken from here http://boofcv.org/index.php?title=Example_Detect_Lines"
)
public class BoofCVDetectLines
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 2784091483353536513L;

  /** Radius for local maximum suppression. */
  protected int m_LocalMaxRadius;
  
  /** Minimum number of counts for detected line. */
  protected int m_MinCounts;
  
  /** Resolution of line range in pixels. */
  protected double m_ResolutionRange;
  
  /** Resolution of line angle in radius. */
  protected double m_ResolutionAngle;
  
  /** the edge threshold to use. */
  protected float m_EdgeThreshold;
  
  /** the maximum number of lines to detec. */
  protected int m_MaxLines;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Detects lines in images (Hough line detector based on polar parametrization).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "local-max-radius", "localMaxRadius",
	    3, 0, null);

    m_OptionManager.add(
	    "min-counts", "minCounts",
	    30, 1, null);

    m_OptionManager.add(
	    "resolution-range", "resolutionRange",
	    2.0, 0.0, null);

    m_OptionManager.add(
	    "resolution-angle", "resolutionAngle",
	    Math.PI / 180, 0.0, null);

    m_OptionManager.add(
	    "edge-threshold", "edgeThreshold",
	    25.0f, 0.0f, null);

    m_OptionManager.add(
	    "max-lines", "maxLines",
	    10, 1, null);
  }

  /**
   * Sets the Radius for local maximum suppression.
   *
   * @param value	the radius
   */
  public void setLocalMaxRadius(int value) {
    if (value >= 0) {
      m_LocalMaxRadius = value;
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
  public int getLocalMaxRadius() {
    return m_LocalMaxRadius;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String localMaxRadiusTipText() {
    return "The Radius for local maximum suppression.";
  }

  /**
   * Sets the Minimum number of counts for detected line.
   *
   * @param value	the minimum
   */
  public void setMinCounts(int value) {
    if (value >= 0) {
      m_MinCounts = value;
      reset();
    }
    else {
      getLogger().warning("Min counts must be at least 0, provided: " + value);
    }
  }

  /**
   * Returns the Minimum number of counts for detected line.
   *
   * @return		the minimum
   */
  public int getMinCounts() {
    return m_MinCounts;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String minCountsTipText() {
    return "The Minimum number of counts for detected line.";
  }

  /**
   * Sets the Resolution of line range in pixels.
   *
   * @param value	the range
   */
  public void setResolutionRange(double value) {
    if (value >= 0.0) {
      m_ResolutionRange = value;
      reset();
    }
    else {
      getLogger().warning("Edge threshold must be at least 0, provided: " + value);
    }
  }

  /**
   * Returns Resolution of line range in pixels.
   *
   * @return		the range
   */
  public double getResolutionRange() {
    return m_ResolutionRange;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String resolutionRangeTipText() {
    return "The Resolution of line range in pixels.";
  }

  /**
   * Sets the Resolution of line angle in radius.
   *
   * @param value	the angle
   */
  public void setResolutionAngle(double value) {
    if (value >= 0.0) {
      m_ResolutionAngle = value;
      reset();
    }
    else {
      getLogger().warning("Edge threshold must be at least 0, provided: " + value);
    }
  }

  /**
   * Returns the Resolution of line angle in radius.
   *
   * @return		the angle
   */
  public double getResolutionAngle() {
    return m_ResolutionAngle;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String resolutionAngleTipText() {
    return "The Resolution of line angle in radius.";
  }

  /**
   * Sets the edge threshold to use.
   *
   * @param value	the threshold
   */
  public void setEdgeThreshold(float value) {
    if (value >= 0.0f) {
      m_EdgeThreshold = value;
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
  public float getEdgeThreshold() {
    return m_EdgeThreshold;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String edgeThresholdTipText() {
    return "The edge threshold to use.";
  }

  /**
   * Sets the maximum number of lines to detect.
   *
   * @param value	the maximum
   */
  public void setMaxLines(int value) {
    m_MaxLines = value;
    reset();
  }

  /**
   * Returns the maximum number of lines to detect.
   *
   * @return		the maximum
   */
  public int getMaxLines() {
    return m_MaxLines;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxLinesTipText() {
    return "The maximum number of lines to detect.";
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
    String			result;
    AbstractImageContainer	 	cont;
    GrayS16 			input;
    ConfigHoughPolar		config;
    DetectLineHoughPolar 	detector;
    List<LineParametric2D_F32> 	found;
    SpreadSheet  		sheet;
    Row				row;
    
    result = null;
    
    try {
      cont   = (AbstractImageContainer) m_InputToken.getPayload();
      input  = (GrayS16) BoofCVHelper.toBoofCVImage(cont, BoofCVImageType.SIGNED_INT_16);
      config = new ConfigHoughPolar(
	  m_LocalMaxRadius, 
	  m_MinCounts, 
	  m_ResolutionRange, 
	  m_ResolutionAngle, 
	  m_EdgeThreshold, 
	  m_MaxLines);
      detector = FactoryDetectLineAlgs.houghPolar(
	  config, 
	  GrayS16.class,
	  GrayS16.class);
      found = detector.detect(input);
      
      sheet = new DefaultSpreadSheet();
      row   = sheet.getHeaderRow();
      row.addCell("I").setContent("Index");
      row.addCell("SX").setContent("Slope X");
      row.addCell("SY").setContent("Slope Y");
      row.addCell("X").setContent("X");
      row.addCell("Y").setContent("Y");
      row.addCell("A").setContent("Angle");
      for (LineParametric2D_F32 line: found) {
	row = sheet.addRow();
	row.addCell("I").setContent(sheet.getRowCount());
	row.addCell("SX").setContent(line.getSlopeX());
	row.addCell("SY").setContent(line.getSlopeY());
	row.addCell("X").setContent(line.getX());
	row.addCell("Y").setContent(line.getY());
	row.addCell("A").setContent(line.getAngle());
      }
      m_OutputToken = new Token(sheet);
    }
    catch (Exception e) {
      result = handleException("Failed to detect lines", e);
    }
    
    return result;
  }
}
