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
 * FilteredDataProvider.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.db;

import adams.data.container.DataContainer;

/**
 * Interface for data providers that normally returned the data filtered.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of container to use
 */
public interface FilteredDataProvider<T extends DataContainer>
  extends DataProvider<T> {

  /**
   * Load a data container with given auto_id, without passing it through
   * the global filter.
   *
   * @param auto_id	the databae ID
   * @return 		the data container, or null if not found
   */
  public T loadRaw(int auto_id);
}
