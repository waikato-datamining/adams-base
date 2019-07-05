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
 * Subset.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.ml.splitgenerator.generic.core;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Container for a dataset subset.
 *
 * @param <T>	the type of wrapped data
 */
public class Subset<T>
  implements Serializable {

  private static final long serialVersionUID = 3833693505441351845L;

  /** the data. */
  protected List<T> m_Data;

  /** the original indices (can be null). */
  protected TIntList m_OriginalIndices;

  /**
   * Initializes the container.
   *
   * @param data		the data
   * @param originalIndices	the indices
   */
  public Subset(List<T> data, TIntList originalIndices) {
    m_Data            = new ArrayList<>(data);
    m_OriginalIndices = new TIntArrayList(originalIndices);
  }

  /**
   * Returns the data.
   *
   * @return		the data
   */
  public List<T> getData() {
    return m_Data;
  }

  /**
   * Returns the original indices.
   *
   * @return		the indices
   */
  public TIntList getOriginalIndices() {
    return m_OriginalIndices;
  }
}
