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
 * LOWESS.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.filter;

import java.awt.geom.Point2D;
import java.util.Date;

import adams.data.container.DataPoint;
import adams.data.timeseries.Timeseries;
import adams.data.timeseries.TimeseriesPoint;

/**
 <!-- globalinfo-start -->
 * A filter that applies LOWESS smoothing.<br/>
 * <br/>
 * For more information see:<br/>
 * <br/>
 * WikiPedia. Local Regression. URL http:&#47;&#47;en.wikipedia.org&#47;wiki&#47;Lowess.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * <pre>
 * &#64;misc{missing_id,
 *    author = {WikiPedia},
 *    title = {Local Regression},
 *    URL = {http:&#47;&#47;en.wikipedia.org&#47;wiki&#47;Lowess}
 * }
 * </pre>
 * <p/>
 <!-- technical-bibtex-end -->
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
 * <pre>-window-size &lt;int&gt; (property: windowSize)
 * &nbsp;&nbsp;&nbsp;The window size to use, must be at least 20.
 * &nbsp;&nbsp;&nbsp;default: 20
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LOWESS
  extends AbstractLOWESS<Timeseries> {

  /** for serialization. */
  private static final long serialVersionUID = -4083374493035684200L;

  /**
   * Returns the X/Y values of the DataPoint as Point2D.
   *
   * @param point	the point to get the X/Y values from
   * @return		the X/Y values as Point2D
   */
  @Override
  protected Point2D convert(DataPoint point) {
    TimeseriesPoint	tpoint;

    tpoint = (TimeseriesPoint) point;
    
    return new Point2D.Double(tpoint.getTimestamp().getTime(), tpoint.getValue());
  }

  /**
   * Creates a new DataPoint from the smoothed one.
   *
   * @param smoothed	the smoothed data point
   * @return		the new DataPoint
   */
  @Override
  protected DataPoint newDataPoint(Point2D smoothed) {
    return new TimeseriesPoint(new Date((long) smoothed.getX()), smoothed.getY());
  }
}
