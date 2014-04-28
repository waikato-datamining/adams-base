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
 * SortedList.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

/**
 * A list (not a proper java list, though) that keeps its elements sorted
 * according to a supplied comparator.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of objects to store in the list
 */
public class SortedList<T extends Serializable>
  implements Serializable {

  /** for serialization. */
  private static final long serialVersionUID = 2084694193825707653L;

  /** for storing the elements. */
  protected ArrayList<T> m_List;

  /** the comparator for sorting. */
  protected Comparator m_Comparator;

  /**
   * A simple comparator that can take any class that implements the
   * Comparable interface.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class ComparableComparator
    implements Comparator<Comparable>, Serializable {

    /** for serialization. */
    private static final long serialVersionUID = -609207775494982996L;

    /**
     * Compares its two arguments for order.  Returns a negative integer,
     * zero, or a positive integer as the first argument is less than, equal
     * to, or greater than the second.
     *
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the
     * 	       first argument is less than, equal to, or greater than the
     *	       second.
     */
    public int compare(Comparable o1, Comparable o2) {
      return o1.compareTo(o2);
    }
  }

  /**
   * Initializes the list with an instance of the ComparableComparator class
   * as comparator.
   *
   * @see 		ComparableComparator
   */
  public SortedList() {
    this(new ComparableComparator());
  }

  /**
   * Initializes the list with an instance of the ComparableComparator class
   * as comparator.
   * Automatically adds the items from the given collection.
   *
   * @param o		the collection to add
   * @see 		ComparableComparator
   */
  public SortedList(Collection<T> o) {
    this(new ComparableComparator(), o);
  }

  /**
   * Initializes the list with an instance of the ComparableComparator class
   * as comparator.
   * Automatically adds the items from the given sorted list.
   *
   * @param o		the sorted list to add
   * @see 		ComparableComparator
   */
  public SortedList(SortedList<T> o) {
    this(new ComparableComparator(), o);
  }

  /**
   * Initializes the list with an instance of the ComparableComparator class
   * as comparator.
   * Automatically adds the items from the given array.
   *
   * @param o		the array to add
   * @see 		ComparableComparator
   */
  public SortedList(T[] o) {
    this(new ComparableComparator(), o);
  }

  /**
   * Initializes the list.
   *
   * @param comparator	the comparator to use for sorting
   */
  public SortedList(Comparator comparator) {
    super();

    m_List       = new ArrayList();
    m_Comparator = comparator;
  }

  /**
   * Initializes the list.
   * Automatically adds the items from the given collection.
   *
   * @param comparator	the comparator to use for sorting
   * @param o		the collection to add
   */
  public SortedList(Comparator comparator, Collection<T> o) {
    this(comparator);
    addAll(o);
  }

  /**
   * Initializes the list.
   * Automatically adds the items from the given sorted list.
   *
   * @param comparator	the comparator to use for sorting
   * @param o		the sorted list to add
   */
  public SortedList(Comparator comparator, SortedList<T> o) {
    this(comparator);
    addAll(o);
  }

  /**
   * Initializes the list.
   * Automatically adds the items from the given array.
   *
   * @param comparator	the comparator to use for sorting
   * @param o		the array to add
   */
  public SortedList(Comparator comparator, T[] o) {
    this(comparator);
    addAll(Arrays.asList(o));
  }

  /**
   * Returns the comparator in use.
   *
   * @return		the comparator
   */
  public Comparator getComparator() {
    return m_Comparator;
  }

  /**
   * Clears the list.
   */
  public synchronized void clear() {
    m_List.clear();
  }

  /**
   * Returns the size of the list.
   *
   * @return		the number of stored items
   */
  public synchronized int size() {
    return m_List.size();
  }

  /**
   * Adds the object at the correct position to maintain a sorted list.
   *
   * @param o		the object to add
   */
  public synchronized void add(T o) {
    int 	index;

    index = Collections.binarySearch(m_List, o, getComparator());
    if (index < 0)
      m_List.add(-index-1, o);
    else
      m_List.add(index, o);
  }

  /**
   * Adds all the objects from the given collection.
   *
   * @param o		the collection to add
   */
  public synchronized void addAll(Collection<T> o) {
    for (T item: o)
      add(item);
  }

  /**
   * Adds all the objects from the given sorted List.
   *
   * @param o		the list to add
   */
  public synchronized void addAll(SortedList<T> o) {
    Iterator<T>	iter;

    iter = o.iterator();
    while (iter.hasNext())
      add(iter.next());
  }

  /**
   * Removes the element at the specified location.
   *
   * @param index	the index of the element to remove
   * @return		the removed element
   */
  public synchronized T remove(int index) {
    return m_List.remove(index);
  }

  /**
   * Returns the element at the specified position.
   *
   * @param index	the index of the element
   * @return		the element
   */
  public synchronized T get(int index) {
    return m_List.get(index);
  }

  /**
   * Returns the first element in the list.
   *
   * @return		the first element
   */
  public synchronized T first() {
    return m_List.get(0);
  }

  /**
   * Returns the last element in the list.
   *
   * @return		the last element
   */
  public synchronized T last() {
    return m_List.get(m_List.size() - 1);
  }

  /**
   * Returns an iterator over the list's items.
   *
   * @return		the iterator
   */
  public synchronized Iterator<T> iterator() {
    return m_List.iterator();
  }

  /**
   * Returns a string representation of the sorted list.
   *
   * @return		the string representation
   */
  public String toString() {
    StringBuilder	result;

    result = new StringBuilder();
    result.append("comparator=");
    result.append(m_Comparator.toString());
    result.append(", list=");
    result.append(m_List.toString());

    return result.toString();
  }
}
