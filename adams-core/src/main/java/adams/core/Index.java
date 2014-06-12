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
 * Index.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import java.io.Serializable;

/**
 * A simple class that translates human-readable 1-based index strings
 * (including "first", "second", "third", "last_2", "last_1" and "last")
 * into integer indices.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Index
  implements Serializable, CustomDisplayStringProvider, Comparable<Index>, 
             ExampleProvider, HelpProvider {

  /** for serialization. */
  private static final long serialVersionUID = 4295722716846349301L;

  /** the special index for "first". */
  public final static String FIRST = "first";

  /** the special string "second". */
  public final static String SECOND = "second";

  /** the special string "third". */
  public final static String THIRD = "third";

  /** the special string "last_1" (2nd to last). */
  public final static String LAST_1 = "last_1";

  /** the special string "last_2" (3rd to last). */
  public final static String LAST_2 = "last_2";

  /** the special index for "last". */
  public final static String LAST = "last";

  /** the uncleaned index. */
  protected String m_Raw;
  
  /** the underlying index. */
  protected String m_Index;

  /** the parsed integer index. */
  protected Integer m_IntIndex;

  /** the maximum number for the 1-based index. */
  protected int m_Max;

  /**
   * Initializes with no index.
   */
  public Index() {
    this("");
  }

  /**
   * Initializes with the given index, but no maximum.
   *
   * @param index	the index to use
   */
  public Index(String index) {
    this(index, -1);
  }

  /**
   * Initializes with the given index and maximum.
   *
   * @param index	the index to use
   * @param max		the maximum of the 1-based index (e.g., use "10" to
   * 			allow "1-10" or -1 for uninitialized)
   */
  public Index(String index, int max) {
    super();

    initialize();
    
    setIndex(index);
    setMax(max);
  }

  /**
   * For initializing the object.
   */
  protected void initialize() {
    m_Index    = "";
    m_IntIndex = -1;
    m_Raw      = null;
  }

  /**
   * Resets the parsed data.
   */
  protected void reset() {
    m_IntIndex = null;
  }
  
  /**
   * Sets the index.
   *
   * @param value	the index to use
   */
  public void setIndex(String value) {
    m_Raw   = value;
    m_Index = null;
    reset();
  }

  /**
   * Returns the currently set index.
   *
   * @return		the index in use
   */
  public synchronized String getIndex() {
    if (m_Index == null)
      m_Index = clean(m_Raw);
      
    if (m_Index.isEmpty())
      return m_Raw;
    else
      return m_Index;
  }

  /**
   * Sets the maximum (1-max will be allowed).
   *
   * @param value	the maximum for the 1-based index
   */
  public void setMax(int value) {
    if (value <= 0)
      m_Max = -1;
    else
      m_Max = value;
    reset();
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
   * Checks whether a valid index has been supplied.
   *
   * @return		true if a valid index is available
   */
  public boolean hasIndex() {
    return (getIndex().length() > 0);
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
   * Cleanses the given string. Only allows "first", "last" and numbers.
   *
   * @param s		the string to clean
   * @return		the cleansed string, "" if invalid one provided
   */
  protected String clean(String s) {
    String	tmp;
    String	result;
    int		num;

    result = "";
    tmp    = s.toLowerCase();
    if (isPlaceholder(tmp)) {
      result = tmp;
    }
    else {
      try {
	num = Integer.parseInt(tmp);
	if (num > 0)
	  result = tmp;
      }
      catch (Exception e) {
	// ignored
      }
    }

    return result;
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
   * Parses the string and checks it against the maximum.
   *
   * @param s		the string to parse
   * @param max		the maximum to allow
   * @return		the parsed value, -1 if invalid
   */
  protected int parse(String s, int max) {
    int		result;

    result = -1;

    if (max > -1) {
      if (isPlaceholder(s)) {
	result = parsePlaceholder(s, max);
      }
      else {
	try {
	  result = Integer.parseInt(s) - 1;
	}
	catch (Exception e) {
	  // ignored
	  result = -1;
	}
      }

      // check boundaries
      if ((result > max - 1) || (result < 0))
	result = -1;
    }
    else {
      result = -1;
    }

    return result;
  }

  /**
   * Returns the integer representation of the index.
   *
   * @return		the integer index, -1 if not possible
   */
  public int getIntIndex() {
    if (m_IntIndex == null)
      m_IntIndex = parse(getIndex(), m_Max);

    return m_IntIndex;
  }

  /**
   * Compares this index with the specified index for order. Returns a
   * negative integer, zero, or a positive integer as this index is less
   * than, equal to, or greater than the specified index.
   *
   * @param   o the subrange to be compared.
   * @return  a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   */
  public int compareTo(Index o) {
    int		result;

    if ((getMax() != -1) && (getMax() != -1)) {
      result = new Integer(getIntIndex()).compareTo(
	  new Integer(o.getIntIndex()));
    }
    else {
      result = new Integer(parse(getIndex(), Integer.MAX_VALUE)).compareTo(
	  new Integer(parse(o.getIndex(), Integer.MAX_VALUE)));
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
    if (!(obj instanceof Index))
      return false;
    else
      return (compareTo((Index) obj) == 0);
  }

  /**
   * Hashcode so can be used as hashtable key. Returns the hashcode of the
   * index string.
   *
   * @return		the hashcode
   */
  @Override
  public int hashCode() {
    return getIndex().hashCode();
  }

  /**
   * Returns a string representation of the index object.
   *
   * @return		the representation
   */
  @Override
  public String toString() {
    return "index=" + getIndex() + ", max=" + m_Max;
  }

  /**
   * Returns the custom display string.
   *
   * @return		the string
   */
  public String toDisplay() {
    return getIndex();
  }

  /**
   * Returns the example.
   *
   * @return		the example
   */
  public String getExample() {
    return
        "An index is a number starting with 1; the following placeholders can be used as well: "
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
}
