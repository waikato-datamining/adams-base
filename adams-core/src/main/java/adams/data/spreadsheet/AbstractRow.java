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
 * AbstractRow.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.spreadsheet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Ancestor for row objects.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractRow
  implements Row {

  /** for serialization. */
  private static final long serialVersionUID = -7629759768897079377L;

  /** the owner. */
  protected SpreadSheet m_Owner;

  /** the cells of the row. */
  protected HashMap<String, Cell> m_Cells;

  /**
   * default constructor.
   * 
   * @param owner	the spreadsheet this row belongs to
   */
  protected AbstractRow(SpreadSheet owner) {
    super();

    m_Owner = owner;
    m_Cells = new HashMap<String, Cell>();
  }

  /**
   * Sets the spreadsheet this row belongs to.
   * 
   * @param owner	the owner
   */
  public void setOwner(SpreadSheet owner) {
    m_Owner = owner;
  }

  /**
   * Returns the spreadsheet this row belongs to.
   * 
   * @return		the owner
   */
  public SpreadSheet getOwner() {
    return m_Owner;
  }

  /**
   * Removes all cells.
   */
  public void clear() {
    m_Cells.clear();
  }

  /**
   * Obtains copies of the cells from the other row, but not the owner.
   *
   * @param row		the row to get the cells from
   */
  public void assign(Row row) {
    int		i;
    
    clear();

    for (i = 0; i < getOwner().getColumnCount(); i++) {
      if (row.hasCell(i))
	getCell(i).assign(row.getCell(i));
    }
  }

  /**
   * Returns whether the row alread contains the cell with the given key.
   *
   * @param cellKey	the key to look for
   * @return		true if the cell already exists
   */
  public boolean hasCell(String cellKey) {
    return m_Cells.containsKey(cellKey);
  }
  
  /**
   * Adds a cell with the key of the cell in the header at the specified 
   * location. If the cell already exists, it returns that instead of
   * creating one.
   *
   * @param columnIndex	the index of the column to create
   * @return		the created cell or the already existing cell
   */
  public abstract Cell addCell(int columnIndex);

  /**
   * Adds a cell with the given key to the list and returns the created object.
   * If the cell already exists, then this cell is returned instead and no new
   * object created.
   *
   * @param cellKey	the key for the cell to create
   * @return		the created cell or the already existing cell
   */
  public abstract Cell addCell(String cellKey);
  
  /**
   * Removes the cell at the specified index.
   * 
   * @param columnIndex	the index of the column
   * @return			the removed cell, null if non removed
   */
  public Cell removeCell(int columnIndex) {
    return removeCell(getCellKey(columnIndex));
  }
  
  /**
   * Removes the cell at the specified index.
   * 
   * @param cellKey	the key of the cell to remove
   * @return			the removed cell, null if non removed
   */
  public Cell removeCell(String cellKey) {
    Cell	result;
    
    if (cellKey == null)
      return null;
    if (!hasCell(cellKey))
      return null;

    result = m_Cells.remove(cellKey);
    
    return result;
  }

  /**
   * Returns the cell with the given key, null if not found.
   *
   * @param cellKey	the cell to look for
   * @return		the cell or null if not found
   */
  public Cell getCell(String cellKey) {
    return m_Cells.get(cellKey);
  }

  /**
   * Returns the cell with the given index, null if not found.
   *
   * @param columnIndex	the index of the column
   * @return			the cell or null if not found
   */
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
   * @return			the cell key, null if invalid index
   */
  public abstract String getCellKey(int columnIndex);

  /**
   * Returns a collection of all stored cell keys.
   *
   * @return		all cell keys (not necessarily sorted)
   */
  public abstract Collection<String> cellKeys();

  /**
   * Returns the number of cells stored in the row.
   *
   * @return		the number of cells
   */
  public int getCellCount() {
    return m_Cells.size();
  }
  
  /**
   * Removes all cells marked "missing".
   * 
   * @return		whether any cell was removed
   */
  public boolean removeMissing() {
    boolean		result;
    ArrayList<String>	list;
    
    result = false;
    list   = new ArrayList<String>(m_Cells.keySet());
    for (String key: list) {
      if (getCell(key).isMissing()) {
	removeCell(key);
	result = true;
      }
    }
    
    return result;
  }

  /**
   * Simply returns the internal hashtable of cells as string.
   *
   * @return		the values of the row
   */
  @Override
  public String toString() {
    return m_Cells.toString();
  }
}