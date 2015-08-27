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
 * DataContainer.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import adams.core.Utils;
import adams.data.id.MutableIDHandler;

/**
 * Superclass for all data structures.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of the container
 */
public abstract class AbstractDataContainer<T extends DataPoint>
  implements DataContainer<T>, MutableIDHandler {

  /** for serialization. */
  private static final long serialVersionUID = 7596037729815376007L;

  /** the ID of the sequence. */
  protected String m_ID;

  /** the data points. */
  protected ArrayList<T> m_Points;

  /**
   * Initializes the container.
   */
  public AbstractDataContainer() {
    super();

    m_ID     = "";
    m_Points = new ArrayList<T>();
  }

  /**
   * Sets the ID of the sequence.
   *
   * @param value	the new ID
   */
  public void setID(String value) {
    m_ID = value;
  }

  /**
   * Returns the ID of the sequence.
   *
   * @return		the ID
   */
  public String getID() {
    return m_ID;
  }

  /**
   * Returns a clone of itself. Note: resets the read-only flag!
   *
   * @return		the clone
   * @see		#setReadOnly(boolean)
   */
  public Object getClone() {
    AbstractDataContainer<T>	result;
    Iterator<T>			iter;

    try {
      result = (AbstractDataContainer<T>) getClass().newInstance();
    }
    catch (Exception e) {
      throw new IllegalStateException(e);
    }
    result.ensureCapacity(size());
    result.assign(this);
    iter = iterator();
    while (iter.hasNext())
      result.add((T) iter.next().getClone());

    return result;
  }

  /**
   * Obtains the stored variables from the other data point, but not the
   * actual data points.
   *
   * @param other	the data point to get the values from
   */
  public void assign(DataContainer<T> other) {
    setID(other.getID());
  }

  /**
   * Returns an empty container with the same payload data as this one.
   *
   * @return		a clone of the payload
   */
  public DataContainer getHeader() {
    DataContainer	result;

    result = newInstance(this);
    result.assign(this);

    return result;
  }

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
  public int compareToHeader(Object o) {
    int				result;
    AbstractDataContainer	c;

    if (o == null)
      return 1;

    c = (AbstractDataContainer) o;

    result = Utils.compare(getID(), c.getID());

    return result;
  }

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
  public int compareToData(Object o) {
    int				result;
    Iterator<T>			iter;
    Iterator<T>			iterOther;
    AbstractDataContainer	c;

    if (o == null)
      return 1;

    c = (AbstractDataContainer) o;

    result = new Integer(size()).compareTo(new Integer(c.size()));

    if (result == 0) {
      iter      = iterator();
      iterOther = c.iterator();

      while (iter.hasNext() && (result == 0))
	result = iter.next().compareTo(iterOther.next());
    }

    return result;
  }

  /**
   * Indicates whether some other container's header is "equal to" this ones.
   *
   * @param obj		the reference object with which to compare.
   * @return		true if this object is the same as the obj argument;
   * 			false otherwise.
   */
  public boolean equalsData(Object obj) {
    return (compareToData(obj) == 0);
  }

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
  public int compareTo(Object o) {
    int		result;

    if (o == null)
      return 1;

    if (!(o instanceof AbstractDataContainer))
      return -1;

    result = compareToHeader(o);

    if (result == 0)
      result = compareToData(o);

    return result;
  }

  /**
   * Indicates whether some other chromatogram's header is "equal to" this ones.
   *
   * @param obj		the reference object with which to compare.
   * @return		true if this object is the same as the obj argument;
   * 			false otherwise.
   */
  public boolean equalsHeader(Object obj) {
    return (compareToHeader(obj) == 0);
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   *
   * @param obj		the reference object with which to compare.
   * @return		true if this object is the same as the obj argument;
   * 			false otherwise.
   */
  @Override
  public boolean equals(Object obj) {
    return (obj instanceof DataContainer) && (compareTo(obj) == 0);
  }

  /**
   * Method that gets notified about changes in the collection of data points.
   * Just passes the modified state through.
   * <br><br>
   * The default implementation only passes the value through.
   *
   * @param modified	whether the action modified the collection
   * @return		the same as the input
   */
  protected boolean modifiedListener(boolean modified) {
    return modified;
  }

  /**
   * Removes all the points.
   */
  public synchronized void clear() {
    T		point;
    Iterator	iter;

    if (m_Points.size() > 0) {
      // remove parents
      iter = iterator();
      while (iter.hasNext()) {
        point = (T) iter.next();
        point.setParent(null);
        if (point instanceof DataContainer)
          ((DataContainer) point).clear();
      }

      m_Points.clear();
      modifiedListener(true);
    }
  }

  /**
   * Returns whether the DataContainer is empty.
   *
   * @return		true if empty
   */
  public boolean isEmpty() {
    return m_Points.isEmpty();
  }

  /**
   * Ensures that the capacity is at least the specified minimum.
   *
   * @param minCapacity	the minimum capacity that the container should have
   * 			for storing data points
   */
  public void ensureCapacity(int minCapacity) {
    m_Points.ensureCapacity(minCapacity);
  }

  /**
   * Trims the capacity of this container to be the
   * container's current size.
   */
  public void trimToSize() {
    m_Points.trimToSize();
  }

  /**
   * Adds the point to the list of points.
   *
   * @param point	the point to add
   * @return		true if the points changed
   */
  public synchronized boolean add(T point) {
    int 	index;
    boolean	modified;

    point.setParent(this);

    index = Collections.binarySearch(m_Points, point, getComparator());
    if (index < 0) {
      m_Points.add(-index-1, point);
      modified = true;
    }
    else {
      m_Points.set(index, point);
      modified = true;
    }

    return modifiedListener(modified);
  }

  /**
   * Adds all the points to the list of points.
   *
   * @param points	the points to add
   * @return		true if the points changed
   */
  public synchronized boolean addAll(Collection points) {
    T		point;
    Iterator	iter;
    int		index;
    boolean	modified;

    modified = false;
    iter     = points.iterator();
    while (iter.hasNext()) {
      point = (T) iter.next();
      point.setParent(this);

      // insert/replace
      index = Collections.binarySearch(m_Points, point, getComparator());
      if (index < 0) {
        m_Points.add(-index-1, point);
        modified = true;
      }
      else {
        m_Points.set(index, point);
        modified = true;
      }
    }

    return modifiedListener(modified);
  }

  /**
   * Removes the given point from its list of points.
   *
   * @param point	the point to remove
   * @return		true if the points changed
   */
  public synchronized boolean remove(Object point) {
    // remove parent
    if (contains(point))
      ((T) point).setParent(null);

    return modifiedListener(m_Points.remove(point));
  }

  /**
   * Removes all the points from the list of points.
   *
   * @param points	the points to remove
   * @return		true if the points changed
   */
  public synchronized boolean removeAll(Collection points) {
    Iterator	iter;
    T		point;

    // remove parents
    iter = points.iterator();
    while (iter.hasNext()) {
      point = (T) iter.next();
      if (contains(point))
	point.setParent(null);
    }

    return modifiedListener(m_Points.removeAll(points));
  }

  /**
   * Checks whether the given point is already stored.
   *
   * @param point	the point to check
   * @return		true if point already exists in list
   */
  public synchronized boolean contains(Object point) {
    return m_Points.contains(point);
  }

  /**
   * Checks whether all the given points are already stored.
   *
   * @param points	the points to check
   * @return		true if points already exist in list
   */
  public synchronized boolean containsAll(Collection points) {
    return m_Points.containsAll(points);
  }

  /**
   * Retains all the given points and removes the others.
   *
   * @param points	the points to keep
   * @return		true if points changed
   */
  public synchronized boolean retainAll(Collection points) {
    Iterator	iter;
    T		point;

    // remove parents
    iter = points.iterator();
    while (iter.hasNext()) {
      point = (T) iter.next();
      if (!contains(point))
	point.setParent(null);
    }

    return modifiedListener(m_Points.retainAll(points));
  }

  /**
   * Returns the number of points stored in the DataContainer.
   *
   * @return		the number of points
   */
  public int size() {
    return m_Points.size();
  }

  /**
   * Adds copies of all the points from the given container to its own.
   * No will get overwritten, only added.
   *
   * @param other	the container to merge with
   */
  public synchronized void mergeWith(DataContainer other) {
    Iterator<T>	iter;
    T		point;

    iter = other.iterator();

    // find points that we don't already have
    while (iter.hasNext()) {
      point = iter.next();
      if (!contains(point))
	add(point);
    }
  }

  /**
   * Returns an iterator over the points.
   *
   * @return		the iterator
   */
  public Iterator<T> iterator() {
    return m_Points.iterator();
  }

  /**
   * Returns the stored points as array.
   *
   * @return		the points as array
   */
  public Object[] toArray() {
    return m_Points.toArray();
  }

  /**
   * Returns the stored points as array.
   *
   * @param array	the array into which the points of this DataContainer
   * 			are to be stored, if it is big enough; otherwise, a
   *  			new array of the same runtime type is allocated for
   *  			this purpose.
   * @return		the points as array
   */
  public Object[] toArray(Object[] array) {
    return m_Points.toArray(array);
  }

  /**
   * Returns a list with the points. Very fast, as it only returns the
   * internal linked list.
   *
   * @return		a list with all the points
   */
  public List<T> toList() {
    return m_Points;
  }

  /**
   * Returns a list with the points.
   *
   * @param comparator	the comparator to use
   * @return		a list with all the points
   */
  public List<T> toList(DataPointComparator comparator) {
    List<T>	result;

    result = new ArrayList<T>(m_Points);
    Collections.sort(result, comparator);

    return result;
  }

  /**
   * Returns a treeset with the points.
   *
   * @return		a treeset with all the points
   */
  public TreeSet<T> toTreeSet() {
    return toTreeSet(getComparator());
  }

  /**
   * Returns a treeset with the points, sorted according to the given
   * comparator.
   *
   * @param comparator	the comparator to use
   * @return		a treeset with all the points
   */
  public TreeSet<T> toTreeSet(DataPointComparator comparator) {
    TreeSet<T>	result;

    result = new TreeSet<T>(comparator);
    result.addAll(m_Points);

    return result;
  }

  /**
   * Returns the hash code for this DataContainer.
   *
   * @return		the hash code
   */
  @Override
  public int hashCode() {
    return new String(getID() + super.hashCode()).hashCode();
  }

  /**
   * Returns a string representation of the DataContainer.
   *
   * @return		the string representation
   */
  @Override
  public String toString() {
    return "ID=" + getID() + ", #points=" + size();
  }

  /**
   * Returns a new (empty) instance of the same class as the specified
   * container.
   *
   * @param cont	the container to use as a class template
   * @return		the instance or null in case of an error
   */
  public static DataContainer newInstance(DataContainer cont) {
    DataContainer	result;

    try {
      result = (DataContainer) cont.getClass().newInstance();
    }
    catch (Exception e) {
      result = null;
      e.printStackTrace();
    }

    return result;
  }
}
