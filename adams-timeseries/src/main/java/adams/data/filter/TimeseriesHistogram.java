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
 * Histogram.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.filter;

import java.util.Date;

import adams.data.container.DataPoint;
import adams.data.timeseries.Timeseries;
import adams.data.timeseries.TimeseriesPoint;

/**
 <!-- globalinfo-start -->
 * Generates a histogram from the timeseries.
 * <p/>
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
 * <pre>-histogram &lt;adams.data.statistics.ArrayHistogram&gt; (property: histogram)
 * &nbsp;&nbsp;&nbsp;The array histogram setup to use for generating the histogram data.
 * &nbsp;&nbsp;&nbsp;default: adams.data.statistics.ArrayHistogram
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 1286 $
 */
public class TimeseriesHistogram
  extends AbstractHistogram<Timeseries> {

  /** for serialization. */
  private static final long serialVersionUID = 1836858988505886282L;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a histogram from the timeseries.";
  }

  /**
   * Obtains the Y value from the given data point.
   * 
   * @param point	the data point to extract the Y value from
   * @return		the Y value
   */
  @Override
  protected double getY(DataPoint point) {
    return ((TimeseriesPoint) point).getValue();
  }

  /**
   * Creates a new data point from the X and Y values.
   * 
   * @param index	the index of the bin
   * @param y		the raw Y value
   * @return		the data point
   */
  @Override
  protected DataPoint newDataPoint(int index, double y) {
    return new TimeseriesPoint(new Date((long) ((index+1) * 1000)), y);
  }
}
