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
 * TableModelWithColumnFilters.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

/**
 * Interface for TableModels that can be filtered per column.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface TableModelWithColumnFilters
  extends SearchableTableModel {

  /**
   * Sets the filter for the column.
   *
   * @param column	the column to filter
   * @param filter	the filter string
   * @param isRegExp	whether the filter is a regular expression
   */
  public void setColumnFilter(int column, String filter, boolean isRegExp);

  /**
   * Returns the filter for the column.
   *
   * @param column	the column to query
   * @return		the filter, null if none present
   */
  public String getColumnFilter(int column);

  /**
   * Returns whether the filter for the column is a regular expression.
   *
   * @param column	the column to query
   * @return		true if filter set and regular expression
   */
  public boolean isColumnFilterRegExp(int column);

  /**
   * Returns whether there is a filter active for the column.
   *
   * @param column	the column to query
   * @return		true if a filter is active
   */
  public boolean isColumnFiltered(int column);

  /**
   * Returns whether there is at least one filter active.
   *
   * @return		true if at least one filter is active
   */
  public boolean isAnyColumnFiltered();

  /**
   * Removes any filter for the column.
   *
   * @param column	the column to update
   */
  public void removeColumnFilter(int column);

  /**
   * Removes all column filters
   */
  public void removeAllColumnFilters();
}
