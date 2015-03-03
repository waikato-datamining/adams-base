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
 * ResultSetTableModel.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Table model for displaying an SQL ResultSet.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see ResultSet
 */
public class ResultSetTableModel
  extends AbstractBaseTableModel {

  /** for serialization. */
  private static final long serialVersionUID = 3381325114377596348L;

  /** the column titles. */
  protected String[] m_Columns;

  /** for fast access to the column names (name - index). */
  protected Hashtable<String,Integer> m_ColumnIndex;
  
  /* the data. */
  protected List<Object[]> m_Data;
  
  /**
   * Initializes an empty model.
   * 
   * @param columns	the column titles
   */
  public ResultSetTableModel(String[] columns) {
    m_Columns = columns.clone();
    m_Data    = new ArrayList<Object[]>();
    
    initialize();
  }
  
  /**
   * Initializes the model with the given result set.
   * 
   * @param rs			the data to use
   * @throws SQLException	in case of an SQL error
   */
  public ResultSetTableModel(ResultSet rs) throws SQLException {
    ResultSetMetaData	meta;
    int			i;
    Object[]		row;
    
    // column names
    meta      = rs.getMetaData();
    m_Columns = new String[meta.getColumnCount()];
    for (i = 0; i < m_Columns.length; i++)
      m_Columns[i] = meta.getColumnName(i + 1);
    
    // data
    m_Data = new ArrayList<Object[]>();
    while (rs.next()) {
      row = new Object[m_Columns.length];
      for (i = 0; i < row.length; i++)
	row[i] = rs.getObject(i + 1);
      m_Data.add(row);
    }
    
    initialize();
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    int		i;
    
    m_ColumnIndex = new Hashtable<String,Integer>();
    for (i = 0; i < m_Columns.length; i++)
      m_ColumnIndex.put(m_Columns[i].toLowerCase(), i);
  }
  
  /**
   * Returns the number of rows in the model.
   *
   * @return 		the number of rows in the model
   */
  public int getRowCount() {
    return m_Data.size();
  }

  /**
   * Returns the name of the column at <code>columnIndex</code>.  This is used
   * to initialize the table's column header name.
   *
   * @param	columnIndex	the index of the column
   * @return  the name of the column
   */
  @Override
  public String getColumnName(int columnIndex) {
    return m_Columns[columnIndex];
  }

  /**
   * Returns the number of columns in the model.
   *
   * @return 		the number of columns in the model
   */
  public int getColumnCount() {
    return m_Columns.length;
  }

  /**
   * Returns the value for the cell at <code>columnIndex</code> and
   * <code>rowIndex</code>.
   *
   * @param	rowIndex	the row whose value is to be queried
   * @param	columnIndex 	the column whose value is to be queried
   * @return	the value Object at the specified cell
   */
  public Object getValueAt(int rowIndex, int columnIndex) {
    return m_Data.get(rowIndex)[columnIndex];
  }

  /**
   * Returns the column index associated with the column name.
   * 
   * @param columnName	the name to look up
   * @return		the index, -1 if not found
   */
  public int getColumnIndex(String columnName) {
    Integer		result;
    
    result = m_ColumnIndex.get(columnName.toLowerCase());
    if (result == null)
      return -1;
    
    return result;
  }
  
  /**
   * Returns the value for the cell row <code>rowIndex</code> and from the
   * column named <code>columnName</code>.
   *
   * @param rowIndex	the row whose value is to be queried
   * @param columnName 	the name of the column whose value is to be queried
   * @return 		the value Object at the specified cell, null if not found or no value available
   */
  public Object getValueAt(int rowIndex, String columnName) {
    int		columnIndex;
    
    columnIndex = getColumnIndex(columnName);
    if (columnIndex == -1)
      return null;
    
    return getValueAt(rowIndex, columnIndex);
  }
}
