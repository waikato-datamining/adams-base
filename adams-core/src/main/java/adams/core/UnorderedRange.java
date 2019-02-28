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
 * UnorderedRange.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.core;

import adams.data.spreadsheet.SpreadSheetUtils;
import adams.data.statistics.StatUtils;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.io.Serializable;

/**
 * Like {@link Range}, but enforces no ordering on indices.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class UnorderedRange
  implements Serializable, CustomDisplayStringProvider, Comparable<UnorderedRange>,
             ExampleProvider, HelpProvider, CloneHandler<UnorderedRange> {

  private static final long serialVersionUID = -6836829341589554650L;

  /** the special string "-". */
  public final static String RANGE = Range.RANGE;

  /** the special string ",". */
  public final static String SEPARATOR = Range.SEPARATOR;

  /** the special string "first". */
  public final static String FIRST = Range.FIRST;

  /** the special string "second". */
  public final static String SECOND = Range.SECOND;

  /** the special string "third". */
  public final static String THIRD = Range.THIRD;

  /** the special string "last_1" (2nd to last). */
  public final static String LAST_1 = Range.LAST_1;

  /** the special string "last_2" (3rd to last). */
  public final static String LAST_2 = Range.LAST_2;

  /** the special string "last". */
  public final static String LAST = Range.LAST;

  /** "first-last" constant. */
  public final static String ALL = Range.ALL;

  /** the raw range string. */
  protected String m_Raw;

  /** the maximum for the 1-based range. */
  protected int m_Max;

  /** the parsed indices. */
  protected TIntList m_Indices;

  /**
   * Initializes with no range.
   */
  public UnorderedRange() {
    this("");
  }

  /**
   * Initializes with the given range, but no maximum.
   *
   * @param range	the range to use
   */
  public UnorderedRange(String range) {
    this(range, -1);
  }

  /**
   * Initializes with the given range and maximum.
   *
   * @param range	the range to use
   * @param max		the maximum of the 1-based index (e.g., use "10" to
   * 			allow "1-10" or -1 for uninitialized)
   */
  public UnorderedRange(String range, int max) {
    super();

    initialize();
    setRange(range);
    setMax(max);
  }

  /**
   * The initializes the members.
   */
  protected void initialize() {
    m_Raw     = "";
    m_Indices = null;
  }

  /**
   * Resets the state.
   */
  protected void reset() {
    m_Indices = null;
  }

  /**
   * Sets the range.
   *
   * @param value	the range to use
   */
  public void setRange(String value) {
    reset();
    m_Raw = value;
  }

  /**
   * Returns the currently set range.
   *
   * @return		the range in use
   */
  public String getRange() {
    return m_Raw;
  }

  /**
   * Checks whether a valid range has been supplied.
   *
   * @return		true if a valid range is available
   */
  public boolean hasRange() {
    return (getRange().length() > 0);
  }

  /**
   * Returns whether the range encompasses all.
   *
   * @return		true if {@link #ALL}
   * @see		#ALL
   */
  public boolean isAllRange() {
    return getRange().equals(Range.ALL);
  }

  /**
   * Returns whether the range is empty.
   *
   * @return		true if empty
   */
  public boolean isEmpty() {
    return getRange().isEmpty();
  }

  /**
   * Sets the maximum (1-max will be allowed).
   *
   * @param value	the maximum for the 1-based index
   */
  public void setMax(int value) {
    if (value != m_Max) {
      if (value <= 0)
	m_Max = -1;
      else
	m_Max = value;
      reset();
    }
  }

  /**
   * Returns the maximum.
   *
   * @return		the maximum for the 1-based index
   */
  public int getMax() {
    return m_Max;
  }

  /**
   * Splits the range into subranges.
   *
   * @param range	the range to split
   * @return		the subranges
   */
  protected String[] splitRange(String range) {
    return SpreadSheetUtils.split(range, ',');
  }

  /**
   * Parses the subrange.
   *
   * @param subrange	the subrange
   * @return		the indices
   */
  protected int[] parseSubRange(String subrange) {
    Range	range;

    range = new Range(subrange);
    range.setMax(m_Max);

    return range.getIntIndices();
  }

  /**
   * Parses the range.
   */
  protected void parse() {
    String[]	ranges;

    if (m_Indices != null)
      return;

    m_Indices = new TIntArrayList();
    ranges    = splitRange(m_Raw);
    for (String r: ranges)
      m_Indices.addAll(parseSubRange(r));
  }

  /**
   * Returns the integer indices. Gets always generated on-the-fly!
   *
   * @return		the indices, 0-length array if not possible
   */
  public synchronized int[] getIntIndices() {
    parse();
    return m_Indices.toArray();
  }

  /**
   * Sets the selected indices. Generates a range string out of the array.
   *
   * @param indices	the indices (0-based)
   */
  public void setIndices(Integer[] indices) {
    setIndices(StatUtils.toIntArray(indices));
  }

  /**
   * Sets the selected indices. Generates a range string out of the array.
   *
   * @param indices	the indices (0-based)
   */
  public void setIndices(int[] indices) {
    StringBuilder	range;

    range = new StringBuilder();
    for (int index: indices) {
      if (range.length() > 0)
        range.append(",");
      range.append("" + (index + 1));
    }

    setRange(range.toString());
  }

  /**
   * Checks whether the provided 0-based index is within the range.
   *
   * @param index	the index to check
   * @return		true if in range
   */
  public boolean isInRange(int index) {
    parse();
    return m_Indices.contains(index);
  }

  /**
   * Returns a clone of the object.
   *
   * @return		the clone
   */
  @Override
  public UnorderedRange getClone() {
    return new UnorderedRange(getRange());
  }

  /**
   * Returns the example.
   *
   * @return		the example
   */
  @Override
  public String getExample() {
    return
        "An unordered range is a comma-separated list of single 1-based indices or "
      + "sub-ranges of indices ('start-end'); "
      + "the following placeholders can be used as well: "
      + FIRST + ", " + SECOND + ", " + THIRD + ", " + LAST_2 + ", " + LAST_1 + ", " + LAST;
  }

  /**
   * Returns a URL with additional information.
   *
   * @return		the URL, null if not available
   */
  public String getHelpURL() {
    return null;
  }

  /**
   * Returns a long help description, e.g., used in tiptexts.
   *
   * @return		the help text, null if not available
   */
  public String getHelpDescription() {
    return getExample();
  }

  /**
   * Returns a short title for the help, e.g., used for buttons.
   *
   * @return		the short title, null if not available
   */
  public String getHelpTitle() {
    return null;
  }

  /**
   * Returns the name of a help icon, e.g., used for buttons.
   *
   * @return		the icon name, null if not available
   */
  public String getHelpIcon() {
    return "help.gif";
  }

  /**
   * Compares this range with the specified range for order.  Returns a
   * negative integer, zero, or a positive integer as this subrange is less
   * than, equal to, or greater than the specified subrange.
   * Uses the "from" as point of comparison and if those are equal, then
   * the "to" (if available).
   *
   * @param   o the range to be compared.
   * @return  a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   */
  @Override
  public int compareTo(UnorderedRange o) {
    int		result;
    int[]	indicesThis;
    int[]	indicesOther;
    int		i;

    indicesThis  = getIntIndices();
    indicesOther = o.getIntIndices();

    result = Integer.compare(indicesThis.length, indicesOther.length);

    if (result == 0) {
      for (i = 0; i < indicesThis.length; i++) {
	result = Integer.compare(indicesThis[i], indicesOther[i]);
	if (result != 0)
	  break;
      }
    }

    return result;
  }

  /**
   * Returns the explicit range, i.e., just comma-separated (1-based) indices.
   *
   * @return		the string
   */
  public String toExplicitRange() {
    StringBuilder	result;
    int[]		indices;
    int			i;

    result  = new StringBuilder();
    indices = getIntIndices();
    for (i = 0; i < indices.length; i++) {
      if (i > 0)
	result.append(",");
      result.append("" + (indices[i] + 1));
    }

    return result.toString();
  }

  /**
   * Returns the custom display string.
   *
   * @return		the string
   */
  @Override
  public String toDisplay() {
    return getRange();
  }

  /**
   * Returns a string representation of the range.
   *
   * @return		the representation
   */
  @Override
  public String toString() {
    return "unordered_range=" + getRange() + ", max=" + m_Max;
  }

  /**
   * Turns the integer indices into a unordered range object.
   *
   * @param indices	the 0-based indices
   * @return		the generated range
   */
  public static UnorderedRange toRange(int[] indices) {
    UnorderedRange 	result;

    result = new UnorderedRange();
    if (indices.length != 0)
      result.setIndices(indices);

    return result;
  }

  /**
   * Returns whether the range string is valid.
   *
   * @param s		the range to check
   * @param max		the maximum
   * @return		true if a valid range string
   */
  public static boolean isValid(String s, int max) {
    boolean		result;
    String[]		ranges;
    Range		range;
    String		clean;

    result = true;
    ranges = new UnorderedRange().splitRange(s);
    for (String r: ranges) {
      range = new Range(r, max);
      clean = range.clean(r);
      result = r.equals(clean) && (clean.length() > 0);
      if (!result)
        break;
    }

    return result;
  }
}
