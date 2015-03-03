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
 * Mergeable.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.core;

/**
 * For objects that allow merging.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> The class type.
 */
public interface Mergeable<T> {

  /**
   * Merges its own data with the one provided by the specified object.
   * 
   * @param other		the object to merge with
   */
  public void mergeWith(T other);
}
