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
 * SplitIndices.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.data.indexedsplits;

import adams.core.logging.LoggingObject;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

/**
 * Wrapper around indices.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SplitIndices
  extends LoggingObject {

  private static final long serialVersionUID = 7768427666356815978L;

  /** the name. */
  protected String m_Name;

  /** the indices. */
  protected TIntList m_Indices;

  /**
   * Initializes the indices.
   *
   * @param name 	the name for the indices
   * @param indices	the indices to use
   */
  public SplitIndices(String name, int[] indices) {
    m_Name    = name;
    m_Indices = new TIntArrayList(indices);
  }

  /**
   * Returns the name for the indices.
   *
   * @return		the name
   */
  public String getName() {
    return m_Name;
  }

  /**
   * Returns the number of indices stored.
   *
   * @return		the number of indices
   */
  public int size() {
    return m_Indices.size();
  }

  /**
   * Returns the indices.
   *
   * @return		the indices in use
   */
  public int[] getIndices() {
    return m_Indices.toArray();
  }

  /**
   * Returns a short textual representation.
   *
   * @return		the representation
   */
  public String toString() {
    return "#indices=" + m_Indices.size();
  }
}
