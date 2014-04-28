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
 * ChromatogramUtils.java
 * Copyright (C) 2008 University of Waikato, Hamilton, New Zealand
 */

package adams.data.container;

/**
 * Utility class for data containers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class DataContainerUtils {

  /**
   * An enumeration of types of gap-filling.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum GapFilling {
    /** nothing is done. */
    NOTHING,
    /** adds data points with the original y. */
    ORIGINAL,
    /** adds data points with zero y. */
    ZERO,
    /** adds data points with abundances that are on a straight line connecting
     * the borders of the gap. */
    CONNECT
  }
}
