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
 * InstancePointComparator.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.data.instance;

import adams.data.container.DataPoint;
import adams.data.container.DataPointComparator;

/**
 * A comparator for InstancePoint objects.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InstancePointComparator
  extends DataPointComparator {

  /** for serialization. */
  private static final long serialVersionUID = 2862272335441705521L;

  /** whether to compare Y or X. */
  protected boolean m_UseY;

  /**
   * The default constructor uses comparison by X in ascending manner.
   */
  public InstancePointComparator() {
    this(false, true);
  }

  /**
   * This constructor initializes the comparator either with comparison by
   * X or by Y. Either in ascending manner or descending.
   *
   * @param useY	if true then Y is used for comparison otherwise X
   * @param ascending	if true then the ordering is done in ascending
   * 			manner, otherwise descending
   */
  public InstancePointComparator(boolean useY, boolean ascending) {
    super(ascending);

    m_UseY = useY;
  }

  /**
   * Returns whether Y or X number is used for ordering.
   *
   * @return		true if Y is used for ordering
   */
  public boolean isUsingY() {
    return m_UseY;
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
  public int compare(DataPoint o1, DataPoint o2) {
    int			result;
    InstancePoint	p1;
    InstancePoint	p2;

    p1 = (InstancePoint) o1;
    p2 = (InstancePoint) o2;

    if (m_UseY)
      result = p1.getY().compareTo(p2.getY());
    else
      result = p1.getX().compareTo(p2.getX());

    // flip ordering?
    if (!m_Ascending)
      result *= -1;

    return result;
  }
}
