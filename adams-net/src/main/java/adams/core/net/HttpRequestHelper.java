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
 * HttpRequestHelper.java
 * Copyright (C) 2018-2019 University of Waikato, Hamilton, NZ
 */

package adams.core.net;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Helper class for http requests.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class HttpRequestHelper {

  /**
   * Creates a random boundary string.
   *
   * @return		the random boundary string
   */
  public static String createBoundary() {
    String	result;
    Random rand;

    rand     = new Random();
    result = Integer.toHexString(rand.nextInt()) + Integer.toHexString(rand.nextInt()) + Integer.toHexString(rand.nextInt());

    return result;
  }

  /**
   * Breaks up the string into lines, using the specified hard line limit.
   *
   * @param s		the string to break up
   * @param columns	the hard line limt
   * @return		the broken up string
   */
  public static String[] breakUp(String s, int columns) {
    List<String> 	result;
    int			i;
    StringBuilder	current;
    char		c;

    result  = new ArrayList<>();
    current = null;

    if (columns < 1)
      columns = 1;

    for (i = 0; i < s.length(); i++) {
      if (current == null)
	current = new StringBuilder();
      c = s.charAt(i);
      current.append(c);
      if (current.length() == columns) {
	result.add(current.toString());
	current = null;
      }
    }

    if (current != null)
      result.add(current.toString());

    return result.toArray(new String[0]);
  }
}
