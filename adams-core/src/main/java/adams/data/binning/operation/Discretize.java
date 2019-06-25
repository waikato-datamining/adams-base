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
 * Discretize.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.binning.operation;

import adams.data.binning.Bin;
import adams.data.binning.Binnable;
import com.github.fracpete.javautils.enumerate.Enumerated;

import java.util.ArrayList;
import java.util.List;

import static com.github.fracpete.javautils.Enumerate.enumerate;

/**
 * Turns the bins into a discretized binnable list.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Discretize {

  /**
   * Uses the bin list index as new value for the binnable items.
   *
   * @param bins	the bins to discretize
   * @param <T>		the payload type
   * @return		the discretized list
   */
  public static <T> List<Binnable<T>> discretize(List<Bin<T>> bins) {
    List<Binnable<T>> 	result;

    result = new ArrayList<>();
    for (Enumerated<Bin<T>> bin : enumerate(bins)) {
      for (Binnable<T> binnable : bin.value.get())
      result.add(new Binnable<>(binnable.getPayload(), bin.index));
    }

    return result;
  }
}
