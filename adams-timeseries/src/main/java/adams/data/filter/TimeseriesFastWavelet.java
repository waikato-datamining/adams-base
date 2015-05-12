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
 * FastWavelet.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.filter;

import java.util.Date;
import java.util.List;

import adams.data.container.DataPoint;
import adams.data.timeseries.Timeseries;
import adams.data.timeseries.TimeseriesPoint;

/**
 <!-- globalinfo-start -->
 * A filter that transforms the data with a wavelet.<br/>
 * <br/>
 * For more information see:<br/>
 * <br/>
 *  (2009). JSci - A science API for Java.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * BibTeX:
 * <pre>
 * &#64;misc{missing_id,
 *    title = {JSci - A science API for Java},
 *    year = {2009},
 *    HTTP = {http:&#47;&#47;jsci.sourceforge.net&#47;}
 * }
 * </pre>
 * <p/>
 <!-- technical-bibtex-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-wavelet &lt;Haar|CDF2 4|Daubechies2|Symmlet8&gt; (property: waveletType)
 * &nbsp;&nbsp;&nbsp;The wavelet type to use for transforming the data.
 * &nbsp;&nbsp;&nbsp;default: HAAR
 * </pre>
 * 
 * <pre>-padding &lt;Zero&gt; (property: paddingType)
 * &nbsp;&nbsp;&nbsp;The padding type to use.
 * &nbsp;&nbsp;&nbsp;default: ZERO
 * </pre>
 * 
 * <pre>-inverse (property: inverseTransform)
 * &nbsp;&nbsp;&nbsp;If true, then the inverse transform is performed (wavelet -&gt; normal space
 * &nbsp;&nbsp;&nbsp;).
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TimeseriesFastWavelet
  extends AbstractFastWavelet<Timeseries> {

  /** for serialization. */
  private static final long serialVersionUID = -5581114911009545192L;

  /**
   * Returns the X-value of the DataPoint.
   *
   * @param point	the point to get the X-Value from
   * @return		the X-value
   */
  @Override
  protected double getValue(DataPoint point) {
    return ((TimeseriesPoint) point).getValue();
  }

  /**
   * Creates a new DataPoint based on the old one and the new X value.
   *
   * @param oldPoint	the old DataPoint
   * @param x		the new X value
   * @return		the new DataPoint
   */
  @Override
  protected DataPoint newDataPoint(DataPoint oldPoint, double x) {
    return new TimeseriesPoint(((TimeseriesPoint) oldPoint).getTimestamp(), x);
  }

  /**
   * Creates a new DataPoint based on the index and the new X value. Used for
   * padded points.
   *
   * @param points	the original points
   * @param index	the index of the padded point
   * @param x		the new X value
   * @return		the new DataPoint
   */
  @Override
  protected DataPoint newDataPoint(List<DataPoint> points, int index, double x) {
    TimeseriesPoint	last;
    TimeseriesPoint	last2;
    
    last  = (TimeseriesPoint) points.get(points.size() - 1);
    last2 = (TimeseriesPoint) points.get(points.size() - 2);

    return new TimeseriesPoint(
	new Date(
	    last.getTimestamp().getTime() 
	    + (last.getTimestamp().getTime() - last2.getTimestamp().getTime()) * (index + 1)), 
	x);
  }
}
