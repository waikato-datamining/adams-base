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
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
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

  public static final String DATABASE_ID = "Database ID";
  public static final String NUMBER_OF_POINTS = "Number of points";
  public static final String MIN_VALUE = "min Value";
  public static final String MAX_VALUE = "max Value";
  public static final String MEAN_VALUE = "mean Value";
  public static final String STDEV_VALUE = "stdev Value";
  public static final String MEDIAN_VALUE = "median Value";

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

    add(DATABASE_ID, m_Data.getDatabaseID());
    add(NUMBER_OF_POINTS, points.size());
    add(MIN_VALUE, (m_Data.getMinValue() != null) ? m_Data.getMinValue().getValue() : Double.NaN);
    add(MAX_VALUE, (m_Data.getMaxValue() != null) ? m_Data.getMaxValue().getValue() : Double.NaN);
    add(MEAN_VALUE, numberToDouble(StatUtils.mean(values)));
    add(STDEV_VALUE, numberToDouble(StatUtils.stddev(values, true)));
    add(MEDIAN_VALUE, numberToDouble(StatUtils.median(values)));

    values = null;
  }
}
