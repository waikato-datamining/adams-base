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
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Provides an always sorted list. If list objects don't implement the 
 * {@link Comparable} interface, a custom {@link Comparator} must be supplied.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SortedList<T>
  implements List<T>, Serializable, Cloneable {

  /** for serialization. */
  private static final long serialVersionUID = -6982960827830177434L;

  /**
   * Default comparator that assumes that objects implement the 
   * {@link Comparable} interface.
   * 
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class DefaultComparator
    implements Comparator, Serializable {

    /** for serialization. */
    private static final long serialVersionUID = -4121107272578264484L;

    /**
     * Compares the two objects.
     * 
     * @param o1	the first object
     * @param o2	the second object
     * @return		less than zero, zero or greater than zero if o1 is 
     * 			smaller than, equal to or greater than o2 respectively
     */
    @Override
    public int compare(Object o1, Object o2) {
      return ((Comparable) o1).compareTo((Comparable) o2);
    }
  }
  
  /** the underlying list. */
  protected ArrayList<T> m_List;
  
  /** the comparator to use. */
  protected Comparator m_Comparator;
  
  /** the default comparator. */
  protected static Comparator m_DefaultComparator;
  static {
    m_DefaultComparator = new DefaultComparator();
  }
  
  /**
   * Initializes the list.
   * Uses the {@link DefaultComparator}.
   */
  public SortedList() {
    m_List       = new ArrayList<T>();
    m_Comparator = m_DefaultComparator;
  }
  
  /**
   * Initializes the list.
   * Uses the specified {@link Comparator}.
   * 
   * @param comp		the comparator to use
   */
  public SortedList(Comparator comp) {
    m_List       = new ArrayList<T>();
    m_Comparator = comp;
  }
  
  /**
   * Initializes the list.
   * Uses the {@link DefaultComparator}.
   * 
   * @param initialCapacity	the initial capacity of the list
   */
  public SortedList(int initialCapacity) {
    m_List       = new ArrayList<T>(initialCapacity);
    m_Comparator = m_DefaultComparator;
  }
  
  /**
   * Initializes the list.
   * Uses the specified {@link Comparator}.
   * 
   * @param initialCapacity	the initial capacity of the list
   * @param comp		the comparator to use
   */
  public SortedList(int initialCapacity, Comparator comp) {
    m_List       = new ArrayList<T>(initialCapacity);
    m_Comparator = comp;
  }
  
  /**
   * Initializes the list.
   * Uses the {@link DefaultComparator}.
   * 
   * @param c		the data to populate the list with
   */
  public SortedList(Collection<? extends T> c) {
    m_List       = new ArrayList<T>(c);
    m_Comparator = m_DefaultComparator;
    sort();
  }
  
  /**
   * Initializes the list.
   * Uses the specified {@link Comparator}.
   * 
   * @param c		the data to populate the list with
   * @param comp	the comparator to use
   */
  public SortedList(Collection<? extends T> c, Comparator comp) {
    m_List       = new ArrayList<T>(c);
    m_Comparator = comp;
    sort();
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
   * Sorts the list.
   */
  protected void sort() {
    Collections.sort(m_List, m_Comparator);
  }
  
  /**
   * Creates a clone of this list.
   * 
   * @return				the clone
   * @throws CloneNotSupportedException	does never occur
   */
  @Override
  public synchronized Object clone() throws CloneNotSupportedException {
    return new SortedList<T>(m_List, m_Comparator);
  }
  
  /**
   * Returns the size of the list.
   * 
   * @return		the size
   */
  @Override
  public synchronized int size() {
    return m_List.size();
  }

  /**
   * Returns whether the list is empty.
   * 
   * @return		true if empty
   */
  @Override
  public synchronized boolean isEmpty() {
    return m_List.isEmpty();
  }

  /**
   * Checks whether the object is present.
   * 
   * @return		true if present
   */
  @Override
  public synchronized boolean contains(Object o) {
    return m_List.contains(o);
  }

  /**
   * Returns an iterator over the items in the list.
   * 
   * @return		the iterator
   */
  @Override
  public synchronized Iterator<T> iterator() {
    return m_List.iterator();
  }

  /**
   * Returns the list as array.
   * 
   * @return		the items as array
   */
  @Override
  public synchronized Object[] toArray() {
    return m_List.toArray();
  }

  /**
   * Returns the list as array.
   * 
   * @param a		the array to copy the items into
   * @return		the items as array
   */
  @Override
  public synchronized <T> T[] toArray(T[] a) {
    return m_List.toArray(a);
  }

  /**
   * Adds the element to the list.
   * 
   * @param e		the element to add
   * @return		true if list modified
   */
  @Override
  public synchronized boolean add(T e) {
    boolean	result;
    int 	index;

    index = Collections.binarySearch(m_List, e, m_Comparator);
    if (index < 0) {
      m_List.add(-index-1, e);
      result = true;
    }
    else {
      m_List.set(index, e);
      result = true;
    }

    return result;
  }

  /**
   * Removes the object from the list.
   * 
   * @param o		the object to remove
   * @return		true if list modified
   */
  @Override
  public synchronized boolean remove(Object o) {
    return m_List.remove(o);
  }

  /**
   * Checks whether all items of the collected are contained in the list.
   * 
   * @param c		the collection to check
   * @return		true if all contained
   */
  @Override
  public synchronized boolean containsAll(Collection<?> c) {
    return m_List.containsAll(c);
  }

  /**
   * Adds all the elements of the collection to this list.
   * 
   * @param c		the collection to add
   * @return		true if list modified
   */
  @Override
  public synchronized boolean addAll(Collection<? extends T> c) {
    boolean	result;
    int		index;
    
    result = false;
    
    m_List.ensureCapacity(size() + c.size());
    
    for (T element: c) {
      index = Collections.binarySearch(m_List, element, m_Comparator);
      if (index < 0) {
        m_List.add(-index-1, element);
        result = true;
      }
      else {
        m_List.set(index, element);
        result = true;
      }
    }
    
    return result;
  }

  /**
   * Removes all items from the collection in this list.
   * 
   * @param c		the items to remove
   * @return		true if list modified
   */
  @Override
  public synchronized boolean removeAll(Collection<?> c) {
    return m_List.removeAll(c);
  }

  /**
   * Keeps all items present in the collection.
   * 
   * @param c		the items to keep
   * @return		true if list modified
   */
  @Override
  public synchronized boolean retainAll(Collection<?> c) {
    return m_List.retainAll(c);
  }

  /**
   * Empties the list.
   */
  @Override
  public synchronized void clear() {
    m_List.clear();
  }

  /**
   * Adds all the elements of the collection starting at the specified index.
   * NB: triggers a complete sort!
   * 
   * @param index	the starting index
   * @param c		the elements to add
   * @return		true if list modified
   */
  @Override
  public synchronized boolean addAll(int index, Collection<? extends T> c) {
    boolean	result;
    
    result = m_List.addAll(index, c);
    sort();
    
    return result;
  }

  /**
   * Returns the item at the specified position.
   * 
   * @param index	the position to get the item at
   */
  @Override
  public synchronized T get(int index) {
    return m_List.get(index);
  }

  /**
   * Sets the item at the specified position.
   * NB: expensive as it triggers a complete sort!
   * 
   * @param index	the position to set the element
   * @param element	the element to set
   * @return		the previous element at that position
   */
  @Override
  public synchronized T set(int index, T element) {
    T		result;
    
    result = m_List.set(index, element);
    sort();
    
    return result;
  }

  /**
   * Adds the element at the specified position.
   * NB: triggers a complete sort!
   * 
   * @param index	the index to insert the element
   * @param element	the element to insert
   */
  @Override
  public synchronized void add(int index, T element) {
    m_List.add(index, element);
    sort();
  }

  /**
   * Removes the item at the specified position.
   * 
   * @param index	the position of the item
   * @return		the removed item
   */
  @Override
  public synchronized T remove(int index) {
    return m_List.remove(index);
  }

  /**
   * Returns the index of the object in the list. Uses fast binary search
   * for locating the object.
   * 
   * @param o		the object to find
   * @return		the index, -1 if not found
   */
  @Override
  public synchronized int indexOf(Object o) {
    int		result;
    
    result = Collections.binarySearch(m_List, o, m_Comparator);
    if (result < 0)
      result = -1;
    
    return result;
  }

  /**
   * Returns the last index for the object in this list.
   * 
   * @param o		the object to locate
   * @return		the index, -1 if not found
   */
  @Override
  public synchronized int lastIndexOf(Object o) {
    return m_List.lastIndexOf(o);
  }

  /**
   * Returns a list iterator over the items.
   * 
   * @return		the iterator
   */
  @Override
  public synchronized ListIterator<T> listIterator() {
    return m_List.listIterator();
  }

  /**
   * Returns a list iterator over the items starting at the position.
   * 
   * @param index	the starting index
   * @return		the iterator
   */
  @Override
  public synchronized ListIterator<T> listIterator(int index) {
    return m_List.listIterator(index);
  }

  /**
   * Returns a sublist.
   * 
   * @param fromIndex	the starting index
   */
  @Override
  public synchronized List<T> subList(int fromIndex, int toIndex) {
    SortedList<T>	result;
    
    result = new SortedList<T>();
    result.m_Comparator = m_Comparator;
    result.m_List.addAll(m_List.subList(fromIndex, toIndex));
    
    return result;
  }

  /**
   * Returns the hashcode of the list.
   * 
   * @return		the hashcode
   * @see		ArrayList#hashCode()
   */
  @Override
  public synchronized int hashCode() {
    return m_List.hashCode();
  }
  
  /**
   * Returns the list as string.
   * 
   * @return		the string
   * @see		ArrayList#toString()
   */
  @Override
  public synchronized String toString() {
    return m_List.toString();
  }
}
