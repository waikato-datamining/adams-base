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
 * Sorting.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core.io.lister;

/**
 * The type of sorting.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 13700 $
 */
public enum Sorting {
  /** no sorting. */
  NO_SORTING,
  /** sort by name. */
  SORT_BY_NAME,
  /** sort by last mod. */
  SORT_BY_LAST_MODIFIED
}
