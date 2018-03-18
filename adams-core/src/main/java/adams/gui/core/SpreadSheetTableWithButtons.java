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
 * SpreadSheetTableWithButtons.java
 * Copyright (C) 2017-2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import adams.core.Utils;
import adams.data.spreadsheet.RowComparator;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.spreadsheettable.CellRenderingCustomizer;
import adams.gui.visualization.core.PopupMenuCustomizer;

import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableModel;

/**
 * Graphical component that consists of a SpreadSheetTable with buttons on the
 * right-hand side.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SpreadSheetTableWithButtons
  extends BaseTableWithButtons {

  private static final long serialVersionUID = -6412636552722254765L;

  /**
   * The default constructor.
   */
  public SpreadSheetTableWithButtons() {
    this(new SpreadSheetTableModel());
  }

  /**
   * Initializes the table with the specified model.
   *
   * @param model	the model to use
   */
  public SpreadSheetTableWithButtons(SpreadSheetTableModel model) {
    super(model);
  }

  /**
   * Creates the component to use in the panel. If a
   *
   * @return		the component
   */
  @Override
  protected BaseTable createComponent() {
    SpreadSheetTable	result;

    result = new SpreadSheetTable(new SpreadSheetTableModel());
    result.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> updateCounts());

    return result;
  }

  /**
   * Sets the data model for this table to <code>newModel</code> and registers
   * with it for listener notifications from the new data model.
   *
   * @param   dataModel        the new data source for this table
   * @see     #getModel
   */
  @Override
  public void setModel(TableModel dataModel) {
    if (!(dataModel instanceof SpreadSheetTableModel))
      throw new IllegalArgumentException("Model is not of type " + Utils.classToString(SpreadSheetTableModel.class));
    super.setModel(dataModel);
  }

  /**
   * Returns the underlying table model.
   *
   * @return		the underlying table model
   */
  @Override
  public SpreadSheetTableModel getModel() {
    return (SpreadSheetTableModel) ((SortableAndSearchableWrapperTableModel) super.getModel()).getUnsortedModel();
  }

  /**
   * Sets the popup menu customizer to use (for the header).
   *
   * @param value	the customizer, null to remove it
   */
  public void setHeaderPopupMenuCustomizer(adams.gui.visualization.core.PopupMenuCustomizer value) {
    ((SpreadSheetTable) m_Component).setHeaderPopupMenuCustomizer(value);
  }

  /**
   * Returns the current popup menu customizer (for the header).
   *
   * @return		the customizer, null if none set
   */
  public adams.gui.visualization.core.PopupMenuCustomizer getHeaderPopupMenuCustomizer() {
    return ((SpreadSheetTable) m_Component).getHeaderPopupMenuCustomizer();
  }

  /**
   * Sets the popup menu customizer to use (for the cells).
   *
   * @param value	the customizer, null to remove it
   */
  public void setCellPopupMenuCustomizer(adams.gui.visualization.core.PopupMenuCustomizer value) {
    ((SpreadSheetTable) m_Component).setCellPopupMenuCustomizer(value);
  }

  /**
   * Returns the current popup menu customizer (for the cells).
   *
   * @return		the customizer, null if none set
   */
  public PopupMenuCustomizer getCellPopupMenuCustomizer() {
    return ((SpreadSheetTable) m_Component).getCellPopupMenuCustomizer();
  }

  /**
   * Sets the renderer.
   *
   * @param value	the renderer
   */
  public void setCellRenderingCustomizer(CellRenderingCustomizer value) {
    ((SpreadSheetTable) m_Component).setCellRenderingCustomizer(value);
  }

  /**
   * Returns the renderer.
   *
   * @return		the renderer
   */
  public CellRenderingCustomizer getCellRenderingCustomizer() {
    return ((SpreadSheetTable) m_Component).getCellRenderingCustomizer();
  }

  /**
   * Sets whether to display the formulas or their calculated values.
   *
   * @param value	true if to display the formulas rather than the calculated values
   */
  public void setShowFormulas(boolean value) {
    ((SpreadSheetTable) m_Component).setShowFormulas(value);
  }

  /**
   * Returns whether to display the formulas or their calculated values.
   *
   * @return		true if to display the formulas rather than the calculated values
   */
  public boolean getShowFormulas() {
    return ((SpreadSheetTable) m_Component).getShowFormulas();
  }

  /**
   * Whether to display the column with the row numbers.
   * 
   * @param value	true if to display column
   */
  public void setShowRowColumn(boolean value) {
    ((SpreadSheetTable) m_Component).setShowRowColumn(value);
  }
  
  /**
   * Returns whether the column with the row numbers is displayed.
   * 
   * @return		true if column displayed
   */
  public boolean getShowRowColumn() {
    return ((SpreadSheetTable) m_Component).getShowRowColumn();
  }

  /**
   * Whether to display a simple header or an HTML one with the column indices.
   *
   * @param value	true if to display simple header
   */
  public void setUseSimpleHeader(boolean value) {
    ((SpreadSheetTable) m_Component).setUseSimpleHeader(value);
  }

  /**
   * Returns whether to display a simple header or an HTML one with the column indices.
   *
   * @return		true if simple header displayed
   */
  public boolean getUseSimpleHeader() {
    return ((SpreadSheetTable) m_Component).getUseSimpleHeader();
  }

  /**
   * Sets whether the table is read-only.
   *
   * @param value	true if read-only
   */
  public void setReadOnly(boolean value) {
    ((SpreadSheetTable) m_Component).setReadOnly(value);
  }

  /**
   * Returns whether the table is read-only.
   *
   * @return		true if read-only
   */
  public boolean isReadOnly() {
    return ((SpreadSheetTable) m_Component).isReadOnly();
  }

  /**
   * Sets whether the table has been modified.
   *
   * @param value	true if modified
   */
  public void setModified(boolean value) {
    ((SpreadSheetTable) m_Component).setModified(value);
  }

  /**
   * Returns whether the table has been modified.
   *
   * @return		true if modified
   */
  public boolean isModified() {
    return ((SpreadSheetTable) m_Component).isModified();
  }

  /**
   * Sets whether to show the cell types rather than the cell values.
   *
   * @param value	true if to show cell types
   */
  public void setShowCellTypes(boolean value) {
    ((SpreadSheetTable) m_Component).setShowCellTypes(value);
  }

  /**
   * Returns whether to show the cell types rather than the cell values.
   *
   * @return		true if showing the cell types
   */
  public boolean getShowCellTypes() {
    return ((SpreadSheetTable) m_Component).getShowCellTypes();
  }

  /**
   * Sorts the spreadsheet with the given comparator.
   *
   * @param comparator	the row comparator to use
   */
  public void sort(RowComparator comparator) {
    ((SpreadSheetTable) m_Component).sort(comparator);
  }

  /**
   * Performs a search for the given string. Limits the display of rows to
   * ones containing the search string.
   *
   * @param searchString	the string to search for
   * @param regexp		whether to perform regular expression matching
   * 				or just plain string comparison
   */
  public synchronized void search(String searchString, boolean regexp) {
    ((SpreadSheetTable) m_Component).search(searchString, regexp);
  }

  /**
   * Returns the current search string.
   *
   * @return		the search string, null if not filtered
   */
  public synchronized String getSeachString() {
    return ((SpreadSheetTable) m_Component).getSeachString();
  }

  /**
   * Returns whether the last search was a regular expression based one.
   *
   * @return		true if last search was a reg exp one
   */
  public synchronized boolean isRegExpSearch() {
    return ((SpreadSheetTable) m_Component).isRegExpSearch();
  }

  /**
   * Returns the underlying sheet.
   *
   * @return		the spread sheet
   */
  @Override
  public SpreadSheet toSpreadSheet() {
    return ((SpreadSheetTable) m_Component).toSpreadSheet();
  }

  /**
   * Returns the underlying sheet.
   *
   * @param range	the type of rows to return
   * @return		the spread sheet
   */
  public SpreadSheet toSpreadSheet(TableRowRange range) {
    return ((SpreadSheetTable) m_Component).toSpreadSheet(range);
  }

  /**
   * Returns the underlying sheet.
   *
   * @param range	the type of rows to return
   * @param view	whether to return only a view (ignored if {@link TableRowRange#ALL})
   * @return		the spread sheet
   */
  public SpreadSheet toSpreadSheet(TableRowRange range, boolean view) {
    return ((SpreadSheetTable) m_Component).toSpreadSheet(range, view);
  }
}
