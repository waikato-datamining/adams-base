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
 * NearestNeighbourSearchWithIndices.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package weka.core.neighboursearch;

import weka.core.Instance;

/**
 * Interface for neighborhood search algorithms that can return the indices of the neighbors.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface NearestNeighbourSearchWithIndices {

  /**
   * Returns the indices of the k nearest instances in the current neighbourhood to the supplied
   * instance.
   *
   * @param target 	The instance to find the k nearest neighbours for.
   * @param kNN		The number of nearest neighbours to find.
   * @return		the indices of the k nearest neighbors in the dataset
   * @throws Exception  if the neighbours could not be found.
   */
  public int[] kNearestNeighboursIndices(Instance target, int kNN) throws Exception;
}
