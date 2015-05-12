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
 * TimeseriesMinPoints.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.outlier;

import adams.data.timeseries.Timeseries;
import adams.data.timeseries.TimeseriesPoint;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Ensures that a minimum number of points in the timeseries have the specified minimum value.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-num-points &lt;double&gt; (property: numPoints)
 * &nbsp;&nbsp;&nbsp;The minimum number of points that must have at least the minimum value; 
 * &nbsp;&nbsp;&nbsp;if less than 1 it is interpreted as percentage, otherwise as an absolute 
 * &nbsp;&nbsp;&nbsp;number.
 * &nbsp;&nbsp;&nbsp;default: 0.5
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 * 
 * <pre>-min-value &lt;double&gt; (property: minValue)
 * &nbsp;&nbsp;&nbsp;The minimum value that the data points must satisfy.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TimeseriesMinPoints
  extends AbstractOutlierDetector<Timeseries> {

  /** for serialization. */
  private static final long serialVersionUID = -3670792009048485874L;

  /** the number of points that need the minimum value (below 1 it is interpreted as percentage). */
  protected double m_NumPoints;
  
  /** the minimum value. */
  protected double m_MinValue;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Ensures that a minimum number of points in the timeseries have the specified minimum value.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "num-points", "numPoints",
	    0.50, 0.0, null);

    m_OptionManager.add(
	    "min-value", "minValue",
	    0.0, null, null);
  }

  /**
   * Sets the minimum number of points.
   *
   * @param value	the number of points
   */
  public void setNumPoints(double value) {
    if (value > 0) {
      m_NumPoints = value;
      reset();
    }
    else {
      getLogger().warning("numPoints must be >0, provided: " + value);
    }
  }

  /**
   * Returns the currently set minimum number of points.
   *
   * @return 		the minimum number of points
   */
  public double getNumPoints() {
    return m_NumPoints;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String numPointsTipText() {
    return 
	"The minimum number of points that must have at least the minimum "
	+ "value; if less than 1 it is interpreted as percentage, otherwise "
	+ "as an absolute number.";
  }

  /**
   * Sets the minimum value.
   *
   * @param value	the minimum
   */
  public void setMinValue(double value) {
    m_MinValue = value;
    reset();
  }

  /**
   * Returns the currently set minimum.
   *
   * @return 		the minimum
   */
  public double getMinValue() {
    return m_MinValue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String minValueTipText() {
    return "The minimum value that the data points must satisfy.";
  }

  /**
   * Performs the actual detection.
   *
   * @param data	the data to process
   * @return		the detections
   */
  @Override
  protected List<String> processData(Timeseries data) {
    List<String>	result;
    int			i;
    int			count;
    double		min;
    TimeseriesPoint	point;
    
    result = new ArrayList<String>();
    
    if (m_NumPoints < 1)
      min = (data.size() * m_NumPoints);
    else
      min = m_NumPoints;
    count = 0;
    for (i = 0; i < data.size(); i++) {
      point = (TimeseriesPoint) data.toList().get(i);
      if (point.getValue() >= m_MinValue)
	count++;
    }
    
    if (count < min)
      result.add("Not enough data points with at least " + m_MinValue + ": " + count + " < " + min);
    
    return result;
  }
}
