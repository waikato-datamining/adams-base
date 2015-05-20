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
 * BasePassword.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

import adams.core.net.InternetHelper;

/**
 * Wrapper for a String object to be editable in the GOE. Used for entering
 * passwords which get Base64 encoded.
 * <br><br>
 * This not an attempt to keep passwords safe in any way, merely of obscuring
 * them slightly.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BasePassword
  extends AbstractBaseString {

  /** for serialization. */
  private static final long serialVersionUID = -3626665478868498359L;

  /** the indicator of base64 encoding (start). */
  public final static String BASE64_START = "{";

  /** the indicator of base64 encoding (end). */
  public final static String BASE64_END = "}";

  /** the error constant, if decoding failed. */
  public final static String ERROR = "-ERROR-";

  /** the character to mask the password with. */
  public final static String MASK_CHAR = "*";
  
  /**
   * Initializes the string with length 0.
   */
  public BasePassword() {
    this("");
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public BasePassword(String s) {
    super(s);
  }

  /**
   * Turns the base64 encoded string into plain text.
   * Expects the string to be surrounded by BASE64_START and BASE64_END
   *
   * @param base64	the encoded string
   * @return		the decoded string, null in case of an error
   * @see		#BASE64_START
   * @see		#BASE64_END
   */
  protected String decode(String base64) {
    String	result;

    result = InternetHelper.decodeBase64(base64.substring(1, base64.length() - 1));
    if (result == null)
      result = ERROR;

    return result;
  }

  /**
   * Encodes the given string in base64.
   *
   * @param raw		the string to encode
   * @return		the encoded string
   */
  protected String encode(String raw) {
    return BASE64_START + InternetHelper.encodeBase64(raw) + BASE64_END;
  }

  /**
   * Checks whether the string value is a valid presentation for this class.
   *
   * @param value	the string value to check
   * @return		always true if not base64 encoded, otherwise the base64
   * 			string must be properly surrounded
   * @see		#BASE64_START
   * @see		#BASE64_END
   */
  @Override
  public boolean isValid(String value) {
    if (value == null)
      return false;
    if (value.startsWith(BASE64_START))
      return value.endsWith(BASE64_END);
    else
      return true;
  }

  /**
   * Sets the string value.
   *
   * @param value	the string value, clear text or base64 encoded
   */
  @Override
  public void setValue(String value) {
    if (!isValid(value))
      return;

    if (value.startsWith(BASE64_START))
      m_Internal = decode(value);
    else
      m_Internal = value;
  }

  /**
   * Returns the current string value.
   *
   * @return		the string value, clear text
   */
  @Override
  public String getValue() {
    return (String) m_Internal;
  }

  /**
   * Returns the current string value masked with MASK_CHAR.
   *
   * @return		the masked password
   * @see		#MASK_CHAR
   */
  public String getMaskedValue() {
    return getValue().replaceAll(".", MASK_CHAR);
  }

  /**
   * Returns the backquoted String value.
   *
   * @return		the base64 encoded string value
   */
  @Override
  public String stringValue() {
    return encode(getValue());
  }

  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return		the tool tip
   */
  @Override
  public String getTipText() {
    return "For handling passwords.";
  }
}
