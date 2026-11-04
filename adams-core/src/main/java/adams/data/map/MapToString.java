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
 * MapToString.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.data.map;

import java.util.Map;

/**
 * Interface for classes that convert {@link java.util.Map} objects into strings.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface MapToString {

  /**
   * Converts the map into a string.
   *
   * @param map 	the map to convert
   * @return		the generated string
   */
  public String mapToString(Map map);
}
