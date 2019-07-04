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
 * Copy.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.binning.operation;

import adams.core.Utils;
import adams.data.binning.Bin;
import adams.data.binning.Binnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates copies of the data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Copy {

  /**
   * How to copy.
   */
  public enum CopyType {
    /** just the list. */
    LIST,
    /** copy of the binnable. */
    BINNABLE,
    /** deep copy of payload. */
    DEEP,
  }

  /**
   * Generates a copy.
   *
   * @param data	the list to copy
   * @param type 	the type of copy to perform
   * @param <T>		the payload type
   * @return		the generated copy
   */
  public static <T> List<Binnable<T>> copyData(List<Binnable<T>> data, CopyType type) {
    List<Binnable<T>>	result;

    switch (type) {
      case LIST:
        result = new ArrayList<>(data);
        break;

      case BINNABLE:
        result = new ArrayList<>();
        for (Binnable<T> b: data)
          result.add(new Binnable<>(b.getPayload(), b.getValue(), b.getMetaData()));
        break;

      case DEEP:
        result = new ArrayList<>();
        for (Binnable<T> b: data)
          result.add(new Binnable<>((T) Utils.deepCopy(b.getPayload()), b.getValue(), b.getMetaData()));
        break;

      default:
        throw new IllegalStateException("Unhandled copy type: " + type);
    }

    return result;
  }

  /**
   * Generates a copy.
   *
   * @param bins	the bins to copy
   * @param type 	the type of copy to perform
   * @param <T>		the payload type
   * @return		the generated copy
   */
  public static <T> List<Bin<T>> copyBins(List<Bin<T>> bins, CopyType type) {
    List<Bin<T>>	result;
    Bin<T>		clone;

    switch (type) {
      case LIST:
        result = new ArrayList<>(bins);
        break;

      case BINNABLE:
        result = new ArrayList<>();
        for (Bin<T> b: bins)
          result.add(b.getClone());
        break;

      case DEEP:
        result = new ArrayList<>();
        for (Bin<T> b: bins) {
          clone = b.getClone();
          clone.get().clear();
          clone.addAll(copyData(b.get(), CopyType.DEEP));
	  result.add(clone);
	}
        break;

      default:
        throw new IllegalStateException("Unhandled copy type: " + type);
    }

    return result;
  }
}
