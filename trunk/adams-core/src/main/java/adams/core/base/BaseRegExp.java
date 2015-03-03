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
 * BaseRegExp.java
 * Copyright (C) 2010-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

import adams.core.Utils;

import java.util.regex.Pattern;

/**
 * Wrapper for a regular expression string to be editable in the GOE. Basically
 * the same as BaseString, but checks whether the string represents a valid
 * regular expression.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseRegExp
  extends AbstractBaseString {

  /** for serialization. */
  private static final long serialVersionUID = -8687858764646783666L;

  /** the match-all expression. */
  public final static String MATCH_ALL = ".*";

  /** for performing pattern matching. */
  protected Pattern m_Pattern;

  /**
   * Initializes the string with length 0.
   */
  public BaseRegExp() {
    this(MATCH_ALL);
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public BaseRegExp(String s) {
    super(s);
  }

  /**
   * Checks whether the string value is a valid presentation for this class.
   *
   * @param value	the string value to check
   * @return		always true
   */
  @Override
  public boolean isValid(String value) {
    if (value == null)
      return false;
    try {
      Pattern.compile(value);
      return true;
    }
    catch (Exception e) {
      return false;
    }
  }

  /**
   * Converts the string according to the specified conversion.
   * <p/>
   * Implementation performs no conversion, merely initializes
   * the pattern matcher.
   *
   * @param value	the string to convert
   * @return		the converted string
   * @see		#m_Pattern
   */
  @Override
  protected String convert(String value) {
    m_Pattern = Pattern.compile(value);
    return super.convert(value);
  }

  /**
   * Returns the backquoted String value.
   *
   * @return		the backquoted String value
   */
  @Override
  public String stringValue() {
    return Utils.backQuoteChars(getValue());
  }

  /**
   * Returns the expression as compiled pattern.
   *
   * @return		the pattern
   */
  public Pattern patternValue() {
    return Pattern.compile(getValue());
  }

  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return		the tool tip
   */
  @Override
  public String getTipText() {
    return "Regular expression.";
  }

  /**
   * Checks whether the string matches the regular expression.
   *
   * @param s		the string to check against regexp
   * @return		true if the string matches the regexp
   */
  public boolean isMatch(String s) {
    return m_Pattern.matcher(s).matches();
  }

  /**
   * Checks whether the expression is the match-all expression.
   *
   * @return		true if the match-all expression
   */
  public boolean isMatchAll() {
    return getValue().equals(MATCH_ALL);
  }

  /**
   * Checks whether the expression empty.
   *
   * @return		true if empty expression
   */
  @Override
  public boolean isEmpty() {
    return (getValue().length() == 0);
  }
}
