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
 * TimeseriesRound.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.filter;

import adams.data.RoundingType;
import adams.data.timeseries.Timeseries;
import adams.data.timeseries.TimeseriesPoint;

/**
 <!-- globalinfo-start -->
 * Rounds the values of the timeseries points.
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
 * <pre>-no-id-update &lt;boolean&gt; (property: dontUpdateID)
 * &nbsp;&nbsp;&nbsp;If enabled, suppresses updating the ID of adams.data.id.IDHandler data containers.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-type &lt;ROUND|CEILING|FLOOR&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of rounding to perform.
 * &nbsp;&nbsp;&nbsp;default: ROUND
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TimeseriesRound
  extends AbstractFilter<Timeseries> {

  /** for serialization. */
  private static final long serialVersionUID = 2616498525816421178L;
  
  /** the rounding type. */
  protected RoundingType m_Type;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Rounds the values of the timeseries points.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "type", "type",
	    RoundingType.ROUND);
  }

  /**
   * Sets the type of rounding to perform.
   *
   * @param value	the type
   */
  public void setType(RoundingType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of rounding to perform.
   *
   * @return		the type
   */
  public RoundingType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The type of rounding to perform.";
  }

  /**
   * Performs the actual filtering.
   *
   * @param data	the data to filter
   * @return		the filtered data
   */
  @Override
  protected Timeseries processData(Timeseries data) {
    Timeseries		result;
    TimeseriesPoint	point;
    int			i;
    double		value;

    result = data.getHeader();
    for (i = 0; i < data.size(); i++) {
      point = (TimeseriesPoint) data.toList().get(i);
      value = point.getValue();
      switch (m_Type) {
	case ROUND:
	  value = Math.round(value);
	  break;
	case CEILING:
	  value = Math.ceil(value);
	  break;
	case FLOOR:
	  value = Math.floor(value);
	  break;
	default:
	  throw new IllegalStateException("Unhandled rounding type: " + m_Type);
      }
      result.add(new TimeseriesPoint(point.getTimestamp(), value));
    }
    
    return result;
  }
}
