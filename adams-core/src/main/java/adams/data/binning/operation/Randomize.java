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
 * Randomize.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.binning.operation;

import adams.data.binning.Bin;
import adams.data.binning.Binnable;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * For randomizing bins/binnable lists.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Randomize {

  /**
   * Randomizes the binnable data in-place.
   *
   * @param data	the data to randomize
   * @param seed	the seed to use
   * @param <T>		the type of payload
   * @return 		returns randomized input
   */
  public static <T> List<Binnable<T>> randomizeData(List<Binnable<T>> data, long seed) {
    return randomizeData(data, new Random(seed));
  }

  /**
   * Randomizes the binnable data in-place.
   *
   * @param data	the data to randomize
   * @param random	the random number generator to use
   * @param <T>		the type of payload
   * @return 		returns randomized input
   */
  public static <T> List<Binnable<T>> randomizeData(List<Binnable<T>> data, Random random) {
    Collections.shuffle(data, random);
    return data;
  }

  /**
   * Randomizes the data in the bins in-place.
   *
   * @param bins	the bins to randomize
   * @param seed	the seed to use
   * @param <T>		the type of payload
   * @return 		returns randomized input
   */
  public static <T> List<Bin<T>> randomizeBins(List<Bin<T>> bins, long seed) {
    return randomizeBins(bins, new Random(seed));
  }

  /**
   * Randomizes the data in the bins in-place.
   *
   * @param bins	the bins to randomize
   * @param random	the random number generator to use
   * @param <T>		the type of payload
   * @return 		returns randomized input
   */
  public static <T> List<Bin<T>> randomizeBins(List<Bin<T>> bins, Random random) {
    for (Bin<T> bin: bins)
      randomizeData(bin.get(), random);
    return bins;
  }
}
