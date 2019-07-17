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
 * Bins.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.binning.operation;

import adams.data.binning.Bin;
import adams.data.binning.Binnable;
import com.github.fracpete.javautils.enumerate.Enumerated;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.util.List;

import static com.github.fracpete.javautils.Enumerate.enumerate;

/**
 * Operations on bins.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Bins {

  /** the temporary bin index. */
  public final static String TMP_BININDEX = "$$$tmpbinindex$$$";

  /**
   * Returns the size of the bins.
   *
   * @param bins	the bins to obtain the size from
   * @return		the sizes
   */
  public static <T> int[] binSizes(List<Bin<T>> bins) {
    TIntList result;

    result = new TIntArrayList();
    for (Bin<T> bin: bins)
      result.add(bin.size());

    return result.toArray();
  }

  /**
   * Pushes the bin index into the meta-data of the binnable items, using
   * {@link #TMP_BININDEX} as key.
   *
   * @param bins	the bins to update
   * @param <T>		the payload type
   * @return		the updated bins
   */
  public static <T> List<Bin<T>> addBinIndex(List<Bin<T>> bins) {
    for (Enumerated<Bin<T>> bin: enumerate(bins)) {
      for (Binnable<T> b:  bin.value.get())
        b.addMetaData(TMP_BININDEX, bin.index);
    }
    return bins;
  }

  /**
   * Removes the bin index from the meta-data of the binnable items, using
   * {@link #TMP_BININDEX} as key.
   *
   * @param bins	the bins to update
   * @param <T>		the payload type
   * @return		the updated bins
   */
  public static <T> List<Bin<T>> removeBinIndex(List<Bin<T>> bins) {
    for (Enumerated<Bin<T>> bin: enumerate(bins)) {
      for (Binnable<T> b:  bin.value.get())
        b.removeMetaData(TMP_BININDEX);
    }
    return bins;
  }
}
