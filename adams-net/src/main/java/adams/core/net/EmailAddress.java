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
 * EmailAddress.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.core.net;

import javax.mail.internet.InternetAddress;

import adams.core.base.AbstractBaseString;

/**
 * Wrapper for a regular expression string to be editable in the GOE. Basically
 * the same as BaseString, but checks whether the string represents a valid
 * address using <code>javax.mail.internet.InternetAddress</code>.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class EmailAddress
  extends AbstractBaseString {

  /** for serialization. */
  private static final long serialVersionUID = -8687858764646783666L;

  /** the dummy address. */
  public final static String DUMMY_ADDRESS = "john.doe@nowhere.org";
  
  /**
   * Initializes the email address with a dummy one.
   */
  public EmailAddress() {
    this(DUMMY_ADDRESS);
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public EmailAddress(String s) {
    super(s);
  }

  /**
   * Checks whether the string value is a valid presentation for this class.
   *
   * @param value	the string value to check
   * @return		true if valid email address
   * @see		InternetAddress
   */
  @Override
  public boolean isValid(String value) {
    if (value == null)
      return false;
    try {
      if (value.indexOf('@') == -1)
	return false;
      new InternetAddress(value);
      return true;
    }
    catch (Exception e) {
      return false;
    }
  }
  
  /**
   * Returns whether the address is just the dummy address.
   * 
   * @return		the dummy address
   */
  public boolean isDummyAddress() {
    return getValue().equals(DUMMY_ADDRESS);
  }

  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return		the tool tip
   */
  @Override
  public String getTipText() {
    return "Email address.";
  }

  /**
   * Returns the actual email address from the email string.
   * 
   * @return		the actual address
   */
  public String strippedValue() {
    String	result;
    
    result = getValue();
    if (getValue().indexOf('<') == -1)
      return result;
    else
      return result.substring(result.lastIndexOf('<') + 1, result.lastIndexOf('>'));
  }
}
