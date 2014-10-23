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
 * ChangeResolution.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.filter;

import java.util.Date;
import java.util.List;

import adams.data.timeseries.Timeseries;
import adams.data.timeseries.TimeseriesPoint;
import adams.data.timeseries.TimeseriesUtils;

/**
 <!-- globalinfo-start -->
 * Generates a new timeseries with a (user-defined) fixed-length interval between data points.
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
 * <pre>-interval &lt;double&gt; (property: interval)
 * &nbsp;&nbsp;&nbsp;The new, fixed-length interval between data points in seconds.
 * &nbsp;&nbsp;&nbsp;default: 30.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.01
 * </pre>
 * 
 * <pre>-polynomial &lt;int&gt; (property: polynomial)
 * &nbsp;&nbsp;&nbsp;The polynomial for interpolation.
 * &nbsp;&nbsp;&nbsp;default: 2
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7881 $
 */
public class ChangeResolution
  extends AbstractFilter<Timeseries> {

  /** for serialization. */
  private static final long serialVersionUID = 2616498525816421178L;
  
  /** the new interval in seconds. */
  protected double m_Interval;

  protected int m_Polynomial;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Generates a new timeseries with a (user-defined) fixed-length "
	+ "interval between data points.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "interval", "interval",
	    30.0, 10e-3, null);

    m_OptionManager.add(
	    "polynomial", "polynomial",
	    2, 1, null);
  }

  /**
   * Sets the interval (in seconds).
   *
   * @param value	the interval
   */
  public void setInterval(double value) {
    if (value > 0) {
      m_Interval = value;
      reset();
    }
    else {
      getLogger().warning("Interval must be > 0, provided: " + value);
    }
  }

  /**
   * Returns the interval (in seconds).
   *
   * @return		the interval
   */
  public double getInterval() {
    return m_Interval;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String intervalTipText() {
    return "The new, fixed-length interval between data points in seconds.";
  }

  /**
   * Sets the polynomial for interpolation.
   *
   * @param value	the polynomial
   */
  public void setPolynomial(int value) {
    m_Polynomial= value;
    reset();
  }

  /**
   * Returns the polynomial for interpolation.
   *
   * @return		the polynomial
   */
  public int getPolynomial() {
    return m_Polynomial;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String polynomialTipText() {
    return "The polynomial for interpolation.";
  }

  /**
   * Determines the closest points for the given timestamp.
   * 
   * @param timestamp	the timestamp
   * @param data	the data points
   * @param numpoints	the number of points to find
   * @return		the list of closest points
   */
  protected List<TimeseriesPoint> getClosestPoints(long timestamp,List<TimeseriesPoint> data, int numpoints) {
    Timeseries 	result;
    int 	found;
    int 	pos;
    int 	foundmin;
    int		foundmax;
    double 	minusposdiff;
    double 	plusposdiff;
    
    result = new Timeseries();
    found  = 0;
    pos    = TimeseriesUtils.findClosestTimestamp(data, new Date(timestamp));
    result.add((TimeseriesPoint)data.get(pos).getClone());
    foundmin = pos;
    foundmax = pos;
    found++;

    while (found < numpoints) {
      minusposdiff = Double.MAX_VALUE;
      plusposdiff  = Double.MAX_VALUE;
      
      if (foundmin -1 > 0)
	minusposdiff=Math.abs(timestamp-data.get(foundmin-1).getTimestamp().getTime());
      
      if ( foundmax+1 < data.size())
	plusposdiff=Math.abs(timestamp-data.get(foundmax+1).getTimestamp().getTime());
      
      if (minusposdiff < plusposdiff){
	result.add((TimeseriesPoint)data.get(foundmin-1).getClone());
	foundmin--;
      } 
      else {
	result.add((TimeseriesPoint)data.get(foundmax+1).getClone());
	foundmax++;
      }
      
      found++;
    }

    return(result.toList());
  }

  protected double L(double timestamp, List<TimeseriesPoint> closest, int m) {
    double 	num;
    double 	den;
    int		k;
    
    num = 1;
    den = 1;
    
    for (k = 0; k < closest.size(); k++) {
      if (k == m)
	continue;
      num *= timestamp - (double) closest.get(k).getTimestamp().getTime();
    }
    
    for (k = 0; k < closest.size(); k++) {
      if (k == m)
	continue;
      den *= (double) closest.get(m).getTimestamp().getTime() - (double) closest.get(k).getTimestamp().getTime();
    }
    
    return num / den;
  }
  
  /**
   * Interpolates the value.
   * 
   * @param timestamp	the timestamp
   * @param closest	the closest points
   * @param poly	the polynomial
   * @return		the interpolated value
   */
  protected double interpolate(double timestamp, List<TimeseriesPoint> closest, int poly) {
    double 	result;
    int		i;
    
    result = 0.0;
    for (i = 0; i <= poly; i++)
      result += L(timestamp, closest, i) * closest.get(i).getValue();
    
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
    List<TimeseriesPoint>	list;
    long			timestamp;
    long			last;
    long			step;
    List<TimeseriesPoint> 	closest;
    double 			value;

    result    = data.getHeader();
    list      = data.toList();
    step      = Math.round(m_Interval * 1000);
    timestamp = list.get(0).getTimestamp().getTime();
    last      = list.get(list.size() - 1).getTimestamp().getTime();

    while (timestamp < last) {
      closest = getClosestPoints(timestamp, list, m_Polynomial + 1);
      value   = interpolate(timestamp, closest, closest.size() - 1);
      result.add(new TimeseriesPoint(new Date(timestamp), value));
      timestamp += step;
    }

    return result;
  }
}
