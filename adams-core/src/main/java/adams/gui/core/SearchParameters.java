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
 * SearchParameters.java
 * Copyright (C) 2010-2018 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import adams.core.Utils;

import java.util.regex.Pattern;

/**
 * A container for search parameters.
 * <br><br>
 * In case of simple substring matching, the search string and the string
 * being searched are both used in lower case.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SearchParameters {

  /** the search string. */
  protected String m_SearchString;

  /** the parts of the search string (split on blanks). */
  protected String[] m_SearchParts;

  /** not null if the search string is a long. */
  protected Long m_Long;

  /** not null if the search string is a double. */
  protected Double m_Double;

  /** whether to perform regular expression matching. */
  protected boolean m_RegExp;

  /** for speeding up the matching of regular expressions. */
  protected Pattern m_Pattern;

  /**
   * Initializes the search with simple substring matching.
   *
   * @param search	the search string
   */
  public SearchParameters(String search) {
    this(search, false);
  }

  /**
   * Initializes the search.
   *
   * @param search	the search string
   * @param regExp	whether to perform regular expression matching or
   * 			substring matching
   */
  public SearchParameters(String search, boolean regExp) {
    m_RegExp      = regExp;
    m_SearchParts = null;

    if (search == null)
      search = "";
    if (!m_RegExp) {
      m_SearchString = search.toLowerCase();
      m_Pattern      = null;
      if (m_SearchString.contains(" "))
        m_SearchParts = m_SearchString.split(" ");
    }
    else {
      m_SearchString = search;
      m_Pattern      = Pattern.compile(search);
    }

    try {
      m_Long = Long.parseLong(m_SearchString);
    }
    catch (Exception e) {
      m_Long = null;
    }

    try {
      m_Double = Utils.toDouble(m_SearchString);
    }
    catch (Exception e) {
      m_Double = null;
    }
  }

  /**
   * Returns the current search string.
   *
   * @return		the search string
   */
  public String getSearchString() {
    return m_SearchString;
  }

  /**
   * Returns whether the search uses regular expression matching or simple
   * substring matching.
   *
   * @return		true if regular expressions are in use
   */
  public boolean isRegExp() {
    return m_RegExp;
  }

  /**
   * Returns whether the search string represents an integer.
   *
   * @return		true if an integer
   */
  public boolean getLong() {
    return (m_Long != null);
  }

  /**
   * Returns whether the search string represents a double.
   *
   * @return		true if an double
   */
  public boolean isDouble() {
    return (m_Double != null);
  }

  /**
   * Matches the search string against the provided string.
   * Empty search string matches everything.
   *
   * @param s		the string to match, null is ignored
   * @return		true if a match
   */
  public boolean matches(String s) {
    boolean	match;

    if (s == null)
      return false;

    if (m_SearchString.length() == 0)
      return true;

    if (m_RegExp) {
      return m_Pattern.matcher(s).matches();
    }
    else {
      s = s.toLowerCase();
      if (m_SearchParts != null) {
        match = true;
        for (String part: m_SearchParts) {
          match = s.contains(part);
          if (!match)
            break;
	}
	return match;
      }
      else {
	return s.contains(m_SearchString);
      }
    }
  }

  /**
   * Matches the integer against the search string if it represents an
   * integer.
   *
   * @param i		the integer to check
   * @return		true if the search is the same integer
   */
  public boolean matches(Integer i) {
    return (m_Long != null) && (i != null) && (m_Long.equals(i.longValue()));
  }

  /**
   * Matches the long against the search string if it represents a
   * long.
   *
   * @param l		the long to check
   * @return		true if the search is the same long
   */
  public boolean matches(Long l) {
    return (m_Long != null) && (l != null) && (m_Long.equals(l));
  }

  /**
   * Matches the float against the search string if it represents a
   * float.
   *
   * @param f		the float to check
   * @return		true if the search is the same float
   */
  public boolean matches(Float f) {
    return (m_Double != null) && (f != null) && (m_Double.equals(f.doubleValue()));
  }

  /**
   * Matches the double against the search string if it represents an
   * double.
   *
   * @param d		the double to check
   * @return		true if the search is the same double
   */
  public boolean matches(Double d) {
    return (m_Double != null) && (d != null) && (m_Double.equals(d));
  }

  /**
   * Returns a short string representation of the search container.
   *
   * @return		the string representation
   */
  @Override
  public String toString() {
    return
        "search='" + getSearchString() + "', "
      + "regexp=" + isRegExp() + ", "
      + "integer=" + getLong() + ", "
      + "double=" + isDouble();
  }
}
