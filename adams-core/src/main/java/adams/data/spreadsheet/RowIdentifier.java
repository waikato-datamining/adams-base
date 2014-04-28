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
 * RowIdentifier.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import adams.core.AbstractDataBackedRange;
import adams.core.Range;

/**
 * Uses a range of columns to identify rows.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RowIdentifier
  implements Serializable {

  /** for serialization. */
  private static final long serialVersionUID = 2071633034869909763L;

  /** the separator between cells in the key. */
  public final static String SEPARATOR = "\t";
  
  /** the placeholder for cells if missing. */
  public final static String MISSING = "missing";
  
  /** the range of columns used as identifier. */
  protected Range m_Columns;
  
  /** the relationship key/identified rows. */
  protected HashMap<String,ArrayList<Integer>> m_Rows;
  
  /** the generated keys. */
  protected ArrayList<String> m_Keys;
  
  /**
   * Initializes the row identifier with the specified range of columns.
   * 
   * @param keys	the columns to use as key columns
   */
  public RowIdentifier(Range keys) {
    super();
    
    m_Columns = new Range();
    if (keys instanceof AbstractDataBackedRange)
      m_Columns.setIndices(keys.getIntIndices());
    else
      m_Columns.setRange(keys.getRange());
    m_Rows    = new HashMap<String,ArrayList<Integer>>();
    m_Keys    = new ArrayList<String>();
  }

  /**
   * Generates a key for the specified row.
   * 
   * @param sheet	the spreadsheet to work on
   * @param index	the index of the row to generate the key for
   * @param indices	the column indices to use
   * @return		the generated key
   */
  protected String generateKey(SpreadSheet sheet, int index, int[] indices) {
    StringBuilder	result;
    Row			header;
    int			i;
    Row			row;
    String		cellKey;

    header = sheet.getHeaderRow();
    row    = sheet.getRow(index);
    result = new StringBuilder();
    for (i = 0; i < indices.length; i++) {
      cellKey = header.getCellKey(indices[i]);
      if (i > 0)
	result.append(SEPARATOR);
      if (row.hasCell(cellKey) && !row.getCell(cellKey).isMissing())
	result.append(row.getCell(cellKey).getContent());
      else
	result.append(MISSING);
    }
    
    return result.toString();
  }
  
  /**
   * Configures the identifier with the specified sheet.
   * 
   * @param sheet	the sheet to use as basis
   */
  public void identify(SpreadSheet sheet) {
    int[]	indices;
    int		i;
    String	key;
    
    m_Columns.setMax(sheet.getColumnCount());
    indices = m_Columns.getIntIndices();
    
    m_Rows.clear();
    
    for (i = 0; i < sheet.getRowCount(); i++) {
      key = generateKey(sheet, i, indices);
      if (!m_Rows.containsKey(key)) {
	m_Rows.put(key, new ArrayList<Integer>());
	m_Keys.add(key);
      }
      m_Rows.get(key).add(i);
    }
  }
  
  /**
   * Returns the columns used as keys.
   * 
   * @return		the range
   */
  public Range getColumns() {
    return m_Columns;
  }
  
  /**
   * Returns the number of generated keys.
   * 
   * @return		the number of keys
   */
  public int size() {
    return m_Keys.size();
  }
  
  /**
   * Returns the generated keys.
   * 
   * @return		the keys
   */
  public List<String> getKeys() {
    return m_Keys;
  }
  
  /**
   * Returns the key at the specified location.
   * 
   * @param keyIndex	the position of the key
   * @return		the key
   */
  public String getKey(int keyIndex) {
    return m_Keys.get(keyIndex);
  }
  
  /**
   * Returns the list of row indices.
   * 
   * @return		the associated list of rows, null if not available
   */
  public List<Integer> getRows(int keyIndex) {
    return getRows(m_Keys.get(keyIndex));
  }
  
  /**
   * Returns the list of row indices associated with the specified key.
   * 
   * @return		the associated list of rows, null if not available
   */
  public List<Integer> getRows(String key) {
    return m_Rows.get(key);
  }
  
  /**
   * Returns a short description of the object.
   * 
   * @return		the description
   */
  @Override
  public String toString() {
    return "cols=" + m_Columns + ", size=" + size();
  }
}
