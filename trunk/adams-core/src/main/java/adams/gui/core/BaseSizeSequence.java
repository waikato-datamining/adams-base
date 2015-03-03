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
 * BaseSizeSequence.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.SizeSequence;

/**
 * Specialized size sequence that can handle mixed row sizes.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseSizeSequence
  extends SizeSequence
  implements Serializable {

  /** for serialization. */
  private static final long serialVersionUID = 4086903170875698686L;

  /** for storing the sizes. */
  protected ArrayList<Integer> m_Sizes;

  /** for storing the overall positions. */
  protected ArrayList<Integer> m_Positions;

  /**
   * Creates a new <code>BaseSizeSequence</code> object
   * that contains no entries.  To add entries, you
   * can use <code>insertEntries</code> or <code>setSizes</code>.
   */
  public BaseSizeSequence() {
    super();

    m_Sizes     = new ArrayList<Integer>();
    m_Positions = new ArrayList<Integer>();
  }

  /**
   * Creates a new <code>BaseSizeSequence</code> object
   * that contains the specified number of entries,
   * all initialized to have size 0.
   *
   * @param numEntries  the number of sizes to track
   * @throws NegativeArraySizeException if <code>numEntries < 0</code>
   */
  public BaseSizeSequence(int numEntries) {
    this(numEntries, 0);
  }

  /**
   * Creates a new <code>BaseSizeSequence</code> object
   * that contains the specified number of entries,
   * all initialized to have size <code>value</code>.
   *
   * @param numEntries  the number of sizes to track
   * @param value       the initial value of each size
   */
  public BaseSizeSequence(int numEntries, int value) {
    super();

    m_Sizes     = new ArrayList<Integer>(numEntries);
    m_Positions = new ArrayList<Integer>(numEntries);

    for (int i = 0; i < numEntries; i++) {
      m_Sizes.add(value);
      m_Positions.add(value);
    }

    updatePositions(0);
  }

  /**
   * Creates a new <code>BaseSizeSequence</code> object
   * that contains the specified sizes.
   *
   * @param sizes  the array of sizes to be contained in
   *		     the <code>BaseSizeSequence</code>
   */
  public BaseSizeSequence(int[] sizes) {
    this();
    setSizes(sizes);
  }

  /**
   * Resets this <code>BaseSizeSequence</code> object,
   * using the data in the <code>sizes</code> argument.
   * This method reinitializes this object so that it
   * contains as many entries as the <code>sizes</code> array.
   * Each entry's size is initialized to the value of the
   * corresponding item in <code>sizes</code>.
   *
   * @param sizes  the array of sizes to be contained in
   *		     this <code>BaseSizeSequence</code>
   */
  public void setSizes(int[] sizes) {
    int		i;

    m_Sizes     = new ArrayList<Integer>(sizes.length);
    m_Positions = new ArrayList<Integer>(sizes.length);

    for (i = 0; i < sizes.length; i++)
      m_Sizes.add(sizes[i]);

    updatePositions(0);
  }

  /**
   * Returns the size of all entries.
   *
   * @return  a new array containing the sizes in this object
   */
  public int[] getSizes() {
    int[]	result;
    int		i;

    result = new int[m_Sizes.size()];
    for (i = 0; i < m_Sizes.size(); i++)
      result[i] = m_Sizes.get(i);

    return result;
  }

  /**
   * Returns the start position for the specified entry.
   * For example, <code>getPosition(0)</code> returns 0,
   * <code>getPosition(1)</code> is equal to
   *   <code>getSize(0)</code>,
   * <code>getPosition(2)</code> is equal to
   *   <code>getSize(0)</code> + <code>getSize(1)</code>,
   * and so on.
   * <p>Note that if <code>index</code> is greater than
   * <code>length</code> the value returned may
   * be meaningless.
   *
   * @param index  the index of the entry whose position is desired
   * @return       the starting position of the specified entry
   */
  public int getPosition(int index) {
    return m_Positions.get(index);
  }

  /**
   * Returns the index of the entry
   * that corresponds to the specified position.
   * For example, <code>getIndex(0)</code> is 0,
   * since the first entry always starts at position 0.
   *
   * @param position  the position of the entry
   * @return  the index of the entry that occupies the specified position
   */
  public int getIndex(int position) {
    int		result;

    result = Collections.binarySearch(m_Positions, position);
    if (result < 0)
      result = -(result + 1);

    if (result > 0)
      result--;

    return result;
  }

  /**
   * Returns the size of the specified entry.
   * If <code>index</code> is out of the range
   * <code>(0 <= index < getSizes().length)</code>
   * the behavior is unspecified.
   *
   * @param index  the index corresponding to the entry
   * @return  the size of the entry, -1 if outside boundaries
   */
  public int getSize(int index) {
    if ((index < 0) || (index >= m_Sizes.size()))
      return -1;
    else
      return m_Sizes.get(index);
  }

  /**
   * Sets the size of the specified entry.
   * Note that if the value of <code>index</code>
   * does not fall in the range:
   * <code>(0 <= index < getSizes().length)</code>
   * the behavior is unspecified.
   *
   * @param index  the index corresponding to the entry
   * @param size   the size of the entry
   */
  public void setSize(int index, int size) {
    m_Sizes.set(index, size);
    updatePositions(index);
  }

  /**
   * Adds a contiguous group of entries to this <code>BaseSizeSequence</code>.
   * Note that the values of <code>start</code> and
   * <code>length</code> must satisfy the following
   * conditions:  <code>(0 <= start < getSizes().length)
   * AND (length >= 0)</code>.  If these conditions are
   * not met, the behavior is unspecified and an exception
   * may be thrown.
   *
   * @param start   the index to be assigned to the first entry
   * 		      in the group
   * @param length  the number of entries in the group
   * @param value   the size to be assigned to each new entry
   * @exception ArrayIndexOutOfBoundsException if the parameters
   *   are outside of the range:
   *   (<code>0 <= start < (getSizes().length)) AND (length >= 0)</code>
   */
  public void insertEntries(int start, int length, int value) {
    int		i;

    for (i = 0; i < length; i++)
      m_Sizes.add(start + i, value);

    updatePositions(start);
  }

  /**
   * Removes a contiguous group of entries
   * from this <code>BaseSizeSequence</code>.
   * Note that the values of <code>start</code> and
   * <code>length</code> must satisfy the following
   * conditions:  <code>(0 <= start < getSizes().length)
   * AND (length >= 0)</code>.  If these conditions are
   * not met, the behavior is unspecified and an exception
   * may be thrown.
   *
   * @param start   the index of the first entry to be removed
   * @param length  the number of entries to be removed
   */
  public void removeEntries(int start, int length) {
    int		i;

    for (i = 0; i < length; i++)
      m_Sizes.remove(start);

    updatePositions(start);
  }

  /**
   * Updates the positions based on the current sizes.
   *
   * @param start	the starting index
   */
  protected void updatePositions(int start) {
    int		i;

    // make sure that the length is the same
    if (m_Positions.size() != m_Sizes.size()) {
      while (m_Positions.size() < m_Sizes.size())
	m_Positions.add(start, 0);
      while (m_Positions.size() > m_Sizes.size())
	m_Positions.remove(start);
    }

    // update values
    for (i = start; i < m_Positions.size(); i++) {
      if (i == 0)
	m_Positions.set(i, 0);
      else
	m_Positions.set(i, m_Positions.get(i - 1) + m_Sizes.get(i));
    }
  }
}
