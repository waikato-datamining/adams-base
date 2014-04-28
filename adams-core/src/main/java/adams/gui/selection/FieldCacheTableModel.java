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
 * FieldCacheTableModel.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.selection;

import java.util.Hashtable;
import java.util.Vector;

import adams.data.report.AbstractField;
import adams.data.report.DataType;
import adams.data.report.FieldType;
import adams.gui.core.ComparableTableModel;
import adams.gui.core.SearchParameters;
import adams.gui.selection.AbstractTableBasedSelectionPanel.AbstractSelectionTableModel;

/**
 * Table model for displaying the fields from a cache item.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FieldCacheTableModel
  extends AbstractSelectionTableModel<AbstractField>
  implements ComparableTableModel {

  /** for serialization. */
  private static final long serialVersionUID = 7823434093663335692L;

  /** the database URL. */
  protected String m_DatabaseURL;

  /** the fields to display. */
  protected Hashtable<FieldType,Vector<AbstractField>> m_Fields;

  /** the field type to use. */
  protected FieldType m_FieldType;

  /** the data type in use (if any). */
  protected DataType m_DataType;

  /**
   * the constructor.
   *
   * @param fields	the fields to display
   * @param fieldType	the fieldtype
   * @param dataType	the data type to display, use null for all
   */
  public FieldCacheTableModel(AbstractFieldCacheItem cache, FieldType fieldType, DataType dataType) {
    super();

    m_Fields = new Hashtable<FieldType,Vector<AbstractField>>();
    if (cache != null) {
      m_DatabaseURL = cache.getDatabaseConnection().getURL();
      if (dataType == null) {
	for (FieldType type: FieldType.values())
	  m_Fields.put(type, (Vector<AbstractField>) cache.getValues(type).clone());
      }
      else {
	for (FieldType type: FieldType.values())
	  m_Fields.put(type, (Vector<AbstractField>) cache.getValues(type, dataType).clone());
      }
    }
    else {
      m_DatabaseURL = "-none-";
      for (FieldType type: FieldType.values())
        m_Fields.put(type, new Vector<AbstractField>());
    }

    m_FieldType = fieldType;
    m_DataType  = dataType;
  }

  /**
   * Returns the database URL this table model was created with.
   *
   * @return		the database URL
   */
  public String getDatabaseURL() {
    return m_DatabaseURL;
  }

  /**
   * Returns the actual number of entries in the table.
   *
   * @return		the number of entries
   */
  public int getRowCount() {
    return m_Fields.get(m_FieldType).size();
  }

  /**
   * Returns the number of columns in the table, i.e., 1.
   *
   * @return		the number of columns, always 1
   */
  public int getColumnCount() {
    return 1;
  }

  /**
   * Returns the name of the column.
   *
   * @param column 	the column to get the name for
   * @return		the name of the column
   */
  public String getColumnName(int column) {
    if (column == 0)
      return "Field";
    else
      throw new IllegalArgumentException("Column " + column + " is invalid!");
  }

  /**
   * Returns the class type of the column.
   *
   * @param columnIndex	the column to get the class for
   * @return			the class for the column
   */
  public Class getColumnClass(int columnIndex) {
    if (columnIndex == 0)
      return String.class;
    else
      throw new IllegalArgumentException("Column " + columnIndex + " is invalid!");
  }

  /**
   * Returns the class type of the column that is used for comparisons.
   *
   * @param columnIndex	the column to get the class for
   * @return			the class for the column
   */
  public Class getComparisonColumnClass(int columnIndex) {
    if (columnIndex == 0)
      return AbstractField.class;
    else
      throw new IllegalArgumentException("Column " + columnIndex + " is invalid!");
  }

  /**
   * Returns the field at the given position.
   *
   * @param row	the row
   * @param column	the column (ignored, since only 1 column)
   * @return		the field
   */
  public Object getValueAt(int row, int column) {
    if (column == 0)
      return m_Fields.get(m_FieldType).get(row).toDisplayString();
    else
      throw new IllegalArgumentException("Column " + column + " is invalid!");
  }

  /**
   * Returns the field at the given position.
   *
   * @param row	the row
   * @param column	the column (ignored, since only 1 column)
   * @return		the field
   */
  public Object getComparisonValueAt(int row, int column) {
    if (column == 0)
      return m_Fields.get(m_FieldType).get(row);
    else
      throw new IllegalArgumentException("Column " + column + " is invalid!");
  }

  /**
   * Returns the field at the specified position.
   *
   * @param row	the (actual, not visible) position of the field
   * @return		the Field at the position, null if not valid index
   */
  public AbstractField getItemAt(int row) {
    if ((row >= 0) && (row < m_Fields.get(m_FieldType).size()))
      return m_Fields.get(m_FieldType).get(row);
    else
      return null;
  }

  /**
   * Returns the index of the given (visible) field, -1 if not found.
   *
   * @param t		the field to look for
   * @return		the index, -1 if not found
   */
  public int indexOf(AbstractField t) {
    int				result;
    int				i;
    Vector<AbstractField>	fields;

    result = -1;

    fields = m_Fields.get(m_FieldType);
    for (i = 0; i < fields.size(); i++) {
      if (t.equals(fields.get(i))) {
        result = i;
        break;
      }
    }

    return result;
  }

  /**
   * Tests whether the search matches the specified row.
   *
   * @param params	the search parameters
   * @param row	the row of the underlying, unsorted model
   * @return		true if the search matches this row
   */
  public boolean isSearchMatch(SearchParameters params, int row) {
    return params.matches(m_Fields.get(m_FieldType).get(row).toString());
  }

  /**
   * Sets the field type.
   *
   * @param value	the new field type
   */
  public void setFieldType(FieldType value) {
    if (m_FieldType != value) {
      m_FieldType = value;
      fireTableDataChanged();
    }
  }

  /**
   * Returns the field type.
   *
   * @return		the current field type
   */
  public FieldType getFieldType() {
    return m_FieldType;
  }

  /**
   * Sets the data type.
   *
   * @param value	the new data type
   */
  public void setDataType(DataType value) {
    if (m_DataType != value) {
      m_DataType = value;
      fireTableDataChanged();
    }
  }

  /**
   * Returns the data type.
   *
   * @return		the current data type
   */
  public DataType getDataType() {
    return m_DataType;
  }
}