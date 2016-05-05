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
 * DatasetInfo.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.ml.data;

import adams.core.logging.LoggingObject;
import adams.data.spreadsheet.Cell.ContentType;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Contains information about the dataset structure but no actual data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DatasetInfo
  extends LoggingObject {

  private static final long serialVersionUID = -2693931121026828848L;

  /** the header. */
  protected Dataset m_Header;

  /** the datatypes per column. */
  protected Map<String,Collection<ContentType>> m_ColumnTypes;

  /** the class columns. */
  protected Set<String> m_ClassColumns;

  /**
   * Initializes the dataset structure.
   *
   * @param data	the data to get the info from
   */
  public DatasetInfo(Dataset data) {
    int		i;

    m_Header       = data.getHeader();
    m_ColumnTypes  = new HashMap<>();
    m_ClassColumns = new HashSet<>();
    for (i = 0; i < data.getColumnCount(); i++) {
      if (data.isClassAttribute(i))
        m_ClassColumns.add(data.getColumnName(i));
      m_ColumnTypes.put(data.getColumnName(i), data.getContentTypes(i));
    }
  }

  /**
   * Returns the header.
   *
   * @return		the header
   */
  public Dataset getHeader() {
    return m_Header;
  }

  /**
   * Returns the number of columns in the dataset.
   *
   * @return		the number of columns
   */
  public int getColumnCount() {
    return m_Header.getColumnCount();
  }

  /**
   * Returns the column types for the specified column.
   *
   * @param col		the column to get the types for
   * @return		the types
   */
  public Collection<ContentType> getColumnTypes(int col) {
    return m_ColumnTypes.get(m_Header.getColumnName(col));
  }

  /**
   * Returns the column types for the specified column.
   *
   * @param colName	the column to get the types for
   * @return		the types
   */
  public Collection<ContentType> getColumnTypes(String colName) {
    return m_ColumnTypes.get(colName);
  }

  /**
   * Returns the class columns.
   *
   * @return		the class columns
   */
  public Set<String> getClassColumns() {
    return m_ClassColumns;
  }
}
