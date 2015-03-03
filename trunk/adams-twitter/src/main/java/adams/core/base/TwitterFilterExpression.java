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
 * TwitterFilterExpression.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

import java.util.HashMap;

import adams.core.net.TwitterHelper;
import adams.data.twitter.TwitterField;
import adams.parser.GrammarSupplier;
import adams.parser.TwitterFilter;

/**
 * Wrapper for a twitter filter expression.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TwitterFilterExpression
  extends BaseObject
  implements GrammarSupplier {

  /** for serialization. */
  private static final long serialVersionUID = -5853830144343397434L;

  /** the default expression. */
  public final static String DEFAULT= "";
  
  /** the default symbols. */
  protected static HashMap m_Symbols;
  
  /**
   * Initializes using an empty string.
   */
  public TwitterFilterExpression() {
    this(DEFAULT);
  }

  /**
   * Initializes the object with the expression to parse.
   *
   * @param s		the expression to parse
   */
  public TwitterFilterExpression(String s) {
    super(s);
  }

  /**
   * Initializes the internal object.
   */
  @Override
  protected void initialize() {
    m_Internal = DEFAULT;
  }

  /**
   * Returns the default symbols to use.
   *  
   * @return		the symbols
   */
  protected synchronized HashMap getSymbols() {
    HashMap	symbols;
    
    if (m_Symbols == null) {
      symbols = new HashMap();
      symbols.put(TwitterField.LANGUAGE_CODE, "en");
      symbols.put(TwitterField.COUNTRY, "new zealand");
      symbols.put(TwitterField.COUNTRY_CODE, "nz");
      symbols.put(TwitterField.PLACE, "hamilton");
      symbols.put(TwitterField.SOURCE, "source");
      symbols.put(TwitterField.TEXT, "a tweet");
      symbols.put(TwitterField.USER_NAME, "user name");
      symbols.put(TwitterField.PLACE, "place");
      symbols.put(TwitterHelper.SYMBOL_HASHTAGS, new String[0]);
      symbols.put(TwitterHelper.SYMBOL_USERMENTIONS, new String[0]);
      // TODO geolocation
      m_Symbols = symbols;
    }
    
    return m_Symbols;
  }

  /**
   * Parses the given expression string.
   *
   * @param s		the expression string to parse
   * @param quiet	whether to print exceptions or not
   * @return		the result of the expression evaluation, null in case of an error
   */
  protected Boolean parse(String s, boolean quiet) {
    try {
      return TwitterFilter.evaluate(s, getSymbols());
    }
    catch (Exception e) {
      if (!quiet) {
	System.err.println("Failed to parse: " + s);
	e.printStackTrace();
      }
      return null;
    }
  }

  /**
   * Checks whether the string value is a valid presentation for this class.
   *
   * @param value	the string value to check
   * @return		true if the string can be parsed
   */
  @Override
  public boolean isValid(String value) {
    if (value == null)
      return false;
    
    if (value.length() == 0)
      return true;

    return (parse(value, true) != null);
  }

  /**
   * Sets the string value.
   *
   * @param value	the string value
   */
  @Override
  public void setValue(String value) {
    if (!isValid(value))
      return;

    m_Internal = value;
  }

  /**
   * Returns the current string value.
   *
   * @return		the string value
   */
  @Override
  public String getValue() {
    return (String) m_Internal;
  }

  /**
   * Returns the expression as string.
   *
   * @return		the expression as string
   */
  public String stringValue() {
    return getValue();
  }

  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return		the tool tip
   */
  @Override
  public String getTipText() {
    return "A Twitter filter expression.";
  }

  /**
   * Returns a string representation of the grammar.
   *
   * @return		the grammar, null if not available
   */
  public String getGrammar() {
    return new TwitterFilter().getGrammar();
  }
}
