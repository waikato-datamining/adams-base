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
 * InstanceView.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.ml.data;

import adams.core.Utils;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.DataRow;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.event.SpreadSheetColumnInsertionEvent;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import weka.core.Instance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Wrapper around an {@link Instance} object.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InstanceView
  implements DataRow {

  private static final long serialVersionUID = 1398119247909453155L;

  /** the owner. */
  protected InstancesView m_Owner;

  /** the underlying data. */
  protected Instance m_Data;

  /**
   * Initializes the row view.
   *
   * @param owner	the owning view
   * @param data	the underlying data
   */
  public InstanceView(InstancesView owner, Instance data) {
    m_Owner = owner;
    m_Data  = data;
  }

  /**
   * Returns the underlying Instance.
   *
   * @return		the underlying data
   */
  public Instance getData() {
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
   * Adds a cell with the given key to the list and returns the created object.
   * If the cell already exists, then this cell is returned instead and no new
   * object created.
   *
   * @param cellKey	the key for the cell to create
   * @return		the created cell or the already existing cell
   */
  @Override
  public Cell addCell(String cellKey) {
    int		col;

    col = cellKeyToIndex(cellKey);
    if (col > -1)
      return new DataCellView(this, col);
    else
      throw new NotImplementedException();
  }

  /**
   * Removes the cell at the specified index.
   *
   * @param columnIndex	the index of the column
   * @return			the removed cell, null if not removed
   */
  @Override
  public Cell removeCell(int columnIndex) {
    m_Data.setMissing(columnIndex);
    return null;
  }

  /**
   * Removes the cell at the specified index.
   *
   * @param cellKey	the key of the cell to remove
   * @return			the removed cell, null if non removed
   */
  @Override
  public Cell removeCell(String cellKey) {
    int		col;

    col = cellKeyToIndex(cellKey);
    if (col > -1)
      return removeCell(col);
    else
      return null;
  }

  /**
   * Returns the cell with the given key, null if not found.
   *
   * @param cellKey	the cell to look for
   * @return		the cell or null if not found
   */
  @Override
  public Cell getCell(String cellKey) {
    int		col;

    col = cellKeyToIndex(cellKey);
    if (col > -1)
      return getCell(col);
    else
      return null;
  }

  /**
   * Returns the cell with the given index, null if not found.
   *
   * @param columnIndex	the index of the column
   * @return			the cell or null if not found
   */
  @Override
  public Cell getCell(int columnIndex) {
    return new DataCellView(this, columnIndex);
  }

  /**
   * Returns the cell content with the given index.
   *
   * @param columnIndex	the index of the column
   * @return			the content or null if not found
   */
  @Override
  public String getContent(int columnIndex) {
    Cell	cell;

    cell = getCell(columnIndex);
    if (cell != null)
      return cell.getContent();
    else
      return null;
  }

  /**
   * Returns the cell key with the given column index.
   *
   * @param columnIndex	the index of the column
   * @return			the cell key, null if invalid index
   */
  @Override
  public String getCellKey(int columnIndex) {
    return "" + columnIndex;
  }

  /**
   * Returns a collection of all stored cell keys.
   *
   * @return		all cell keys
   */
  @Override
  public Collection<String> cellKeys() {
    List<String> 	result;
    int			i;

    result = new ArrayList<>();
    for (i = 0; i < m_Data.numAttributes(); i++)
      result.add("" + i);

    return result;
  }

  /**
   * Returns all cells.
   *
   * @return		the cells
   */
  @Override
  public Collection<Cell> cells() {
    List<Cell>		result;
    int			i;

    result = new ArrayList<>();
    for (i = 0; i < m_Data.numAttributes(); i++)
      result.add(getCell(i));

    return result;
  }

  /**
   * Returns the number of cells stored in the row.
   *
   * @return		the number of cells
   */
  @Override
  public int getCellCount() {
    return m_Data.numAttributes();
  }

  /**
   * Removes all cells marked "missing".
   *
   * @return		true if any cell was removed
   */
  @Override
  public boolean removeMissing() {
    return false;
  }

  /**
   * Returns the column this particular cell is in (must belong to this row!).
   *
   * @param cell	the cell to get the column index of
   * @return		the column index, -1 if not found
   */
  @Override
  public int indexOf(Cell cell) {
    DataCellView view;

    if (cell instanceof DataCellView) {
      view = (DataCellView) cell;
      if (view.getOwner().getData() == getData())
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
  @Override
  public void mergeWith(Row other) {
    throw new NotImplementedException();
  }

  /**
   * Sets the spreadsheet this row belongs to.
   *
   * @param owner	the owner
   */
  @Override
  public void setOwner(SpreadSheet owner) {
    if (owner instanceof InstancesView)
      m_Owner = (InstancesView) owner;
    else
      throw new IllegalArgumentException("Owner can only be " + InstancesView.class.getName());
  }

  /**
   * Returns the spreadsheet this row belongs to.
   *
   * @return		the owner
   */
  @Override
  public SpreadSheet getOwner() {
    return m_Owner;
  }

  /**
   * Returns a clone of itself.
   *
   * @param owner	the new owner
   * @return		the clone
   */
  @Override
  public DataRow getClone(SpreadSheet owner) {
    InstanceView	result;

    if (owner instanceof InstancesView) {
      result = new InstanceView((InstancesView) owner, m_Data);
      result.assign(this);
      return result;
    }
    else {
      throw new IllegalArgumentException("Owner can only be " + InstancesView.class.getName());
    }
  }

  /**
   * Removes all cells.
   */
  @Override
  public void clear() {
    int		i;

    for (i = 0; i < getCellCount(); i++)
      m_Data.setMissing(i);
  }

  /**
   * Obtains copies of the cells from the other row, but not the owner.
   *
   * @param row		the row to get the cells from
   */
  @Override
  public void assign(Row row) {
    int		i;

    for (i = 0; i < getCellCount(); i++) {
      if (!row.hasCell(i) || row.getCell(i).isMissing())
	m_Data.setMissing(i);
      else
	getCell(i).assign(row.getCell(i));
    }
  }

  /**
   * Creates a new instance of a cell.
   * <br><br>
   * Not implemented!
   *
   * @param owner	the owner
   * @return		the cell
   */
  @Override
  public Cell newCell(Row owner) {
    throw new NotImplementedException();
  }

  /**
   * Returns whether the row alread contains the cell at the specified location.
   *
   * @param columnIndex	the column index
   * @return		true if the cell already exists
   */
  @Override
  public boolean hasCell(int columnIndex) {
    return (columnIndex >= 0) && (columnIndex < m_Data.numAttributes());
  }

  /**
   * Returns whether the row alread contains the cell with the given key.
   *
   * @param cellKey	the key to look for
   * @return		true if the cell already exists
   */
  @Override
  public boolean hasCell(String cellKey) {
    return hasCell(cellKeyToIndex(cellKey));
  }

  /**
   * Adds a cell with the key of the cell in the header at the specified
   * location. If the cell already exists, it returns that instead of
   * creating one.
   *
   * @param columnIndex	the index of the column to create
   * @return		the created cell or the already existing cell
   */
  @Override
  public Cell addCell(int columnIndex) {
    if (hasCell(columnIndex))
      return getCell(columnIndex);
    else
      return null;
  }

  /**
   * A column got inserted.
   *
   * @param e		the insertion event
   */
  @Override
  public void spreadSheetColumnInserted(SpreadSheetColumnInsertionEvent e) {

  }
}
