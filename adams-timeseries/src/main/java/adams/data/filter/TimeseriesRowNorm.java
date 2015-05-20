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
 * TimeseriesRowNorm.java
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.data.filter;

import adams.data.statistics.StatUtils;
import adams.data.timeseries.Timeseries;
import adams.data.timeseries.TimeseriesPoint;

import java.util.List;

/**
 <!-- globalinfo-start -->
 * Row wise normalization.
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
 <!-- options-end -->
 *
 * @author  dale (dale at waikato dot ac dot nz) -- original RowNorm
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 1286 $
 */
public class TimeseriesRowNorm
  extends AbstractFilter<Timeseries> {

  /** for serialization. */
  private static final long serialVersionUID = 1836858988505886282L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Row wise normalization.";
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
    double[]			x;
    double[]			norm;
    int				i;

    result = data.getHeader();
    points = data.toList();
    x      = new double[points.size()];
    for (i = 0; i < points.size(); i++)
      x[i] = points.get(i).getValue();
    norm   = StatUtils.rowNorm(x);
    for (i = 0; i < points.size(); i++)
      result.add(new TimeseriesPoint(points.get(i).getTimestamp(), norm[i]));

    return result;
  }
}
