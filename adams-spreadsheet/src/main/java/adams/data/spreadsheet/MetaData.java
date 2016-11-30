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
 * MetaData.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.spreadsheet;

import adams.core.CloneHandler;

import java.io.Serializable;

/**
 * Simple meta-data storage.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MetaData
  implements Serializable, SpreadSheetSupporter, CloneHandler<MetaData> {

  private static final long serialVersionUID = -7702963792155360094L;

  /** for storing the meta-data. */
  protected SpreadSheet m_Data;

  /**
   * Initializes the meta-data.
   */
  public MetaData() {
    super();
    m_Data = new DefaultSpreadSheet();
    m_Data.getHeaderRow().addCell("K").setContentAsString("Key");
    m_Data.getHeaderRow().addCell("V").setContentAsString("Value");
  }

  /**
   * Initializes the meta-data with the provided meta-data.
   *
   * @param source	the meta-data to copy
   */
  public MetaData(MetaData source) {
    this();
    for (Row row: source.toSpreadSheet().rows())
      m_Data.addRow().assign(row);
  }

  /**
   * Checks the meta-data for an existing key.
   *
   * @param key		the key to check
   * @return		true if exists
   */
  public boolean has(String key) {
    boolean 	result;
    int		i;
    Row		row;

    result = false;

    for (i = 0; i < m_Data.getRowCount(); i++) {
      row = m_Data.getRow(i);
      if (row.hasCell(0) && row.getCell(0).getContent().equals(key)) {
	result = true;
	break;
      }
    }

    return result;
  }

  /**
   * Adds the meta-data.
   *
   * @param key		the key
   * @param value	the value
   */
  public void add(String key, Object value) {
    Row		row;

    row = m_Data.addRow();
    row.addCell("K").setContentAsString(key);
    row.addCell("V").setNative(value);
  }

  /**
   * Returns the meta-data as spreadsheet.
   *
   * @return		the meta-data
   */
  @Override
  public SpreadSheet toSpreadSheet() {
    return m_Data;
  }

  /**
   * Returns a clone of itself.
   *
   * @return		the clone
   */
  @Override
  public MetaData getClone() {
    return new MetaData(this);
  }

  /**
   * Returns the underlying spreadsheet as string.
   *
   * @return		the string representation
   */
  public String toString() {
    return m_Data.toString();
  }
}
