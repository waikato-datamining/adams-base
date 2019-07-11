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
 * Stratification.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.splitgenerator.generic.stratification;

import adams.data.binning.Binnable;

import java.util.List;

/**
 * Interface for schemes that stratify data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface Stratification {

  /**
   * Resets the scheme.
   */
  public void reset();

  /**
   * Stratifies the list of binnable data, using the value of the items.
   * Creates copy of list, then sorts list items according to their values.
   * Based on Weka's Instances.stratify(int) method.
   *
   * @param data	the data to stratify
   * @param folds	the number of folds
   * @param <T>		the payload type
   * @return 		the stratified data
   */
  public <T> List<Binnable<T>> stratify(List<Binnable<T>> data, int folds);
}
