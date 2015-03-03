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
 * DataPointComparator.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.data.container;

import java.io.Serializable;
import java.util.Comparator;

/**
 * A comparator for sequence points.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class DataPointComparator<T extends DataPoint>
  implements Comparator<T>, Serializable {

  /** for serialization. */
  private static final long serialVersionUID = -5536677097973106152L;

  /** whether the ordering is ascending or descending. */
  protected boolean m_Ascending;

  /**
   * The default constructor uses comparison in ascending manner.
   */
  public DataPointComparator() {
    this(true);
  }

  /**
   * This constructor initializes the comparator either in ascending manner or descending.
   *
   * @param ascending		if true then the ordering is done in ascending
   * 				manner, otherwise descending
   */
  public DataPointComparator(boolean ascending) {
    super();

    m_Ascending = ascending;
  }

  /**
   * Returns whether the ordering is done in ascending or descending manner.
   *
   * @return		true if ordering it in ascending manner
   */
  public boolean isAscending() {
    return m_Ascending;
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
  public abstract int compare(T o1, T o2);
}
