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
 * BaseText.java
 * Copyright (C) 2009-2012 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

import adams.core.Utils;

/**
 * Wrapper for a String object to be editable in the GOE. Basically the same
 * as BaseString, but used for longer, multi-line strings. Uses a different
 * GOE editor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseText
  extends AbstractBaseString {

  /** for serialization. */
  private static final long serialVersionUID = -7223597009565454854L;

  /**
   * Initializes the string with length 0.
   */
  public BaseText() {
    this("");
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public BaseText(String s) {
    super(s);
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
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return		the tool tip
   */
  @Override
  public String getTipText() {
    return "An arbitrary long string.";
  }
}
