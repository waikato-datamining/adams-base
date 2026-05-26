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
 * LookUpHelper.java
 * Copyright (C) 2014-2026 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet;

import adams.flow.control.StorageName;
import adams.flow.core.Actor;

import java.util.HashMap;

/**
 * Helper class for LookUp related stuff.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class LookUpHelper {

  /**
   * Returns an empty new lookup table.
   *
   * @return		the empty lookup
   */
  public static HashMap<String,Object> newTable() {
    return newTable(-1);
  }

  /**
   * Returns an empty new lookup table.
   *
   * @param initialCapacity 	the initial capacity for the map
   * @return			the empty lookup
   */
  public static HashMap<String,Object> newTable(int initialCapacity) {
    if (initialCapacity > 0)
      return new HashMap<>(initialCapacity);
    else
      return new HashMap<>();
  }

  /**
   * Returns the specified lookup table.
   *
   * @return		the table
   */
  public static HashMap<String,Object> getTable(Actor context, StorageName name) {
    return (HashMap<String,Object>) context.getStorageHandler().getStorage().get(name);
  }

  /**
   * Generates a lookup table from the given spreadsheet.
   *
   * @param sheet	the spreadsheet to use
   * @param key		the key column (or 1-based index)
   * @param value	the value column (or 1-based index)
   * @param useNative	whether to use native objects or just string representation
   * @param error	for storing error messages
   * @return		the lookup table, null in case of an error
   */
  public static HashMap<String,Object> load(SpreadSheet sheet, String key, String value, boolean useNative, StringBuilder error) {
    return load(-1, sheet, key, value, useNative, error);
  }

  /**
   * Generates a lookup table from the given spreadsheet.
   *
   * @param initialCap	the initial capacity to use for the lookup table, <= 0 for default
   * @param sheet	the spreadsheet to use
   * @param key		the key column (or 1-based index)
   * @param value	the value column (or 1-based index)
   * @param useNative	whether to use native objects or just string representation
   * @param error	for storing error messages
   * @return		the lookup table, null in case of an error
   */
  public static HashMap<String,Object> load(int initialCap, SpreadSheet sheet, String key, String value, boolean useNative, StringBuilder error) {
    HashMap<String,Object>	result;
    int				keyCol;
    int				valCol;
    String			k;
    Object			v;
    SpreadSheetColumnIndex	m_KeyColumn;
    SpreadSheetColumnIndex	m_ValueColumn;
    
    if (sheet.getColumnCount() < 2) {
      error.append("Spreadsheet must have at least 2 columns, available: ").append(sheet.getColumnCount());
      return null;
    }

    // key
    m_KeyColumn = new SpreadSheetColumnIndex(key);
    m_KeyColumn.setSpreadSheet(sheet);
    keyCol = m_KeyColumn.getIntIndex();
    if (keyCol == -1) {
      error.append("Failed to locate key column: ").append(m_KeyColumn.getIndex());
      return null;
    }

    // value
    m_ValueColumn = new SpreadSheetColumnIndex(value);
    m_ValueColumn.setSpreadSheet(sheet);
    valCol = m_ValueColumn.getIntIndex();
    if (valCol == -1) {
      error.append("Failed to locate value column: ").append(m_ValueColumn.getIndex());
      return null;
    }

    // create lookup table
    if (initialCap > 0)
      result = newTable(initialCap);
    else
      result = newTable(sheet.getRowCount());
    for (Row row: sheet.rows()) {
      if (!row.hasCell(keyCol) || row.getCell(keyCol).isMissing())
	continue;
      if (!row.hasCell(valCol) || row.getCell(valCol).isMissing())
	continue;
      k = row.getCell(keyCol).getContent();
      if (useNative)
	v = row.getCell(valCol).getNative();
      else
	v = row.getCell(valCol).getContent();
      if ((k != null) && (v != null))
	result.put(k, v);
    }
    
    return result;
  }
}
