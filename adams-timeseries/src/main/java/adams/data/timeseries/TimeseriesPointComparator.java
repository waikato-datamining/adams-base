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
 * TimeseriesPointComparator.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.timeseries;

import adams.data.container.DataPointComparator;

/**
 * A comparator for timeseries points.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TimeseriesPointComparator<P extends TimeseriesPoint>
  extends DataPointComparator<P> {

  /** for serialization. */
  private static final long serialVersionUID = -5536677097973106152L;

  /**
   * The default constructor uses comparison by timestamp in ascending manner.
   */
  public TimeseriesPointComparator() {
    this(true);
  }

  /**
   * This constructor initializes the comparator either in ascending
   * manner or descending.
   *
   * @param ascending	if true then the ordering is done in ascending
   * 			manner, otherwise descending
   */
  public TimeseriesPointComparator(boolean ascending) {
    super(ascending);
  }

  /**
   * Compares its two arguments for order. Returns a negative integer, zero,
   * or a positive integer as the first argument is less than, equal to, or
   * greater than the second.
   *
   * @param o1		the first object
   * @param o2		the second object
   * @return		a negative integer, zero, or a positive integer as
   * 			the first argument is less than, equal to, or greater
   * 			than the second.
   */
  @Override
  public int compare(TimeseriesPoint o1, TimeseriesPoint o2) {
    int		result;

    result = o1.getTimestamp().compareTo(o2.getTimestamp());

    // flip ordering?
    if (!m_Ascending)
      result *= -1;

    return result;
  }
}
