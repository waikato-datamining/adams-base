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
 * Stratify.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.binning.operation;

import adams.data.binning.Binnable;
import adams.data.binning.operation.Copy.CopyType;

import java.util.ArrayList;
import java.util.List;

/**
 * For stratifying binnable data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Stratify {

  /**
   * Stratifies the list of binnable data, using the value of the items.
   * Creates copy of list, then sorts list items according to their values.
   * Based on Weka's Instances.stratify(int) method.
   *
   * @param data	the data to stratify
   * @param folds	the number of folds
   * @param <T>		the payload type
   */
  public static <T> List<Binnable<T>> stratify(List<Binnable<T>> data, int folds) {
    List<Binnable<T>>	result;
    int 		start;
    int 		i;

    data = Copy.copyData(data, CopyType.LIST);
    Sort.group(data);
    result = new ArrayList<>();
    start  = 0;
    while (result.size() < data.size()) {
      i = start;
      while (i < data.size()) {
        result.add(data.get(i));
        i = i + folds;
      }
      start++;
    }
    return result;
  }
}
