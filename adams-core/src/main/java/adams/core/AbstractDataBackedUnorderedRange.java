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
 * AbstractDataBackedUnorderedRange.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Ancestor for unordered range classes that allow additional names in the range string,
 * just like placeholders for 'first', 'second', etc). If names contain
 * "-" or "," then they need to be surrounded by double-quotes.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @param <T> the type of the underlying data
 */
public abstract class AbstractDataBackedUnorderedRange<T>
  extends UnorderedRange {

  /** for serialization. */
  private static final long serialVersionUID = 5215987200366396733L;

  /**
   * Simply compares the length of the strings, with longer strings rating
   * lower.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   */
  public static class InvertedStringLengthComparator
    implements Comparator<String>, Serializable {

    /** for serializastion. */
    private static final long serialVersionUID = -3821875620058964511L;

    /**
     * Compares the two strings. Returns -1 if o1 longer than o2, returns 0
     * if both strings have the same length, returns 1 if o2 longer than o1.
     *
     * @param o1	first string
     * @param o2	second string
     * @return		the comparison result
     */
    @Override
    public int compare(String o1, String o2) {
      return -(Integer.compare(o1.length(), o2.length()));
    }
  }

  /** the underlying dataset. */
  protected T m_Data;

  /**
   * Initializes with no range.
   */
  public AbstractDataBackedUnorderedRange() {
    super();
  }

  /**
   * Initializes with the given range, but no maximum.
   *
   * @param range	the range to use
   */
  public AbstractDataBackedUnorderedRange(String range) {
    super(range);
  }

  /**
   * Initializes with the given range and maximum.
   *
   * @param range	the range to use
   * @param max		the maximum of the 1-based index (e.g., use "10" to
   * 			allow "1-10" or -1 for uninitialized)
   */
  public AbstractDataBackedUnorderedRange(String range, int max) {
    super(range, max);
  }
  
  /**
   * For initializing the object.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Data = null;
  }

  /**
   * Returns a clone of the object.
   *
   * @return		the clone
   */
  @Override
  public AbstractDataBackedUnorderedRange<T> getClone() {
    AbstractDataBackedUnorderedRange<T> result;
    
    result = (AbstractDataBackedUnorderedRange<T>) super.getClone();
    result.setData(getData());
    
    return result;
  }

  /**
   * Sets the data to use for interpreting the names.
   * 
   * @param value	the data to use, can be null
   */
  public void setData(T value) {
    m_Data = value;
    if (m_Data == null)
      setMax(-1);
    else
      setMax(getNumNames(value));
    reset();
  }
  
  /**
   * Returns the underlying data.
   * 
   * @return		the underlying data, null if none set
   */
  public T getData() {
    return m_Data;
  }

  /**
   * Returns the number of names the data has.
   * 
   * @param data	the data to retrieve the number of names
   */
  protected abstract int getNumNames(T data);
  
  /**
   * Returns the name at the specified index.
   * 
   * @param data	the data to use
   * @param colIndex	the name index
   * @return		the name
   */
  protected abstract String getName(T data, int colIndex);
}
