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
 * NamedCounter.java
 * Copyright (C) 2012-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Provides counters that can be referenced by a name.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NamedCounter
  implements Serializable {

  /** for serialization. */
  private static final long serialVersionUID = -690032882282626773L;

  /**
   * Comparator for sorting the names based on the counts associated with them.
   * 
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class CounterComparator
    implements Comparator<String> {
    
    /** the basis for the comparison. */
    protected NamedCounter m_Counter;
    
    /**
     * Initalizes the comparator.
     */
    public CounterComparator(NamedCounter counter) {
      if (counter == null)
	throw new IllegalArgumentException("No named counter provided to obtain associated counts from!");
      m_Counter = counter;
    }

    /**
     * Compares its two arguments for order.  Returns a negative integer,
     * zero, or a positive integer as the first argument is less than, equal
     * to, or greater than the second.<p>
     *
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the
     * 	       first argument is less than, equal to, or greater than the
     *	       second.
     */
    @Override
    public int compare(String o1, String o2) {
      if (m_Counter == null)
	return 0;
      
      if (!m_Counter.has(o1))
	return 1;
      else if (!m_Counter.has(o2))
	return -1;
      else
	return new Integer(m_Counter.current(o1)).compareTo(m_Counter.current(o2));
    }
  }
  
  /** for storing the counts. */
  protected Hashtable<String,Integer> m_Counts;

  /**
   * Initializes the counter.
   */
  public NamedCounter() {
    super();
    m_Counts = new Hashtable<String,Integer>();
  }

  /**
   * Clears the counter.
   */
  public synchronized void clear() {
    m_Counts.clear();
  }

  /**
   * Clears only the specific counter.
   * 
   * @param name	the name of the counter
   */
  public synchronized void clear(String name) {
    m_Counts.put(name, 0);
  }

  /**
   * Returns the current counter value.
   *
   * @param name	the name of the counter
   * @return		the current counter value
   */
  public synchronized int current(String name) {
    if (!m_Counts.containsKey(name))
      m_Counts.put(name, 0);
    return m_Counts.get(name);
  }

  /**
   * Returns the incremented counter.
   *
   * @param name	the name of the counter
   * @return		the incremented counter
   */
  public synchronized int next(String name) {
    m_Counts.put(name, current(name) + 1);
    return m_Counts.get(name);
  }

  /**
   * Returns whether the counter has reached the limit.
   *
   * @param name	the name of the counter
   * @param limit	the limit to check
   * @return		true if limit reached (or above)
   */
  public synchronized boolean hasReached(String name, int limit) {
    return (current(name) >= limit);
  }

  /**
   * Returns whether the counter has a value stored at the moment.
   *
   * @param name	the name of the counter
   * @return		true if counter present
   */
  public synchronized boolean has(String name) {
    return m_Counts.containsKey(name);
  }
  
  /**
   * Returns the currently stored names.
   * 
   * @return		the name enumeration
   */
  public synchronized Iterator<String> names() {
    return m_Counts.keySet().iterator();
  }
  
  /**
   * Returns the currently stored names sorted based on associated.
   * 
   * @param asc		if true then names are sorted in ascending manner,
   * 			otherwise in descending manner
   * @return		the name enumeration
   */
  public synchronized Iterator<String> names(boolean asc) {
    List<String>	result;
    CounterComparator	comp;
    
    result = new ArrayList<>(m_Counts.keySet());
    comp   = new CounterComparator(this);
    Collections.sort(result, comp);
    
    if (!asc)
      Collections.reverse(result);
    
    return result.iterator();
  }
  
  /**
   * Returns the currently stored names.
   * 
   * @return		the name set
   */
  public synchronized Set<String> nameSet() {
    return m_Counts.keySet();
  }
  
  /**
   * Returns the current counters as string.
   *
   * @return		the string representation
   */
  @Override
  public synchronized String toString() {
    return m_Counts.toString();
  }
}
