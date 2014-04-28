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
 * StringCompare.java
 * Copyright (C) 2005-2010 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Compares two strings with the following order:
 * <ul>
 *    <li>case insensitive</li>
 *    <li>german umlauts (&auml; , &ouml; etc.) or other non-ASCII letters
 *    are treated as special chars</li>
 *    <li>special chars &lt; numbers &lt; letters</li>
 * </ul>
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StringCompare
  implements Comparator, Serializable {

  /** for serialization. */
  private static final long serialVersionUID = 7944139083514487579L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return 
	"Compares two strings with the following order:\n"
	+ "- case insensitive\n"
	+ "- german umlauts or other non-ASCII letters are treated as special chars\n"
	+ "- special chars < numbers < letters";
  }
  
  /**
   * appends blanks to the string if its shorter than <code>len</code>.
   *
   * @param s		the string to pad
   * @param len	the minimum length for the string to have
   * @return		the padded string
   */
  protected String fillUp(String s, int len) {
    while (s.length() < len)
      s += " ";
    return s;
  }

  /**
   * returns the group of the character: 0=special char, 1=number, 2=letter.
   *
   * @param c		the character to check
   * @return		the group
   */
  protected int charGroup(char c) {
    int         result;

    result = 0;

    if ( (c >= 'a') && (c <= 'z') )
      result = 2;
    else if ( (c >= '0') && (c <= '9') )
      result = 1;

    return result;
  }

  /**
   * Compares its two arguments for order.
   *
   * @param o1	the first object
   * @param o2	the second object
   * @return		-1 if o1&lt;o2, 0 if o1=o2 and 1 if o1&;gt;o2
   */
  public int compare(Object o1, Object o2) {
    String        s1;
    String        s2;
    int           i;
    int           result;
    int           v1;
    int           v2;

    result = 0;   // they're equal

    // get lower case string
    s1 = o1.toString().toLowerCase();
    s2 = o2.toString().toLowerCase();

    // same length
    s1 = fillUp(s1, s2.length());
    s2 = fillUp(s2, s1.length());

    for (i = 0; i < s1.length(); i++) {
      // same char?
      if (s1.charAt(i) == s2.charAt(i)) {
        result = 0;
      }
      else {
        v1 = charGroup(s1.charAt(i));
        v2 = charGroup(s2.charAt(i));

        // different type (special, number, letter)?
        if (v1 != v2) {
          if (v1 < v2)
            result = -1;
          else
            result = 1;
        }
        else {
          if (s1.charAt(i) < s2.charAt(i))
            result = -1;
          else
            result = 1;
        }

        break;
      }
    }

    return result;
  }

  /**
   * Indicates whether some other object is "equal to" this Comparator.
   *
   * @param obj	the object to compare with this Comparator
   * @return		true if the object is a StringCompare object as well
   */
  @Override
  public boolean equals(Object obj) {
    return (obj instanceof StringCompare);
  }
}