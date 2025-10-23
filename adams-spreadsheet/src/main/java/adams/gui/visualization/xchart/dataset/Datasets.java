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
 * Datasets.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.xchart.dataset;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Dataset list.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Datasets<T extends Dataset>
  extends ArrayList<T> {

  private static final long serialVersionUID = 3778013218098340934L;

  /**
   * Constructs an empty dataset list
   */
  public Datasets() {
    super();
  }

  /**
   * Constructs a dataset list containing the elements of the specified
   * collection, in the order they are returned by the collection's
   * iterator.
   *
   * @param c the collection whose elements are to be placed into this list
   * @throws NullPointerException if the specified collection is null
   */
  public Datasets(Collection<? extends T> c) {
    super(c);
  }
}