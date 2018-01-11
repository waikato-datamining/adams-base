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
 * InstancesHeader.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.ml.data;

import adams.core.Utils;
import adams.core.exception.NotImplementedException;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.DoubleCell;
import adams.data.spreadsheet.HeaderRow;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Header row for an {@link Instances} object.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InstancesHeaderRow
  extends HeaderRow {

  private static final long serialVersionUID = 5290309704559917874L;

  /** the underlying data. */
  protected Instances m_Data;

  /**
   * Initializes the header row.
   *
   * @param owner	the owner
   */
  public InstancesHeaderRow(InstancesView owner) {
    super(owner);
    m_Data = owner.getData();
  }

  /**
   * Returns the underlying Instance.
   *
   * @return		the underlying data
   */
  public Instances getData() {
    return m_Data;
  }

  /**
   * Turns the cellKey into a column index.
   *
   * @param cellKey	the cellKey to convert
   * @return		the column index, -1 if failed to convert
   */
  protected int cellKeyToIndex(String cellKey) {
    if (Utils.isInteger(cellKey))
      return Integer.parseInt(cellKey);
    else
      return -1;
  }

  /**
   * Sets the spreadsheet this row belongs to.
   *
   * @param owner	the owner
   */
  public void setOwner(SpreadSheet owner) {
    if (owner instanceof InstancesView)
      super.setOwner(owner);
    else
      throw new IllegalArgumentException("Owner can only be " + InstancesView.class.getName());
  }

  /**
   * Returns a clone of itself.
   *
   * @param owner	the new owner
   * @return		the clone
   */
  public HeaderRow getClone(SpreadSheet owner) {
    if (owner instanceof InstancesView)
      return new InstancesHeaderRow((InstancesView) owner);
    else
      throw new IllegalArgumentException("Owner can only be " + InstancesView.class.getName());
  }

  /**
   * Removes all cells.
   * <br>
   * Does nothing.
   */
  public void clear() {
  }

  /**
   * Obtains copies of the cells from the other row, but not the owner.
   * <br>
   * Does nothing.
   *
   * @param row		the row to get the cells from
   */
  public void assign(Row row) {
  }

  /**
   * Creates a new instance of a cell.
   *
   * @param owner	the owner
   * @return		always null
   */
  public Cell newCell(Row owner) {
    return null;
  }

  /**
   * Returns whether the row alread contains the cell at the specified location.
   *
   * @param columnIndex	the column index
   * @return		true if the cell already exists
   */
  public boolean hasCell(int columnIndex) {
    return (getCell(columnIndex) != null);
  }

  /**
   * Returns whether the row alread contains the cell with the given key.
   *
   * @param cellKey	the key to look for
   * @return		true if the cell already exists
   */
  public boolean hasCell(String cellKey) {
    return (getCell(cellKey) != null);
  }

  /**
   * Adds a cell with the key of the cell in the header at the specified
   * location. If the cell already exists, it returns that instead of
   * creating one.
   *
   * @param columnIndex	the index of the column to create
   * @return		the created cell or the already existing cell
   */
  public Cell addCell(int columnIndex) {
    return getCell(columnIndex);
  }

  /**
   * Adds a cell with the given key to the list and returns the created object.
   * If the cell already exists, then this cell is returned instead and no new
   * object created.
   *
   * @param cellKey	the key for the cell to create
   * @return		the created cell or the already existing cell
   */
  public Cell addCell(String cellKey) {
    int		col;

    col = cellKeyToIndex(cellKey);
    if ((col >= 0) && (col < getCellCount()))
      return getCell(col);
    else
      return null;
  }

  /**
   * Removes the cell at the specified index.
   *
   * @param columnIndex	the index of the column
   * @return			the removed cell, null if not removed
   */
  public Cell removeCell(int columnIndex) {
    return null;
  }

  /**
   * Removes the cell at the specified index.
   *
   * @param cellKey	the key of the cell to remove
   * @return			the removed cell, null if non removed
   */
  public Cell removeCell(String cellKey) {
    return null;
  }

  /**
   * Returns the cell with the given key, null if not found.
   *
   * @param cellKey	the cell to look for
   * @return		the cell or null if not found
   */
  public Cell getCell(String cellKey) {
    return getCell(cellKeyToIndex(cellKey));
  }

  /**
   * Returns the cell with the given index, null if not found.
   *
   * @param columnIndex	the index of the column
   * @return			the cell or null if not found
   */
  public Cell getCell(int columnIndex) {
    if ((columnIndex >= 0) && (columnIndex < getCellCount()))
      return new DoubleCell(this).setContentAsString(m_Data.attribute(columnIndex).name());
    else
      return null;
  }

  /**
   * Returns the cell content with the given index.
   *
   * @param columnIndex	the index of the column
   * @return			the content or null if not found
   */
  public String getContent(int columnIndex) {
    return getCell(columnIndex).getContent();
  }

  /**
   * Returns the cell key with the given column index.
   *
   * @param columnIndex	the index of the column
   * @return			the cell key, null if invalid index
   */
  public String getCellKey(int columnIndex) {
    return "" + columnIndex;
  }

  /**
   * Returns a collection of all stored cell keys.
   *
   * @return		all cell keys
   */
  public Collection<String> cellKeys() {
    List<String>	result;
    int			i;

    result = new ArrayList<>();
    for (i = 0; i < getCellCount(); i++)
      result.add("" + i);

    return result;
  }

  /**
   * Returns all cells.
   *
   * @return		the cells
   */
  public Collection<Cell> cells() {
    List<Cell>		result;
    int			i;

    result = new ArrayList<>();
    for (i = 0; i < getCellCount(); i++)
      result.add(getCell(i));

    return result;
  }

  /**
   * Returns the number of cells stored in the row.
   *
   * @return		the number of cells
   */
  public int getCellCount() {
    return m_Data.numAttributes();
  }

  /**
   * Removes all cells marked "missing".
   *
   * @return		true if any cell was removed
   */
  public boolean removeMissing() {
    return false;
  }

  /**
   * Returns the column this particular cell is in (must belong to this row!).
   *
   * @param cell	the cell to get the column index of
   * @return		the column index, -1 if not found
   */
  public int indexOf(Cell cell) {
    if (cell.getOwner() instanceof InstancesHeaderRow) {
      if (((InstancesHeaderRow) cell.getOwner()) == this)
	return cell.index();
      else
	return -1;
    }
    else {
      return -1;
    }
  }

  /**
   * Merges its own data with the one provided by the specified row.
   * <br><br>
   * Not implemented!
   *
   * @param other		the row to merge with
   */
  public void mergeWith(Row other) {
    throw new NotImplementedException();
  }

  /**
   * Simply returns the internal hashtable of cells as string.
   *
   * @return		the values of the row
   */
  public String toString() {
    StringBuilder 	result;
    int			i;

    result = new StringBuilder();
    for (i = 0; i < m_Data.numAttributes(); i++) {
      if (i > 0)
	result.append(",");
      result.append(m_Data.attribute(i).name());
    }

    return result.toString();
  }
}
