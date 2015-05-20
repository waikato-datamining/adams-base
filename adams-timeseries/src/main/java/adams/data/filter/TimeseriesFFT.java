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
 * TimeseriesFFT.java
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.data.filter;

import adams.data.container.DataPoint;
import adams.data.timeseries.Timeseries;
import adams.data.timeseries.TimeseriesPoint;

import java.util.Date;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * A filter that transforms the data with Fast Fourier Transform.<br>
 * <br>
 * For more information see:<br>
 * <br>
 * Mark Hale (2009). JSci - A science API for Java.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * <pre>
 * &#64;misc{Hale2009,
 *    author = {Mark Hale},
 *    title = {JSci - A science API for Java},
 *    year = {2009},
 *    HTTP = {http:&#47;&#47;jsci.sourceforge.net&#47;}
 * }
 * </pre>
 * <br><br>
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
 * <pre>-padding &lt;ZERO&gt; (property: paddingType)
 * &nbsp;&nbsp;&nbsp;The padding type to use.
 * &nbsp;&nbsp;&nbsp;default: ZERO
 * </pre>
 * 
 * <pre>-inverse &lt;boolean&gt; (property: inverseTransform)
 * &nbsp;&nbsp;&nbsp;If true, then the inverse transform is performed.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-real &lt;boolean&gt; (property: real)
 * &nbsp;&nbsp;&nbsp;If enabled, the real part of the tranformation is returned.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7305 $
 */
public class TimeseriesFFT
  extends AbstractFFT<Timeseries> {

  /** for serialization. */
  private static final long serialVersionUID = -6722542153654514316L;

  /**
   * Returns the Y-value of the DataPoint.
   *
   * @param point	the point to get the Y-Value from
   * @return		the Y-value
   */
  @Override
  protected double getValue(DataPoint point) {
    return ((TimeseriesPoint) point).getValue();
  }

  /**
   * Creates a new DataPoint based on the index and the new Y value. Used for
   * padded points.
   *
   * @param points	the original points
   * @param index	the index of the padded point in the output data
   * @param y		the new Y value
   * @return		the new DataPoint
   */
  @Override
  protected DataPoint newDataPoint(List<DataPoint> points, int index, double y) {
    return new TimeseriesPoint(new Date(index * 1000), y);
  }
}
