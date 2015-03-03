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
 * Destroyable.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

/**
 * Interface for classes that need some further cleaning up that cannot be
 * done with the CleanUpHandler, as it is destructive.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see CleanUpHandler
 */
public interface Destroyable {

  /**
   * Frees up memory in a "destructive" non-reversible way.
   */
  public void destroy();
}
