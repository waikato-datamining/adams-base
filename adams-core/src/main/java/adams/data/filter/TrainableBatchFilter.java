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
 * TrainableBatchFilter.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.filter;

import adams.data.container.DataContainer;

/**
 * Interface for trainable batch filters.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of data
 */
public interface TrainableBatchFilter<T extends DataContainer>
  extends Filter<T> {

  /**
   * Resets the filter, i.e., flags it as "not trained".
   *
   * @see		#isTrained()
   */
  public void resetFilter();

  /**
   * Trains the filter with the specified data.
   */
  public void trainFilter(T[] data);

  /**
   * Returns whether the filter has been trained already and is ready to use.
   *
   * @return		true if already trained
   */
  public boolean isTrained();
}
