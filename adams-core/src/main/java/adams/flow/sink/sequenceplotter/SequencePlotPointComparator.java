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
 * SequencePlotPointComparator.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink.sequenceplotter;

import adams.data.container.DataPoint;
import adams.data.sequence.XYSequencePointComparator;

/**
 * A comparator for XY sequence points.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <X> the type of X
 * @param <Y> the type of Y
 */
public class SequencePlotPointComparator<X extends Number & Comparable, Y extends Number & Comparable>
  extends XYSequencePointComparator<X, Y> {

  /** for serialization. */
  private static final long serialVersionUID = -5536677097973106152L;

  /** the meta-data value to take into account for the sorting. */
  protected String m_MetaDataKey;

  /**
   * The default constructor uses comparison by XandY in ascending manner
   * and not meta-data key.
   */
  public SequencePlotPointComparator() {
    this(Comparison.X_AND_Y, true, null);
  }

  /**
   * This constructor initializes the comparator either with comparison by
   * X or by Y or both. Either in ascending manner or descending.
   *
   * @param comp	the type of comparison
   * @param ascending	if true then the ordering is done in ascending
   * 			manner, otherwise descending
   */
  public SequencePlotPointComparator(Comparison comp, boolean ascending, String metaDataKey) {
    super(comp, ascending);
    m_MetaDataKey = metaDataKey;
  }

  /**
   * Returns the meta-data key to use for the comparison.
   *
   * @return		the key, null if not used
   */
  public String getMetaDataKey() {
    return m_MetaDataKey;
  }

  /**
   * Returns the associated meta-data value.
   *
   * @param p		the point to obtain the meta-data from
   * @return		the value, null if not available
   */
  protected Object getMetaData(SequencePlotPoint p) {
    if (!p.hasMetaData())
      return null;
    return p.getMetaData().get(m_MetaDataKey);
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
    SequencePlotPoint 	p1;
    SequencePlotPoint	p2;
    Object		m1;
    Object		m2;

    p1 = (SequencePlotPoint) o1;
    p2 = (SequencePlotPoint) o2;

    result = super.compare(o1, o2);

    // flip ordering?
    if (!m_Ascending)
      result *= -1;

    if (result == 0) {
      if (m_MetaDataKey != null) {
	m1 = getMetaData(p1);
	m2 = getMetaData(p2);
	if ((m1 ==  null) && (m2 == null)) {
	  result = 0;
	}
	else if (m1 == null) {
	  result = -1;
	}
	else if (m2 == null) {
	  result = +1;
	}
	else {
	  if ((m1 instanceof Comparable) && (m2 instanceof Comparable))
	    result = ((Comparable) m1).compareTo(m2);
	  else
	    result = new Integer(m1.hashCode()).compareTo(m2.hashCode());
	}
      }
    }

    // flip ordering?
    if (!m_Ascending)
      result *= -1;

    return result;
  }
}
