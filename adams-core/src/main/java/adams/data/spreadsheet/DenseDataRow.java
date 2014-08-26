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
 * DenseDataRow.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet;

import java.util.Arrays;
import java.util.Collection;

import adams.event.SpreadSheetColumnInsertionEvent;

/**
 * A row for dense data representation.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DenseDataRow
  implements DataRow {

  /** for serialization. */
  private static final long serialVersionUID = -174960053020463008L;

  /** the owner. */
  protected SpreadSheet m_Owner;

  /** the cells of the row. */
  protected Cell[] m_Cells;

  /**
   * Default constructor for GOE only.
   */
  public DenseDataRow() {
    this(null);
  }

  /**
   * Constructor that ties row to spreadsheet.
   * 
   * @param owner	the spreadsheet this row belongs to
   */
  public DenseDataRow(SpreadSheet owner) {
    super();

    m_Owner = owner;
    
    clear();
  }
  
  /**
   * Sets the spreadsheet this row belongs to.
   * <p/>
   * Clears the cells if the number of columns differ.
   * 
   * @param owner	the owner
   */
  @Override
  public void setOwner(SpreadSheet owner) {
    m_Owner = owner;
    if (owner.getColumnCount() != m_Cells.length)
      clear();
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
   * Creates a copy of itself.
   * 
   * @param owner	the new owner
   * @return		the cloned object
   */
  @Override
  public DataRow getClone(SpreadSheet owner) {
    DenseDataRow	result;
    int			i;
    
    result = new DenseDataRow(owner);
    for (i = 0; i < m_Cells.length; i++) {
      result.m_Cells[i] = newCell(result);
      result.m_Cells[i].assign(m_Cells[i]);
    }
    
    return result;
  }

  /**
   * Removes all cells.
   */
  @Override
  public void clear() {
    int		i;
    
    if (getOwner() == null) {
      m_Cells = new Cell[0];
      return;
    }
    
    m_Cells = new Cell[getOwner().getColumnCount()];
    
    for (i = 0; i < m_Cells.length; i++) {
      m_Cells[i] = newCell(this);
      m_Cells[i].setMissing();
    }
  }

  /**
   * Obtains copies of the cells from the other row, but not the owner.
   *
   * @param row		the row to get the cells from
   */
  public void assign(Row row) {
    int		i;
    
    clear();

    if (getOwner() == null)
      return;

    for (i = 0; i < getOwner().getColumnCount(); i++) {
      if (row.hasCell(i))
	getCell(i).assign(row.getCell(i));
    }
  }

  /**
   * Creates a new instance of a cell.
   * 
   * @param owner	the owner
   * @return		the cell
   */
  public Cell newCell(Row owner) {
    return new DoubleCell(owner);
  }

  /**
   * Returns whether the row alread contains the cell at the specified location.
   *
   * @param columnIndex	the column index
   * @return		true if the cell already exists
   */
  @Override
  public boolean hasCell(int columnIndex) {
    return (columnIndex >= 0) && (columnIndex < m_Cells.length);
  }

  /**
   * Returns whether the row alread contains the cell with the given key.
   *
   * @param cellKey	the key to look for
   * @return		true if the cell already exists
   */
  @Override
  public boolean hasCell(String cellKey) {
    if (getOwner() == null)
      return false;
    else
      return getOwner().getHeaderRow().hasCell(cellKey);
  }

  /**
   * Adds a cell with the key of the cell in the header at the specified 
   * location. If the cell already exists, it returns that instead of
   * creating one.
   *
   * @param columnIndex	the index of the column to create
   * @return		the created cell or the already existing cell, null if outside header bounds
   */
  @Override
  public Cell addCell(int columnIndex) {
    if ((columnIndex >= 0) && (columnIndex < m_Cells.length))
      return m_Cells[columnIndex];
    else
      return null;
  }

  /**
   * Adds a cell with the given key to the list and returns the created object.
   * If the cell already exists, then this cell is returned instead and no new
   * object created.
   *
   * @param cellKey	the key for the cell to create
   * @return		the created cell or the already existing cell, null if outside header bounds
   */
  @Override
  public Cell addCell(String cellKey) {
    if (getOwner() == null)
      return null;
    else
      return addCell(getOwner().getHeaderRow().indexOf(cellKey));
  }

  /**
   * Removes the cell at the specified index.
   * 
   * @param columnIndex	the index of the column
   * @return		the removed cell, null if not removed
   */
  @Override
  public Cell removeCell(int columnIndex) {
    Cell	result;
    Cell[]	cells;
    int		i;
    int		n;
    
    result = null;
    
    if ((columnIndex >= 0) && (columnIndex < m_Cells.length)) {
      synchronized(m_Cells) {
	cells = new Cell[m_Cells.length - 1];
	n     = 0;
	for (i = 0; i < m_Cells.length; i++) {
	  if (i != columnIndex) {
	    cells[n] = m_Cells[i];
	    n++;
	  }
	  else {
	    result = m_Cells[i];
	  }
	}
	m_Cells = cells;
      }
    }
    
    return result;
  }

  /**
   * Removes the cell at the specified index.
   * 
   * @param cellKey	the key of the cell to remove
   * @return			the removed cell, null if non removed
   */
  @Override
  public Cell removeCell(String cellKey) {
    if (getOwner() == null)
      return null;
    else
      return removeCell(getOwner().getHeaderRow().indexOf(cellKey));
  }

  /**
   * Returns the cell with the given key, null if not found.
   *
   * @param cellKey	the cell to look for
   * @return		the cell or null if not found
   */
  @Override
  public Cell getCell(String cellKey) {
    if (getOwner() == null)
      return null;
    else
      return getCell(getOwner().getHeaderRow().indexOf(cellKey));
  }

  /**
   * Returns the cell with the given index, null if not found.
   *
   * @param columnIndex	the index of the column
   * @return			the cell or null if not found
   */
  @Override
  public Cell getCell(int columnIndex) {
    if ((columnIndex >= 0) && (columnIndex < m_Cells.length))
      return m_Cells[columnIndex];
    else
      return null;
  }

  /**
   * Returns the cell content with the given index.
   *
   * @param columnIndex	the index of the column
   * @return			the content or null if not found
   */
  @Override
  public String getContent(int columnIndex) {
    if ((columnIndex >= 0) && (columnIndex < m_Cells.length))
      return m_Cells[columnIndex].getContent();
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
    if (getOwner() == null)
      return null;
    else
      return getOwner().getHeaderRow().getCellKey(columnIndex);
  }

  /**
   * Returns a collection of all stored cell keys.
   *
   * @return		all cell keys (sorted according to columns)
   */
  @Override
  public Collection<String> cellKeys() {
    if (getOwner() == null)
      return null;
    else
      return getOwner().getHeaderRow().cellKeys();
  }

  /**
   * Returns all cells.
   *
   * @return		the cells
   */
  @Override
  public Collection<Cell> cells() {
    return Arrays.asList(m_Cells);
  }

  /**
   * Returns the number of cells stored in the row.
   *
   * @return		the number of cells - constant0
   */
  @Override
  public int getCellCount() {
    return m_Cells.length;
  }

  /**
   * Does nothing as the underlying data structure is fixed.
   * 
   * @return		always false
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
    int		result;
    int		i;
    
    result = -1;
    
    for (i = 0; i < m_Cells.length; i++) {
      if (m_Cells[i] == cell) {
	result = i;
	break;
      }
    }
    
    return result;
  }

  /**
   * A column got inserted.
   * 
   * @param e		the insertion event
   */
  @Override
  public void spreadSheetColumnInserted(SpreadSheetColumnInsertionEvent e) {
    Cell[]	cells;
    int		i;
    int		n;
    
    synchronized(m_Cells) {
      cells = new Cell[m_Cells.length + 1];
      n     = 0;
      cells[e.getColumnIndex()] = newCell(this);
      for (i = 0; i < m_Cells.length; i++) {
	if (i == e.getColumnIndex())
	  n++;
	cells[n] = m_Cells[i];
	n++;
      }

      m_Cells = cells;
    }
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
    
    result = new StringBuilder("[");
    for (i = 0; i < m_Cells.length; i++) {
      if (i > 0)
	result.append(", ");
      result.append(m_Cells[i].toString());
    }
    result.append("]");
    
    return result.toString();
  }

  /**
   * Merges its own data with the one provided by the specified row.
   * <p/>
   * Assumes that this sheet's header has already been updated.
   * 
   * @param other		the row to merge with
   */
  @Override
  public void mergeWith(Row other) {
    Cell[]	cellsNew;
    int		i;
    int		start;

    if (getOwner() == null)
      return;

    // do we need to extend array?
    if (m_Cells.length != getOwner().getColumnCount()) {
      cellsNew = new Cell[getOwner().getColumnCount()];
      System.arraycopy(m_Cells, 0, cellsNew, 0, m_Cells.length);
      m_Cells = cellsNew;
    }
    
    start = m_Cells.length - other.getCellCount();
    for (i = 0; i < other.getOwner().getColumnCount(); i++) {
      m_Cells[start + i] = newCell(this);
      if (!other.hasCell(i) || other.getCell(i).isMissing())
	m_Cells[start + i].setMissing();
      else
	m_Cells[start + i].assign(other.getCell(i));
    }
  }
}
