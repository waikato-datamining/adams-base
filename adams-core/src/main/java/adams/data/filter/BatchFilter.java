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
 * BatchFilter.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.filter;

import adams.data.container.DataContainer;

/**
 * Interface for filters that can filter multiple data containers in one
 * go. Number of input and output containers does not have to be the same,
 * in case a transformation takes place.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface BatchFilter<T extends DataContainer>
  extends Filter<T> {

  /**
   * Batch filters the data.
   *
   * @param data	the data to filter
   * @return		the filtered data
   */
  public T[] batchFilter(T[] data);
}
