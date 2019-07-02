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
 * Split.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.binning.operation;

import adams.data.binning.Binnable;
import adams.data.binning.BinnableGroup;
import com.github.fracpete.javautils.struct.Struct2;

import java.util.ArrayList;
import java.util.List;

/**
 * For splitting data according to a percentage.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Split {

  /**
   * Calculates the number of items the percentage represents.
   *
   * @param total	the total number of items
   * @param percentage	the percentage to use (0-1)
   * @return		the number of items
   */
  protected static int calcNumItems(int total, double percentage) {
    return (int) Math.round(total * percentage);
  }

  /**
   * Splits the data into two, using the percentage for the first lot of data.
   *
   * @param data	the data to split
   * @param percentage	the percentage (0-1)
   * @param <T>		the payload type
   * @return		the split data
   */
  public static <T> Struct2<List<Binnable<T>>,List<Binnable<T>>> split(List<Binnable<T>> data, double percentage) {
    List<Binnable<T>>	first;
    List<Binnable<T>>	second;
    int			num;

    num    = calcNumItems(data.size(), percentage);
    first  = new ArrayList<>();
    second = new ArrayList<>();
    for (Binnable<T> item: data) {
      if (first.size() < num)
        first.add(item);
      else
        second.add(item);
    }

    return new Struct2<>(first, second);
  }

  /**
   * Splits the grouped data into two, using the percentage for the first lot of data.
   *
   * @param data	the data to split
   * @param percentage	the percentage (0-1)
   * @param <T>		the payload type
   * @return		the split data
   */
  public static <T> Struct2<List<BinnableGroup<T>>,List<BinnableGroup<T>>> splitGroups(List<BinnableGroup<T>> data, double percentage) {
    List<BinnableGroup<T>>	first;
    List<BinnableGroup<T>>	second;
    int				num;

    num    = calcNumItems(data.size(), percentage);
    first  = new ArrayList<>();
    second = new ArrayList<>();
    for (BinnableGroup<T> item: data) {
      if (first.size() < num)
        first.add(item);
      else
        second.add(item);
    }

    return new Struct2<>(first, second);
  }
}
