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
 * HeaderRow.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.spreadsheet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Represents a header row.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HeaderRow
  extends AbstractRow {

  /** for serialization. */
  private static final long serialVersionUID = 6495559418329338208L;
  
  /** the cell keys of the row. */
  protected ArrayList<String> m_CellKeys;

  /**
   * default constructor.
   * 
   * @param owner	the spreadsheet this row belongs to
   */
  protected HeaderRow(SpreadSheet owner) {
    super(owner);

    m_CellKeys = new ArrayList<String>();
  }

  /**
   * Obtains copies of the cells from the other row, but not the owner.
   *
   * @param row		the row to get the cells from
   */
  @Override
  public void assign(Row row) {
    int		i;
    String	key;
    
    clear();

    for (i = 0; i < row.getOwner().getColumnCount(); i++) {
      key = row.getCellKey(i);
      addCell(key).assign(row.getCell(key));
    }
  }
  
  /**
   * Returns a clone of itself.
   *
   * @param owner	the new owner
   * @return		the clone
   */
  public HeaderRow getClone(SpreadSheet owner) {
    HeaderRow	result;
    int		i;
    Cell	cell;

    result = new HeaderRow(owner);
    result.m_CellKeys.addAll(m_CellKeys);
    for (i = 0; i < m_CellKeys.size(); i++) {
      cell = new Cell(result);
      cell.assign(m_Cells.get(m_CellKeys.get(i)));
      result.m_Cells.put(result.m_CellKeys.get(i), cell);
    }

    return result;
  }

  /**
   * Removes all cells.
   */
  @Override
  public void clear() {
    super.clear();
    m_CellKeys.clear();
  }
  
  /**
   * Returns whether the row alread contains the cell at the specified location.
   *
   * @param columnIndex	the column index
   * @return		true if the cell already exists
   */
  public boolean hasCell(int columnIndex) {
    return hasCell(getCellKey(columnIndex));
  }

  /**
   * Returns the index of the specified key.
   * 
   * @param cellKey	the key to look for
   * @return		the index of the cell, -1 if not found
   */
  public int indexOf(String cellKey) {
    return m_CellKeys.indexOf(cellKey);
  }

  /**
   * Returns the index of the cell that has the specified content.
   * 
   * @param content	the content to look for
   * @return		the index of the cell, -1 if not found
   */
  public int indexOfContent(String content) {
    int		result;
    int		i;
    
    result = -1;
    
    for (i = 0; i < m_CellKeys.size(); i++) {
      if (hasCell(i) && !getCell(i).isMissing() && getCell(i).getContent().equals(content)) {
	result = i;
	break;
      }
    }
    
    return result;
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
    return addCell(getCellKey(columnIndex));
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
      result = new Cell(this);
      m_CellKeys.add(cellKey);
      m_Cells.put(cellKey, result);
    }

    return result;
  }
  
  /**
   * Inserts a cell at the specified location.
   * 
   * @param columnIndex	the location of the new cell
   * @return		the created cell
   */
  public Cell insertCell(int columnIndex) {
    Cell	result;
    int		i;
    String	col;
    
    result = new Cell(this);
    
    synchronized(m_Cells) {
      synchronized(m_CellKeys) {
	i = 0;
	do {
	  i++;
	  col = "inserted-" + i;
	}
	while (hasCell(col));

	m_CellKeys.add(columnIndex, col);
	m_Cells.put(col, result);
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
    Cell	result;

    result = super.removeCell(cellKey);
    if (result == null)
      return null;

    m_CellKeys.remove(cellKey);
    
    return result;
  }

  /**
   * Returns the cell key with the given column index.
   *
   * @param columnIndex	the index of the column
   * @return			the cell key, null if invalid index
   */
  @Override
  public String getCellKey(int columnIndex) {
    if (columnIndex < m_CellKeys.size())
      return m_CellKeys.get(columnIndex);
    else
      return null;
  }

  /**
   * Returns a collection of all stored cell keys.
   *
   * @return		all cell keys (sorted according to columns)
   */
  @Override
  public Collection<String> cellKeys() {
    return Collections.unmodifiableCollection(m_CellKeys);
  }

  /**
   * Returns all cells.
   *
   * @return		the cells (sorted)
   */
  public Collection<Cell> cells() {
    ArrayList<Cell>	result;
    
    result = new ArrayList<Cell>();
    for (String key: m_CellKeys)
      result.add(m_Cells.get(key));
    
    return result;
  }

  /**
   * Returns the column this particular cell is in (must belong to this row!).
   * 
   * @param cell	the cell to get the column index of
   * @return		the column index, -1 if not found
   */
  public int indexOf(Cell cell) {
    int		result;
    int		i;
    String	key;
    
    result = -1;
    
    if (cell.getOwner() == this) {
      if (m_Cells.containsValue(cell)) {
	for (i = 0; i < m_CellKeys.size(); i++) {
	  key = m_CellKeys.get(i);
	  if (m_Cells.get(key) == cell) {
	    result = i;
	    break;
	  }
	}
      }
    }
    
    return result;
  }

  /**
   * Ensures that the key is not yet present. In case the key is already used
   * it is made unique.
   * 
   * @param key		the key to check
   * @return		the potentially updated key
   */
  protected synchronized String uniqueKey(String key) {
    String	result;
    int		count;
    
    if (!m_CellKeys.contains(key))
      return key;
    count = m_CellKeys.size();
    do {
      count++;
      result = key + "-" + count;
    }
    while (m_CellKeys.contains(result));
    
    return result;
  }
  
  /**
   * Merges its own data with the one provided by the specified row.
   * 
   * @param other		the row to merge with
   */
  @Override
  public void mergeWith(Row other) {
    int		i;
    String	keyOther;
    String	keyThis;
    
    for (i = 0; i < other.getCellCount(); i++) {
      keyOther = other.getCellKey(i);
      keyThis  = uniqueKey(keyOther);
      addCell(keyThis).assign(other.getCell(keyOther));
    }
  }
}