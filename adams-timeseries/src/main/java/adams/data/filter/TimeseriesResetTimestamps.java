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
 * ResetTimestamps.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.data.filter;

import adams.core.base.BaseDateTime;
import adams.data.timeseries.Timeseries;
import adams.data.timeseries.TimeseriesPoint;

import java.util.Date;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Resets the timestamps, makes them start at the specified date&#47;time. The relative time differences between the data points are kept.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-start &lt;adams.core.base.BaseDateTime&gt; (property: start)
 * &nbsp;&nbsp;&nbsp;The start date&#47;time to reset the timestamps to.
 * &nbsp;&nbsp;&nbsp;default: -INF
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 958 $
 */
public class TimeseriesResetTimestamps
  extends AbstractFilter<Timeseries> {

  /** for serialization. */
  private static final long serialVersionUID = -1306518673446335794L;

  /** the new start date. */
  protected BaseDateTime m_Start;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Resets the timestamps, makes them start at the specified date/time. "
      + "The relative time differences between the data points are kept.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"start", "start",
	new BaseDateTime(BaseDateTime.INF_PAST));
  }

  /**
   * Sets the start date.
   *
   * @param value 	the date
   */
  public void setStart(BaseDateTime value) {
    m_Start = value;
    reset();
  }

  /**
   * Returns the start date.
   *
   * @return 		the date
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
    return "The start date/time to reset the timestamps to.";
  }

  /**
   * Performs the actual filtering.
   *
   * @param data	the data to filter
   * @return		the filtered data
   */
  @Override
  protected Timeseries processData(Timeseries data) {
    Timeseries			result;
    List<TimeseriesPoint>	points;
    long			newStart;
    long			oldStart;
    long			diff;

    result   = data.getHeader();
    points   = data.toList();

    if (points.size() == 0)
      return result;

    newStart = m_Start.dateValue().getTime();
    oldStart = points.get(0).getTimestamp().getTime();
    diff     = oldStart - newStart;

    for (TimeseriesPoint point: points) {
      result.add(
	  new TimeseriesPoint(
	      new Date(point.getTimestamp().getTime() - diff),
	      point.getValue()));
    }

    return result;
  }
}
