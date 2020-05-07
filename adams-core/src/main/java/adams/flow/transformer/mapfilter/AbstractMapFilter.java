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
 * AbstractMapFilter.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.mapfilter;

import adams.core.option.AbstractOptionHandler;

import java.util.Map;

/**
 * Ancestor for map filters.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractMapFilter
  extends AbstractOptionHandler {

  private static final long serialVersionUID = 3009952990315967235L;

  /**
   * Hook method for checking the map before filtering it.
   *
   * @param map		the map to check
   * @return		null if checks passed, otherwise error message
   */
  protected String check(Map map) {
    if (map == null)
      return "No map object provided!";
    return null;
  }

  /**
   * Filters the map.
   *
   * @param map		the map to filter
   * @return		the filtered map
   */
  protected abstract Map doFilterMap(Map map);

  /**
   * Filters the map.
   *
   * @param map		the map to filter
   * @return		the filtered map
   */
  public Map filterMap(Map map) {
    String	msg;

    msg = check(map);
    if (msg != null)
      throw new IllegalStateException(msg);

    return doFilterMap(map);
  }
}
