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
 * TimeseriesEquiDistance.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.data.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import adams.data.timeseries.Timeseries;
import adams.data.timeseries.TimeseriesPoint;
import adams.data.timeseries.TimeseriesPointComparator;

/**
 <!-- globalinfo-start -->
 * A filter for interpolating the values of a time series. One can either specify a fixed number of points or just use the same amount of points as currently in the input data.
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
 * <pre>-num-points &lt;int&gt; (property: numPoints)
 * &nbsp;&nbsp;&nbsp;The number of points to generate, '-1' will use the same amount of points 
 * &nbsp;&nbsp;&nbsp;as currently in the input data.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-allow-oversampling (property: allowOversampling)
 * &nbsp;&nbsp;&nbsp;If set to true, then over-sampling is allowed, ie, generating more data 
 * &nbsp;&nbsp;&nbsp;points than in the original data.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TimeseriesEquiDistance
  extends AbstractEquiDistance<Timeseries> {

  /** for serialization. */
  private static final long serialVersionUID = -17911247313401753L;

  /**
   * Returns a string describing the object.
   *
   * @return 		a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "A filter for interpolating the values of a time series. "
      + "One can either specify a fixed number of points or just use the "
      + "same amount of points as currently in the input data.";
  }

  /**
   * Returns a TimeseriesPoint with interpolated value.
   *
   * @param time	the time (msecs) we have to interpolate for
   * @param left	the "earlier" TimeseriesPoint
   * @param right	the "later" TimeseriesPoint
   * @return		the interpolated TimeseriesPoint
   */
  protected TimeseriesPoint interpolate(long time, TimeseriesPoint left, TimeseriesPoint right) {
    TimeseriesPoint	result;
    double		wavenodiff;
    double		percLeft;
    double		percRight;

    wavenodiff = right.getTimestamp().getTime() - left.getTimestamp().getTime();
    percLeft   = 1.0f - ((double) (time - left.getTimestamp().getTime()) / wavenodiff);
    percRight  = 1.0f - ((double) (right.getTimestamp().getTime() - time) / wavenodiff);
    result     = new TimeseriesPoint(
			new Date(time),
			      (double) left.getValue()*percLeft
			    + (double) right.getValue()*percRight);

    return result;
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
    int				actualPoints;
    double			averageSpacing;
    long			time;
    long			timeStart;
    double			value;
    int				i;
    List<TimeseriesPoint>	orderedData;
    int				index;
    TimeseriesPointComparator	comp;
    Timeseries			tmpData;
    ArrayList<Integer>		exact;
    TimeseriesPoint		newPoint;

    tmpData     = data;
    orderedData = tmpData.toList();
    result      = (Timeseries) tmpData.getHeader();

    // determine actual number of points to generate
    if (m_NumPoints == -1) {
      actualPoints = orderedData.size();
    }
    else {
      actualPoints = m_NumPoints;
      if (!m_AllowOversampling && (actualPoints > orderedData.size()))
	actualPoints = orderedData.size();
    }

    // the average spacing between points
    averageSpacing = orderedData.get(orderedData.size() - 1).getTimestamp().getTime() - orderedData.get(0).getTimestamp().getTime();
    averageSpacing /= (actualPoints - 1);

    // initialize output data
    result.add((TimeseriesPoint) orderedData.get(0).getClone());
    result.add((TimeseriesPoint) orderedData.get(orderedData.size() - 1).getClone());

    // interpolate data (excluding first/last)
    exact       = new ArrayList<Integer>();
    comp        = new TimeseriesPointComparator();
    timeStart = orderedData.get(0).getTimestamp().getTime();
    for (i = 1; i < actualPoints - 1; i++) {
      time = (long)((double) timeStart + (double) i * averageSpacing);
      index  = Collections.binarySearch(orderedData, new TimeseriesPoint(new Date(time), 0), comp);
      if (index >= 0) {
	exact.add(result.size());
	result.add((TimeseriesPoint) orderedData.get(index).getClone());  // gets post-processed
      }
      else {
	result.add(
	    interpolate(
		time,
		orderedData.get(-index - 2),
		orderedData.get(-index - 1)));
      }
    }

    // post-process exact hits, using interpolated points either side
    orderedData = result.toList();
    for (i = 0; i < exact.size(); i++) {
      index = exact.get(i);
      if (index < orderedData.size() - 1)
	newPoint = interpolate(
	    orderedData.get(index).getTimestamp().getTime(),
	    orderedData.get(index - 1),
	    orderedData.get(index + 1));
      else
	newPoint = interpolate(
	    orderedData.get(index).getTimestamp().getTime(),
	    orderedData.get(index - 1),
	    orderedData.get(index));
      value = (newPoint.getValue() + orderedData.get(index).getValue()) / 2;
      orderedData.get(index).setValue(value);
    }

    return result;
  }
}
