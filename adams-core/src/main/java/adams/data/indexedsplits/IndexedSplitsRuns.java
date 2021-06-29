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
 * IndexedSplitsRuns.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.data.indexedsplits;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Combines multiple IndexedSplitRun objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class IndexedSplitsRuns
  extends ArrayList<IndexedSplitsRun> {

  private static final long serialVersionUID = -7149163938744310061L;

  /** the meta-data. */
  protected MetaData m_MetaData;

  /**
   * Constructs an empty list with an initial capacity of ten.
   */
  public IndexedSplitsRuns() {
    super();
    initialize();
  }

  /**
   * Constructs a list containing the elements of the specified
   * collection, in the order they are returned by the collection's
   * iterator.
   *
   * @param c the collection whose elements are to be placed into this list
   * @throws NullPointerException if the specified collection is null
   */
  public IndexedSplitsRuns(Collection<IndexedSplitsRun> c) {
    super(c);
    initialize();
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    m_MetaData = new MetaData();
  }

  /**
   * Returns the underlying meta-data.
   *
   * @return	the meta-data
   */
  public MetaData getMetaData() {
    return m_MetaData;
  }
}
