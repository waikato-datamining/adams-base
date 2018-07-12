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
 * AbstractRowCellFinder.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.data.spreadsheet.cellfinder;

import adams.data.spreadsheet.Row;

import java.util.Iterator;

/**
 * Ancestor for cell finders that operate on sheets or rows.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractRowCellFinder
  extends AbstractCellFinder
  implements RowCellFinder {

  private static final long serialVersionUID = -8885493337178062450L;

  /**
   * Checks whether the row can be processed.
   * <br><br>
   * Default implementation only checks whether a spreadsheet was provided.
   *
   * @param row	the row to check
   */
  protected void check(Row row) {
    if (row == null)
      throw new IllegalArgumentException("No row provided!");
  }

  /**
   * Performs the actual locating.
   *
   * @param row		the row to locate the cells in
   * @return		the iterator over the locations
   */
  protected abstract Iterator<CellLocation> doFindCells(Row row);

  /**
   * Locates the cells in the row.
   *
   * @param row		the row to locate the cells in
   * @return		the iterator over the locations
   */
  public Iterator<CellLocation> findCells(Row row) {
    check(row);
    return doFindCells(row);
  }
}
