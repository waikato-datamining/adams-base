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
 * TimeseriesAutocorrelation.java
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.data.filter;

import adams.data.container.DataPoint;
import adams.data.timeseries.Timeseries;
import adams.data.timeseries.TimeseriesPoint;

import java.util.Date;

/**
 <!-- globalinfo-start -->
 * Performs autocorrelation on the timeseries.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-no-id-update &lt;boolean&gt; (property: dontUpdateID)
 * &nbsp;&nbsp;&nbsp;If enabled, suppresses updating the ID of adams.data.id.IDHandler data containers.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-algorithm &lt;adams.data.autocorrelation.AbstractAutoCorrelation&gt; (property: algorithm)
 * &nbsp;&nbsp;&nbsp;The autocorrelation algorithm to use.
 * &nbsp;&nbsp;&nbsp;default: adams.data.autocorrelation.BruteForce
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7305 $
 */
public class TimeseriesAutocorrelation
  extends AbstractAutocorrelation<Timeseries> {

  /** for serialization. */
  private static final long serialVersionUID = -6722542153654514316L;

  /**
   * Returns the X value of the DataPoint.
   *
   * @param point the point to get the X value from
   * @return the X value
   */
  protected double getX(DataPoint point) {
    return ((TimeseriesPoint) point).getTimestamp().getTime();
  }

  /**
   * Returns the Y value of the DataPoint.
   *
   * @param point the point to get the Y value from
   * @return the Y value
   */
  protected double getY(DataPoint point) {
    return ((TimeseriesPoint) point).getValue();
  }

  /**
   * Creates a new DataPoint from the X/Y data.
   *
   * @param x the X of the data point
   * @param y the Y of the data point
   * @return the new DataPoint
   */
  protected DataPoint newDataPoint(double x, double y) {
    return new TimeseriesPoint(new Date((long) x), y);
  }
}
