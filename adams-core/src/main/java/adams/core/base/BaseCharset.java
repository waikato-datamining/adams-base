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
 * BaseCharset.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

import java.nio.charset.Charset;

import adams.core.management.CharsetHelper;

/**
 * Wrapper for a {@link Charset} object to be editable in the GOE.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseCharset
  extends AbstractBaseString {

  /** for serialization. */
  private static final long serialVersionUID = -7223597009565454854L;

  /**
   * Initializes the string with the default Charset.
   */
  public BaseCharset() {
    this(CharsetHelper.CHARSET_DEFAULT);
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public BaseCharset(String s) {
    super(s);
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param c		the charset to use
   */
  public BaseCharset(Charset c) {
    super(c.name());
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
    if (value.equals(CharsetHelper.CHARSET_DEFAULT))
      return true;
    try {
      Charset.forName(value);
      return true;
    }
    catch (Exception e) {
      return false;
    }
  }
  
  /**
   * Returns the String value.
   *
   * @return		the String value
   */
  @Override
  public String stringValue() {
    return getValue();
  }

  /**
   * Returns the charset value.
   *
   * @return		the charset value
   */
  public Charset charsetValue() {
    return CharsetHelper.valueOf(getValue());
  }

  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return		the tool tip
   */
  @Override
  public String getTipText() {
    return "Charset wrapper.";
  }
}
