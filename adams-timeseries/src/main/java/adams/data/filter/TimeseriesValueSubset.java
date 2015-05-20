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
 * TimeseriesValueSubset.java
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.filter;

import adams.data.timeseries.Timeseries;
import adams.data.timeseries.TimeseriesPoint;

import java.util.List;

/**
 <!-- globalinfo-start -->
 * Generates a new timeseries with the first block of values that fits between the specified min&#47;max (both included).
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
 * <pre>-minimum &lt;double&gt; (property: minimum)
 * &nbsp;&nbsp;&nbsp;The minimum (included) that the values must satisfy.
 * &nbsp;&nbsp;&nbsp;default: 4.9E-324
 * </pre>
 * 
 * <pre>-maximum &lt;double&gt; (property: maximum)
 * &nbsp;&nbsp;&nbsp;The maximum (included) that the values must satisfy.
 * &nbsp;&nbsp;&nbsp;default: 1.7976931348623157E308
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7881 $
 */
public class TimeseriesValueSubset
  extends AbstractFilter<Timeseries> {

  /** for serialization. */
  private static final long serialVersionUID = 2616498525816421178L;
  
  /** the minimum. */
  protected double m_Minimum;

  /** the maximum. */
  protected double m_Maximum;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Generates a new timeseries with the first block of values that "
	+ "fits between the specified min/max (both included).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "minimum", "minimum",
	    Double.MIN_VALUE);

    m_OptionManager.add(
	    "maximum", "maximum",
	    Double.MAX_VALUE);
  }

  /**
   * Sets the minimum (included) that the values need to satisfy.
   *
   * @param value	the minimum
   */
  public void setMinimum(double value) {
    m_Minimum = value;
    reset();
  }

  /**
   * Returns the minimum (included) that the values need to satisfy.
   *
   * @return		the minimum
   */
  public double getMinimum() {
    return m_Minimum;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String minimumTipText() {
    return "The minimum (included) that the values must satisfy.";
  }

  /**
   * Sets the maximum (included) that the values need to satisfy.
   *
   * @param value	the maximum
   */
  public void setMaximum(double value) {
    m_Maximum = value;
    reset();
  }

  /**
   * Returns the maximum (included) that the values need to satisfy.
   *
   * @return		the maximum
   */
  public double getMaximum() {
    return m_Maximum;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maximumTipText() {
    return "The maximum (included) that the values must satisfy.";
  }

  /**
   * Ensures that min/max are sensible.
   *
   * @param data	the data to filter
   */
  @Override
  protected void checkData(Timeseries data) {
    super.checkData(data);
    
    if (m_Minimum >= m_Maximum)
      throw new IllegalStateException("Minimum must be smaller than maximum!");
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
    TimeseriesPoint		point;
    boolean			adding;
    int				i;

    result = data.getHeader();
    points = (List<TimeseriesPoint>) data.toList();
    adding = false;

    for (i = 0; i < points.size(); i++) {
      point = points.get(i);
      if ((point.getValue() >= m_Minimum) && (point.getValue() <= m_Maximum)) {
	adding = true;
      }
      else {
	if (adding)
	  break;
	continue;
      }
      result.add((TimeseriesPoint) point.getClone());
    }
    
    return result;
  }
}
