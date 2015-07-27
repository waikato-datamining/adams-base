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
 * SparseFloatDataRow.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.data.spreadsheet;

/**
 * Represents a data row with sparse data, using floats internally for
 * representing the cells.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SparseFloatDataRow
  extends SparseDataRow {

  private static final long serialVersionUID = 888420460140542133L;

  /**
   * Default constructor for GOE only.
   */
  public SparseFloatDataRow() {
    super();
  }

  /**
   * Constructor that ties row to spreadsheet.
   *
   * @param owner	the spreadsheet this row belongs to
   */
  public SparseFloatDataRow(SpreadSheet owner) {
    super(owner);
  }

  /**
   * Creates a new instance of a cell.
   *
   * @param owner	the owner
   * @return		the cell
   */
  public Cell newCell(Row owner) {
    return new FloatCell(owner);
  }
}
