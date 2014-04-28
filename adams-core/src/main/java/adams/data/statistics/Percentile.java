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
 * Percentile.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.statistics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Container class for sortable values, which allows to extract percentiles
 * and percentiles.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <E> The type of element to store in the container
 */
public class Percentile<E extends Comparable>
  implements Serializable {

  /** for serialization. */
  private static final long serialVersionUID = 3693932591687404328L;

  /** contains the values to obtain the percentile from. */
  protected List<E> m_Values;

  /** whether the vector has been sorted already. */
  protected boolean m_Sorted;

  /** a custom comparator. */
  protected Comparator m_Comparator;

  /**
   * Initializes the container.
   */
  public Percentile() {
    this(null);
  }

  /**
   * Initializes the container with a custom comparator.
   *
   * @param comparator	the comparator to use for sorting, use null to use
   * 			default sorting
   */
  public Percentile(Comparator comparator) {
    m_Values     = new ArrayList<E>();
    m_Sorted     = false;
    m_Comparator = comparator;
  }

  /**
   * Adds the value to the internal list of values.
   *
   * @param value	the value to add
   */
  public void add(E value) {
    m_Values.add(value);
    m_Sorted = false;
  }

  /**
   * Adds the values to the internal list of values.
   *
   * @param values	the values to add
   */
  public void addAll(Collection<E> values) {
    m_Values.addAll(values);
    m_Sorted = false;
  }

  /**
   * Adds the values to the internal list of values.
   *
   * @param values	the values to add
   */
  public void addAll(E[] values) {
    for (E value: values)
      m_Values.add(value);
    m_Sorted = false;
  }

  /**
   * Returns the currently stored values.
   *
   * @return		the current values
   */
  public List<E> getValues() {
    return m_Values;
  }

  /**
   * Sorts the values, if necessary.
   */
  protected void sort() {
    if (m_Sorted)
      return;

    if (m_Comparator == null)
      Collections.sort(m_Values);
    else
      Collections.sort(m_Values, m_Comparator);

    m_Sorted = true;
  }

  /**
   * Returns the specified percentile. Math.round(...) is used to determine
   * the index of the percentile.
   *
   * @param percentage	the percentage of the percentile (e.g., Q1: 0.25, Q3: 0.75)
   * @return		the corresponding value or null if no percentile could be computed
   */
  public E getPercentile(double percentage) {
    E		result;
    int		index;

    result = null;

    sort();

    if (size() > 0) {
      index  = (int) Math.round(((double) m_Values.size()) * percentage) - 1;
      if (index < 0)
	index = 0;
      result = m_Values.get(index);
    }

    return result;
  }

  /**
   * Removes all values.
   */
  public void clear() {
    m_Values.clear();
    m_Sorted = false;
  }

  /**
   * Returns the number of stored values.
   *
   * @return		the number of stored values
   */
  public int size() {
    return m_Values.size();
  }

  /**
   * Returns the stored values as string.
   *
   * @return		the values as string
   */
  @Override
  public String toString() {
    return m_Values.toString();
  }
}
