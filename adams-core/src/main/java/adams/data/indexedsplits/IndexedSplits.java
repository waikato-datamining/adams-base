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
 * IndexedSplits.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.data.indexedsplits;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Combines multiple IndexedSplit objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class IndexedSplits
  extends ArrayList<IndexedSplit> {

  private static final long serialVersionUID = 2038127012987944539L;

  /**
   * Constructs an empty list with an initial capacity of ten.
   */
  public IndexedSplits() {
    super();
  }

  /**
   * Constructs a list containing the elements of the specified
   * collection, in the order they are returned by the collection's
   * iterator.
   *
   * @param c the collection whose elements are to be placed into this list
   * @throws NullPointerException if the specified collection is null
   */
  public IndexedSplits(Collection<IndexedSplit> c) {
    super(c);
  }
}