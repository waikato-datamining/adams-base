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
 * TimeseriesSlidingWindow.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.data.smoothing;

import adams.data.container.DataPoint;
import adams.data.timeseries.Timeseries;
import adams.data.timeseries.TimeseriesPoint;

/**
 <!-- globalinfo-start -->
 * Uses a sliding window for determining the median&#47;average inside the window. This measure is then used as new abundance for the time series point in the center of the window. The left and the right ends of the series are filled with dummy points to return a series with the same number of points.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-window &lt;int&gt; (property: windowSize)
 * &nbsp;&nbsp;&nbsp;The window size for determining the 'smoothed' abundances.
 * &nbsp;&nbsp;&nbsp;default: 20
 * </pre>
 * 
 * <pre>-measure &lt;MEDIAN|MEAN&gt; (property: measure)
 * &nbsp;&nbsp;&nbsp;The measure to use for calculating the 'smoothed' abundances.
 * &nbsp;&nbsp;&nbsp;default: MEDIAN
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TimeseriesSlidingWindow
  extends AbstractSlidingWindow<Timeseries> {

  /** for serialization. */
  private static final long serialVersionUID = 5542490162825298823L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Uses a sliding window for determining the median/average inside the window. "
      + "This measure is then used as new abundance for the time series point in the "
      + "center of the window. The left and the right ends of the series "
      + "are filled with dummy points to return a series with the same "
      + "number of points.";
  }

  /**
   * Returns the X-value of the data point.
   *
   * @param point	the point to get the X-value from
   * @return		the X-value
   */
  @Override
  protected Double getValue(DataPoint point) {
    return ((TimeseriesPoint) point).getValue();
  }

  /**
   * Updates the X-value of the data point.
   *
   * @param point	the point to update
   * @param value	the value to update the point with
   */
  @Override
  protected void updatePoint(DataPoint point, double value) {
    ((TimeseriesPoint) point).setValue(value);
  }
}
