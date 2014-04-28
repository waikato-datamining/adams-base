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

/**
 * CustomSearchListModel.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import javax.swing.ListModel;

/**
 * Interface for list models that customize how the search identifies a
 * match.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface CustomSearchListModel
  extends ListModel {

  /**
   * Tests whether the search matches the specified element index.
   *
   * @param params	the search parameters
   * @param index	the element index of the underlying, unsorted model
   * @return		true if the search matches this index
   */
  public boolean isSearchMatch(SearchParameters params, int index);
}
