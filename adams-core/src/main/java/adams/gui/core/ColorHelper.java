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
 * ColorHelper.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import java.awt.Color;

import adams.core.License;
import adams.core.annotation.MixedCopyright;

/**
 * Helper class for converting Colors to-and-from strings.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ColorHelper {

  /**
   * The type of color notation.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum ColorNotation {
    /** hex. */
    HEX,
    /** rgb. */
    RGB,
    /** name. */
    NAME
  }

  /**
   * Tries to determine the type of color notation.
   *
   * @param str		the color string to analyze
   * @return		the used notation
   */
  public static ColorNotation getNotation(String str) {
    if (str.startsWith("#"))
      return ColorNotation.HEX;
    else if (str.indexOf(',') > 0)
      return ColorNotation.RGB;
    else
      return ColorNotation.NAME;
  }

  /**
   * Returns a color generated from the string. Uses {@link Color#BLACK} as 
   * default color.
   * Formats:
   * <ul>
   *   <li>hex notation: #(AA)RRGGBB with AA/RR/GG/BB being hexadecimal strings</li>
   *   <li>RGB notation: (A,)R,G,B with A/R/G/B from 0-255</li>
   *   </li>predefined names (case-insensitive) : black, blue, cyan, darkgray,
   *   darkgrey, gray, grey, green, lightgray, lightgrey, magenta, orange,
   *   pink, red, white, yellow</li>
   * </ul>
   *
   * @param str		the string to convert to a color
   * @return		the generated color
   * @see		#getNotation(String)
   * @see		#valueOf(String, Color)
   */
  public static Color valueOf(String str) {
    return valueOf(str, Color.BLACK);
  }

  /**
   * Returns a color generated from the string.
   * Formats:
   * <ul>
   *   <li>hex notation: #(AA)RRGGBB with AA/RR/GG/BB being hexadecimal strings</li>
   *   <li>RGB notation: (A,)R,G,B with A/R/G/B from 0-255</li>
   *   </li>predefined names (case-insensitive) : black, blue, cyan, darkgray,
   *   darkgrey, gray, grey, green, lightgray, lightgrey, magenta, orange,
   *   pink, red, white, yellow</li>
   * </ul>
   *
   * @param str		the string to convert to a color
   * @param defColor	the default color if parsing fails
   * @return		the generated color
   * @see		#getNotation(String)
   */
  public static Color valueOf(String str, Color defColor) {
    Color		result;
    ColorNotation	notation;
    String[]		parts;

    result = defColor;

    notation = getNotation(str);
    switch (notation) {
      case HEX:
	str    = str.replaceAll("#", "");
	if (str.length() == 6) {
	  result = new Color(
	      Integer.parseInt(str.substring(0, 2), 16),
	      Integer.parseInt(str.substring(2, 4), 16),
	      Integer.parseInt(str.substring(4, 6), 16));
	}
	else if (str.length() == 8) {
	  result = new Color(
	      Integer.parseInt(str.substring(2, 4), 16),
	      Integer.parseInt(str.substring(4, 6), 16),
	      Integer.parseInt(str.substring(6, 8), 16),
	      Integer.parseInt(str.substring(0, 2), 16));
	}
	break;

      case RGB:
	parts = str.split(",");
	if (parts.length == 3) {
	  try {
	    result = new Color(
		Integer.parseInt(parts[0]),
		Integer.parseInt(parts[1]),
		Integer.parseInt(parts[2]));
	  }
	  catch (Exception e) {
	    // ignored
	  }
	}
	else if (parts.length == 4) {
	  try {
	    result = new Color(
		Integer.parseInt(parts[1]),
		Integer.parseInt(parts[2]),
		Integer.parseInt(parts[3]),
		Integer.parseInt(parts[0]));
	  }
	  catch (Exception e) {
	    // ignored
	  }
	}
	break;

      case NAME:
	str = str.toLowerCase();
	if (str.equals("black"))
	  result = Color.BLACK;
	else if (str.equals("blue"))
	  result = Color.BLUE;
	else if (str.equals("cyan"))
	  result = Color.CYAN;
	else if (str.equals("darkgray"))
	  result = Color.DARK_GRAY;
	else if (str.equals("darkgrey"))
	  result = Color.DARK_GRAY;
	else if (str.equals("gray"))
	  result = Color.GRAY;
	else if (str.equals("grey"))
	  result = Color.GRAY;
	else if (str.equals("green"))
	  result = Color.GREEN;
	else if (str.equals("lightgray"))
	  result = Color.LIGHT_GRAY;
	else if (str.equals("lightgrey"))
	  result = Color.LIGHT_GRAY;
	else if (str.equals("magenta"))
	  result = Color.MAGENTA;
	else if (str.equals("orange"))
	  result = Color.ORANGE;
	else if (str.equals("pink"))
	  result = Color.PINK;
	else if (str.equals("red"))
	  result = Color.RED;
	else if (str.equals("white"))
	  result = Color.WHITE;
	else if (str.equals("yellow"))
	  result = Color.YELLOW;
	break;

      default:
	throw new IllegalStateException("Unhandled color notation: " + notation);
    }

    return result;
  }

  /**
   * Turns the integer into a hex string, left-pads with zero.
   *
   * @param i		the integer to convert
   * @return		the generated string
   */
  protected static String toHex(int i) {
    String	result;

    result = Integer.toHexString(i);
    if (result.length() % 2 == 1)
      result = "0" + result;

    return result;
  }

  /**
   * Returns the color as hex string ("#RRGGBB" or "#AARRGGBB").
   * Alpha is only output if different from 255.
   *
   * @param color	the color to convert
   * @return		the generated string
   */
  public static String toHex(Color color) {
    String	result;

    if (color.getAlpha() < 255)
      result = "#" 
	  + toHex(color.getAlpha())
	  + toHex(color.getRed())
	  + toHex(color.getGreen())
	  + toHex(color.getBlue());
    else
      result = "#" 
	  + toHex(color.getRed())
	  + toHex(color.getGreen())
	  + toHex(color.getBlue());

    return result;
  }

  /**
   * Turns the color into RGB notation ("R,G,B" or "A,R,G,B" with A/R/G/B ranging from 0-255).
   * Alpha is only output if different from 255.
   *
   * @param color	the color to convert
   * @return		the generated string
   */
  public static String toRGB(Color color) {
    if (color.getAlpha() < 255)
      return color.getAlpha() + "," + color.getRed() + "," + color.getGreen() + "," + color.getBlue();
    else
      return color.getRed() + "," + color.getGreen() + "," + color.getBlue();
  }

  /**
   * Tries to turn color into a name, like 'black' or 'red'. If it cannot
   * find a match, null gets returned.
   *
   * @param color	the color to convert
   * @return		the name of the color, null if no match
   */
  public static String toName(Color color) {
    if (color.equals(Color.BLACK))
      return "black";
    else if (color.equals(Color.BLUE))
      return "blue";
    else if (color.equals(Color.CYAN))
      return "cyan";
    else if (color.equals(Color.DARK_GRAY))
      return "darkgray";
    else if (color.equals(Color.GRAY))
      return "gray";
    else if (color.equals(Color.GREEN))
      return "green";
    else if (color.equals(Color.LIGHT_GRAY))
      return "lightgray";
    else if (color.equals(Color.MAGENTA))
      return "magenta";
    else if (color.equals(Color.ORANGE))
      return "orange";
    else if (color.equals(Color.PINK))
      return "pink";
    else if (color.equals(Color.RED))
      return "red";
    else if (color.equals(Color.WHITE))
      return "white";
    else if (color.equals(Color.YELLOW))
      return "yellow";
    else
      return null;
  }

  /**
   * Returns a contrast color.
   * <p/>
   * Taken from <a href="http://stackoverflow.com/a/13030061" target="_blank">here</a>.
   * 
   * @param color	the color to return the contrast color for
   * @return		the contrast color
   */
  @MixedCopyright(
      author = "brimborium",
      copyright = "2012 stackoverflow",
      license = License.CC_BY_SA_3
  )
  public static Color getContrastColor(Color color) {
    double y = (299 * color.getRed() + 587 * color.getGreen() + 114 * color.getBlue()) / 1000;
    return y >= 128 ? Color.black : Color.white;
  }
}
