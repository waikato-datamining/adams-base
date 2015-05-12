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
 * Derivative.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.filter;

import java.util.Date;

import adams.data.container.DataPoint;
import adams.data.timeseries.Timeseries;
import adams.data.timeseries.TimeseriesPoint;

/**
 <!-- globalinfo-start -->
 * A filter for generating derivatives of timeseries data.
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
 * <pre>-order &lt;int&gt; (property: order)
 * &nbsp;&nbsp;&nbsp;The order of the derivative to calculate.
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 * 
 * <pre>-scaling &lt;double&gt; (property: scalingRange)
 * &nbsp;&nbsp;&nbsp;The range to scale the abundances to after each derivation step; use 0 to 
 * &nbsp;&nbsp;&nbsp;turn off and -1 to set it to the input range.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TimeseriesDerivative
  extends AbstractDerivative<Timeseries> {

  /** for serialization. */
  private static final long serialVersionUID = 530300053103127948L;

  /**
   * Returns a string describing the object.
   *
   * @return 		a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "A filter for generating derivatives of timeseries data.";
  }

  /**
   * Turns the DataPoint into the intermediate format.
   *
   * @param point	the DataPoint to convert
   * @return		the generated intermediate format point
   */
  @Override
  protected Point toPoint(DataPoint point) {
    Point		result;
    TimeseriesPoint	sp;

    sp     = (TimeseriesPoint) point;
    result = new Point(sp.getTimestamp().getTime(), sp.getValue());

    return result;
  }

  /**
   * Turns the intermediate format point back into a DataPoint.
   *
   * @param point	the intermediate format point to convert
   * @return		the generated DataPoint
   */
  @Override
  protected DataPoint toDataPoint(Point point) {
    return new TimeseriesPoint(new Date((long) point.getX()), point.getY());
  }
}
