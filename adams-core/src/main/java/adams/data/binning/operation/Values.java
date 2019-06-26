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
 * Values.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.binning.operation;

import adams.data.binning.Binnable;

import java.util.List;

/**
 * For extracting/changing the values from binnable lists.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Values {

  /**
   * Turns the values of the binnable objects into a double array.
   *
   * @param objects	the binnable objects to get the values from
   * @return		the values as array
   */
  public static <T> double[] toDoubleArray(List<Binnable<T>> objects) {
    double[] 	result;
    int		i;

    result = new double[objects.size()];
    for (i = 0; i < objects.size(); i++)
      result[i] = objects.get(i).getValue();

    return result;
  }

  /**
   * Turns the values of the binnable objects into a number array.
   *
   * @param objects	the binnable objects to get the values from
   * @return		the values as array
   */
  public static <T> Number[] toNumberArray(List<Binnable<T>> objects) {
    Number[] 	result;
    int		i;

    result = new Number[objects.size()];
    for (i = 0; i < objects.size(); i++)
      result[i] = objects.get(i).getValue();

    return result;
  }
}
