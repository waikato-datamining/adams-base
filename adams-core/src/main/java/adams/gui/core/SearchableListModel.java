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
 * SearchableListModel.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

import javax.swing.ListModel;

/**
 * Interface for ListModels that can be searched.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface SearchableListModel
  extends ListModel {

  /**
   * Returns the actual index in the model.
   *
   * @param index	the index of the currently displayed data
   * @return		the index in the underlying data
   */
  public int getActualIndex(int index);

  /**
   * Returns the actual size of the model.
   *
   * @return		the size in the underlying data
   */
  public int getActualSize();

  /**
   * Performs a search for the given string. Limits the display of rows to
   * ones containing the search string.
   *
   * @param searchString	the string to search for
   * @param regexp		whether to perform regular expression matching
   * 				or just plain string comparison
   */
  public void search(String searchString, boolean regexp);

  /**
   * Returns the current search string.
   *
   * @return		the search string, null if not filtered
   */
  public String getSeachString();

  /**
   * Returns whether the last search was a regular expression based one.
   *
   * @return		true if last search was a reg exp one
   */
  public boolean isRegExpSearch();
}
