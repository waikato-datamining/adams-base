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
 * CheckableTableModel.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import javax.swing.table.TableModel;

/**
 * Meta-model that wraps another table model and allows "ticking" of rows.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CheckableTableModel<T extends TableModel>
  extends AbstractBaseTableModel
  implements ClearableModel, CustomSearchTableModel {

  private static final long serialVersionUID = -9169425499326950832L;

  /** the wrapped table model. */
  protected T m_Model;

  /** whether a row got selected. */
  protected boolean[] m_Selected;

  /** the name for the "tick" column. */
  protected String m_CheckColName;

  /**
   * Initializes the model.
   *
   * @param model		the model to wrap
   * @param checkColName	the name for the "check" column
   */
  public CheckableTableModel(T model, String checkColName) {
    super();

    m_Model        = model;
    m_CheckColName = checkColName;
    m_Selected     = new boolean[model.getRowCount()];
  }

  /**
   * Returns the underlying model.
   *
   * @return		the model
   */
  public T getModel() {
    return m_Model;
  }

  /**
   * Returns the number of rows in the model.
   *
   * @return		the number of rows
   */
  @Override
  public int getRowCount() {
    return m_Model.getRowCount();
  }

  /**
   * Returns the number of columns in the table.
   *
   * @return		the number of columns
   */
  public int getColumnCount() {
    return m_Model.getColumnCount() + 1;
  }

  /**
   * Returns the name of the column.
   *
   * @param column 	the column to get the name for
   * @return		the name of the column
   */
  public String getColumnName(int column) {
    if (column == 0)
      return m_CheckColName;
    else
      return m_Model.getColumnName(column - 1);
  }

  /**
   * Returns the class type of the column.
   *
   * @param columnIndex	the column to get the class for
   * @return			the class for the column
   */
  public Class getColumnClass(int columnIndex) {
    if (columnIndex == 0)
      return Boolean.class;
    else
      return m_Model.getColumnClass(columnIndex - 1);
  }

  /**
   * Returns the value at the given position.
   *
   * @param row		the row
   * @param column	the column
   * @return		the value
   */
  public Object getValueAt(int row, int column) {
    if (column == 0)
      return m_Selected[row];
    else
      return m_Model.getValueAt(row, column - 1);
  }

  /**
   * Returns whether the cell is editable.
   *
   * @param rowIndex		the row
   * @param columnIndex	the column
   * @return			true if editable
   */
  @Override
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    if (columnIndex == 0)
      return true;
    else if (columnIndex > 0)
      return m_Model.isCellEditable(rowIndex, columnIndex - 1);
    else
      return false;
  }

  /**
   * Sets the value of the cell.
   *
   * @param aValue		the value to set
   * @param rowIndex		the row
   * @param columnIndex	the column
   */
  @Override
  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    if (columnIndex == 0) {
      m_Selected[rowIndex] = (Boolean) aValue;
      fireTableCellUpdated(rowIndex, columnIndex);
    }
    else if (columnIndex > 0) {
      m_Model.setValueAt(aValue, rowIndex, columnIndex - 1);
    }
  }

  /**
   * Returns whether the row at the specified position is selected.
   *
   * @param row		the (actual, not visible) position of the row
   * @return		true if selected
   */
  public boolean getSelectedAt(int row) {
    return ((row >= 0) && (row < m_Selected.length)) && m_Selected[row];
  }

  /**
   * Marks all rows as selected.
   */
  public void selectAll() {
    select(true);
  }

  /**
   * Marks all rows as un-selected.
   */
  public void selectNone() {
    select(false);
  }

  /**
   * Marks all rows with the specified select state.
   */
  protected void select(boolean select) {
    for (int i = 0; i < m_Selected.length; i++)
      m_Selected[i] = select;

    fireTableDataChanged();
  }

  /**
   * Inverts the selection state.
   */
  public void invertSelection() {
    for (int i = 0; i < m_Selected.length; i++)
      m_Selected[i] = !m_Selected[i];

    fireTableDataChanged();
  }

  /**
   * Returns how many rows are currently checked.
   *
   * @return		the number of checked
   */
  public int getSelectedCount() {
    int	result;

    result = 0;

    for (boolean sel: m_Selected)
      result += (sel) ? 1 : 0;

    return result;
  }

  /**
   * Clears the internal model.
   */
  @Override
  public void clear() {
    if (m_Model instanceof ClearableModel)
      ((ClearableModel) m_Model).clear();
    m_Selected = new boolean[m_Model.getRowCount()];
  }

  /**
   * Tests whether the search matches the specified row.
   *
   * @param params	the search parameters
   * @param row		the row of the underlying, unsorted model
   * @return		true if the search matches this row
   */
  @Override
  public boolean isSearchMatch(SearchParameters params, int row) {
    return (m_Model instanceof CustomSearchTableModel)
      && ((CustomSearchTableModel) m_Model).isSearchMatch(params, row);
  }
}
