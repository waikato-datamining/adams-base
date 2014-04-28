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
 * TimeseriesStatistic.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.statistics;

import java.util.List;

import adams.data.timeseries.Timeseries;
import adams.data.timeseries.TimeseriesPoint;

/**
 * Statistical information specific to a Timeseries.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TimeseriesStatistic<T extends Timeseries>
  extends AbstractDataStatistic<T> {

  /** for serialization. */
  private static final long serialVersionUID = -2482267274581297567L;

  /**
   * Initializes the statistic.
   */
  public TimeseriesStatistic() {
    super();
  }

  /**
   * Initializes the statistic.
   *
   * @param data	the profile to generate the statistics for
   */
  public TimeseriesStatistic(T data) {
    super(data);
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Calculates a few statistics for a timeseries.";
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    super.cleanUp();
    m_Data = null;
  }

  /**
   * Sets the data to use as basis for the calculations.
   *
   * @param value	the profile to use, can be null
   */
  @Override
  public void setData(T value) {
    m_Calculated = false;
    m_Data       = value;
  }

  /**
   * Returns the currently stored profile.
   *
   * @return		the profile, can be null
   */
  @Override
  public T getData() {
    return m_Data;
  }

  /**
   * Returns a description for this statistic, i.e., database ID.
   *
   * @return		the description
   */
  public String getStatisticDescription() {
    return m_Data.getID() + " (" + m_Data.getDatabaseID() + ")";
  }

  /**
   * calculates the statistics.
   */
  @Override
  protected void calculate() {
    List<TimeseriesPoint>	points;
    int				i;
    Double[]			values;

    super.calculate();

    if (m_Data == null)
      return;

    points = m_Data.toList();
    values = new Double[0];

    // gather statistics
    if (points.size() > 0) {
      values = new Double[points.size()];
      for (i = 0; i < points.size(); i++)
	values[i] = points.get(i).getValue();
    }

    add("Database ID", m_Data.getDatabaseID());
    add("Number of points", points.size());
    add("min Value", (m_Data.getMinValue() != null) ? m_Data.getMinValue().getValue() : Double.NaN);
    add("max Value", (m_Data.getMaxValue() != null) ? m_Data.getMaxValue().getValue() : Double.NaN);
    add("mean Value", numberToDouble(StatUtils.mean(values)));
    add("stdev Value", numberToDouble(StatUtils.stddev(values, true)));
    add("median Value", numberToDouble(StatUtils.median(values)));

    values = null;
  }
}
