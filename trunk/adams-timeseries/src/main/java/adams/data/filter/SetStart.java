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
 * SetStart.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.filter;

import java.util.Date;

import adams.core.base.BaseDateTime;
import adams.data.timeseries.Timeseries;
import adams.data.timeseries.TimeseriesPoint;

/**
 <!-- globalinfo-start -->
 * Sets the starting point of the timeseries to a new fixed timestamp and all other data points accordingly.
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
 * <pre>-start &lt;adams.core.base.BaseDateTime&gt; (property: start)
 * &nbsp;&nbsp;&nbsp;The new timestamp for the first data point in series.
 * &nbsp;&nbsp;&nbsp;default: NOW
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SetStart
  extends AbstractFilter<Timeseries> {

  /** for serialization. */
  private static final long serialVersionUID = 2616498525816421178L;
  
  /** the new starting point. */
  protected BaseDateTime m_Start;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Sets the starting point of the timeseries to a new fixed timestamp "
	+ "and all other data points accordingly.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "start", "start",
	    new BaseDateTime(BaseDateTime.NOW));
  }

  /**
   * Sets the new start timestamp for series.
   *
   * @param value	the timestamp
   */
  public void setStart(BaseDateTime value) {
    m_Start = value;
    reset();
  }

  /**
   * The new start timestamp of series.
   *
   * @return		the timestamp
   */
  public BaseDateTime getStart() {
    return m_Start;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String startTipText() {
    return "The new timestamp for the first data point in series.";
  }

  /**
   * Performs the actual filtering.
   *
   * @param data	the data to filter
   * @return		the filtered data
   */
  @Override
  protected Timeseries processData(Timeseries data) {
    Timeseries		result;
    TimeseriesPoint	point;
    TimeseriesPoint	pointNew;
    int			i;
    long		diff;

    result = data.getHeader();
    diff   = 0;
    for (i = 0; i < data.size(); i++) {
      point = (TimeseriesPoint) data.toList().get(i);
      if (i == 0)
	diff = point.getTimestamp().getTime() - m_Start.dateValue().getTime();
      pointNew = new TimeseriesPoint(new Date(point.getTimestamp().getTime() - diff), point.getValue());
      result.add(pointNew);
    }
    
    return result;
  }
}
