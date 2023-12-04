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
 * SpreadSheetTable.java
 * Copyright (C) 2009-2023 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.RowComparator;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.spreadsheettable.CellRenderingCustomizer;
import adams.gui.core.spreadsheettable.SpreadSheetTablePopupMenuItemHelper;
import adams.gui.core.spreadsheettable.SpreadSheetTablePopupMenuItemHelper.TableState;
import adams.gui.event.PopupMenuListener;
import adams.gui.sendto.SendToActionSupporter;
import adams.gui.sendto.SendToActionUtils;
import adams.gui.visualization.core.PopupMenuCustomizer;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;

/**
 * A specialized table for displaying a SpreadSheet table model.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SpreadSheetTable
  extends SortableAndSearchableTable
  implements SendToActionSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 1333317577811620786L;

  /** the customizer for the table header popup menu. */
  protected PopupMenuCustomizer m_HeaderPopupMenuCustomizer;

  /** the customizer for the table cells popup menu. */
  protected PopupMenuCustomizer m_CellPopupMenuCustomizer;

  /** for keeping track of the setups being used (classname-{plot|process}-{column|row} - setup). */
  protected HashMap<String,Object> m_LastSetup;
  
  /**
   * Initializes the table.
   *
     * @param sheet	the underlying spread sheet
   */
  public SpreadSheetTable(SpreadSheet sheet) {
    this(new SpreadSheetTableModel(sheet));
  }

  /**
   * Initializes the table.
   *
     * @param model	the underlying spread sheet model
   */
  public SpreadSheetTable(SpreadSheetTableModel model) {
    super(model);
  }

  /**
   * Initializes some GUI-related things.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    m_HeaderPopupMenuCustomizer = null;
    m_CellPopupMenuCustomizer   = null;
    m_LastSetup                 = new HashMap<>();

    addHeaderPopupMenuListener(new PopupMenuListener() {
      @Override
      public void showPopupMenu(MouseEvent e) {
	showHeaderPopupMenu(e);
      }
    });
    addCellPopupMenuListener(new PopupMenuListener() {
      @Override
      public void showPopupMenu(MouseEvent e) {
        showCellPopupMenu(e);
      }
    });
  }

  /**
   * Sets the custom cell renderer.
   */
  protected void setCustomCellRenderer() {
    int				i;
    SpreadSheetCellRenderer	renderer;
    TableColumnModel		colModel;

    renderer = new SpreadSheetCellRenderer();
    colModel = getColumnModel();
    for (i = 0; i < getColumnCount(); i++)
      colModel.getColumn(i).setCellRenderer(renderer);
  }

  /**
   * Sets the model to display - only {@link #getTableModelClass()}.
   * Also notifies all the {@link TableModelListener}s.
   *
   * @param model	the model to display
   */
  @Override
  public void setModel(TableModel model) {
    SpreadSheetTableModel	modelOld;
    TableModelListener[]	listeners;
    
    modelOld  = (SpreadSheetTableModel) getUnsortedModel();
    listeners = null;
    if (modelOld != null)
      listeners = modelOld.getListeners(TableModelListener.class);

    super.setModel(model);
    setCustomCellRenderer();
    
    if (listeners != null) {
      for (TableModelListener listener: listeners) {
	modelOld.removeTableModelListener(listener);
	model.addTableModelListener(listener);
	listener.tableChanged(new TableModelEvent(model));
      }
    }

    if (modelOld != null)
      modelOld.cleanUp();
  }

  /**
   * Returns the initial setting of whether to set optimal column widths.
   *
   * @return		adaptive
   */
  @Override
  protected ColumnWidthApproach initialUseOptimalColumnWidths() {
    return ColumnWidthApproach.ADAPTIVE;
  }

  /**
   * Returns the initial setting of whether to sort new models.
   *
   * @return		true
   */
  @Override
  protected boolean initialSortNewTableModel() {
    return false;
  }

  /**
   * Returns the class of the table model that the models need to be derived
   * from. The default implementation just returns TableModel.class
   *
   * @return		the class the models must be derived from
   */
  @Override
  protected Class getTableModelClass() {
    return SpreadSheetTableModel.class;
  }

  /**
   * Creates an empty default model.
   *
   * @return		the model
   */
  @Override
  protected TableModel createDefaultDataModel() {
    return new SpreadSheetTableModel();
  }

  /**
   * Returns the spread sheet cell at the specified location.
   *
   * @param rowIndex	the current display row index
   * @param columnIndex	the column index
   * @return		the cell or null if invalid coordinates
   */
  public Cell getCellAt(int rowIndex, int columnIndex) {
    Cell			result;
    SpreadSheetTableModel	sheetmodel;

    sheetmodel = (SpreadSheetTableModel) getUnsortedModel();
    result     = sheetmodel.getCellAt(getActualRow(rowIndex), columnIndex);

    return result;
  }

  /**
   * Sets the number of decimals to display. Use -1 to display all.
   *
   * @param value	the number of decimals
   */
  public void setNumDecimals(int value) {
    ((SpreadSheetTableModel) getUnsortedModel()).setNumDecimals(value);
  }

  /**
   * Returns the currently set number of decimals. -1 if displaying all.
   *
   * @return		the number of decimals
   */
  public int getNumDecimals() {
    return ((SpreadSheetTableModel) getUnsortedModel()).getNumDecimals();
  }

  /**
   * Determines the actual row index.
   *
   * @param index	the selected row
   * @return		the actual model row
   */
  protected int selectionRowToModelRow(int index) {
    return getActualRow(index);
  }

  /**
   * Shows a popup menu for the header.
   *
   * @param e		the event that triggered the menu
   */
  protected void showHeaderPopupMenu(MouseEvent e) {
    BasePopupMenu	menu;
    JMenuItem		menuitem;
    final boolean	asc;
    final TableState	state;
    TableRowRange	range;

    menu   = new BasePopupMenu();
    if (getSelectedRows().length > 0)
      range = TableRowRange.SELECTED;
    else if (isAnyColumnFiltered() || ((getSeachString() != null) && !getSeachString().isEmpty()))
      range = TableRowRange.VISIBLE;
    else
      range = TableRowRange.ALL;
    state  = SpreadSheetTablePopupMenuItemHelper.getState(this, e, range);

    menuitem = new JMenuItem("Copy column name", ImageManager.getIcon("copy.gif"));
    menuitem.setEnabled((getShowRowColumn() && (state.selCol > 0) || !getShowRowColumn()));
    menuitem.addActionListener((ActionEvent ae) -> ClipboardHelper.copyToClipboard(((SpreadSheetTableModel) getUnsortedModel()).toSpreadSheet().getColumnName(state.actCol)));
    menu.add(menuitem);
    
    menuitem = new JMenuItem("Copy column", ImageManager.getIcon("copy_column.gif"));
    menuitem.setEnabled((getShowRowColumn() && (state.selCol > 0) || !getShowRowColumn()));
    menuitem.addActionListener((ActionEvent ae) -> {
      SpreadSheet sheet = toSpreadSheet(TableRowRange.VISIBLE);
      StringBuilder content = new StringBuilder();
      String sep = System.getProperty("line.separator");
      content.append(sheet.getColumnName(state.actCol) + sep);
      for (int i = 0; i < sheet.getRowCount(); i++) {
	if (!sheet.hasCell(i, state.actCol) || sheet.getCell(i, state.actCol).isMissing())
	  content.append(sep);
	else
	  content.append(sheet.getCell(i, state.actCol).getContent() + sep);
      }
      ClipboardHelper.copyToClipboard(content.toString());
    });
    menu.add(menuitem);
    
    menuitem = new JMenuItem("Rename column", ImageManager.getEmptyIcon());
    menuitem.setEnabled(!isReadOnly() && (getShowRowColumn() && (state.selCol > 0) || !getShowRowColumn()));
    menuitem.addActionListener((ActionEvent ae) -> {
      SpreadSheet sheet = ((SpreadSheetTableModel) getUnsortedModel()).toSpreadSheet();
      boolean readOnly = isReadOnly();
      String newName = sheet.getColumnName(state.actCol);
      newName = GUIHelper.showInputDialog(getParent(), "Please enter new column name", newName);
      if (newName == null)
	return;
      sheet = sheet.getClone();
      sheet.getHeaderRow().getCell(state.actCol).setContent(newName);
      setModel(new SpreadSheetTableModel(sheet));
      setReadOnly(readOnly);
      setModified(true);
      ((SpreadSheetTableModel) getUnsortedModel()).fireTableDataChanged();
    });
    menu.add(menuitem);

    asc  = !e.isShiftDown();
    if (asc)
      menuitem = new JMenuItem("Sort (asc)", ImageManager.getIcon("sort-ascending.png"));
    else
      menuitem = new JMenuItem("Sort (desc)", ImageManager.getIcon("sort-descending.png"));
    menuitem.setEnabled(!isReadOnly() && (getShowRowColumn() && (state.selCol > 0)) || !getShowRowColumn());
    menuitem.addActionListener((ActionEvent ae) -> {
      SpreadSheet sheet = toSpreadSheet();
      boolean readOnly = isReadOnly();
      sheet.sort(state.actCol, asc);
      setModel(new SpreadSheetTableModel(sheet));
      setReadOnly(readOnly);
      setModified(true);
      ((SpreadSheetTableModel) getUnsortedModel()).fireTableDataChanged();
    });
    menu.add(menuitem);

    menu.addSeparator();

    menuitem = new JMenuItem("Filter", ImageManager.getIcon("filter.png"));
    menuitem.setEnabled(state.selCol > 0);
    menuitem.addActionListener((ActionEvent ae) -> {
      String filter = "";
      if (getColumnFilter(state.selCol) != null)
        filter = getColumnFilter(state.selCol);
      filter = GUIHelper.showInputDialog(getParent(), "Please enter filter string", filter);
      if ((filter == null) || filter.isEmpty())
        return;
      setColumnFilter(state.selCol, filter, false);
    });
    menu.add(menuitem);

    menuitem = new JMenuItem("Filter (RegExp)", ImageManager.getEmptyIcon());
    menuitem.setEnabled(state.selCol > 0);
    menuitem.addActionListener((ActionEvent ae) -> {
      String filter = "";
      if (getColumnFilter(state.selCol) != null)
        filter = getColumnFilter(state.selCol);
      filter = GUIHelper.showInputDialog(getParent(), "Please enter regular expression filter", filter);
      if ((filter == null) || filter.isEmpty())
        return;
      setColumnFilter(state.selCol, filter, true);
    });
    menu.add(menuitem);

    menuitem = new JMenuItem("Remove filter", ImageManager.getIcon("delete.gif"));
    menuitem.setEnabled(isColumnFiltered(state.selCol));
    menuitem.addActionListener((ActionEvent ae) -> removeColumnFilter(state.selCol));
    menu.add(menuitem);

    menuitem = new JMenuItem("Remove all filters", ImageManager.getIcon("delete_all.gif"));
    menuitem.setEnabled(isAnyColumnFiltered());
    menuitem.addActionListener((ActionEvent ae) -> removeAllColumnFilters());
    menu.add(menuitem);

    menu.addSeparator();

    menuitem = new JMenuItem("Insert column", ImageManager.getIcon("insert-column.png"));
    menuitem.setEnabled(!isReadOnly());
    menuitem.addActionListener((ActionEvent ae) -> {
      String colName = GUIHelper.showInputDialog(
	GUIHelper.getParentComponent(SpreadSheetTable.this),
	"Please enter the name of the column", "New");
      if (colName == null)
	return;
      SpreadSheet sheet = toSpreadSheet();
      boolean readOnly = isReadOnly();
      sheet.insertColumn(state.actCol, colName, SpreadSheet.MISSING_VALUE);
      setModel(new SpreadSheetTableModel(sheet));
      setReadOnly(readOnly);
      setModified(true);
      ((SpreadSheetTableModel) getUnsortedModel()).fireTableDataChanged();
    });
    menu.add(menuitem);

    menuitem = new JMenuItem("Remove column", ImageManager.getIcon("delete-column.png"));
    menuitem.setEnabled(!isReadOnly());
    menuitem.addActionListener((ActionEvent ae) -> {
      SpreadSheet sheet = toSpreadSheet();
      boolean readOnly = isReadOnly();
      sheet.removeColumn(state.actCol);
      setModel(new SpreadSheetTableModel(sheet));
      setReadOnly(readOnly);
      setModified(true);
      ((SpreadSheetTableModel) getUnsortedModel()).fireTableDataChanged();
    });
    menu.add(menuitem);

    menu.addSeparator();

    menuitem = new JMenuItem("Optimal column width", ImageManager.getEmptyIcon());
    menuitem.addActionListener((ActionEvent ae) -> setOptimalColumnWidth(state.selCol));
    menu.add(menuitem);

    menuitem = new JMenuItem("Optimal column widths", ImageManager.getEmptyIcon());
    menuitem.addActionListener((ActionEvent ae) -> setOptimalColumnWidth());
    menu.add(menuitem);

    menuitem = new JMenuItem("Set column width...", ImageManager.getEmptyIcon());
    menuitem.addActionListener((ActionEvent ae) -> setColumnWidth(state.selCol));
    menu.add(menuitem);

    menuitem = new JMenuItem("Set column widths...", ImageManager.getEmptyIcon());
    menuitem.addActionListener((ActionEvent ae) -> setColumnWidths());
    menu.add(menuitem);

    SpreadSheetTablePopupMenuItemHelper.addToPopupMenu(state, menu, false);

    if (m_HeaderPopupMenuCustomizer != null)
      m_HeaderPopupMenuCustomizer.customizePopupMenu(e, menu);

    menu.showAbsolute(getTableHeader(), e);
  }

  /**
   * Shows a popup menu for the cells.
   *
   * @param e		the event that triggered the menu
   */
  protected void showCellPopupMenu(final MouseEvent e) {
    BasePopupMenu   menu;

    menu = createCellPopupMenu(e);
    menu.showAbsolute(this, e);
  }

  /**
   * Creates a popup menu for the cells.
   *
   * @param e		the event that triggered the menu
   * @return		the menu
   */
  protected BasePopupMenu createCellPopupMenu(final MouseEvent e) {
    BasePopupMenu	menu;
    JMenuItem		menuitem;
    final TableRowRange	range;
    final TableState	state;

    menu  = new BasePopupMenu();
    if (getSelectedRows().length > 0)
      range = TableRowRange.SELECTED;
    else if (isAnyColumnFiltered() || ((getSeachString() != null) && !getSeachString().isEmpty()))
      range = TableRowRange.VISIBLE;
    else
      range = TableRowRange.ALL;
    state = SpreadSheetTablePopupMenuItemHelper.getState(this, e, range);

    if (getSelectedRowCount() > 1)
      menuitem = new JMenuItem("Copy rows");
    else
      menuitem = new JMenuItem("Copy row");
    menuitem.setIcon(ImageManager.getIcon("copy_row.gif"));
    menuitem.setEnabled(getSelectedRowCount() > 0);
    menuitem.addActionListener((ActionEvent ae) -> copyToClipboard());
    menu.add(menuitem);

    menuitem = new JMenuItem("Copy cell");
    menuitem.setIcon(ImageManager.getIcon("copy_cell.gif"));
    menuitem.setEnabled(getSelectedRowCount() == 1);
    menuitem.addActionListener((ActionEvent ae) -> {
      if (state.selRow == -1)
	return;
      if (state.selCol == -1)
	return;
      ClipboardHelper.copyToClipboard("" + getValueAt(state.selRow, state.selCol));
    });
    menu.add(menuitem);

    menu.addSeparator();

    menuitem = new JMenuItem("Insert row", ImageManager.getIcon("insert-row.png"));
    menuitem.setEnabled(!isReadOnly());
    menuitem.addActionListener((ActionEvent ae) -> {
      SpreadSheet sheet = toSpreadSheet();
      boolean readOnly = isReadOnly();
      sheet.insertRow(state.actRow);
      setModel(new SpreadSheetTableModel(sheet));
      setReadOnly(readOnly);
      setModified(true);
      ((SpreadSheetTableModel) getUnsortedModel()).fireTableDataChanged();
    });
    menu.add(menuitem);

    menuitem = new JMenuItem("Remove row", ImageManager.getIcon("delete-row.png"));
    menuitem.setEnabled(!isReadOnly());
    menuitem.addActionListener((ActionEvent ae) -> {
      SpreadSheet sheet = toSpreadSheet();
      boolean readOnly = isReadOnly();
      for (int i = state.actRows.length - 1; i >= 0; i--)
	sheet.removeRow(state.actRows[i]);
      setModel(new SpreadSheetTableModel(sheet));
      setReadOnly(readOnly);
      setModified(true);
      ((SpreadSheetTableModel) getUnsortedModel()).fireTableDataChanged();
    });
    menu.add(menuitem);

    menu.addSeparator();

    menuitem = new JMenuItem("Select all");
    menuitem.setIcon(ImageManager.getEmptyIcon());
    menuitem.addActionListener((ActionEvent ae) -> {
      selectAll();
    });
    menu.add(menuitem);

    menuitem = new JMenuItem("Select none");
    menuitem.setIcon(ImageManager.getEmptyIcon());
    menuitem.addActionListener((ActionEvent ae) -> {
      clearSelection();
    });
    menu.add(menuitem);

    menuitem = new JMenuItem("Invert selection");
    menuitem.setIcon(ImageManager.getEmptyIcon());
    menuitem.addActionListener((ActionEvent ae) -> {
      invertRowSelection();
    });
    menu.add(menuitem);

    menu.addSeparator();

    if (range == TableRowRange.ALL)
      menuitem = new JMenuItem("Save...");
    else if (range == TableRowRange.SELECTED)
      menuitem = new JMenuItem("Save selected...");
    else
      menuitem = new JMenuItem("Save visible...");
    menuitem.setIcon(ImageManager.getIcon("save.gif"));
    menuitem.addActionListener((ActionEvent ae) -> saveAs(range));
    menu.add(menuitem);

    SendToActionUtils.addSendToSubmenu(this, menu);

    SpreadSheetTablePopupMenuItemHelper.addToPopupMenu(state, menu, true);

    menu.addSeparator();

    menuitem = new JCheckBoxMenuItem("Show formulas");
    menuitem.setEnabled(getRowCount() > 0);
    menuitem.setSelected(getShowFormulas());
    menuitem.addActionListener((ActionEvent ae) -> setShowFormulas(!getShowFormulas()));
    menu.add(menuitem);

    menuitem = new JCheckBoxMenuItem("Show cell types");
    menuitem.setEnabled(getRowCount() > 0);
    menuitem.setSelected(getShowCellTypes());
    menuitem.addActionListener((ActionEvent ae) -> setShowCellTypes(!getShowCellTypes()));
    menu.add(menuitem);

    menuitem = new JMenuItem("Set number of decimals");
    menuitem.setIcon(ImageManager.getIcon("decimal-place.png"));
    menuitem.setEnabled(getRowCount() > 0);
    menuitem.addActionListener((ActionEvent ae) -> enterNumDecimals());
    menu.add(menuitem);

    if (m_CellPopupMenuCustomizer != null)
      m_CellPopupMenuCustomizer.customizePopupMenu(e, menu);

    return menu;
  }

  /**
   * Sets the popup menu customizer to use (for the header).
   *
   * @param value	the customizer, null to remove it
   */
  public void setHeaderPopupMenuCustomizer(PopupMenuCustomizer value) {
    m_HeaderPopupMenuCustomizer = value;
  }

  /**
   * Returns the current popup menu customizer (for the header).
   *
   * @return		the customizer, null if none set
   */
  public PopupMenuCustomizer getHeaderPopupMenuCustomizer() {
    return m_HeaderPopupMenuCustomizer;
  }

  /**
   * Sets the popup menu customizer to use (for the cells).
   *
   * @param value	the customizer, null to remove it
   */
  public void setCellPopupMenuCustomizer(PopupMenuCustomizer value) {
    m_CellPopupMenuCustomizer = value;
  }

  /**
   * Returns the current popup menu customizer (for the cells).
   *
   * @return		the customizer, null if none set
   */
  public PopupMenuCustomizer getCellPopupMenuCustomizer() {
    return m_CellPopupMenuCustomizer;
  }

  /**
   * Sets the cell rendering customizer.
   *
   * @param value	the customizer
   */
  public void setCellRenderingCustomizer(CellRenderingCustomizer value) {
    ((SpreadSheetTableModel) getUnsortedModel()).setCellRenderingCustomizer(value);
  }

  /**
   * Returns the cell rendering customizer.
   *
   * @return		the customizer
   */
  public CellRenderingCustomizer getCellRenderingCustomizer() {
    return ((SpreadSheetTableModel) getUnsortedModel()).getCellRenderingCustomizer();
  }

  /**
   * Sets whether to display the formulas or their calculated values.
   *
   * @param value	true if to display the formulas rather than the calculated values
   */
  public void setShowFormulas(boolean value) {
    ((SpreadSheetTableModel) getUnsortedModel()).setShowFormulas(value);
  }

  /**
   * Returns whether to display the formulas or their calculated values.
   *
   * @return		true if to display the formulas rather than the calculated values
   */
  public boolean getShowFormulas() {
    return ((SpreadSheetTableModel) getUnsortedModel()).getShowFormulas();
  }

  /**
   * Whether to display the column with the row numbers.
   * 
   * @param value	true if to display column
   */
  public void setShowRowColumn(boolean value) {
    ((SpreadSheetTableModel) getUnsortedModel()).setShowRowColumn(value);
  }
  
  /**
   * Returns whether the column with the row numbers is displayed.
   * 
   * @return		true if column displayed
   */
  public boolean getShowRowColumn() {
    return ((SpreadSheetTableModel) getUnsortedModel()).getShowRowColumn();
  }

  /**
   * Whether to display a simple header or an HTML one with the column indices.
   *
   * @param value	true if to display simple header
   */
  public void setUseSimpleHeader(boolean value) {
    ((SpreadSheetTableModel) getUnsortedModel()).setUseSimpleHeader(value);
  }

  /**
   * Returns whether to display a simple header or an HTML one with the column indices.
   *
   * @return		true if simple header displayed
   */
  public boolean getUseSimpleHeader() {
    return ((SpreadSheetTableModel) getUnsortedModel()).getUseSimpleHeader();
  }

  /**
   * Sets whether the table is read-only.
   *
   * @param value	true if read-only
   */
  public void setReadOnly(boolean value) {
    ((SpreadSheetTableModel) getUnsortedModel()).setReadOnly(value);
  }

  /**
   * Returns whether the table is read-only.
   *
   * @return		true if read-only
   */
  public boolean isReadOnly() {
    return ((SpreadSheetTableModel) getUnsortedModel()).isReadOnly();
  }

  /**
   * Sets whether the table has been modified.
   *
   * @param value	true if modified
   */
  public void setModified(boolean value) {
    ((SpreadSheetTableModel) getUnsortedModel()).setModified(value);
  }

  /**
   * Returns whether the table has been modified.
   *
   * @return		true if modified
   */
  public boolean isModified() {
    return ((SpreadSheetTableModel) getUnsortedModel()).isModified();
  }

  /**
   * Sets whether to show the cell types rather than the cell values.
   *
   * @param value	true if to show cell types
   */
  public void setShowCellTypes(boolean value) {
    ((SpreadSheetTableModel) getUnsortedModel()).setShowCellTypes(value);
  }

  /**
   * Returns whether to show the cell types rather than the cell values.
   *
   * @return		true if showing the cell types
   */
  public boolean getShowCellTypes() {
    return ((SpreadSheetTableModel) getUnsortedModel()).getShowCellTypes();
  }

  /**
   * Sorts the spreadsheet with the given comparator.
   *
   * @param comparator	the row comparator to use
   */
  public void sort(RowComparator comparator) {
    toSpreadSheet().sort(comparator);
    ((SpreadSheetTableModel) getUnsortedModel()).fireTableDataChanged();
  }

  /**
   * Prompts the user to enter the number of displayed decimals
   */
  protected void enterNumDecimals() {
    String	decimals;
    int		num;

    decimals = GUIHelper.showInputDialog(
      GUIHelper.getParentComponent(this), "Please enter number of decimals:", "" + getNumDecimals());

    if (decimals == null)
      return;

    try {
      num = Integer.parseInt(decimals);
      setNumDecimals(num);
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(
	GUIHelper.getParentComponent(this), "Not a valid number: " + decimals);
    }
  }


  /**
   * Generates a key for the HashMap used for the last setups.
   *
   * @param cls       the scheme
   * @param plot      plot or process
   * @param row       row or column
   * @return          the generated key
   */
  protected String createLastSetupKey(Class cls, boolean plot, boolean row) {
    return cls.getName() + "-" + (plot ? "plot" : "process") + "-" + (row ? "row" : "column");
  }

  /**
   * Stores this last setup.
   *
   * @param cls       the scheme
   * @param plot      plot or process
   * @param row       row or column
   * @param setup     the setup to add
   */
  public void addLastSetup(Class cls, boolean plot, boolean row, Object setup) {
    m_LastSetup.put(createLastSetupKey(cls, plot, row), setup);
  }

  /**
   * Returns any last setup if available.
   *
   * @param cls       the scheme
   * @param plot      plot or process
   * @param row       row or column
   * @return          the last setup or null if none stored
   */
  public Object getLastSetup(Class cls, boolean plot, boolean row) {
    return m_LastSetup.get(createLastSetupKey(cls, plot, row));
  }

  /**
   * Returns the underlying sheet.
   *
   * @return		the spread sheet
   */
  @Override
  protected SpreadSheet modelToSpreadSheet() {
    return ((SpreadSheetTableModel) getUnsortedModel()).toSpreadSheet();
  }

  /**
   * Returns the classes that the supporter generates.
   *
   * @return		the classes
   */
  @Override
  public Class[] getSendToClasses() {
    return new Class[]{JTable.class};
  }

  /**
   * Checks whether something to send is available.
   *
   * @param cls		the classes to retrieve the item for
   * @return		true if an object is available for sending
   */
  @Override
  public boolean hasSendToItem(Class[] cls) {
    return (SendToActionUtils.isAvailable(new Class[]{JTable.class}, cls));
  }

  /**
   * Returns the object to send.
   *
   * @param cls		the classes to retrieve the item for
   * @return		the item to send
   */
  @Override
  public Object getSendToItem(Class[] cls) {
    Object	result;

    result = null;

    if (SendToActionUtils.isAvailable(JTable.class, cls))
      result = this;

    return result;
  }
}