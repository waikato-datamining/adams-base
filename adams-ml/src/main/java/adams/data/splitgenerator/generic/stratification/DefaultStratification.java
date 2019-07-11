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
 * DefaultStratification.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.splitgenerator.generic.stratification;

import adams.data.binning.Binnable;
import adams.data.binning.operation.Stratify;

import java.util.List;

/**
 * Stratifies the data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @see Stratify#stratify(List, int)
 */
public class DefaultStratification
  extends AbstractStratification {

  private static final long serialVersionUID = 1656173611217395496L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Stratifies the data.";
  }

  /**
   * Stratifies the data.
   *
   * @param data	the data to stratify
   * @param folds	the number of folds
   * @param <T>		the payload type
   * @return		the stratified data
   */
  @Override
  protected <T> List<Binnable<T>> doStratify(List<Binnable<T>> data, int folds) {
    return Stratify.stratify(data, folds);
  }
}
