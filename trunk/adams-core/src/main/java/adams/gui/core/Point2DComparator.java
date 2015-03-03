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
 * Point2DComparator.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.Comparator;

/**
 * A comparator for {@link Point2D} objects.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Point2DComparator
  implements Comparator<Point2D>, Serializable {

  /** for serialization. */
  private static final long serialVersionUID = -5536677097973106152L;

  /** whether to compare ascending or descending. */
  protected boolean m_Ascending;
  
  /** whether to only use X for comparison. */
  protected boolean m_UseOnlyX;
  
  /**
   * The default constructor uses comparison by points in ascending manner,
   * using only X.
   */
  public Point2DComparator() {
    this(true, true);
  }

  /**
   * This constructor initializes the comparator either in ascending
   * manner or descending.
   *
   * @param ascending	if true then the ordering is done in ascending
   * 			manner, otherwise descending
   */
  public Point2DComparator(boolean ascending, boolean useOnlyX) {
    super();
    
    m_Ascending = ascending;
    m_UseOnlyX  = useOnlyX;
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
  public int compare(Point2D o1, Point2D o2) {
    int		result;

    result = new Double(o1.getX()).compareTo(o2.getX());
    
    if (!m_UseOnlyX && (result == 0))
      result = new Double(o1.getY()).compareTo(o2.getY());

    // flip ordering?
    if (!m_Ascending)
      result *= -1;

    return result;
  }
}
