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
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.flow.container;

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

  /** the identifier for the name of the chart. */
  public final static String VALUE_CHART = "Chart";

  /** the identifier for the data (double array). */
  public final static String VALUE_DATA = "Data";

  /** the identifier for the perpared data (double array). */
  public final static String VALUE_PREPARED = "Prepared";

  /** the identifier for the lower limit (double). */
  public final static String VALUE_LOWER = "Lower";

  /** the identifier for the center (double). */
  public final static String VALUE_CENTER = "Center";

  /** the identifier for the upper limit (double). */
  public final static String VALUE_UPPER = "Upper";

  /** the identifier for the indices (int array) of violoations. */
  public final static String VALUE_VIOLATIONS = "Violations";

  /**
   * Initializes the container with dummy values.
   * <br><br>
   * Only used for generating help information.
   */
  public ControlChartContainer() {
    this("Dummy", new double[]{}, new double[]{}, 0.0, 0.5, 1.0);
  }

  /**
   * Initializes the container.
   *
   * @param chart	the name of the chart
   * @param data        the original data
   * @param prepared	the prepared/processed data
   * @param lower	the lower limit
   * @param center	the center
   * @param upper	the upper limit
   */
  public ControlChartContainer(String chart, Object data, Object prepared, double lower, double center, double upper) {
    this(chart, data, prepared, lower, center, upper, null);
  }

  /**
   * Initializes the container.
   *
   * @param chart	the name of the chart
   * @param data        the original data
   * @param prepared	the prepared/processed data
   * @param lower	the lower limit
   * @param center	the center
   * @param upper	the upper limit
   * @param violations	the indices of the violations, null if none
   */
  public ControlChartContainer(String chart, Object data, Object prepared, double lower, double center, double upper, int[] violations) {
    super();

    if ((violations != null) && (violations.length == 0))
      violations = null;

    store(VALUE_CHART,      chart);
    store(VALUE_DATA,       data);
    store(VALUE_PREPARED,   prepared);
    store(VALUE_LOWER,      lower);
    store(VALUE_CENTER,     center);
    store(VALUE_UPPER,      upper);
    store(VALUE_VIOLATIONS, violations);
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

    result.add(VALUE_CHART);
    result.add(VALUE_DATA);
    result.add(VALUE_PREPARED);
    result.add(VALUE_LOWER);
    result.add(VALUE_CENTER);
    result.add(VALUE_UPPER);
    result.add(VALUE_VIOLATIONS);

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
      hasValue(VALUE_CHART)
	&& hasValue(VALUE_DATA)
	&& hasValue(VALUE_PREPARED)
	&& hasValue(VALUE_LOWER)
	&& hasValue(VALUE_CENTER)
	&& hasValue(VALUE_UPPER);
  }
}
