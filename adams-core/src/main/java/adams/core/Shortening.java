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
 * Shortening.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core;

/**
 * Helper class for shortening operations.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Shortening {

  /**
   * Shortens the string in the middle (inserting "..") if longer than
   * specified number of characters.
   *
   * @param s		the string to (potentially) shorten
   * @param max		the maximum number of chars
   * @return		the processed string
   */
  public static String shortenStart(String s, int max) {
    return (s.length() > max ? ("..." + s.substring(s.length() - max, s.length())) : s);
  }

  /**
   * Shortens the string in the middle (inserting "..") if longer than
   * specified number of characters.
   *
   * @param s		the string to (potentially) shorten
   * @param max		the maximum number of chars
   * @return		the processed string
   */
  public static String shortenMiddle(String s, int max) {
    String	result;
    int		len;

    if (s.length() > max) {
      len    = max / 2;
      result = s.substring(0, len) + ".." + s.substring(s.length() - len);
    }
    else {
      result = s;
    }

    return result;
  }

  /**
   * Shortens a string (and appends "...") if longer than the allowed
   * maximum number of characters.
   *
   * @param s		the string to process
   * @param max		the maximum number of characters.
   * @return		the processed string
   */
  public static String shortenEnd(String s, int max) {
    if (s.length() > max)
      return s.substring(0, max) + "...";
    else
      return s;
  }

  /**
   * Shortens a string if longer than the maximum.
   *
   * @param s		the string to process
   * @param max		the maximum number of characters
   * @param type	the type of shortening
   * @return		the processed string
   */
  public static String shorten(String s, int max, ShorteningType type) {
    switch (type) {
      case NONE:
	return s;
      case START:
	return shortenStart(s, max);
      case MIDDLE:
	return shortenMiddle(s, max);
      case END:
	return shortenEnd(s, max);
      default:
	throw new IllegalArgumentException("Unhandled shortening type: " + type);
    }
  }
}
