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
 * DataRowView.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.spreadsheet;

import adams.event.SpreadSheetColumnInsertionEvent;
import gnu.trove.list.array.TIntArrayList;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Provides a view to a data row.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DataRowView
  implements DataRow {

  private static final long serialVersionUID = -9144857545548275536L;

  /** the owning spreadsheet. */
  protected SpreadSheet m_Owner;

  /** the wrapped row. */
  protected DataRow m_Row;

  /** the column subset (null for all). */
  protected TIntArrayList m_Columns;

  /**
   * Initializes the view.
   *
   * @param owner	the owning spreadsheet
   * @param row		the row to wrap
   * @param columns	the column subset, null for all
   */
  public DataRowView(SpreadSheet owner, DataRow row, int[] columns) {
    super();

    m_Owner   = owner;
    m_Row     = row;
    m_Columns = null;
    if (columns != null)
      m_Columns = new TIntArrayList(columns);
  }

  /**
   * Returns the actual index of the column.
   *
   * @param columnIndex	the index
   * @return		the actual index
   */
  protected int getActualColumn(int columnIndex) {
    if (m_Columns == null)
      return columnIndex;
    else
      return m_Columns.get(columnIndex);
  }

  /**
   * Returns the actual key of the column.
   *
   * @param cellKey	the index
   * @return		the actual key, null if not found
   */
  protected String getActualColumn(String cellKey) {
    String	result;
    int		col;

    result = null;

    if (m_Columns == null) {
      result = cellKey;
    }
    else {
      if (m_Row.hasCell(cellKey)) {
	col = m_Row.getCell(cellKey).index();
	if (m_Columns.contains(col))
	  result = cellKey;
      }
    }

    return result;
  }

  /**
   * Adds a cell with the given key to the list and returns the created object.
   * If the cell already exists, then this cell is returned instead and no new
   * object created.
   * <br>
   * Not implemented!
   *
   * @param cellKey	the key for the cell to create
   * @return		the created cell or the already existing cell
   */
  @Override
  public Cell addCell(String cellKey) {
    throw new NotImplementedException();
  }

  /**
   * Removes the cell at the specified index.
   * <br>
   * Not implemented!
   *
   * @param columnIndex	the index of the column
   * @return			the removed cell, null if not removed
   */
  @Override
  public Cell removeCell(int columnIndex) {
    throw new NotImplementedException();
  }

  /**
   * Removes the cell at the specified index.
   * <br>
   * Not implemented!
   *
   * @param cellKey	the key of the cell to remove
   * @return			the removed cell, null if non removed
   */
  @Override
  public Cell removeCell(String cellKey) {
    throw new NotImplementedException();
  }

  /**
   * Returns the cell with the given key, null if not found.
   *
   * @param cellKey	the cell to look for
   * @return		the cell or null if not found
   */
  @Override
  public Cell getCell(String cellKey) {
    cellKey = getActualColumn(cellKey);
    if (cellKey != null)
      return m_Row.getCell(cellKey);
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
    return new CellView(this, m_Row.getCell(getActualColumn(columnIndex)));
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
    return m_Row.getCellKey(getActualColumn(columnIndex));
  }

  /**
   * Returns a collection of all stored cell keys.
   *
   * @return		all cell keys
   */
  @Override
  public Collection<String> cellKeys() {
    Collection<String>	result;
    int			i;

    if (m_Columns == null) {
      result = m_Row.cellKeys();
    }
    else {
      result = new ArrayList<>();
      for (i = 0; i < m_Columns.size(); i++)
	result.add(m_Row.getCellKey(m_Columns.get(i)));
    }

    return result;
  }

  /**
   * Returns all cells.
   *
   * @return		the cells
   */
  @Override
  public Collection<Cell> cells() {
    Collection<Cell>	result;
    int			i;

    result = new ArrayList<>();
    if (m_Columns == null) {
      for (Cell cell: m_Row.cells())
        result.add(new CellView(this, cell));
    }
    else {
      result = new ArrayList<>();
      for (i = 0; i < m_Columns.size(); i++)
	result.add(new CellView(this, m_Row.getCell(m_Columns.get(i))));
    }

    return result;
  }

  /**
   * Returns the number of cells stored in the row.
   *
   * @return		the number of cells
   */
  @Override
  public int getCellCount() {
    return (m_Columns == null) ? m_Row.getCellCount() : m_Columns.size();
  }

  /**
   * Removes all cells marked "missing".
   * <br>
   * Not implemented!
   *
   * @return		true if any cell was removed
   */
  @Override
  public boolean removeMissing() {
    throw new NotImplementedException();
  }

  /**
   * Returns the column this particular cell is in (must belong to this row!).
   *
   * @param cell	the cell to get the column index of
   * @return		the column index, -1 if not found
   */
  @Override
  public int indexOf(Cell cell) {
    int		result;

    result = m_Row.indexOf(cell);
    if ((result > -1) && (m_Columns != null))
      result = m_Columns.indexOf(result);

    // unwrap?
    if ((result == -1) && (cell instanceof CellView))
      result = indexOf(((CellView) cell).getCell());

    return result;
  }

  /**
   * Merges its own data with the one provided by the specified row.
   * <br>
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
   * <br>
   * Not implemented!
   *
   * @param owner	the owner
   */
  @Override
  public void setOwner(SpreadSheet owner) {
    throw new NotImplementedException();
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
    return new DataRowView(m_Owner, m_Row.getClone(m_Owner), (m_Columns == null) ? null : m_Columns.toArray());
  }

  /**
   * Removes all cells.
   * <br>
   * Not implemented!
   */
  @Override
  public void clear() {
    throw new NotImplementedException();
  }

  /**
   * Obtains copies of the cells from the other row, but not the owner.
   *
   * @param row		the row to get the cells from
   */
  @Override
  public void assign(Row row) {
    m_Row = (DataRow) row;
  }

  /**
   * Creates a new instance of a cell.
   *
   * @param owner	the owner
   * @return		the cell
   */
  @Override
  public Cell newCell(Row owner) {
    return new CellView(this, m_Row.newCell(owner));
  }

  /**
   * Returns whether the row alread contains the cell at the specified location.
   *
   * @param columnIndex	the column index
   * @return		true if the cell already exists
   */
  @Override
  public boolean hasCell(int columnIndex) {
    return m_Row.hasCell(getActualColumn(columnIndex));
  }

  /**
   * Returns whether the row alread contains the cell with the given key.
   *
   * @param cellKey	the key to look for
   * @return		true if the cell already exists
   */
  @Override
  public boolean hasCell(String cellKey) {
    cellKey = getActualColumn(cellKey);
    return (cellKey != null) && m_Row.hasCell(cellKey);
  }

  /**
   * Adds a cell with the key of the cell in the header at the specified
   * location. If the cell already exists, it returns that instead of
   * creating one.
   * <br>
   * Not implemented!
   *
   * @param columnIndex	the index of the column to create
   * @return		the created cell or the already existing cell
   */
  @Override
  public Cell addCell(int columnIndex) {
    throw new NotImplementedException();
  }

  /**
   * A column got inserted.
   * <br>
   * Does nothing.
   *
   * @param e		the insertion event
   */
  @Override
  public void spreadSheetColumnInserted(SpreadSheetColumnInsertionEvent e) {
    // nothing to do
  }

  /**
   * Returns the underlying data row.
   *
   * @return		the row
   */
  public DataRow getDataRow() {
    return m_Row;
  }

  /**
   * Returns the internal array of cells as string.
   *
   * @return		the values of the row
   */
  @Override
  public String toString() {
    StringBuilder	result;
    int			i;

    if (getOwner() == null)
      return getClass().getSimpleName();

    result = new StringBuilder();
    for (i = 0; i < getOwner().getColumnCount(); i++) {
      if (i > 0)
	result.append(",");
      result.append(getCell(i).toString());
    }

    return result.toString();
  }
}
