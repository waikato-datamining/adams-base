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
 * Statistics.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.binning.operation;

import adams.data.binning.Binnable;
import com.github.fracpete.javautils.struct.Struct2;

import java.util.List;

/**
 * For computing statistics on binnable data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Statistics {

  /**
   * Determines the min/max of the values.
   *
   * @param data	the list to process
   * @param <T>		the payload type
   * @return		the min/max
   */
  public static <T> Struct2<Double,Double> minMax(List<Binnable<T>> data) {
    double	min;
    double	max;

    min = Double.MAX_VALUE;
    max = -Double.MAX_VALUE;
    for (Binnable<T> b: data) {
      min = Math.min(min, b.getValue());
      max = Math.max(max, b.getValue());
    }

    return new Struct2<>(min, max);
  }
}
