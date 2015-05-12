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
 * Range.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.outlier;

import adams.core.DateFormat;
import adams.core.DateUtils;
import adams.data.timeseries.Timeseries;
import adams.data.timeseries.TimeseriesPoint;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Ensures that timeseries values lie within the defined range.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-min &lt;double&gt; (property: min)
 * &nbsp;&nbsp;&nbsp;The minimum value to accept.
 * &nbsp;&nbsp;&nbsp;default: 4.9E-324
 * </pre>
 * 
 * <pre>-max &lt;double&gt; (property: max)
 * &nbsp;&nbsp;&nbsp;The maximum value accept.
 * &nbsp;&nbsp;&nbsp;default: 1.7976931348623157E308
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 995 $
 */
public class TimeseriesRange
  extends AbstractOutlierDetector<Timeseries> {

  /** for serialization. */
  private static final long serialVersionUID = -5300001549269138646L;

  /** the minimum acceptable value (incl). */
  protected double m_Min;

  /** the maximum acceptable value (incl). */
  protected double m_Max;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Ensures that timeseries values lie within the defined range.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "min", "min",
	    Double.MIN_VALUE);

    m_OptionManager.add(
	    "max", "max",
	    Double.MAX_VALUE);
  }

  /**
   * Sets the minimum value to accept.
   *
   * @param value	the minimum
   */
  public void setMin(double value) {
    m_Min = value;
    reset();
  }

  /**
   * Returns the minimum value accept.
   *
   * @return 		the minimum
   */
  public double getMin() {
    return m_Min;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String minTipText() {
    return "The minimum value to accept.";
  }

  /**
   * Sets the maximum value accept.
   *
   * @param value	the maximum
   */
  public void setMax(double value) {
    m_Max = value;
    reset();
  }

  /**
   * Returns the maximum value accept.
   *
   * @return 		the maximum
   */
  public double getMax() {
    return m_Max;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String maxTipText() {
    return "The maximum value accept.";
  }

  /**
   * The default implementation only checks whether there is any data set.
   *
   * @param data	the data to process
   */
  @Override
  protected void checkData(Timeseries data) {
    super.checkData(data);
    
    if (m_Min >= m_Max)
      throw new IllegalStateException(
	  "Maximum must be larger than minimum: " + m_Max + " > " + m_Min);
  }
  
  /**
   * Performs the actual detection.
   *
   * @param data	the data to process
   * @return		the detections
   */
  @Override
  protected List<String> processData(Timeseries data) {
    List<String>		result;
    String			msg;
    DateFormat			dformat;
    List<TimeseriesPoint>	points;

    result = new ArrayList<String>();
    
    dformat = DateUtils.getTimestampFormatter();
    msg     = null;
    points  = data.toList();
    for (TimeseriesPoint point: points) {
      if (point.getValue() < m_Min) {
	msg = "Value at " + dformat.format(point.getTimestamp()) + " is below " + m_Min + ": " + point.getValue();
	break;
      }
      if (point.getValue() > m_Max) {
	msg = "Value at " + dformat.format(point.getTimestamp()) + " is above " + m_Max + ": " + point.getValue();
	break;
      }
    }
    
    if (msg != null) {
      result.add(msg);
      if (isLoggingEnabled())
	getLogger().info(data.getDatabaseID() + " - " + getClass().getName() + ": " + msg);
    }

    return result;
  }
}
