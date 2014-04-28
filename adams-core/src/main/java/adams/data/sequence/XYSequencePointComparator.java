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
 * XYSequencePointComparator.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.sequence;

import adams.data.container.DataPoint;
import adams.data.container.DataPointComparator;

/**
 * A comparator for XY sequence points.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <X> the type of X
 * @param <Y> the type of Y
 */
public class XYSequencePointComparator<X extends Number & Comparable, Y extends Number & Comparable>
  extends DataPointComparator {

  /** for serialization. */
  private static final long serialVersionUID = -5536677097973106152L;

  /**
   * The type of comparison to perform.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum Comparison {
    /** only X. */
    X,
    /** only Y. */
    Y,
    /** X and Y. */
    X_AND_Y
  }

  /** how to compare. */
  protected Comparison m_Comparison;

  /**
   * The default constructor uses comparison by X in ascending manner.
   */
  public XYSequencePointComparator() {
    this(Comparison.X, true);
  }

  /**
   * This constructor initializes the comparator either with comparison by
   * X or by Y or both. Either in ascending manner or descending.
   *
   * @param comp	the type of comparison
   * @param ascending	if true then the ordering is done in ascending
   * 			manner, otherwise descending
   */
  public XYSequencePointComparator(Comparison comp, boolean ascending) {
    super(ascending);

    m_Comparison = comp;
  }

  /**
   * Returns the type of comparison.
   *
   * @return		the comparison
   */
  public Comparison getComparison() {
    return m_Comparison;
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
  public int compare(DataPoint o1, DataPoint o2) {
    int			result;
    XYSequencePoint	p1;
    XYSequencePoint	p2;

    p1 = (XYSequencePoint) o1;
    p2 = (XYSequencePoint) o2;

    if (m_Comparison == Comparison.Y) {
      result = new Double(p1.getY()).compareTo(p2.getY());
    }
    else if (m_Comparison == Comparison.X) {
      result = new Double(p1.getX()).compareTo(p2.getX());
    }
    else if (m_Comparison == Comparison.X_AND_Y) {
      result = new Double(p1.getX()).compareTo(p2.getX());
      if (result == 0)
	result = new Double(p1.getY()).compareTo(p2.getY());
    }
    else {
      throw new IllegalStateException("Unhandled comparison type: " + m_Comparison);
    }

    // flip ordering?
    if (!m_Ascending)
      result *= -1;

    return result;
  }
}
