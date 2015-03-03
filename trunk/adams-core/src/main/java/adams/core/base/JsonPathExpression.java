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
 * JsonPathExpression.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.core.base;

import adams.core.HelpProvider;

import com.jayway.jsonpath.JsonPath;

/**
 * Encapsulates a JSON Path expression.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class JsonPathExpression
  extends AbstractBaseString
  implements HelpProvider {

  /** for serialization. */
  private static final long serialVersionUID = -6084976027405972444L;

  /**
   * Initializes the string with length 0.
   */
  public JsonPathExpression() {
    super();
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public JsonPathExpression(String s) {
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
    
    // empty or simple key?
    if ((value.length() == 0) || !value.startsWith("$"))
      return true;
    
    try {
      JsonPath.compile(value);
      return true;
    }
    catch (Exception e) {
      return false;
    }
  }
  
  /**
   * Returns whether the path expression is just a simple key rather than a path.
   * 
   * @return		true if just a simple key and not a path
   */
  public boolean isSimpleKey() {
    return !getValue().startsWith("$");
  }
  
  /**
   * Returns the {@link JsonPath} object, if possible.
   * 
   * @return		the 
   */
  public JsonPath toJsonPath() {
    try {
      return JsonPath.compile(getValue());
    }
    catch (Exception e) {
      return null;
    }
  }
  
  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return		the tool tip
   */
  @Override
  public String getTipText() {
    return "JSON Path expression (interpreted as path if starts with '$', otherwise simple key)";
  }
  
  /**
   * Returns a URL with additional information.
   * 
   * @return		the URL, null if not available
   */
  public String getHelpURL() {
    return "http://code.google.com/p/json-path/";
  }
  
  /**
   * Returns a long help description, e.g., used in tiptexts.
   * 
   * @return		the help text, null if not available
   */
  public String getHelpDescription() {
    return "More information on JSON Path";
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
