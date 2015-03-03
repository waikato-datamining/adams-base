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
 * WrapperHashSet.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * An extended HashSet that wraps its objects in a wrapper object before
 * storing them in a HashSet. The wrapper object uses the hashCode() method
 * of the payload object in its equals(Object) method for comparison.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <E> the type to store
 */
public class WrapperHashSet<E>
  extends AbstractCollection<E>
  implements Set<E>, Serializable {

  /** for serialization. */
  private static final long serialVersionUID = 7724908287353061207L;

  /**
   * The class that wraps.
   */
  public static class Wrapper
    implements Serializable {

    /** for serialization. */
    private static final long serialVersionUID = -4446213875293513654L;

    /** the payload object. */
    protected Object m_Payload;

    /**
     * Initializes the wrapper.
     *
     * @param payload	the payload to wrap around
     */
    public Wrapper(Object payload) {
      super();

      m_Payload = payload;
    }

    /**
     * Returns the stored object.
     *
     * @return		the payload
     */
    public Object getPayload() {
      return m_Payload;
    }

    /**
     * Returns the hashcode of the payload object.
     *
     * @return		the hashcode of the payload
     */
    @Override
    public int hashCode() {
      return m_Payload.hashCode();
    }

    /**
     * Compares the hashcodes of the payloads.
     *
     * @param o		the object to compare with
     * @return		true if the hashcodes of the payloads are the same
     */
    @Override
    public boolean equals(Object o) {
      if (o == null)
	return false;
      return (((Wrapper) o).hashCode() == hashCode());
    }

    /**
     * Returns the string representation of the payload.
     *
     * @return		the string representation of the payload
     */
    @Override
    public String toString() {
      return m_Payload.toString();
    }
  }

  /** the hashset used internally. */
  protected HashSet<Wrapper> m_HashSet;

  /**
   * Constructs a new, empty set; the backing <tt>HashMap</tt> instance has
   * default initial capacity (16) and load factor (0.75).
   */
  public WrapperHashSet() {
    super();

    m_HashSet = new HashSet<Wrapper>();
  }

  /**
   * Constructs a new set containing the elements in the specified
   * collection.  The <tt>HashMap</tt> is created with default load factor
   * (0.75) and an initial capacity sufficient to contain the elements in
   * the specified collection.
   *
   * @param c the collection whose elements are to be placed into this set
   * @throws NullPointerException if the specified collection is null
   */
  public WrapperHashSet(Collection<? extends E> c) {
    super();

    m_HashSet = new HashSet<Wrapper>();
    addAll(c);
  }

  /**
   * Constructs a new, empty set; the backing <tt>HashMap</tt> instance has
   * the specified initial capacity and the specified load factor.
   *
   * @param      initialCapacity   the initial capacity of the hash map
   * @param      loadFactor        the load factor of the hash map
   * @throws     IllegalArgumentException if the initial capacity is less
   *             than zero, or if the load factor is nonpositive
   */
  public WrapperHashSet(int initialCapacity, float loadFactor) {
    super();

    m_HashSet = new HashSet<Wrapper>(initialCapacity, loadFactor);
  }

  /**
   * Constructs a new, empty set; the backing <tt>HashMap</tt> instance has
   * the specified initial capacity and default load factor (0.75).
   *
   * @param      initialCapacity   the initial capacity of the hash table
   * @throws     IllegalArgumentException if the initial capacity is less
   *             than zero
   */
  public WrapperHashSet(int initialCapacity) {
    super();

    m_HashSet = new HashSet<Wrapper>(initialCapacity);
  }

  /**
   * Removes all of the elements from this set.
   * The set will be empty after this call returns.
   */
  @Override
  public void clear() {
    m_HashSet.clear();
  }

  /**
   * Returns a shallow copy of this <tt>HashSet</tt> instance: the elements
   * themselves are not cloned.
   *
   * @return a shallow copy of this set
   */
  @Override
  public Object clone() {
    WrapperHashSet<E> 	result;

    result = new WrapperHashSet<E>();
    result.m_HashSet = (HashSet<Wrapper>) m_HashSet.clone();

    return result;
  }

  /**
   * Returns an iterator over the elements in this set.  The elements
   * are returned in no particular order.
   *
   * @return an Iterator over the elements in this set
   * @see ConcurrentModificationException
   */
  @Override
  public Iterator<E> iterator() {
    List<E>	result;

    result = new ArrayList<E>();
    for (Wrapper w: m_HashSet)
      result.add((E) w.getPayload());

    return result.iterator();
  }

  /**
   * Returns <tt>true</tt> if this set contains the specified element.
   * More formally, returns <tt>true</tt> if and only if this set
   * contains an element <tt>e</tt> such that
   * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>.
   *
   * @param o element whose presence in this set is to be tested
   * @return <tt>true</tt> if this set contains the specified element
   */
  @Override
  public boolean contains(Object o) {
    return m_HashSet.contains(new Wrapper(o));
  }

  /**
   * Adds the specified element to this set if it is not already present.
   * More formally, adds the specified element <tt>e</tt> to this set if
   * this set contains no element <tt>e2</tt> such that
   * <tt>(e==null&nbsp;?&nbsp;e2==null&nbsp;:&nbsp;e.equals(e2))</tt>.
   * If this set already contains the element, the call leaves the set
   * unchanged and returns <tt>false</tt>.
   *
   * @param e element to be added to this set
   * @return <tt>true</tt> if this set did not already contain the specified
   * element
   */
  @Override
  public boolean add(E e) {
    return m_HashSet.add(new Wrapper(e));
  }

  /**
   * Removes the specified element from this set if it is present.
   * More formally, removes an element <tt>e</tt> such that
   * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>,
   * if this set contains such an element.  Returns <tt>true</tt> if
   * this set contained the element (or equivalently, if this set
   * changed as a result of the call).  (This set will not contain the
   * element once the call returns.)
   *
   * @param o object to be removed from this set, if present
   * @return <tt>true</tt> if the set contained the specified element
   */
  @Override
  public boolean remove(Object o) {
    return m_HashSet.remove(new Wrapper(o));
  }

  /**
   * Returns <tt>true</tt> if this set contains no elements.
   *
   * @return <tt>true</tt> if this set contains no elements
   */
  @Override
  public boolean isEmpty() {
    return m_HashSet.isEmpty();
  }

  /**
   * Returns the number of elements in this set (its cardinality).
   *
   * @return the number of elements in this set (its cardinality)
   */
  @Override
  public int size() {
    return m_HashSet.size();
  }

  /**
   * Returns a string representation of the stored objects.
   *
   * @return		the string representation
   */
  @Override
  public String toString() {
    return m_HashSet.toString();
  }
}
