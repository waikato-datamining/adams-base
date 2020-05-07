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
 * PassThrough.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.mapfilter;

import java.util.Map;

/**
 * Dummy, performs no filtering.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PassThrough
  extends AbstractMapFilter {

  private static final long serialVersionUID = 5724671899097703956L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Dummy, performs no filtering.";
  }

  /**
   * Filters the map.
   *
   * @param map		the map to filter
   * @return		the filtered map
   */
  @Override
  protected Map doFilterMap(Map map) {
    return map;
  }
}
