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
 * DataContainer.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.container;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import adams.core.CloneHandler;
import adams.core.Mergeable;
import adams.data.id.MutableIDHandler;

/**
 * Generic Interface for data containers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of data points this container deals with
 */
public interface DataContainer<T extends DataPoint>
  extends Serializable, Comparable, CloneHandler,
          Collection<T>, Mergeable<DataContainer>, MutableIDHandler {

  /**
   * Returns a new instance of the default comparator.
   *
   * @return		the comparator instance
   */
  public DataPointComparator<T> newComparator();

  /**
   * Returns the comparator in use.
   *
   * @return		the comparator in use
   */
  public DataPointComparator<T> getComparator();

  /**
   * Returns a new instance of a DataContainer point.
   *
   * @return		the new DataContainer point
   */
  public T newPoint();

  /**
   * Returns a clone of itself.
   *
   * @return		the clone
   */
  public Object getClone();

  /**
   * Returns an empty container with the same payload data as this one.
   *
   * @return		a clone of the payload
   */
  public DataContainer getHeader();

  /**
   * Obtains the stored variables from the other data point, but not the
   * actual data points.
   *
   * @param other	the data point to get the values from
   */
  public void assign(DataContainer<T> other);

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   *
   * @param   o the object to be compared.
   * @return  a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   * @throws ClassCastException if the specified object's type prevents it
   *         from being compared to this object.
   */
  public int compareToHeader(Object o);

  /**
   * Indicates whether some other chromatogram's header is "equal to" this ones.
   *
   * @param obj		the reference object with which to compare.
   * @return		true if this object is the same as the obj argument;
   * 			false otherwise.
   */
  public boolean equalsHeader(Object obj);

  /**
   * Returns a vector with the points.
   *
   * @return		a vector with all the points
   */
  public List<T> toList();

  /**
   * Returns a list with the points.
   *
   * @param comparator	the comparator to use
   * @return		a list with all the points
   */
  public List<T> toList(DataPointComparator<T> comparator);

  /**
   * Returns a treeset with the points.
   *
   * @return		a treeset with all the points
   */
  public TreeSet<T> toTreeSet();

  /**
   * Returns a treeset with the points, sorted according to the given
   * comparator.
   *
   * @param comparator	the comparator to use
   * @return		a treeset with all the points
   */
  public TreeSet<T> toTreeSet(DataPointComparator<T> comparator);
}
