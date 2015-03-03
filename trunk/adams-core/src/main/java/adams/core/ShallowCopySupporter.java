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
 * ShallowCopySupporter.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

/**
 * Interface for classes that support shallow copying, i.e., basically
 * only the setup without the internal state.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of object the shallow copy returns
 */
public interface ShallowCopySupporter<T> {

  /**
   * Returns a shallow copy of itself.
   *
   * @return		the shallow copy
   */
  public T shallowCopy();

  /**
   * Returns a shallow copy of itself.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public T shallowCopy(boolean expand);
}
