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
 * XPathExpression.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core.base;

import adams.core.HelpProvider;

/**
 * Encapsulates an XPath expression.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class XPathExpression
  extends AbstractBaseString
  implements HelpProvider {

  /** for serialization. */
  private static final long serialVersionUID = -6084976027405972444L;

  /**
   * Initializes the string with length 0.
   */
  public XPathExpression() {
    super();
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public XPathExpression(String s) {
    super(s);
  }

  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return		the tool tip
   */
  @Override
  public String getTipText() {
    return "XPath expression";
  }
  
  /**
   * Returns a URL with additional information.
   * 
   * @return		the URL, null if not available
   */
  public String getHelpURL() {
    return "http://www.w3schools.com/xpath/";
  }
  
  /**
   * Returns a long help description, e.g., used in tiptexts.
   * 
   * @return		the help text, null if not available
   */
  public String getHelpDescription() {
    return "More information on XPath";
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
