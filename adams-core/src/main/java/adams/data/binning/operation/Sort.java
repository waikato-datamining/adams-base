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
 * Sort.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.binning.operation;

import adams.data.binning.Binnable;

import java.util.List;

/**
 * For sorting binnable data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Sort {

  /**
   * Swaps two elements in the list.
   *
   * @param first 	the first list item
   * @param second 	the second list item
   */
  protected static <T> void swap(List<Binnable<T>> list, int first, int second) {
    Binnable<T> 	tmp;

    tmp = list.get(first);
    list.set(first, list.get(second));
    list.set(second, tmp);
  }

  /**
   * Groups the binnable data in-place using its values.
   * Based on Weka's sorting method in Instances#stratify(int), assuming
   * that the values are discrete.
   *
   * @param data	the data to sort
   * @param <T>		the payload type
   */
  public static <T> void group(List<Binnable<T>> data) {
    int 		i;
    int 		n;
    Binnable<T> 	b1;
    Binnable<T> 	b2;

    i = 1;
    while (i < data.size()) {
      b1 = data.get(i - 1);
      for (n = i; n < data.size(); n++) {
	b2 = data.get(n);
	if ((b1.getValue() == b2.getValue()) || (Double.isNaN(b1.getValue()) && Double.isNaN(b2.getValue()))) {
	  swap(data, i, n);
	  i++;
	}
      }
      i++;
    }
  }

  /**
   * Sorts the binnable data in-place using its values.
   *
   * @param data	the data to sort
   * @param <T>		the payload type
   */
  public static <T> void sort(List<Binnable<T>> data) {
    data.sort((o1, o2) -> Double.compare(o1.getValue(), o2.getValue()));
  }
}
