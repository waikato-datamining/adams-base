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
 * AbstractDataBackedRange.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import adams.data.spreadsheet.SpreadSheetUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Ancestor for range classes that allow additional names in the range string,
 * just like placeholders for 'first', 'second', etc). If names contain
 * "-" or "," then they need to be surrounded by double-quotes.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of the underlying data
 */
public abstract class AbstractDataBackedRange<T>
  extends Range {

  /** for serialization. */
  private static final long serialVersionUID = 5215987200366396733L;

  /**
   * Simply compares the length of the strings, with longer strings rating
   * lower.
   * 
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
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
      return -(new Integer(o1.length()).compareTo(o2.length()));
    }
  }
  
  /** the underlying dataset. */
  protected T m_Data;
  
  /** the names to replace. */
  protected List<String> m_Names;
  
  /** the indices of the names. */
  protected HashMap<String,Integer> m_Indices;
  
  /** the comparator to use. */
  protected transient Comparator m_Comparator;

  /**
   * Initializes with no range.
   */
  public AbstractDataBackedRange() {
    super();
  }

  /**
   * Initializes with the given range, but no maximum.
   *
   * @param range	the range to use
   */
  public AbstractDataBackedRange(String range) {
    super(range);
  }

  /**
   * Initializes with the given range and maximum.
   *
   * @param range	the range to use
   * @param max		the maximum of the 1-based index (e.g., use "10" to
   * 			allow "1-10" or -1 for uninitialized)
   */
  public AbstractDataBackedRange(String range, int max) {
    super(range, max);
  }
  
  /**
   * For initializing the object.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Data       = null;
    m_Names      = null;
    m_Indices    = null;
    m_Comparator = null;
  }
  
  /**
   * Resets the object.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Names   = null;
    m_Indices = null;
  }

  /**
   * Returns a clone of the object.
   *
   * @return		the clone
   */
  @Override
  public AbstractDataBackedRange<T> getClone() {
    AbstractDataBackedRange<T>	result;
    
    result = (AbstractDataBackedRange<T>) super.getClone();
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

  /**
   * Returns a new comparator to use for sorting the names.
   * 
   * @return		the comparator
   */
  protected abstract Comparator newComparator();

  /**
   * Returns the comparator to use for sorting the names.
   * 
   * @return		the comparator
   */
  protected synchronized Comparator getComparator() {
    if (m_Comparator == null)
      m_Comparator = newComparator();
    return m_Comparator;
  }
  
  /**
   * Initializes the lookup tables.
   * 
   * @see		#getComparator()
   */
  protected synchronized void initLookUp() {
    int				i;
    String			name;
    List<String>		names;
    HashMap<String,Integer>	indices;
    
    if (m_Names == null) {
      names   = new ArrayList<String>();
      indices = new HashMap<String,Integer>();
      if (m_Data != null) {
	for (i = 0; i < getNumNames(m_Data); i++) {
	  name = getName(m_Data, i);
	  name = escapeName(name);
	  names.add(name);
	  indices.put(name, i);
	}
	Collections.sort(names, getComparator());
      }
      m_Names   = names;
      m_Indices = indices;
    }
  }
  
  /**
   * Returns the names.
   * 
   * @return		the names
   */
  protected List<String> getNames() {
    initLookUp();
    return m_Names;
  }
  
  /**
   * Returns the indices in use.
   * 
   * @return		the indices
   */
  public HashMap<String,Integer> getIndices() {
    initLookUp();
    return m_Indices;
  }
  
  /**
   * Checks whether the strings represents a name.
   * 
   * @param s		the string to process
   * @return		true if string is a name
   */
  protected boolean isName(String s) {
    boolean		result;
    int			i;
    List<String>	names;
    
    result = false;
    
    names = getNames();
    for (i = 0; i < names.size(); i++) {
      if (s.equals(names.get(i))) {
	result = true;
	break;
      }
    }
    
    return result;
  }

  /**
   * Returns the placeholders to allow in the ranges. This includes the
   * names.
   * 
   * @return		the placeholders
   * @see		#getNames()
   */
  @Override
  protected List<String> getPlaceholders() {
    List<String>	result;
    
    result = super.getPlaceholders();
    result.addAll(getNames());
    
    return result;
  }
  
  /**
   * Returns whether invalid characters should get removed.
   * 
   * @return		true if to replace invalid chars
   */
  @Override
  protected boolean canReplaceInvalidChars() {
    return (m_Data != null);
  }
  
  /**
   * Attempts to split a range into the parts resembling it.
   * 
   * @param s		the string to split
   * @return		the parts (single array element if no range)
   */
  @Override
  protected String[] splitRange(String s) {
    return SpreadSheetUtils.split(s, RANGE.charAt(0));
  }
  
  /**
   * Attempts to split a list into the parts resembling it.
   * 
   * @param s		the string to split
   * @return		the parts (single array element if no list)
   */
  @Override
  protected String[] splitList(String s) {
    return SpreadSheetUtils.split(s, SEPARATOR.charAt(0));
  }

  /**
   * Removes invalid characters. Digits, {@link #RANGE} and {@link #SEPARATOR}
   * get added automatically.
   * 
   * @param s		the string to process
   * @param valid	the list of valid characters/placeholders (processing happens in this order!)
   * @return		the processed string
   */
  @Override
  protected String removeInvalidChars(String s, List<String> valid) {
    String[]	list;
    int		i;
    
    list = splitList(s);
    for (i = 0; i < list.length; i++) {
      if (!list[i].startsWith("\""))
	list[i] = super.removeInvalidChars(s, valid);
    }
    
    return Utils.flatten(list, ",");
  }

  /**
   * Parses the 1-based index, 'first' and 'last' are accepted as well.
   *
   * @param s		the string to parse
   * @param max		the maximum value to use
   * @return		the 0-based index
   */
  @Override
  protected int parse(String s, int max) {
    if (isName(s))
      return getIndices().get(s);
    else
      return super.parse(s, max);
  }
  
  /**
   * Escapes the name, if necessary.
   * 
   * @param col		the name to (potentially) escape
   * @return		the processed name
   */
  public static String escapeName(String col) {
    if (col.indexOf(RANGE) > -1)
      col = "\"" + col + "\"";
    else if (col.indexOf(SEPARATOR) > -1)
      col = "\"" + col + "\"";
    else if (col.indexOf("_") > -1)
      col = "\"" + col + "\"";
    return col;
  }
  
  /**
   * Unescapes the name, if necessary.
   * 
   * @param col		the name to (potentially) unescape
   * @return		the processed name
   */
  public static String unescapeName(String col) {
    if (col.startsWith("\"") && (col.endsWith("\"")))
      return col.substring(1, col.length() - 1);
    else
      return col;
  }
}
