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
 * SortableAndSearchableTableWithButtons.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import adams.gui.event.PopupMenuListener;
import adams.gui.event.RemoveItemsListener;

/**
 * Graphical component that consists of a SortableAndSearchableTable with
 * buttons on the right-hand side.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SortableAndSearchableTableWithButtons<T extends SortableAndSearchableTable>
  extends AbstractDoubleClickableComponentWithButtons<T> {

  /** for serialization. */
  private static final long serialVersionUID = -773857386370817514L;

  /** the model listener for updating the counts. */
  protected TableModelListener m_CountModelListener;

  /**
   * The default constructor.
   */
  public SortableAndSearchableTableWithButtons() {
    super();
  }

  /**
   * Initializes the table with the specified model.
   *
   * @param model	the model to use
   */
  public SortableAndSearchableTableWithButtons(TableModel model) {
    super();

    m_Component.setModel(model);
    m_Component.setOptimalColumnWidth();
    model.addTableModelListener(m_Component);
    updateCountsModelListener(model);
  }

  /**
   * Returns whether the component requires a JScrollPane around it.
   *
   * @return		true if the component requires a JScrollPane
   */
  public boolean requiresScrollPane() {
    return true;
  }

  /**
   * Creates the component to use in the panel. If a
   *
   * @return		the component
   */
  protected T createComponent() {
    T	result;

    result = (T) new SortableAndSearchableTable();
    result.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> updateCounts());

    return result;
  }

  /**
   * Returns the underlying component.
   *
   * @return		the underlying component
   */
  public T getComponent() {
    return m_Component;
  }

  /**
   * Checks whether the double click is valid for this component.
   *
   * @param e		the mouse event of the double click
   * @return		true if valid double click
   */
  protected boolean isValidDoubleClick(MouseEvent e) {
    return (m_Component.getSelectedRowCount() == 1);
  }

  /**
   * Sets the data model for this table to <code>newModel</code> and registers
   * with it for listener notifications from the new data model.
   *
   * @param   dataModel        the new data source for this table
   * @see     #getModel
   */
  public void setModel(TableModel dataModel) {
    updateCountsModelListener(dataModel);
    m_Component.setModel(dataModel);
    updateCounts();
  }

  /**
   * Returns the underlying table model.
   *
   * @return		the underlying table model
   */
  public TableModel getModel() {
    return m_Component.getModel();
  }

  /**
   * Sets the data model for this table to <code>newModel</code> and registers
   * with it for listener notifications from the new data model.
   *
   * @param   dataModel        the new data source for this table
   * @see     #getUnsortedModel
   */
  public void setUnsortedModel(TableModel dataModel) {
    m_Component.setUnsortedModel(dataModel);
  }

  /**
   * Sets the data model for this table to <code>newModel</code> and registers
   * with it for listener notifications from the new data model.
   *
   * @param dataModel        	the new data source for this table
   * @param restoreSorting	whether to restore the sorting settings
   * @see   #getUnsortedModel
   */
  public void setUnsortedModel(TableModel dataModel, boolean restoreSorting) {
    m_Component.setUnsortedModel(dataModel, restoreSorting);
  }

  /**
   * Returns the underlying table model.
   *
   * @return		the underlying table model
   */
  public TableModel getUnsortedModel() {
    return m_Component.getUnsortedModel();
  }

  /**
   * Returns whether the table model supports moving of rows.
   *
   * @return		true if the model supports moving rows around
   * @see		MoveableTableModel
   */
  public boolean supportsMovingRows() {
    return (getModel() instanceof MoveableTableModel);
  }

  /**
   * moves the selected items up by 1.
   */
  public void moveUp() {
    if (supportsMovingRows())
      ((MoveableTableModel) getModel()).moveUp(m_Component.getSelectedRows());
  }

  /**
   * moves the selected items down by 1.
   */
  public void moveDown() {
    if (supportsMovingRows())
      ((MoveableTableModel) getModel()).moveDown(m_Component.getSelectedRows());
  }

  /**
   * moves the selected items to the top.
   */
  public void moveTop() {
    if (supportsMovingRows())
      ((MoveableTableModel) getModel()).moveTop(m_Component.getSelectedRows());
  }

  /**
   * moves the selected items to the end.
   */
  public void moveBottom() {
    if (supportsMovingRows())
      ((MoveableTableModel) getModel()).moveBottom(m_Component.getSelectedRows());
  }

  /**
   * checks whether the selected items can be moved up.
   *
   * @return		true if the selected items can be moved
   */
  public boolean canMoveUp() {
    if (supportsMovingRows())
      return ((MoveableTableModel) getModel()).canMoveUp(m_Component.getSelectedRows());
    else
      return false;
  }

  /**
   * checks whether the selected items can be moved down.
   *
   * @return		true if the selected items can be moved
   */
  public boolean canMoveDown() {
    if (supportsMovingRows())
      return ((MoveableTableModel) getModel()).canMoveDown(m_Component.getSelectedRows());
    else
      return false;
  }

  /**
   * Sets the column model for this table to <code>newModel</code> and registers
   * for listener notifications from the new column model. Also sets
   * the column model of the <code>JTableHeader</code> to <code>columnModel</code>.
   *
   * @param   columnModel        the new data source for this table
   * @exception IllegalArgumentException      if <code>columnModel</code> is <code>null</code>
   * @see     #getColumnModel
   */
  public void setColumnModel(TableColumnModel columnModel) {
    m_Component.setColumnModel(columnModel);
  }

  /**
   * Returns the <code>TableColumnModel</code> that contains all column information
   * of this table.
   *
   * @return  the object that provides the column state of the table
   * @see     #setColumnModel
   */
  public TableColumnModel getColumnModel() {
    return m_Component.getColumnModel();
  }

  /**
   * Sets the row selection model for this table to <code>newModel</code>
   * and registers for listener notifications from the new selection model.
   *
   * @param   newModel        the new selection model
   * @exception IllegalArgumentException      if <code>newModel</code> is <code>null</code>
   * @see     #getSelectionModel
   */
  public void setSelectionModel(ListSelectionModel newModel) {
    m_Component.setSelectionModel(newModel);
  }

  /**
   * Returns the <code>ListSelectionModel</code> that is used to maintain row
   * selection state.
   *
   * @return  the object that provides row selection state, <code>null</code>
   *          if row selection is not allowed
   * @see     #setSelectionModel
   */
  public ListSelectionModel getSelectionModel() {
    return m_Component.getSelectionModel();
  }

  /**
   * Sets the table's auto resize mode when the table is resized.
   *
   * @param   mode One of 5 legal values:
   *                   AUTO_RESIZE_OFF,
   *                   AUTO_RESIZE_NEXT_COLUMN,
   *                   AUTO_RESIZE_SUBSEQUENT_COLUMNS,
   *                   AUTO_RESIZE_LAST_COLUMN,
   *                   AUTO_RESIZE_ALL_COLUMNS
   *
   * @see     #getAutoResizeMode
   * @see     #doLayout
   */
  public void setAutoResizeMode(int mode) {
    m_Component.setAutoResizeMode(mode);
  }

  /**
   * Returns the auto resize mode of the table.  The default mode
   * is AUTO_RESIZE_SUBSEQUENT_COLUMNS.
   *
   * @return  the autoResizeMode of the table
   * @see     #setAutoResizeMode
   */
  public int getAutoResizeMode() {
    return m_Component.getAutoResizeMode();
  }

  /**
   * Sets the optimal column width for all columns. AutoResize must be set
   * to BaseTable.AUTO_RESIZE_OFF.
   */
  public void setOptimalColumnWidth() {
    m_Component.setOptimalColumnWidth();
  }

  /**
   * Sets the optimal column width for the specified column. AutoResize must be set
   * to BaseTable.AUTO_RESIZE_OFF.
   *
   * @param column	the column to resize
   */
  public void setOptimalColumnWidth(int column) {
    m_Component.setOptimalColumnWidth(column);
  }

  /**
   * Sets the height, in pixels, of all cells to <code>rowHeight</code>,
   * revalidates, and repaints.
   * The height of the cells will be equal to the row height minus
   * the row margin.
   *
   * @param   rowHeight                       new row height
   */
  public void setRowHeight(int rowHeight) {
    m_Component.setRowHeight(rowHeight);
  }

  /**
   * Returns the height for the specified row.
   *
   * @param row		the row to get the height in pixels for
   * @return		the row height
   */
  public int getRowHeight(int row) {
    return m_Component.getRowHeight(row);
  }

  /**
   * Returns the index of the row that <code>point</code> lies in,
   * or -1 if the result is not in the range
   * [0, <code>getRowCount()</code>-1].
   *
   * @param   point   the location of interest
   * @return  the index of the row that <code>point</code> lies in,
   *          or -1 if the result is not in the range
   *          [0, <code>getRowCount()</code>-1]
   * @see     #getRowModel()
   */
  public int rowAtPoint(Point point) {
    return m_Component.rowAtPoint(point);
  }

  /**
   * Returns the index of the column that <code>point</code> lies in,
   * or -1 if the result is not in the range
   * [0, <code>getColumnCount()</code>-1].
   *
   * @param   point   the location of interest
   * @return  the index of the column that <code>point</code> lies in,
   *		or -1 if the result is not in the range
   *		[0, <code>getColumnCount()</code>-1]
   * @see     #rowAtPoint
   */
  public int columnAtPoint(Point point) {
    return m_Component.columnAtPoint(point);
  }

  /**
   * Returns the index of the first selected row, -1 if no row is selected.
   * @return the index of the first selected row
   */
  public int getSelectedRow() {
    return m_Component.getSelectedRow();
  }

  /**
   * Returns the index of the first selected column,
   * -1 if no column is selected.
   * @return the index of the first selected column
   */
  public int getSelectedColumn() {
    return m_Component.getSelectedColumn();
  }

  /**
   * Returns the indices of all selected rows.
   *
   * @return an array of integers containing the indices of all selected rows,
   *         or an empty array if no row is selected
   * @see #getSelectedRow
   */
  public int[] getSelectedRows() {
    return m_Component.getSelectedRows();
  }

  /**
   * Returns the indices of all selected columns.
   *
   * @return an array of integers containing the indices of all selected columns,
   *         or an empty array if no column is selected
   * @see #getSelectedColumn
   */
  public int[] getSelectedColumns() {
    return m_Component.getSelectedColumns();
  }

  /**
   * Returns the number of selected rows.
   *
   * @return the number of selected rows, 0 if no rows are selected
   */
  public int getSelectedRowCount() {
    return m_Component.getSelectedRowCount();
  }

  /**
   * Returns the number of selected columns.
   *
   * @return the number of selected columns, 0 if no columns are selected
   */
  public int getSelectedColumnCount() {
    return m_Component.getSelectedColumnCount();
  }

  /**
   * Returns true if the specified index is in the valid range of rows,
   * and the row at that index is selected.
   *
   * @return true if <code>row</code> is a valid index and the row at
   *              that index is selected (where 0 is the first row)
   */
  public boolean isRowSelected(int row) {
    return m_Component.isRowSelected(row);
  }

  /**
   * Returns true if the specified index is in the valid range of columns,
   * and the column at that index is selected.
   *
   * @param   column   the column in the column model
   * @return true if <code>column</code> is a valid index and the column at
   *              that index is selected (where 0 is the first column)
   */
  public boolean isColumnSelected(int column) {
    return m_Component.isColumnSelected(column);
  }

  /**
   * Returns true if the specified indices are in the valid range of rows
   * and columns and the cell at the specified position is selected.
   * @param row   the row being queried
   * @param column  the column being queried
   *
   * @return true if <code>row</code> and <code>column</code> are valid indices
   *              and the cell at index <code>(row, column)</code> is selected,
   *              where the first row and first column are at index 0
   */
  public boolean isCellSelected(int row, int column) {
    return m_Component.isCellSelected(row, column);
  }

  /**
   * Returns the number of rows that can be shown in the
   * <code>JTable</code>, given unlimited space.  If a
   * <code>RowSorter</code> with a filter has been specified, the
   * number of rows returned may differ from that of the underlying
   * <code>TableModel</code>.
   *
   * @return the number of rows shown in the <code>JTable</code>
   * @see #getColumnCount
   */
  public int getRowCount() {
    return m_Component.getRowCount();
  }

  /**
   * Returns the number of columns in the column model. Note that this may
   * be different from the number of columns in the table model.
   *
   * @return  the number of columns in the table
   * @see #getRowCount
   * @see #removeColumn
   */
  public int getColumnCount() {
    return m_Component.getColumnCount();
  }

  /**
   * Returns the name of the column appearing in the view at
   * column position <code>column</code>.
   *
   * @param  column    the column in the view being queried
   * @return the name of the column at position <code>column</code>
			in the view where the first column is column 0
   */
  public String getColumnName(int column) {
    return m_Component.getColumnName(column);
  }

  /**
   * Returns the type of the column appearing in the view at
   * column position <code>column</code>.
   *
   * @param   column   the column in the view being queried
   * @return the type of the column at position <code>column</code>
   * 		in the view where the first column is column 0
   */
  public Class<?> getColumnClass(int column) {
    return m_Component.getColumnClass(column);
  }

  /**
   * Returns the cell value at <code>row</code> and <code>column</code>.
   * <p>
   * <b>Note</b>: The column is specified in the table view's display
   *              order, and not in the <code>TableModel</code>'s column
   *		    order.  This is an important distinction because as the
   *		    user rearranges the columns in the table,
   *		    the column at a given index in the view will change.
   *              Meanwhile the user's actions never affect the model's
   *              column ordering.
   *
   * @param   row             the row whose value is to be queried
   * @param   column          the column whose value is to be queried
   * @return  the Object at the specified cell
   */
  public Object getValueAt(int row, int column) {
    return m_Component.getValueAt(row, column);
  }

  /**
   * Sets the value for the cell in the table model at <code>row</code>
   * and <code>column</code>.
   * <p>
   * <b>Note</b>: The column is specified in the table view's display
   *              order, and not in the <code>TableModel</code>'s column
   *		    order.  This is an important distinction because as the
   *		    user rearranges the columns in the table,
   *		    the column at a given index in the view will change.
   *              Meanwhile the user's actions never affect the model's
   *              column ordering.
   *
   * <code>aValue</code> is the new value.
   *
   * @param   aValue          the new value
   * @param   row             the row of the cell to be changed
   * @param   column          the column of the cell to be changed
   * @see #getValueAt
   */
  public void setValueAt(Object aValue, int row, int column) {
    m_Component.setValueAt(aValue, row, column);
  }

  /**
   * Returns true if the cell at <code>row</code> and <code>column</code>
   * is editable.  Otherwise, invoking <code>setValueAt</code> on the cell
   * will have no effect.
   * <p>
   * <b>Note</b>: The column is specified in the table view's display
   *              order, and not in the <code>TableModel</code>'s column
   *		    order.  This is an important distinction because as the
   *		    user rearranges the columns in the table,
   *		    the column at a given index in the view will change.
   *              Meanwhile the user's actions never affect the model's
   *              column ordering.
   *
   *
   * @param   row      the row whose value is to be queried
   * @param   column   the column whose value is to be queried
   * @return  true if the cell is editable
   * @see #setValueAt
   */
  public boolean isCellEditable(int row, int column) {
    return m_Component.isCellEditable(row, column);
  }

  /**
   * Sets the table's selection mode to allow only single selections, a single
   * contiguous interval, or multiple intervals.
   *
   * @param selectionMode	the selection mode to use
   * @see			JList#setSelectionMode(int)
   */
  public void setSelectionMode(int selectionMode) {
    m_Component.setSelectionMode(selectionMode);
  }

  /**
   * Whether to display the information JLabel or not.
   *
   * @param value	if true then the information is being displayed
   */
  public void setInfoVisible(boolean value) {
    super.setInfoVisible(value);
    if (value)
      updateCounts();
  }

  /**
   * Updates the table model's listener for updating the counts.
   *
   * @param dataModel	the model to update
   */
  protected void updateCountsModelListener(TableModel dataModel) {
    if (m_CountModelListener != null)
      getModel().removeTableModelListener(m_CountModelListener);

    m_CountModelListener = new TableModelListener() {
      public void tableChanged(TableModelEvent e) {
	updateCounts();
      }
    };
    dataModel.addTableModelListener(m_CountModelListener);
  }

  /**
   * Updates the information about the counts.
   */
  protected void updateCounts() {
    updateInfo(
	"Total: " + m_Component.getRowCount()
	+ ", Selected: " + m_Component.getSelectedRowCount());
  }

  /**
   * Returns the actual underlying row the given visible one represents. Useful
   * for retrieving "non-visual" data that is also stored in a TableModel.
   *
   * @param visibleRow	the displayed row to retrieve the original row for
   * @return		the original row
   */
  public synchronized int getActualRow(int visibleRow) {
    return m_Component.getActualRow(visibleRow);
  }

  /**
   * Returns the "visible" row derived from row in the actual table model.
   *
   * @param internalRow	the row in the actual model
   * @return		the row in the sorted model, -1 in case of an error
   */
  public synchronized int getDisplayRow(int internalRow) {
    return m_Component.getDisplayRow(internalRow);
  }

  /**
   * Adds the remove items listener to its internal list.
   *
   * @param l		the listener to add
   */
  public void addRemoveItemsListener(RemoveItemsListener l) {
    m_Component.addRemoveItemsListener(l);
  }

  /**
   * Removes the remove items listener from its internal list.
   *
   * @param l		the listener to remove
   */
  public void removeRemoveItemsListener(RemoveItemsListener l) {
    m_Component.removeRemoveItemsListener(l);
  }

  /**
   * Adds the popup menu listeners for the header.
   *
   * @param l		the listener to add
   */
  public void addHeaderPopupMenuListener(PopupMenuListener l) {
    m_Component.addHeaderPopupMenuListener(l);
  }

  /**
   * Removes the popup menu listener for the header from its internal list.
   *
   * @param l		the listener to remove
   */
  public void removeHeaderPopupMenuListener(PopupMenuListener l) {
    m_Component.removeHeaderPopupMenuListener(l);
  }

  /**
   * Adds the popup menu listeners for the cell.
   *
   * @param l		the listener to add
   */
  public void addCellPopupMenuListener(PopupMenuListener l) {
    m_Component.addCellPopupMenuListener(l);
  }

  /**
   * Removes the popup menu listener for the cell from its internal list.
   *
   * @param l		the listener to remove
   */
  public void removeCellPopupMenuListener(PopupMenuListener l) {
    m_Component.removeCellPopupMenuListener(l);
  }

  /**
   * Performs a search for the given string. Limits the display of rows to
   * ones containing the search string.
   *
   * @param searchString	the string to search for
   * @param regexp		whether to perform regular expression matching
   * 				or just plain string comparison
   */
  public void search(String searchString, boolean regexp) {
    m_Component.search(searchString, regexp);
  }

  /**
   * Returns the current search string.
   *
   * @return		the search string, null if not filtered
   */
  public String getSeachString() {
    return m_Component.getSeachString();
  }

  /**
   * Returns whether the last search was a regular expression based one.
   *
   * @return		true if last search was a reg exp one
   */
  public boolean isRegExpSearch() {
    return m_Component.isRegExpSearch();
  }
}
