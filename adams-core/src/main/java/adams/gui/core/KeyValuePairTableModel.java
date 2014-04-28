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
 * KeyValuePairTableModel.java
 * Copyright (C) 2009-2010 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

/**
 * The model for displaying key-value pairs. It is assumed that the key is
 * always a string.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class KeyValuePairTableModel
  extends AbstractBaseTableModel
  implements CustomSearchTableModel {

  /** for serialization. */
  private static final long serialVersionUID = -8212085458244592181L;

  /** the names of the columns. */
  protected String[] m_ColumnNames;
  
  /** the data to display. */
  protected Object[][] m_Data;

  /** whether the values can be edited. */
  protected boolean m_Editable;

  /** whether the values were modified. */
  protected boolean m_Modified;

  /**
   * Initializes the table model. The key is always assumed to be of type
   * string.
   *
   * @param data	the key-value pairs to display
   */
  public KeyValuePairTableModel(Object[][] data) {
    this(data, new String[]{"Name", "Value"});
  }

  /**
   * Initializes the table model with custom column names. The key is always 
   * assumed to be of type string.
   *
   * @param data	the key-value pairs to display
   * @param colNames	the names of the columns (only 2 entries!)
   */
  public KeyValuePairTableModel(Object[][] data, String[] colNames) {
    super();

    m_Data     = data.clone();
    m_Editable = false;
    m_Modified = false;
    if (colNames.length != 2)
      throw new IllegalArgumentException("Exact two column names must be provided, but instead: " + colNames.length);
    m_ColumnNames = colNames.clone();
  }

  /**
   * Sets whether the values will be editable or not.
   *
   * @param value	if true the values will be editable
   */
  public void setEditable(boolean value) {
    m_Editable = value;
  }

  /**
   * Returns whether the values are editable or not.
   *
   * @return		true if the value are editable
   */
  public boolean isEditable() {
    return m_Editable;
  }

  /**
   * Sets whether the values will were modified.
   *
   * @param value	if true the values were modified
   */
  public void setModified(boolean value) {
    m_Modified = value;
  }

  /**
   * Returns whether the values were modified.
   *
   * @return		true if the values were modified
   */
  public boolean isModified() {
    return m_Modified;
  }

  /**
   * Returns the number of rows.
   *
   * @return		the number of rows
   */
  public int getRowCount() {
    if (m_Data == null)
      return 0;
    else
      return m_Data.length;
  }

  /**
   * Returns the number of columns in the table.
   *
   * @return		always 2
   */
  public int getColumnCount() {
    return 2;
  }

  /**
   * Returns the name of the column.
   *
   * @param column	the column to retrieve the name for
   * @return		the name of the column
   */
  public String getColumnName(int column) {
    if ((column == 0) || (column == 1))
      return m_ColumnNames[column];
    else
      throw new IllegalArgumentException("Invalid column: " + column);
  }

  /**
   * Returns the value at the given position. Multi-line content gets shortened.
   *
   * @param row		the row in the table
   * @param column	the column in the table
   * @return		the value
   * @see		#getValueAt(int, int)
   */
  public Object getValueAt(int row, int column) {
    Object	result;
    int		pos;

    result = m_Data[row][column];
    if ((column == 1) && (result instanceof String)) {
      pos    = ((String) result).indexOf('\n');
      if (pos > -1)
	result = ((String) result).substring(0, pos) + "...";
    }

    return result;
  }

  /**
   * Returns the key at the given position.
   *
   * @param row		the row in the table
   * @return		the key
   */
  public String getKeyAt(int row) {
    return (String) m_Data[row][0];
  }

  /**
   * Returns the value at the given position.
   *
   * @param row		the row in the table
   * @return		the value
   */
  public Object getValueAt(int row) {
    return m_Data[row][1];
  }

  /**
   * Returns whether the values are editable.
   *
   * @param rowIndex  		the row being queried
   * @param columnIndex 	the column being queried
   * @return 			true if editable
   */
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return m_Editable && (columnIndex == 1);
  }

  /**
   * Updates the value.
   *
   * @param aValue   	value to assign to cell
   * @param rowIndex   	row of cell
   * @param columnIndex	column of cell
   */
  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    if (columnIndex == 1) {
      if (aValue == null)
	m_Data[rowIndex][1] = "";
      else
	m_Data[rowIndex][1] = aValue;

      m_Modified = true;

      fireTableCellUpdated(rowIndex, columnIndex);
    }
  }

  /**
   * Returns the class for the column.
   *
   * @param column	the column to retrieve the class for
   * @return		the class
   */
  public Class getColumnClass(int column) {
    if (column == 0)
      return String.class;
    else
      return Object.class;
  }

  /**
   * Tests whether the search matches the specified row.
   *
   * @param params	the search parameters
   * @param row		the row of the underlying, unsorted model
   * @return		true if the search matches this row
   */
  public boolean isSearchMatch(SearchParameters params, int row) {
    if (params.matches((String) m_Data[row][0]))
      return true;

    if (m_Data[row][1] instanceof String) {
      if (params.matches((String) m_Data[row][1]))
	return true;
    }
    else if (m_Data[row][1] instanceof Integer) {
      if (params.matches((Integer) m_Data[row][1]))
	return true;
    }
    else if (m_Data[row][1] instanceof Double) {
      if (params.matches((Double) m_Data[row][1]))
	return true;
    }

    return false;
  }
}