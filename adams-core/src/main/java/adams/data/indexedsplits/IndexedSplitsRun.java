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
 * IndexedSplitsRun.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.data.indexedsplits;

import adams.core.logging.LoggingObject;

/**
 * Encapsulates a single run of indexed splits.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class IndexedSplitsRun
  extends LoggingObject {

  private static final long serialVersionUID = 2961289043024002739L;

  /** the run ID. */
  protected int m_ID;

  /** the splits. */
  protected IndexedSplits m_Splits;

  /**
   * Initializes the run.
   */
  public IndexedSplitsRun(int id, IndexedSplits splits) {
    m_ID     = id;
    m_Splits = splits;
  }

  /**
   * Returns the run ID.
   *
   * @return		the ID
   */
  public int getID() {
    return m_ID;
  }

  /**
   * Returns the splits.
   *
   * @return		the splits
   */
    public IndexedSplits getSplits() {
    return m_Splits;
  }

  /**
   * Returns a short textual description.
   *
   * @return		the description
   */
  public String toString() {
    return "runid=" + m_ID + ", splits=" + m_Splits;
  }
}
