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
 * ControlChartContainer.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.container;

import adams.data.spc.ControlChart;
import adams.data.spc.Limits;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Container to store control chart data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ControlChartContainer
  extends AbstractContainer {

  private static final long serialVersionUID = 1960872156580346093L;

  /** the identifier for the algorithm. */
  public final static String VALUE_ALGORITHM = "Algor";

  /** the identifier for the chart name. */
  public final static String VALUE_CHART = "Chart";

  /** the identifier for the data (double array). */
  public final static String VALUE_DATA = "Data";

  /** the identifier for the perpared data (double array). */
  public final static String VALUE_PREPARED = "Prepared";

  /** the identifier for the limits (array of Limits). */
  public final static String VALUE_LIMITS = "Limits";

  /**
   * Initializes the container with dummy values.
   * <br><br>
   * Only used for generating help information.
   */
  public ControlChartContainer() {
    this(null, null, new double[0], new double[0], new Limits[]{new Limits()});
  }

  /**
   * Initializes the container.
   *
   * @param algorithm	the algorithm used for generating the data
   * @param chart	the name of the chart, can be null
   * @param data        the original data
   * @param prepared	the prepared/processed data
   * @param limits	the limits
   */
  public ControlChartContainer(ControlChart algorithm, String chart, Object data, Object prepared, Limits[] limits) {
    super();

    if (limits.length < 1)
      throw new IllegalArgumentException("At least one Limit container has to be provided!");

    if ((chart != null) && chart.isEmpty())
      chart = null;

    store(VALUE_ALGORITHM, algorithm);
    store(VALUE_CHART,     chart);
    store(VALUE_DATA,      data);
    store(VALUE_PREPARED,  prepared);
    store(VALUE_LIMITS,    limits);
  }

  /**
   * Initializes the help strings.
   */
  protected void initHelp() {
    super.initHelp();

    addHelp(VALUE_ALGORITHM, "control chart algorithm; " + ControlChart.class.getName());
    addHelp(VALUE_CHART, "name of the chart; " + String.class.getName());
    addHelp(VALUE_DATA, "original data; " + Object.class.getName());
    addHelp(VALUE_PREPARED, "prepared/processed data; " + Object.class.getName());
    addHelp(VALUE_LIMITS, "limits; array of " + Limits.class.getName());
  }

  /**
   * Returns all value names that can be used (theoretically).
   *
   * @return		iterator over all possible value names
   */
  @Override
  public Iterator<String> names() {
    List<String> result;

    result = new ArrayList<String>();

    result.add(VALUE_ALGORITHM);
    result.add(VALUE_CHART);
    result.add(VALUE_DATA);
    result.add(VALUE_PREPARED);
    result.add(VALUE_LIMITS);

    return result.iterator();
  }

  /**
   * Checks whether the setup of the container is valid.
   *
   * @return		true if all the necessary values are available
   */
  @Override
  public boolean isValid() {
    return
      hasValue(VALUE_ALGORITHM)
	&& hasValue(VALUE_DATA)
	&& hasValue(VALUE_PREPARED)
	&& hasValue(VALUE_LIMITS);
  }
}
