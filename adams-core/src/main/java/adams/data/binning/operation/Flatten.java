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
 * Flatten.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.binning.operation;

import adams.data.binning.Bin;
import adams.data.binning.Binnable;

import java.util.ArrayList;
import java.util.List;

/**
 * For flattening bins into a binnable list.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Flatten {

  /**
   * Flattens the bins back into a list of binnables in the order of the bins.
   *
   * @param bins	the bins to flatten
   * @param <T>		the payload type
   * @return		the flattened list
   */
  public static <T> List<Binnable<T>> flatten(List<Bin<T>> bins) {
    List<Binnable<T>>	result;

    result = new ArrayList<>();
    for (Bin<T> bin: bins)
      result.addAll(bin.get());

    return result;
  }
}
