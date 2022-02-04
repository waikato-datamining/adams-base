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
 * FontParsing.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.core.option.parsing;

import adams.core.option.AbstractOption;

import java.awt.Font;

/**
 * For parsing Font options.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class FontParsing
    extends AbstractParsing {

  /** the string for PLAIN. */
  public final static String PLAIN = "PLAIN";

  /** the string for BOLD. */
  public final static String BOLD = "BOLD";

  /** the string for ITALIC. */
  public final static String ITALIC = "ITALIC";

  /** the separator. */
  public final static char SEPARATOR = '-';

  /**
   * Returns the color as string.
   *
   * @param option	the current option
   * @param object	the color object to convert
   * @return		the generated string
   */
  public static String toString(AbstractOption option, Object object) {
    String	result;
    Font font;

    font  = (Font) object;
    result = font.getName();
    result += SEPARATOR + (font.isBold() ? BOLD : PLAIN);
    if (font.isItalic())
      result += "," + ITALIC;
    result += "" + SEPARATOR + font.getSize();

    return result;
  }

  /**
   * Returns a color generated from the string.
   *
   * @param option	the current option
   * @param str		the string to convert to a color
   * @return		the generated color
   */
  public static Object valueOf(AbstractOption option, String str) {
    Font	result;
    String	name;
    int		size;
    String	attsStr;
    int		atts;

    // size
    size = Integer.parseInt(str.substring(str.lastIndexOf(SEPARATOR) + 1));
    str  = str.substring(0, str.lastIndexOf(SEPARATOR));

    // face
    attsStr = str.substring(str.lastIndexOf(SEPARATOR) + 1);
    str     = str.substring(0, str.lastIndexOf(SEPARATOR));
    atts    = (attsStr.indexOf(BOLD) > -1) ? Font.BOLD : Font.PLAIN;
    if (attsStr.indexOf(ITALIC) > -1)
      atts |= Font.ITALIC;

    // name
    name = str;

    // create font object
    result = new Font(name, atts, size);

    return result;
  }
}
