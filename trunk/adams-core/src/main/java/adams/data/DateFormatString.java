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
 * DateFormatString.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data;

import adams.core.Constants;
import adams.core.DateFormat;
import adams.core.HelpProvider;
import adams.core.base.AbstractBaseString;

/**
 * Wrapper for date/time formats.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DateFormatString
  extends AbstractBaseString
  implements HelpProvider {

  /** for serialization. */
  private static final long serialVersionUID = -7134897961930112280L;
  
  /**
   * Initializes the string with {@link Constants#DATE_FORMAT}.
   */
  public DateFormatString() {
    this(Constants.DATE_FORMAT);
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public DateFormatString(String s) {
    super(s);
  }

  /**
   * Initializes the internal object.
   */
  @Override
  protected void initialize() {
    m_Internal = Constants.DATE_FORMAT;
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
      new DateFormat(value);
      return true;
    }
    catch (Exception e) {
      return false;
    }
  }
  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return		the tool tip
   */
  @Override
  public String getTipText() {
    return "Format string for date and/or time.";
  }
  
  /**
   * Returns a configured {@link DateFormat} object.
   * 
   * @return		the configured object
   */
  public DateFormat toDateFormat() {
    return new DateFormat(getValue());
  }
  
  /**
   * Returns a URL with additional information.
   * 
   * @return		the URL, null if not available
   */
  public String getHelpURL() {
    return "http://docs.oracle.com/javase/6/docs/api/java/text/SimpleDateFormat.html";
  }
  
  /**
   * Returns a long help description, e.g., used in tiptexts.
   * 
   * @return		the help text, null if not available
   */
  public String getHelpDescription() {
    return "Information on the format string";
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
