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
 * SparseDataRow.java
 * Copyright (C) 2009-2012 University of Waikato, Hamilton, New Zealand
 */

package adams.data.spreadsheet;

import java.util.Collection;
import java.util.Collections;

import adams.event.SpreadSheetColumnInsertionEvent;

/**
 * Represents a data row with sparse data.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SparseDataRow
  extends AbstractRow
  implements DataRow {

  /** for serialization. */
  private static final long serialVersionUID = 6870041975871526417L;

  /**
   * Default constructor for GOE only.
   */
  public SparseDataRow() {
    this(null);
  }

  /**
   * Constructor that ties row to spreadsheet.
   * 
   * @param owner	the spreadsheet this row belongs to
   */
  public SparseDataRow(SpreadSheet owner) {
    super(owner);
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
    SparseDataRow	result;
    Cell		cell;

    result = new SparseDataRow(owner);
    for (String key: m_Cells.keySet()) {
      cell = newCell(result);
      cell.assign(m_Cells.get(key));
      cell.setOwner(result);
      result.m_Cells.put(key, cell);
    }

    return result;
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
   * Adds a cell with the given key to the list and returns the created object.
   * If the cell already exists, then this cell is returned instead and no new
   * object created.
   *
   * @param cellKey	the key for the cell to create
   * @return		the created cell or the already existing cell
   */
  @Override
  public Cell addCell(String cellKey) {
    Cell	result;

    if (hasCell(cellKey)) {
      result = getCell(cellKey);
    }
    else {
      result = newCell(this);
      m_Cells.put(cellKey, result);
    }

    return result;
  }
  
  /**
   * Returns whether the row alread contains the cell at the specified location.
   *
   * @param columnIndex	the column index
   * @return		true if the cell already exists
   */
  @Override
  public boolean hasCell(int columnIndex) {
    if (getOwner() == null)
      return false;
    else
      return hasCell(getOwner().getHeaderRow().getCellKey(columnIndex));
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
    if (getOwner() == null)
      return null;
    else
      return addCell(getOwner().getHeaderRow().getCellKey(columnIndex));
  }

  /**
   * Returns the cell with the given index, null if not found.
   *
   * @param columnIndex	the index of the column
   * @return			the cell or null if not found
   */
  @Override
  public Cell getCell(int columnIndex) {
    Cell	result;
    String	key;

    result = null;
    key    = getCellKey(columnIndex);
    if (key != null)
      result = getCell(key);

    return result;
  }

  /**
   * Returns the cell content with the given index.
   *
   * @param columnIndex	the index of the column
   * @return			the content or null if not found
   */
  @Override
  public String getContent(int columnIndex) {
    String	result;
    String	key;

    result = null;
    key    = getCellKey(columnIndex);
    if (key != null)
      result = getCell(key).getContent();

    return result;
  }

  /**
   * Returns the cell key with the given column index.
   *
   * @param columnIndex	the index of the column
   * @return		the cell key, null if invalid index
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
   * @return		all cell keys (not sorted)
   */
  @Override
  public Collection<String> cellKeys() {
    return Collections.unmodifiableCollection(m_Cells.keySet());
  }

  /**
   * Returns all cells.
   *
   * @return		the cells (unsorted)
   */
  @Override
  public Collection<Cell> cells() {
    return m_Cells.values();
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
    
    result = -1;

    if (getOwner() == null)
      return result;
    
    if (cell.getOwner() == this) {
      if (m_Cells.containsValue(cell)) {
	for (String key: m_Cells.keySet()) {
	  if (m_Cells.get(key) == cell) {
	    result = getOwner().getHeaderRow().indexOf(key);
	    break;
	  }
	}
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
    // no need to change data structure
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
    int		i;
    int		start;

    if (getOwner() == null)
      return;
    
    start = getOwner().getColumnCount();
    for (i = 0; i < other.getOwner().getColumnCount(); i++) {
      if (!other.hasCell(i))
	continue;
      addCell(start + i).assign(other.getCell(i));
    }
  }
}