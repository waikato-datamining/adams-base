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
 * BaseMarkdown.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

import adams.core.Utils;

/**
 * Wrapper for a Markdown string to be editable in the GOE.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseMarkdown
  extends AbstractBaseString {

  /** for serialization. */
  private static final long serialVersionUID = -7223597009565454854L;

  /**
   * Initializes the string with length 0.
   */
  public BaseMarkdown() {
    this("");
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public BaseMarkdown(String s) {
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
    return "A Markdown string.";
  }
}
