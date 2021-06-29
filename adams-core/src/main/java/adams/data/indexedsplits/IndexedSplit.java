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
 * IndexedSplit.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.data.indexedsplits;

import adams.core.logging.LoggingObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Combines several lists of (named) indices. E.g., for train and test.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class IndexedSplit
  extends LoggingObject {

  private static final long serialVersionUID = -5715920089798537036L;

  /** the id of the of this split. */
  protected int m_ID;

  /** the splits. */
  protected Map<String,SplitIndices> m_Indices;

  /**
   * Initializes the split.
   */
  public IndexedSplit(int id) {
    m_ID      = id;
    m_Indices = new HashMap<>();
  }

  /**
   * Returns the split ID.
   *
   * @return		the ID
   */
  public int getID() {
    return m_ID;
  }

  /**
   * Adds the indices (eg for train or test).
   *
   * @param indices	the indices to add
   */
  public void add(SplitIndices indices) {
    m_Indices.put(indices.getName(), indices);
  }

  /**
   * The current indices.
   *
   * @return		the indices
   */
  public Map<String, SplitIndices> getIndices() {
    return m_Indices;
  }

  /**
   * Returns a short textual representation.
   *
   * @return		the representation
   */
  @Override
  public String toString() {
    return "splitid=" + m_ID + ", indices=" + m_Indices.toString();
  }
}
