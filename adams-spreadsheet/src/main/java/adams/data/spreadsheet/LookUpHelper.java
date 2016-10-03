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
 * LookUpHelper.java
 * Copyright (C) 2014-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet;

import adams.core.base.BaseRegExp;
import adams.core.io.PlaceholderFile;
import adams.data.io.input.CsvSpreadSheetReader;

import java.util.HashMap;

/**
 * Helper class for LookUp related stuff.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LookUpHelper {

  /**
   * Returns an empty new lookup table.
   *
   * @return		the empty table
   */
  public static HashMap<String,Object> newTable() {
    return new HashMap<>();
  }

  /**
   * Generates a lookup table from the given file. Uses empty string as 
   * missing value.
   * 
   * @param file	the spreadsheet (CSV) to load
   * @param key		the key column (or 1-based index)
   * @param value	the value column (or 1-based index)
   * @param useNative	whether to use native objects or just string representation
   * @param error	for storing error messages
   * @return		the lookup table, null in case of an error
   */
  public static HashMap<String,Object> load(PlaceholderFile file, String key, String value, boolean useNative, StringBuilder error) {
    CsvSpreadSheetReader	reader;
    SpreadSheet			sheet;
    
    reader = new CsvSpreadSheetReader();
    reader.setMissingValue(new BaseRegExp(""));
    sheet  = reader.read(file);
    
    return load(sheet, key, value, useNative, error);
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
    HashMap<String,Object>	result;
    int				keyCol;
    int				valCol;
    String			k;
    Object			v;
    SpreadSheetColumnIndex	m_KeyColumn;
    SpreadSheetColumnIndex	m_ValueColumn;
    
    keyCol = -1;
    valCol = -1;
    
    if (sheet.getColumnCount() < 2) {
      error.append("Spreadsheet must have at least 2 columns, available: " + sheet.getColumnCount());
      return null;
    }

    // key
    m_KeyColumn = new SpreadSheetColumnIndex(key);
    m_KeyColumn.setSpreadSheet(sheet);
    keyCol = m_KeyColumn.getIntIndex();
    if (keyCol == -1) {
      error.append("Failed to locate key column: " + m_KeyColumn.getIndex());
      return null;
    }

    // value
    m_ValueColumn = new SpreadSheetColumnIndex(value);
    m_ValueColumn.setSpreadSheet(sheet);
    valCol = m_ValueColumn.getIntIndex();
    if (valCol == -1) {
      error.append("Failed to locate value column: " + m_ValueColumn.getIndex());
      return null;
    }

    // create lookup table
    result = newTable();
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
