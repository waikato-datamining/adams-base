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
 * AbstractMapToString.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.data.map;

import adams.core.option.AbstractOptionHandler;

import java.util.Map;

/**
 * Ancestor for conversion schemes that turn maps to strings.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractMapToString
  extends AbstractOptionHandler
  implements MapToString {

  private static final long serialVersionUID = -3511341771427549081L;

  /**
   * Checks the map before converting it.
   *
   * @param map 	the map to check
   * @return		null if checks passed, otherwise error message
   */
  protected String check(Map map) {
    if (map == null)
      return "No map provided!";
    return null;
  }

  /**
   * Converts the map into a string.
   *
   * @param map 	the map to convert
   * @return		the generated string
   */
  protected abstract String toString(Map map);

  /**
   * Converts the map into a string.
   *
   * @param map 	the map to convert
   * @return		the generated string
   */
  public String mapToString(Map map) {
    String	msg;

    msg = check(map);
    if (msg != null)
      throw new IllegalArgumentException(msg);

    return toString(map);
  }
}
