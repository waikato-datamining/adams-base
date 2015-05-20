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
 * Range.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import gnu.trove.list.array.TIntArrayList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * A class for managing a range of 1-based indices, e.g., 1-5, 3,7,9 or 1-7,9
 * (including "first", "second", "third", "last_2", "last_1" and "last").
 * A range can be inverted by surrounding it with "inv(...)".
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Range
  implements Serializable, CustomDisplayStringProvider, Comparable<Range>, 
             ExampleProvider, HelpProvider, CloneHandler<Range> {

  /** for serialization. */
  private static final long serialVersionUID = -7995710565507092711L;

  /**
   * Represents a sub-range, either a single number of from-to.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class SubRange
    implements Serializable, Comparable<SubRange> {

    /** for serialization. */
    private static final long serialVersionUID = -1352323320597824993L;

    /** the "from" (0-based). */
    protected Integer m_From;

    /** the "to" (0-based). */
    protected Integer m_To;

    /**
     * Initializes the sub-range as single number (0-based).
     *
     * @param from	the single number of the sub-range
     */
    public SubRange(int from) {
      this(from, null);
    }

    /**
     * Initializes the sub-range as range between (0-based) numbers.
     *
     * @param from	the start of the sub-range (incl)
     * @param to	the end of the sub-range (incl)
     */
    public SubRange(int from, Integer to) {
      super();

      m_From = from;
      m_To   = to;
    }

    /**
     * Returns the "from" part of the sub-range.
     *
     * @return		the from
     */
    public Integer getFrom() {
      return m_From;
    }

    /**
     * Returns whether a "to" is available.
     *
     * @return		true if a "to" is available
     */
    public boolean hasTo() {
      return (m_To != null);
    }

    /**
     * Returns the "to" of the sub-range.
     *
     * @return		the "to", null if not set
     */
    public Integer getTo() {
      return m_To;
    }

    /**
     * Checks whether the given index is within the limits of the sub-range.
     *
     * @param index	the (0-based) index to check
     * @return		true if within range
     */
    public boolean isInRange(int index) {
      if (m_To == null)
	return (m_From == index);
      else
	return ((m_From <= index) && (index <= m_To));
    }

    /**
     * Compares this subrange with the specified subrange for order.  Returns a
     * negative integer, zero, or a positive integer as this subrange is less
     * than, equal to, or greater than the specified subrange.
     * Uses the "from" as point of comparison and if those are equal, then
     * the "to" (if available).
     *
     * @param   o the subrange to be compared.
     * @return  a negative integer, zero, or a positive integer as this object
     *		is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(SubRange o) {
      int	result;

      result = getFrom().compareTo(o.getFrom());

      if (result == 0) {
	if (hasTo() && o.hasTo()) {
	  result = getTo().compareTo(o.getTo());
	}
	else if (!hasTo()) {
	  result = -1;
	}
	else {
	  result = +1;
	}
      }

      return result;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj	the reference object with which to compare.
     * @return		true if this object is the same as the obj argument;
     * 			false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof SubRange))
	return false;
      else
	return (compareTo((SubRange) obj) == 0);
    }

    /**
     * Returns a string representation of the sub-range.
     *
     * @return		the representation
     */
    @Override
    public String toString() {
      if (m_To == null)
	return "num=" + m_From;
      else
	return "from=" + m_From + ", to=" + m_To;
    }
  }

  /** the special string "-". */
  public final static String RANGE = "-";

  /** the special string ",". */
  public final static String SEPARATOR = ",";

  /** the special string "first". */
  public final static String FIRST = "first";

  /** the special string "second". */
  public final static String SECOND = "second";

  /** the special string "third". */
  public final static String THIRD = "third";

  /** the special string "last_1" (2nd to last). */
  public final static String LAST_1 = "last_1";

  /** the special string "last_2" (3rd to last). */
  public final static String LAST_2 = "last_2";

  /** the special string "last". */
  public final static String LAST = "last";

  /** the start string for inversion. */
  public final static String INV_START = "inv(";

  /** the end string for inversion. */
  public final static String INV_END = ")";

  /** "first-last" constant. */
  public final static String ALL = FIRST + RANGE + LAST;

  /** the uncleaned range string. */
  protected String m_Raw;

  /** the range string. */
  protected String m_Range;

  /** the actual range, without the inversion. */
  protected String m_ActualRange;

  /** the maximum for the 1-based range. */
  protected int m_Max;

  /** whether the range is inverted. */
  protected Boolean m_Inverted;

  /** the range parts. */
  protected List<SubRange> m_SubRanges;

  /**
   * Initializes with no range.
   */
  public Range() {
    this("");
  }

  /**
   * Initializes with the given range, but no maximum.
   *
   * @param range	the range to use
   */
  public Range(String range) {
    this(range, -1);
  }

  /**
   * Initializes with the given range and maximum.
   *
   * @param range	the range to use
   * @param max		the maximum of the 1-based index (e.g., use "10" to
   * 			allow "1-10" or -1 for uninitialized)
   */
  public Range(String range, int max) {
    super();

    initialize();
    setRange(range);
    setMax(max);
    getRange();
  }

  /**
   * Initializes the object.
   */
  protected void initialize() {
    m_Range = null;
    m_Raw   = null;
    m_Max   = -1;
    
    reset();
  }

  /**
   * Resets the object.
   */
  protected void reset() {
    m_SubRanges   = null;
    m_Inverted    = null;
    m_ActualRange = null;
  }
  
  /**
   * Sets the range.
   *
   * @param value	the range to use
   */
  public void setRange(String value) {
    reset();
    m_Raw   = value;
    m_Range = null;
  }

  /**
   * Sets the selected indices. Generates a range string out of the array.
   *
   * @param indices	the indices (0-based)
   */
  public void setIndices(Integer[] indices) {
    int[]	intIndices;
    int		i;

    intIndices = new int[indices.length];
    for (i = 0; i < indices.length; i++)
      intIndices[i] = indices[i];

    setIndices(intIndices);
  }

  /**
   * Sets the selected indices. Generates a range string out of the array.
   *
   * @param indices	the indices (0-based)
   */
  public void setIndices(int[] indices) {
    StringBuilder	range;
    int			i;
    int			start;
    int			end;
    int			diff;

    range = new StringBuilder();

    start = -1;
    end   = -1;
    if (indices.length > 0)
      start = indices[0];
    for (i = 1; i < indices.length; i++) {
      diff = indices[i] - indices[i - 1];
      if (diff > 1) {
	if (range.length() > 0)
	  range.append(",");
	if (start != indices[i - 1])
	  range.append((start + 1) + "-" + (indices[i - 1] + 1));
	else
	  range.append((start + 1));
	start = indices[i];
	end   = -1;
      }
      else {
	end = indices[i];
      }
    }

    if (start != -1) {
      if (end != -1) {
	if (range.length() > 0)
	  range.append(",");
	if (start != end)
	  range.append((start + 1) + "-" + (end + 1));
	else
	  range.append((start + 1));
      }
      else {
	if (range.length() > 0)
	  range.append(",");
	range.append((start + 1));
      }
    }

    setRange(range.toString());
  }

  /**
   * Checks whether the range is inverted.
   *
   * @return		true if inverted
   */
  public synchronized boolean isInverted()  {
    if (m_Inverted == null)
      m_Inverted = getRange().startsWith(INV_START) && getRange().endsWith(INV_END);

    return m_Inverted;
  }

  /**
   * Sets whether the range is inverted or not.
   *
   * @param value	if true then the range is inverted
   */
  public void setInverted(boolean value) {
    if (value)
      setRange(INV_START + getActualRange() + INV_END);
    else
      setRange(getActualRange());
  }

  /**
   * Returns the currently set range.
   *
   * @return		the range in use
   */
  public synchronized String getRange() {
    if (m_Range == null)
      m_Range = clean(m_Raw);
    
    if (m_Range.isEmpty())
      return m_Raw;
    else
      return m_Range;
  }
  
  /**
   * Returns the actual range, without inversion.
   * 
   * @return		the actual range
   */
  protected synchronized String getActualRange() {
    if (m_ActualRange == null) {
      if (isInverted())
	m_ActualRange = getRange().substring(INV_START.length(), getRange().length() - INV_END.length());
      else
	m_ActualRange = getRange();
    }
    
    return m_ActualRange;
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
   * Checks whether a valid range has been supplied.
   *
   * @return		true if a valid range is available
   */
  public boolean hasRange() {
    return (getRange().length() > 0);
  }

  /**
   * Removes invalid characters. Digits, {@link #RANGE} and {@link #SEPARATOR}
   * get added automatically.
   * 
   * @param s		the string to process
   * @param valid	the list of valid characters/placeholders (processing happens in this order!)
   * @return		the processed string
   */
  protected String removeInvalidChars(String s, List<String> valid) {
    String			shortened;
    StringBuilder		result;
    int				i;
    char			chr;
    List<Character>		chars;
    HashMap<Character,String>	relation;
    
    // add implicit chars
    for (chr = '0'; chr <= '9'; chr++)
      valid.add(Character.toString(chr));
    valid.add(RANGE);
    valid.add(SEPARATOR);

    // build replacement relation
    chars    = new ArrayList<Character>();
    relation = new HashMap<Character,String>();
    for (i = 0; i < valid.size(); i++) {
      chr = (char) i;
      chars.add(chr);
      relation.put(chr, valid.get(i));
    }
    
    // replace strings with chars
    shortened = s.toLowerCase();
    for (i = 0; i < chars.size(); i++)
      shortened = shortened.replace(relation.get(chars.get(i)).toLowerCase(), chars.get(i).toString());
    
    // ignore invalid chars
    result = new StringBuilder();
    for (i = 0; i < shortened.length(); i++) {
      chr = shortened.charAt(i);
      if (relation.containsKey(chr))
	result.append(relation.get(chr));
    }
    
    return result.toString();
  }

  /**
   * Returns the placeholders to allow in the ranges.
   * 
   * @return		the placeholders
   */
  protected List<String> getPlaceholders() {
    return new ArrayList<String>(
	Arrays.asList(
	    new String[]{
		FIRST, 
		SECOND, 
		THIRD, 
		LAST_2, 
		LAST_1, 
		LAST, 
		INV_START, 
		INV_END,
	    }));
  }
  
  /**
   * Removes invalid characters.
   * 
   * @param s		the string to process
   * @return		the processed string
   */
  protected String removeInvalidChars(String s) {
    return removeInvalidChars(s, getPlaceholders());
  }
  
  /**
   * Returns whether invalid characters should get removed.
   * <br><br>
   * Default implementation always returns true.
   * 
   * @return		true if to replace invalid chars
   */
  protected boolean canReplaceInvalidChars() {
    return true;
  }
  
  /**
   * Attempts to split a list into the parts resembling it.
   * 
   * @param s		the string to split
   * @return		the parts (single array element if no list)
   */
  protected String[] splitList(String s) {
    return s.split(SEPARATOR);
  }
  
  /**
   * Attempts to split a range into the parts resembling it.
   * 
   * @param s		the string to split
   * @return		the parts (single array element if no range)
   */
  protected String[] splitRange(String s) {
    return s.split(RANGE);
  }
  
  /**
   * Cleanses the given string. Only allows "first", "last", ",", "-" and numbers.
   *
   * @param s		the string to clean
   * @return		the cleansed string, "" if invalid one provided
   */
  protected String clean(String s) {
    StringBuilder	result;
    String		tmp;
    int			i;
    String[]		ranges;
    String[]		parts;
    int			from;
    int			to;
    boolean		inverted;

    result = new StringBuilder();

    // remove all invalid characters
    if (canReplaceInvalidChars())
      tmp = removeInvalidChars(s);
    else
      tmp = s;

    // test for inversion
    inverted = false;
    if (tmp.length() >= INV_START.length() + 1 + INV_END.length()) {
      inverted =    tmp.startsWith(INV_START)
                 && tmp.endsWith(INV_END);
      if (inverted)
	tmp = tmp.substring(INV_START.length(), tmp.length() - INV_END.length());
    }

    // remove invalid sub-ranges
    ranges = splitList(tmp);
    for (i = 0; i < ranges.length; i++) {
      parts = splitRange(ranges[i]);
      // single number?
      if (parts.length == 1) {
	try {
	  if (canReplaceInvalidChars())
	    parse(parts[0]);
	  if (result.length() > 0)
	    result.append(SEPARATOR);
	  result.append(parts[0]);
	}
	catch (Exception e) {
	  // ignored
	}
      }
      // from-to?
      else {
	if (parts.length == 2) {
	  try {
	    if (canReplaceInvalidChars()) {
	      from = parse(parts[0], Integer.MAX_VALUE);
	      to   = parse(parts[1], Integer.MAX_VALUE);
	    }
	    else {
	      from = 0;
	      to   = 0;
	    }
	    if (from <= to) {
	      if (result.length() > 0)
		result.append(SEPARATOR);
	      result.append(ranges[i]);
	    }
	  }
	  catch (Exception e) {
	    // ignored
	  }
	}
      }
    }

    if (inverted) {
      result.insert(0, INV_START);
      result.append(INV_END);
    }

    return result.toString();
  }

  /**
   * Checks whether the string represents a placeholder.
   * 
   * @param s		the string to check
   * @return		true if a placeholder
   */
  protected boolean isPlaceholder(String s) {
    String	tmp;
    
    tmp = s.toLowerCase();
    
    if (tmp.equals(FIRST))
      return true;
    else if (tmp.equals(SECOND))
      return true;
    else if (tmp.equals(THIRD))
      return true;
    else if (tmp.equals(LAST_2))
      return true;
    else if (tmp.equals(LAST_1))
      return true;
    else if (tmp.equals(LAST))
      return true;
    else
      return false;
  }

  /**
   * Parses the placeholder.
   * 
   * @param s		the placeholder to parse
   * @param max		the max to use
   * @return		the placeholder's integer equivalent, -1 if not a placeholder
   */
  protected int parsePlaceholder(String s, int max) {
    if (s.equals(FIRST))
      return 0;
    else if (s.equals(SECOND))
      return 1;
    else if (s.equals(THIRD))
      return 2;
    else if (s.equals(LAST_2))
      return max - 3;
    else if (s.equals(LAST_1))
      return max - 2;
    else if (s.equals(LAST))
      return max - 1;
    else
      return -1;
  }

  /**
   * Parses the 1-based index, 'first' and 'last' are accepted as well.
   *
   * @param s		the string to parse
   * @param max		the maximum value to use
   * @return		the 0-based index
   */
  protected int parse(String s, int max) {
    int		result;

    if (isPlaceholder(s)) {
      result = parsePlaceholder(s, max);
    }
    else {
      try {
        result = Integer.parseInt(s) - 1;
      }
      catch (Exception e) {
	result = -1;
      }
    }

    return result;
  }

  /**
   * Parses the 1-based index, 'first' and 'last' are accepted as well.
   *
   * @param s		the string to parse
   * @return		the 0-based index
   */
  protected int parse(String s) {
    return parse(s, m_Max);
  }

  /**
   * Parses the string and generates the sub-ranges.
   *
   * @param errors	for adding errors to it
   * @return		the parsed sub-ranges
   */
  protected List<SubRange> parse(StringBuilder errors) {
    List<SubRange>	result;
    String[]		ranges;
    String[]		parts;
    int			i;
    int			from;
    int			to;

    if (errors.length() > 0)
      errors.delete(0, errors.length());

    result = new ArrayList<SubRange>();
    if (m_Max == -1)
      return result;

    ranges = splitList(getActualRange());
    for (i = 0; i < ranges.length; i++) {
      if (ranges[i].length() == 0)
	continue;
      parts = splitRange(ranges[i]);
      if (parts.length == 1) {
	from = parse(parts[0]);
	if ((from >= 0) && (from < m_Max))
	  result.add(new SubRange(from));
	else
	  errors.append(parts[0] + "\n");
      }
      else {
	from = parse(parts[0]);
	to   = parse(parts[1]);
	if ((from <= to) && (from >= 0) && (to < m_Max))
	  result.add(new SubRange(from, to));
	else if ((from <= to) && (from >= 0) && (from < m_Max))
	  result.add(new SubRange(from, m_Max));
	else
	  errors.append(ranges[i] + "\n");
      }
    }

    return result;
  }

  /**
   * Returns the sub-ranges, initializes them if necessary.
   *
   * @return		the sub-ranges
   */
  protected synchronized List<SubRange> getSubRanges() {
    if (m_SubRanges == null)
      m_SubRanges = parse(new StringBuilder());

    return m_SubRanges;
  }

  /**
   * Checks whether the provided 0-based index is within the range.
   *
   * @param index	the index to check
   * @return		true if in range
   */
  public boolean isInRange(int index) {
    boolean		result;
    int			i;
    List<SubRange>	ranges;

    result = isInverted();

    ranges = getSubRanges();

    for (i = 0; i < ranges.size(); i++) {
      if (isInverted()) {
	if (ranges.get(i).isInRange(index)) {
	  result = false;
	  break;
	}
      }
      else {
	if (ranges.get(i).isInRange(index)) {
	  result = true;
	  break;
	}
      }
    }

    return result;
  }

  /**
   * Returns the integer indices. Gets always generated on-the-fly!
   *
   * @return		the indices, 0-length array if not possible
   */
  public int[] getIntIndices() {
    TIntArrayList	result;
    int			i;

    // collect indices
    result = new TIntArrayList();
    for (i = 0; i < m_Max; i++) {
      if (isInRange(i))
	result.add(i);
    }

    return result.toArray();
  }

  /**
   * Turns the range into a list of from-to segements. The indices are 0-based.
   * In case a subrange consists only of a single index, the second one is the
   * same.
   *
   * @return		the segments
   */
  public int[][] getIntSegments() {
    int[][]	result;
    int		i;
    SubRange	sub;

    if (getSubRanges() == null)
      return new int[0][];

    result = new int[getSubRanges().size()][2];
    for (i = 0; i < getSubRanges().size(); i++) {
      sub          = getSubRanges().get(i);
      result[i][0] = sub.getFrom();
      if (sub.hasTo())
	result[i][1] = sub.getTo();
      else
	result[i][1] = sub.getFrom();
    }

    return result;
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
   * Compares this subrange with the specified subrange for order.  Returns a
   * negative integer, zero, or a positive integer as this subrange is less
   * than, equal to, or greater than the specified subrange.
   * Uses the "from" as point of comparison and if those are equal, then
   * the "to" (if available).
   *
   * @param   o the subrange to be compared.
   * @return  a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   */
  @Override
  public int compareTo(Range o) {
    int		result;
    int[]	indicesThis;
    int[]	indicesOther;
    int		i;

    indicesThis  = getIntIndices();
    indicesOther = o.getIntIndices();

    result = new Integer(indicesThis.length).compareTo(new Integer(indicesOther.length));

    if (result == 0) {
      for (i = 0; i < indicesThis.length; i++) {
	result = new Integer(indicesThis[i]).compareTo(new Integer(indicesOther[i]));
	if (result != 0)
	  break;
      }
    }

    return result;
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
    if (!(obj instanceof Range))
      return false;
    else
      return (compareTo((Range) obj) == 0);
  }

  /**
   * Hashcode so can be used as hashtable key. Returns the hashcode of the
   * range string.
   *
   * @return		the hashcode
   */
  @Override
  public int hashCode() {
    return getRange().hashCode();
  }

  /**
   * Returns a clone of the object.
   *
   * @return		the clone
   */
  public Range getClone() {
    Range	result;
    
    try {
      result = getClass().newInstance();
    }
    catch (Exception e) {
      throw new IllegalStateException("Failed to create new instance of " + getClass().getName(), e);
    }
    
    result.setMax(getMax());
    result.setRange(getRange());
    
    return result;
  }

  /**
   * Returns a string representation of the range.
   *
   * @return		the representation
   */
  @Override
  public String toString() {
    return "range=" + getRange() + ", max=" + m_Max + ", inv=" + isInverted();
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
   * Returns the example.
   *
   * @return		the example
   */
  @Override
  public String getExample() {
    return
        "A range is a comma-separated list of single 1-based indices or "
      + "sub-ranges of indices ('start-end'); "
      + "'inv(...)' inverts the range '...'; "
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
    return "help2.png";
  }

  /**
   * Turns the integer indices into a range object.
   *
   * @param indices	the 0-based indices
   * @return		the generated range
   */
  public static Range toRange(int[] indices) {
    Range		result;
    StringBuilder	range;
    int			i;
    Integer		start;
    Integer		current;

    if (indices.length == 0)
      return new Range();

    range   = new StringBuilder();
    i       = 1;
    start   = indices[0];
    current = indices[0];
    range.append((current + 1));
    while (i < indices.length) {
      if (indices[i] - current > 1) {
	if (start < current) {
	  range.append("-");
	  range.append((current + 1));
	}
	range.append(",");
	start   = indices[i];
	current = indices[i];
	range.append((current + 1));
      }
      else {
	current = indices[i];
      }

      i++;

      if (i == indices.length) {
	if (start < current) {
	  range.append("-");
	  range.append((current + 1));
	}
      }
    }

    result = new Range(range.toString());

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
    Range		range;

    range  = new Range(s);
    result = s.equals(range.getRange()) && (range.getRange().length() > 0);

    return result;
  }
}
