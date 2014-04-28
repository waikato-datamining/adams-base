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
 * Row.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.spreadsheet;

import java.io.Serializable;
import java.util.Collection;

import adams.core.Mergeable;

/**
 * Interface for a row in a {@link SpreadSheet}.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface Row
  extends Serializable, Mergeable<Row> {

  /**
   * Sets the spreadsheet this row belongs to.
   * 
   * @param owner	the owner
   */
  public void setOwner(SpreadSheet owner);

  /**
   * Returns the spreadsheet this row belongs to.
   * 
   * @return		the owner
   */
  public SpreadSheet getOwner();
  
  /**
   * Returns a clone of itself.
   *
   * @param owner	the new owner
   * @return		the clone
   */
  public Row getClone(SpreadSheet owner);

  /**
   * Removes all cells.
   */
  public void clear();

  /**
   * Obtains copies of the cells from the other row, but not the owner.
   *
   * @param row		the row to get the cells from
   */
  public void assign(Row row);

  /**
   * Returns whether the row alread contains the cell at the specified location.
   *
   * @param columnIndex	the column index
   * @return		true if the cell already exists
   */
  public boolean hasCell(int columnIndex);
  
  /**
   * Returns whether the row alread contains the cell with the given key.
   *
   * @param cellKey	the key to look for
   * @return		true if the cell already exists
   */
  public boolean hasCell(String cellKey);
  
  /**
   * Adds a cell with the key of the cell in the header at the specified 
   * location. If the cell already exists, it returns that instead of
   * creating one.
   *
   * @param columnIndex	the index of the column to create
   * @return		the created cell or the already existing cell
   */
  public Cell addCell(int columnIndex);

  /**
   * Adds a cell with the given key to the list and returns the created object.
   * If the cell already exists, then this cell is returned instead and no new
   * object created.
   *
   * @param cellKey	the key for the cell to create
   * @return		the created cell or the already existing cell
   */
  public Cell addCell(String cellKey);
  
  /**
   * Removes the cell at the specified index.
   * 
   * @param columnIndex	the index of the column
   * @return			the removed cell, null if not removed
   */
  public Cell removeCell(int columnIndex);
  
  /**
   * Removes the cell at the specified index.
   * 
   * @param cellKey	the key of the cell to remove
   * @return			the removed cell, null if non removed
   */
  public Cell removeCell(String cellKey);

  /**
   * Returns the cell with the given key, null if not found.
   *
   * @param cellKey	the cell to look for
   * @return		the cell or null if not found
   */
  public Cell getCell(String cellKey);

  /**
   * Returns the cell with the given index, null if not found.
   *
   * @param columnIndex	the index of the column
   * @return			the cell or null if not found
   */
  public Cell getCell(int columnIndex);

  /**
   * Returns the cell content with the given index.
   *
   * @param columnIndex	the index of the column
   * @return			the content or null if not found
   */
  public String getContent(int columnIndex);

  /**
   * Returns the cell key with the given column index.
   *
   * @param columnIndex	the index of the column
   * @return			the cell key, null if invalid index
   */
  public String getCellKey(int columnIndex);

  /**
   * Returns a collection of all stored cell keys.
   *
   * @return		all cell keys
   */
  public Collection<String> cellKeys();

  /**
   * Returns all cells.
   *
   * @return		the cells
   */
  public Collection<Cell> cells();

  /**
   * Returns the number of cells stored in the row.
   *
   * @return		the number of cells
   */
  public int getCellCount();

  /**
   * Removes all cells marked "missing".
   * 
   * @return		true if any cell was removed
   */
  public boolean removeMissing();

  /**
   * Returns the column this particular cell is in (must belong to this row!).
   * 
   * @param cell	the cell to get the column index of
   * @return		the column index, -1 if not found
   */
  public int indexOf(Cell cell);

  /**
   * Merges its own data with the one provided by the specified row.
   * 
   * @param other		the row to merge with
   */
  public void mergeWith(Row other);
  
  /**
   * Simply returns the internal hashtable of cells as string.
   *
   * @return		the values of the row
   */
  public String toString();
}