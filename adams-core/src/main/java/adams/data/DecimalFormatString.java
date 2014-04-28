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
 * DecimalFormatString.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data;

import java.text.DecimalFormat;

import adams.core.base.AbstractBaseString;

/**
 * Wrapper for decimal formats.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DecimalFormatString
  extends AbstractBaseString {

  /** for serialization. */
  private static final long serialVersionUID = -7134897961930112280L;
  
  /**
   * Initializes the string with "0".
   */
  public DecimalFormatString() {
    this("0");
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public DecimalFormatString(String s) {
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
      new DecimalFormat(value);
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
    return "Format string for decimal numbers.";
  }
  
  /**
   * Returns a configured {@link DecimalFormat} object.
   * 
   * @return		the configured object
   */
  public DecimalFormat toDecimalFormat() {
    return new DecimalFormat(getValue());
  }
  
  /**
   * Returns a URL with additional information.
   * 
   * @return		the URL, null if not available
   */
  public String getHelpURL() {
    return "http://docs.oracle.com/javase/6/docs/api/java/text/DecimalFormat.html";
  }
  
  /**
   * Returns a long help description, e.g., used in tiptexts.
   * 
   * @return		the help text, null if not available
   */
  public String getHelpDescription() {
    return "Information on the decimal format string";
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
