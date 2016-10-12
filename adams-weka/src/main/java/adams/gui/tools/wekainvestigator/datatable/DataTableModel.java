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
 * DataTableModel.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.datatable;

import adams.gui.core.AbstractMoveableTableModel;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import weka.core.Attribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Model for displaying the loaded data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DataTableModel
  extends AbstractMoveableTableModel {

  private static final long serialVersionUID = 8586181476263855804L;

  /** the underlying data. */
  protected List<DataContainer> m_Data;

  /** whether the model is read-only. */
  protected boolean m_ReadOnly;

  /**
   * Initializes the model.
   *
   * @param data	the data to use
   * @param readOnly	whether the model is readonly
   */
  public DataTableModel(List<DataContainer> data, boolean readOnly) {
    super();
    m_Data     = new ArrayList<>(data);
    m_ReadOnly = readOnly;
  }

  /**
   * Returns whether the model is readonly.
   *
   * @return		true if readonly
   */
  public boolean isReadOnly() {
    return m_ReadOnly;
  }

  /**
   * The number of datasets loaded.
   *
   * @return		the number of datasets
   */
  @Override
  public int getRowCount() {
    return m_Data.size();
  }

  /**
   * The number of columns.
   *
   * @return		the number of columns
   */
  @Override
  public int getColumnCount() {
    int	result;

    result = 0;
    result++;  // index
    result++;  // modified
    result++;  // relation
    result++;  // class
    result++;  // source

    return result;
  }

  /**
   * Returns the column name.
   *
   * @param column	the index of the column
   * @return		the name
   */
  @Override
  public String getColumnName(int column) {
    switch (column) {
      case 0:
	return "Index";
      case 1:
	return "Mod";
      case 2:
	return "Relation";
      case 3:
	return "Class";
      case 4:
	return "Source";
      default:
	return null;
    }
  }

  /**
   * Returns the value at the specified position.
   *
   * @param rowIndex		the row
   * @param columnIndex	the column
   * @return			the value
   */
  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    DataContainer	cont;

    cont = m_Data.get(rowIndex);

    switch (columnIndex) {
      case 0:
	return (rowIndex + 1);
      case 1:
	return cont.isModified();
      case 2:
	return cont.getData().relationName();
      case 3:
	return (cont.getData().classIndex() == -1) ? "<none>" : cont.getData().classAttribute().name();
      case 4:
	return cont.getSource();
      default:
	return null;
    }
  }

  /**
   * Returns the class type for the column.
   *
   * @param columnIndex	the column
   * @return		the class
   */
  @Override
  public Class<?> getColumnClass(int columnIndex) {
    if (columnIndex == 0)
      return Integer.class;
    if (columnIndex == 1)
      return Boolean.class;
    return super.getColumnClass(columnIndex);
  }

  /**
   * Returns whether a cell is editable.
   *
   * @param rowIndex	the row
   * @param columnIndex	the column
   * @return		true if editable
   */
  @Override
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    if (m_ReadOnly)
      return false;
    if (columnIndex == 3)
      return true;
    return false;
  }

  /**
   * Sets the value at the specified position, if possible.
   *
   * @param aValue	the value to set
   * @param rowIndex	the row
   * @param columnIndex	the column
   */
  @Override
  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    DataContainer	cont;
    Attribute		att;
    String		name;

    if (m_ReadOnly)
      return;
    if (aValue == null)
      return;

    cont = m_Data.get(rowIndex);

    switch (columnIndex) {
      case 3:  // class
	name = "" + aValue;
	if (name.isEmpty()) {
          cont.addUndoPoint("Unsetting class");
	  cont.getData().setClassIndex(-1);
	  fireTableRowsUpdated(rowIndex, rowIndex);
	}
	else {
	  att = cont.getData().attribute(name);
	  if (att != null) {
            cont.addUndoPoint("Setting class");
	    cont.getData().setClassIndex(att.index());
	    fireTableRowsUpdated(rowIndex, rowIndex);
	  }
	}
	break;
    }
  }

  /**
   * Sets the underlying data and notifies listeners of change.
   *
   * @param value	the data to use
   */
  public void setData(List<DataContainer> value) {
    setData(value, true);
  }

  /**
   * Sets the underlying data.
   *
   * @param value	the data to use
   * @param notify	whether to notify listeners that data has changed
   */
  public void setData(List<DataContainer> value, boolean notify) {
    m_Data = new ArrayList<>(value);
    if (notify)
      fireTableDataChanged();
  }

  /**
   * Returns the underlying data.
   *
   * @return		the data
   */
  public List<DataContainer> getData() {
    return m_Data;
  }

  /**
   * Swaps the two rows.
   *
   * @param firstIndex	the index of the first row
   * @param secondIndex	the index of the second row
   */
  @Override
  protected void swap(int firstIndex, int secondIndex) {
    Collections.swap(m_Data, firstIndex, secondIndex);
  }
}
