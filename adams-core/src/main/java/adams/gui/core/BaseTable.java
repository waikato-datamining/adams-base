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
 * BaseTable.java
 * Copyright (C) 2009-2019 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

import adams.core.Range;
import adams.core.Utils;
import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetSupporter;
import adams.data.spreadsheet.SpreadSheetView;
import adams.gui.chooser.SpreadSheetFileChooser;
import adams.gui.event.PopupMenuListener;
import adams.gui.event.RemoveItemsEvent;
import adams.gui.event.RemoveItemsListener;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.HashSet;
import java.util.Vector;

/**
 * A specialized JTable that allows double-clicking on header for resizing to
 * optimal width.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class BaseTable
  extends JTable
  implements SpreadSheetSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -2360462659067336490L;

  /**
   * Enumeration of possible ways to calculate column width approach.
   */
  public enum ColumnWidthApproach {
    NONE,
    OPTIMAL,
    ADAPTIVE,
  }

  /** for setting the optimal column width. */
  protected JTableHelper m_TableHelper;

  /** the listeners for items to be removed. */
  protected HashSet<RemoveItemsListener> m_RemoveItemsListeners;

  /** the popup menu listeners for the header row. */
  protected HashSet<PopupMenuListener> m_HeaderPopupMenuListeners;

  /** the popup menu listeners for the cells. */
  protected HashSet<PopupMenuListener> m_CellPopupMenuListeners;

  /** whether to show a simple header popup menu. */
  protected boolean m_ShowSimpleHeaderPopupMenu;

  /** whether to show a simple cell popup menu. */
  protected boolean m_ShowSimpleCellPopupMenu;

  /** the simple header popup menu listener. */
  protected PopupMenuListener m_SimpleHeaderPopupMenuListener;

  /** the simple cell popup menu listener. */
  protected PopupMenuListener m_SimpleCellPopupMenuListener;

  /** the file chooser for saving the spreadsheet. */
  protected SpreadSheetFileChooser m_FileChooser;

  /** the maximum number of columns for optimal column width calculation. */
  protected int m_MaxColumnOptimalColumnWidthCalc;

  /** the maximum number of columns for optimal header width calculation. */
  protected int m_MaxColumnOptimalHeaderWidthCalc;

  /** the column width to use when too many columns present. */
  protected int m_TooManyColumnsDefaultWidth;

  /** whether to automatically set optimal column widths. */
  protected ColumnWidthApproach m_ColumnWidthApproach;

  /**
   * Constructs a default <code>BaseTable</code> that is initialized with a default
   * data model, a default column model, and a default selection
   * model.
   */
  public BaseTable() {
    super();
    initGUI();
    finishInit();
  }

  /**
   * Constructs a <code>BaseTable</code> with <code>numRows</code>
   * and <code>numColumns</code> of empty cells using
   * <code>DefaultTableModel</code>.  The columns will have
   * names of the form "A", "B", "C", etc.
   *
   * @param numRows           the number of rows the table holds
   * @param numColumns        the number of columns the table holds
   */
  public BaseTable(int numRows, int numColumns) {
    super(numRows, numColumns);
    initGUI();
    finishInit();
  }

  /**
   * Constructs a <code>BaseTable</code> to display the values in the two dimensional array,
   * <code>rowData</code>, with column names, <code>columnNames</code>.
   * <code>rowData</code> is an array of rows, so the value of the cell at row 1,
   * column 5 can be obtained with the following code:
   * <p>
   * <pre> rowData[1][5]; </pre>
   * <p>
   * All rows must be of the same length as <code>columnNames</code>.
   * <p>
   * @param rowData           the data for the new table
   * @param columnNames       names of each column
   */
  public BaseTable(final Object[][] rowData, final Object[] columnNames) {
    super(rowData, columnNames);
    initGUI();
    finishInit();
  }

  /**
   * Constructs a <code>BaseTable</code> to display the values in the
   * <code>Vector</code> of <code>Vectors</code>, <code>rowData</code>,
   * with column names, <code>columnNames</code>.  The
   * <code>Vectors</code> contained in <code>rowData</code>
   * should contain the values for that row. In other words,
   * the value of the cell at row 1, column 5 can be obtained
   * with the following code:
   * <p>
   * <pre>((Vector)rowData.elementAt(1)).elementAt(5);</pre>
   * <p>
   * @param rowData           the data for the new table
   * @param columnNames       names of each column
   */
  public BaseTable(Vector rowData, Vector columnNames) {
    super(rowData, columnNames);
    initGUI();
    finishInit();
  }

  /**
   * Constructs a <code>BaseTable</code> that is initialized with
   * <code>dm</code> as the data model, a default column model,
   * and a default selection model.
   *
   * @param dm        the data model for the table
   */
  public BaseTable(TableModel dm) {
    super(dm);
    initGUI();
    finishInit();
  }

  /**
   * Constructs a <code>BaseTable</code> that is initialized with
   * <code>dm</code> as the data model, <code>cm</code>
   * as the column model, and a default selection model.
   *
   * @param dm        the data model for the table
   * @param cm        the column model for the table
   */
  public BaseTable(TableModel dm, TableColumnModel cm) {
    super(dm, cm);
    initGUI();
    finishInit();
  }

  /**
   * Constructs a <code>BaseTable</code> that is initialized with
   * <code>dm</code> as the data model, <code>cm</code> as the
   * column model, and <code>sm</code> as the selection model.
   * If any of the parameters are <code>null</code> this method
   * will initialize the table with the corresponding default model.
   * The <code>autoCreateColumnsFromModel</code> flag is set to false
   * if <code>cm</code> is non-null, otherwise it is set to true
   * and the column model is populated with suitable
   * <code>TableColumns</code> for the columns in <code>dm</code>.
   *
   * @param dm        the data model for the table
   * @param cm        the column model for the table
   * @param sm        the row selection model for the table
   */
  public BaseTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
    super(dm, cm, sm);
    initGUI();
    finishInit();
  }

  /**
   * Returns the table helper instance. Instantiates it if necessary.
   *
   * @return		the table helper
   */
  protected JTableHelper getTableHelper() {
    if (m_TableHelper == null)
      m_TableHelper = new JTableHelper(this);

    return m_TableHelper;
  }

  /**
   * Initializes some GUI-related things.
   */
  protected void initGUI() {
    m_RemoveItemsListeners            = new HashSet<>();
    m_HeaderPopupMenuListeners        = new HashSet<>();
    m_CellPopupMenuListeners          = new HashSet<>();
    m_ShowSimpleHeaderPopupMenu       = false;
    m_ShowSimpleCellPopupMenu         = false;
    m_MaxColumnOptimalColumnWidthCalc = 100;
    m_MaxColumnOptimalHeaderWidthCalc = 1000;
    m_TooManyColumnsDefaultWidth      = 100;
    m_ColumnWidthApproach             = initialUseOptimalColumnWidths();

    m_SimpleHeaderPopupMenuListener = (MouseEvent e) -> {
      if (m_ShowSimpleHeaderPopupMenu) {
        showSimpleHeaderPopupMenu(e);
      }
    };

    m_SimpleCellPopupMenuListener = (MouseEvent e) -> {
      if (m_ShowSimpleCellPopupMenu) {
        showSimpleCellPopupMenu(e);
      }
    };

    getTableHeader().addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
	// just the selecte column
	if (MouseUtils.isDoubleClick(e) && e.isControlDown() && !e.isAltDown() && !e.isShiftDown()) {
	  final int col = columnAtPoint(e.getPoint());
	  if ((col != -1) && isVisible())
	    SwingUtilities.invokeLater(() -> getTableHelper().setOptimalColumnWidth(col));
	}
	// all columns
	else if (MouseUtils.isDoubleClick(e) && e.isControlDown() && !e.isAltDown() && e.isShiftDown()) {
	  if (isVisible()) {
	    SwingUtilities.invokeLater(() -> getTableHelper().setOptimalColumnWidth());
	  }
	}
	else if (MouseUtils.isRightClick(e)) {
	  notifyHeaderPopupMenuListener(e);
	}
	else {
	  super.mouseClicked(e);
	}
      }
    });
    
    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
	if (MouseUtils.isRightClick(e)) {
	  notifyCellPopupMenuListener(e);
	}
	else {
	  super.mouseClicked(e);
	}
      }
    });

    addKeyListener(new KeyListener() {
      public void keyTyped(KeyEvent e) {
	// ignored
      }
      public void keyReleased(KeyEvent e) {
	// ignored
      }
      public void keyPressed(KeyEvent e) {
	if (m_RemoveItemsListeners.size() > 0) {
	  if ((e.getKeyCode() == KeyEvent.VK_DELETE) && (e.getModifiers() == 0)) {
	    e.consume();
	    notifyRemoveItemsListeners(getSelectedRows());
	  }
	}
      }
    });
  }

  /**
   * Finishes the initialization.
   */
  protected void finishInit() {
    setColumnWidthApproach(m_ColumnWidthApproach);
  }

  /**
   * Returns the initial setting of whether to set optimal column widths.
   * Default implementation returns "false", since large tables might take too
   * long to be displayed otherwise.
   *
   * @return		true if optimal column widths are used by default
   */
  protected ColumnWidthApproach initialUseOptimalColumnWidths() {
    return ColumnWidthApproach.NONE;
  }

  /**
   * Sets whether to automatically set optimal column widths.
   *
   * @param value	if true then optimal column widths are used
   */
  public void setColumnWidthApproach(ColumnWidthApproach value) {
    m_ColumnWidthApproach = value;
    switch (m_ColumnWidthApproach) {
      case NONE:
        // do nothing
	break;
      case OPTIMAL:
	setAutoResizeMode(AUTO_RESIZE_OFF);
	setOptimalColumnWidth();
        break;
      case ADAPTIVE:
	setAutoResizeMode(AUTO_RESIZE_OFF);
	adaptiveOptimalColumnWidth();
        break;
    }
  }

  /**
   * Returns how to set the optimal column widths.
   * Default implementation is initialized with "NONE".
   *
   * @return		the optimal column widths strategy
   */
  public ColumnWidthApproach getColumnWidthApproach() {
    return m_ColumnWidthApproach;
  }

  /**
   * Sets the selected row (clears all others).
   *
   * @param row		the row to select
   */
  public void setSelectedRow(int row) {
    setSelectedRows(new int[]{row});
  }

  /**
   * Sets the selected rows (clears all others).
   *
   * @param rows	the rows to select
   */
  public void setSelectedRows(int[] rows) {
    Range	range;
    int[][]	segs;

    range = new Range();
    range.setMax(getRowCount());
    range.setIndices(rows);
    segs = range.getIntSegments();
    getSelectionModel().clearSelection();
    for (int[] seg: segs)
      getSelectionModel().addSelectionInterval(seg[0], seg[1]);
  }

  /**
   * Sets the maximum number of columns before no longer attempting to calculate
   * the optimal column width.
   *
   * @param value	the maximum number of columns (incl)
   */
  public void setMaxColumnOptimalColumnWidthCalc(int value) {
    m_MaxColumnOptimalColumnWidthCalc = value;
  }

  /**
   * Returns the maximum number of columns before no longer attempting to calculate
   * the optimal column width.
   *
   * @return		the maximum number of columns (incl)
   * @see		#adaptiveOptimalColumnWidth()
   */
  public int getMaxColumnOptimalColumnWidthCalc() {
    return m_MaxColumnOptimalColumnWidthCalc;
  }

  /**
   * Sets the maximum number of columns before no longer attempting to calculate
   * the optimal header width.
   *
   * @param value	the maximum number of columns (incl)
   * @see		#adaptiveOptimalColumnWidth()
   */
  public void setMaxColumnOptimalHeaderWidthCalc(int value) {
    m_MaxColumnOptimalHeaderWidthCalc = value;
  }

  /**
   * Returns the maximum number of columns before no longer attempting to calculate
   * the optimal header width.
   *
   * @return		the maximum number of columns (incl)
   * @see		#adaptiveOptimalColumnWidth()
   */
  public int getMaxColumnOptimalHeaderWidthCalc() {
    return m_MaxColumnOptimalHeaderWidthCalc;
  }

  /**
   * Sets the default width to use if there are too many column to neither
   * calculate the optimal column or header width.
   *
   * @param value	the default width
   * @see		#adaptiveOptimalColumnWidth()
   */
  public void setTooManyColumnsDefaultWidth(int value) {
    m_TooManyColumnsDefaultWidth = value;
  }

  /**
   * Returns the default width to use if there are too many column to neither
   * calculate the optimal column or header width.
   *
   * @return		the default width
   * @see		#adaptiveOptimalColumnWidth()
   */
  public int getTooManyColumnsDefaultWidth() {
    return m_TooManyColumnsDefaultWidth;
  }

  /**
   * Adaptive way of setting the optimal column width for all columns. AutoResize must be set
   * to BaseTable.AUTO_RESIZE_OFF.
   *
   * Approach:
   * If less or equal to {@link #m_MaxColumnOptimalColumnWidthCalc}, then calculate optimal column width.
   * If less or equal to {@link #m_MaxColumnOptimalHeaderWidthCalc}, then calculate optimal header width.
   * Otherwise set the width of all columns to {@link #m_TooManyColumnsDefaultWidth}.
   */
  public void adaptiveOptimalColumnWidth() {
    if (getColumnCount() <= m_MaxColumnOptimalColumnWidthCalc)
      setOptimalColumnWidth();
    else if (getColumnCount() <= m_MaxColumnOptimalHeaderWidthCalc)
      setOptimalHeaderWidth();
    else
      setColumnWidths(m_TooManyColumnsDefaultWidth);
  }

  /**
   * Sets the optimal column width for all columns. AutoResize must be set
   * to BaseTable.AUTO_RESIZE_OFF.
   */
  public void setOptimalColumnWidth() {
    if (isVisible())
      SwingUtilities.invokeLater(() -> getTableHelper().setOptimalColumnWidth());
  }

  /**
   * Sets the optimal column width for all columns. AutoResize must be set
   * to BaseTable.AUTO_RESIZE_OFF.
   *
   * @param max		the maximum width
   */
  public void setOptimalColumnWidthBounded(final int max) {
    if (isVisible())
      SwingUtilities.invokeLater(() -> getTableHelper().setOptimalColumnWidthBounded(max));
  }

  /**
   * Sets the optimal column width for the specified column. AutoResize must be set
   * to BaseTable.AUTO_RESIZE_OFF.
   *
   * @param column	the column to resize
   */
  public void setOptimalColumnWidth(final int column) {
    if (isVisible())
      SwingUtilities.invokeLater(() -> getTableHelper().setOptimalColumnWidth(column));
  }

  /**
   * Sets the optimal column width for the specified column. AutoResize must be set
   * to BaseTable.AUTO_RESIZE_OFF.
   *
   * @param column	the column to resize
   * @param max		the maximum width
   */
  public void setOptimalColumnWidthBounded(final int column, final int max) {
    if (isVisible())
      SwingUtilities.invokeLater(() -> getTableHelper().setOptimalColumnWidthBounded(column, max));
  }

  /**
   * Sets the optimal header width for all columns. AutoResize must be set
   * to BaseTable.AUTO_RESIZE_OFF.
   */
  public void setOptimalHeaderWidth() {
    if (isVisible())
      SwingUtilities.invokeLater(() -> getTableHelper().setOptimalHeaderWidth());
  }

  /**
   * Sets the optimal header width for the specified column. AutoResize must be set
   * to BaseTable.AUTO_RESIZE_OFF.
   *
   * @param column	the column to resize
   */
  public void setOptimalHeaderWidth(final int column) {
    if (isVisible())
      SwingUtilities.invokeLater(() -> getTableHelper().setOptimalHeaderWidth(column));
  }

  /**
   * Prompts the user to enter a column width.
   *
   * @return		the column width, -1 if not to proceed
   */
  protected int enterColumnWidth() {
    String	widthStr;
    final int	width;

    widthStr = GUIHelper.showInputDialog(getParent(), "Please enter column width (pixels)");
    if (widthStr == null)
      return -1;
    if (!Utils.isInteger(widthStr)) {
      GUIHelper.showErrorMessage(getParent(), "Entered width is not a number: " + widthStr);
      return - 1;
    }
    width = Integer.parseInt(widthStr);
    if (width < 1) {
      GUIHelper.showErrorMessage(getParent(), "Entered width is less than 1: " + widthStr);
      return - 1;
    }
    return width;
  }

  /**
   * Prompts the user for a col width value and sets the column width for all
   * columns. AutoResize must be set to BaseTable.AUTO_RESIZE_OFF.
   */
  public void setColumnWidths() {
    setColumnWidths(enterColumnWidth());
  }

  /**
   * Sets the specified column width for all columns.
   * AutoResize must be set to BaseTable.AUTO_RESIZE_OFF.
   *
   * @param width 	the width for all columns
   */
  public void setColumnWidths(int width) {
    if (width < 1)
      return;
    SwingUtilities.invokeLater(() -> {
      JTableHeader header = getTableHeader();
      for (int i = 0; i < getColumnModel().getColumnCount(); i++) {
	TableColumn column = getColumnModel().getColumn(i);
	column.setPreferredWidth(width);
      }
      doLayout();
      header.repaint();
    });
  }

  /**
   * Prompts the user for a col width value and sets the column width
   * for the specified column. AutoResize must be set to BaseTable.AUTO_RESIZE_OFF.
   *
   * @param column	the column to resize
   */
  public void setColumnWidth(final int column) {
    setColumnWidth(column, enterColumnWidth());
  }

  /**
   * Sets the specified column width for the specified column.
   * AutoResize must be set to BaseTable.AUTO_RESIZE_OFF.
   *
   * @param column	the column to resize
   * @param width 	the width to use
   */
  public void setColumnWidth(final int column, int width) {
    if (width < 1)
      return;
    SwingUtilities.invokeLater(() -> {
      JTableHeader header = getTableHeader();
      TableColumn col = getColumnModel().getColumn(column);
      col.setPreferredWidth(width);
      doLayout();
      header.repaint();
    });
  }

  /**
   * Scrolls the row into view.
   *
   * @param row		the row to scroll into view
   */
  public void scrollRowToVisible(int row) {
    scrollRectToVisible(getCellRect(row, 0, true));
  }

  /**
   * Scrolls the column into view.
   *
   * @param col		the column to scroll into view
   */
  public void scrollColumnToVisible(int col) {
    scrollRectToVisible(getCellRect(0, col, true));
  }

  /**
   * Adds the remove items listener to its internal list.
   *
   * @param l		the listener to add
   */
  public void addRemoveItemsListener(RemoveItemsListener l) {
    m_RemoveItemsListeners.add(l);
  }

  /**
   * Removes the remove items listener from its internal list.
   *
   * @param l		the listener to remove
   */
  public void removeRemoveItemsListener(RemoveItemsListener l) {
    m_RemoveItemsListeners.remove(l);
  }

  /**
   * Notifies the remove items listeners about the indices that are to be
   * removed.
   *
   * @param indices	the indices that should get removed
   */
  protected void notifyRemoveItemsListeners(int[] indices) {
    RemoveItemsEvent	event;

    event = new RemoveItemsEvent(this, indices);
    for (RemoveItemsListener l: m_RemoveItemsListeners)
      l.removeItems(event);
  }

  /**
   * Adds the popup menu listeners for the header.
   *
   * @param l		the listener to add
   */
  public void addHeaderPopupMenuListener(PopupMenuListener l) {
    m_HeaderPopupMenuListeners.add(l);
  }

  /**
   * Removes the popup menu listener for the header from its internal list.
   *
   * @param l		the listener to remove
   */
  public void removeHeaderPopupMenuListener(PopupMenuListener l) {
    m_HeaderPopupMenuListeners.remove(l);
  }

  /**
   * Notifies the popup menu listeners (header).
   * 
   * @param e		the mouse event
   */
  protected void notifyHeaderPopupMenuListener(MouseEvent e) {
    for (PopupMenuListener l: m_HeaderPopupMenuListeners)
      l.showPopupMenu(e);
  }

  /**
   * Adds the popup menu listeners for the cell.
   *
   * @param l		the listener to add
   */
  public void addCellPopupMenuListener(PopupMenuListener l) {
    m_CellPopupMenuListeners.add(l);
  }

  /**
   * Removes the popup menu listener for the cell from its internal list.
   *
   * @param l		the listener to remove
   */
  public void removeCellPopupMenuListener(PopupMenuListener l) {
    m_CellPopupMenuListeners.remove(l);
  }

  /**
   * Notifies the popup menu listeners (cell).
   * 
   * @param e		the mouse event
   */
  protected void notifyCellPopupMenuListener(MouseEvent e) {
    for (PopupMenuListener l: m_CellPopupMenuListeners)
      l.showPopupMenu(e);
  }

  /**
   * Copies either the selected rows to the clipboard.
   */
  public void copyToClipboard() {
    Action 	copy;
    ActionEvent	event;

    copy  = getActionMap().get("copy");
    event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "");
    copy.actionPerformed(event);  
  }

  /**
   * Displays the specified cell.
   * 
   * @param row		the row of the cell
   * @param column	the column of the cell
   */
  public void showCell(int row, int column) {
    scrollRectToVisible(getCellRect(row, column, true));
  }

  /**
   * Sets whether to show a simple header/cell popup menu.
   *
   * @param value	true if to show menus
   */
  public void setShowSimplePopupMenus(boolean value) {
    setShowSimpleHeaderPopupMenu(value);
    setShowSimpleCellPopupMenu(value);
  }

  /**
   * Sets whether to show a simple header popup menu.
   *
   * @param value	true if to show menu
   */
  public void setShowSimpleHeaderPopupMenu(boolean value) {
    m_ShowSimpleHeaderPopupMenu = value;
    removeHeaderPopupMenuListener(m_SimpleHeaderPopupMenuListener);
    if (value)
      addHeaderPopupMenuListener(m_SimpleHeaderPopupMenuListener);
  }

  /**
   * Returns whether to show a simple header popup menu.
   *
   * @return		true if to show menu
   */
  public boolean getShowSimpleHeaderPopupMenu() {
    return m_ShowSimpleHeaderPopupMenu;
  }

  /**
   * Pops up a simple header menu.
   *
   * @param e		the trigger event
   */
  protected void showSimpleHeaderPopupMenu(MouseEvent e) {
    BasePopupMenu menu;

    menu = createSimpleHeaderPopupMenu(e);
    menu.showAbsolute(this, e);
  }

  /**
   * Creates a simple header popup menu.
   *
   * @param e		the trigger event
   * @return		the popup menu
   */
  protected BasePopupMenu createSimpleHeaderPopupMenu(MouseEvent e) {
    BasePopupMenu 	menu;
    JMenuItem 		menuitem;
    final int 		col;

    menu = new BasePopupMenu();
    col = columnAtPoint(e.getPoint());

    menuitem = new JMenuItem("Copy column name", GUIHelper.getIcon("copy.gif"));
    menuitem.addActionListener((ActionEvent ae) -> ClipboardHelper.copyToClipboard(getColumnName(col)));
    menu.add(menuitem);

    menuitem = new JMenuItem("Copy column", GUIHelper.getIcon("copy_column.gif"));
    menuitem.addActionListener((ActionEvent ae) -> {
      SpreadSheet sheet = toSpreadSheet(TableRowRange.VISIBLE);
      StringBuilder content = new StringBuilder();
      String sep = System.getProperty("line.separator");
      content.append(sheet.getColumnName(col) + sep);
      for (int i = 0; i < sheet.getRowCount(); i++) {
	if (!sheet.hasCell(i, col) || sheet.getCell(i, col).isMissing())
	  content.append(sep);
	else
	  content.append(sheet.getCell(i, col).getContent() + sep);
      }
      ClipboardHelper.copyToClipboard(content.toString());
    });
    menu.add(menuitem);

    menu.addSeparator();

    menuitem = new JMenuItem("Optimal column width", GUIHelper.getEmptyIcon());
    menuitem.addActionListener((ActionEvent ae) -> setOptimalColumnWidth(col));
    menu.add(menuitem);

    menuitem = new JMenuItem("Optimal column widths", GUIHelper.getEmptyIcon());
    menuitem.addActionListener((ActionEvent ae) -> setOptimalColumnWidth());
    menu.add(menuitem);

    menuitem = new JMenuItem("Set column width...", GUIHelper.getEmptyIcon());
    menuitem.addActionListener((ActionEvent ae) -> setColumnWidth(col));
    menu.add(menuitem);

    menuitem = new JMenuItem("Set column widths...", GUIHelper.getEmptyIcon());
    menuitem.addActionListener((ActionEvent ae) -> setColumnWidths());
    menu.add(menuitem);

    return menu;
  }

  /**
   * Sets whether to show a simple cell popup menu.
   *
   * @param value	true if to show menu
   */
  public void setShowSimpleCellPopupMenu(boolean value) {
    m_ShowSimpleCellPopupMenu = value;
    removeCellPopupMenuListener(m_SimpleCellPopupMenuListener);
    if (value)
      addCellPopupMenuListener(m_SimpleCellPopupMenuListener);
  }

  /**
   * Returns whether to show a simple cell popup menu.
   *
   * @return		true if to show menu
   */
  public boolean getShowSimpleCellPopupMenu() {
    return m_ShowSimpleCellPopupMenu;
  }

  /**
   * Pops up a simple cell menu.
   *
   * @param e		the trigger event
   */
  protected void showSimpleCellPopupMenu(MouseEvent e) {
    BasePopupMenu menu;

    menu = createSimpleCellPopupMenu(e);
    menu.showAbsolute(this, e);
  }

  /**
   * Creates a simple cell popup menu.
   *
   * @param e		the trigger event
   * @return		the popup menu
   */
  protected BasePopupMenu createSimpleCellPopupMenu(MouseEvent e) {
    BasePopupMenu	menu;
    JMenu		submenu;
    JMenuItem		menuitem;
    final int		row;
    final int		col;

    menu = new BasePopupMenu();
    row  = rowAtPoint(e.getPoint());
    col  = columnAtPoint(e.getPoint());

    menuitem = new JMenuItem("Select all");
    menuitem.setIcon(GUIHelper.getEmptyIcon());
    menuitem.addActionListener((ActionEvent ae) -> {
      selectAll();
    });
    menu.add(menuitem);

    menuitem = new JMenuItem("Select none");
    menuitem.setIcon(GUIHelper.getEmptyIcon());
    menuitem.addActionListener((ActionEvent ae) -> {
      clearSelection();
    });
    menu.add(menuitem);

    menuitem = new JMenuItem("Invert selection");
    menuitem.setIcon(GUIHelper.getEmptyIcon());
    menuitem.addActionListener((ActionEvent ae) -> {
      invertRowSelection();
    });
    menu.add(menuitem);

    menu.addSeparator();

    if (getSelectedRowCount() > 1)
      menuitem = new JMenuItem("Copy rows");
    else
      menuitem = new JMenuItem("Copy row");
    menuitem.setIcon(GUIHelper.getIcon("copy_row.gif"));
    menuitem.setEnabled(getSelectedRowCount() > 0);
    menuitem.addActionListener((ActionEvent ae) -> copyToClipboard());
    menu.add(menuitem);

    menuitem = new JMenuItem("Copy cell");
    menuitem.setIcon(GUIHelper.getIcon("copy_cell.gif"));
    menuitem.setEnabled(getSelectedRowCount() == 1);
    menuitem.addActionListener((ActionEvent ae) -> {
      if (row == -1)
	return;
      if (col == -1)
	return;
      ClipboardHelper.copyToClipboard("" + getValueAt(row, col));
    });
    menu.add(menuitem);

    menuitem = new JMenuItem("Copy column", GUIHelper.getIcon("copy_column.gif"));
    menuitem.addActionListener((ActionEvent ae) -> {
      if (col == -1)
        return;
      String sep = System.getProperty("line.separator");
      StringBuilder content = new StringBuilder(getColumnName(col));
      for (int i = 0; i < getRowCount(); i++) {
        content.append(sep);
        Object value = getValueAt(i, col);
        if (value != null)
          content.append(value.toString());
        else
          content.append("");
      }
      ClipboardHelper.copyToClipboard(content.toString());
    });
    menu.add(menuitem);

    menuitem = new JMenuItem("Copy table", GUIHelper.getIcon("copy_table.gif"));
    menuitem.addActionListener((ActionEvent ae) -> ClipboardHelper.copyToClipboard(toSpreadSheet().toString()));
    menu.add(menuitem);

    menu.addSeparator();

    submenu = new JMenu("Save");
    submenu.setIcon(GUIHelper.getIcon("save.gif"));
    menu.add(submenu);

    menuitem = new JMenuItem("Save all...");
    menuitem.addActionListener((ActionEvent ae) -> saveAs(TableRowRange.ALL));
    submenu.add(menuitem);

    menuitem = new JMenuItem("Save selected...");
    menuitem.addActionListener((ActionEvent ae) -> saveAs(TableRowRange.SELECTED));
    submenu.add(menuitem);

    menuitem = new JMenuItem("Save visible...");
    menuitem.addActionListener((ActionEvent ae) -> saveAs(TableRowRange.VISIBLE));
    submenu.add(menuitem);

    return menu;
  }

  /**
   * Returns the underlying sheet.
   *
   * @return		the spread sheet
   */
  protected SpreadSheet modelToSpreadSheet() {
    SpreadSheet		result;
    Row			row;
    int			i;
    int			n;
    Object		value;

    if (getModel() instanceof SpreadSheetSupporter)
      return ((SpreadSheetSupporter) getModel()).toSpreadSheet();

    result = new DefaultSpreadSheet();

    // header
    row = result.getHeaderRow();
    for (i = 0; i < getColumnCount(); i++)
      row.addCell("" + i).setContentAsString(getColumnName(i));

    // data
    for (n = 0; n < getRowCount(); n++) {
      row = result.addRow();
      for (i = 0; i < getColumnCount(); i++) {
        value = getValueAt(n, i);
        if (value == null)
          row.addCell("" + i).setMissing();
        else
          row.addCell("" + i).setContent("" + value);
      }
    }

    return result;
  }

  /**
   * Determines the actual row index.
   *
   * @param index	the selected row
   * @return		the actual model row
   */
  protected int selectionRowToModelRow(int index) {
    return index;
  }

  /**
   * Returns the underlying sheet.
   *
   * @return		the spread sheet
   */
  @Override
  public SpreadSheet toSpreadSheet() {
    return toSpreadSheet(TableRowRange.ALL);
  }

  /**
   * Returns the underlying sheet.
   *
   * @param range	the type of rows to return
   * @return		the spread sheet
   */
  public SpreadSheet toSpreadSheet(TableRowRange range) {
    return toSpreadSheet(range, false);
  }

  /**
   * Returns the underlying sheet.
   *
   * @param range	the type of rows to return
   * @param view	whether to return only a view (ignored if {@link TableRowRange#ALL})
   * @return		the spread sheet
   */
  public SpreadSheet toSpreadSheet(TableRowRange range, boolean view) {
    SpreadSheet	result;
    SpreadSheet	full;
    int[] 	indices;
    int		i;

    full = modelToSpreadSheet();
    switch (range) {
      case ALL:
	result = full;
	break;
      case SELECTED:
	indices = getSelectedRows();
        for (i = 0; i < indices.length; i++)
          indices[i] = selectionRowToModelRow(indices[i]);
	if (view) {
	  result = new SpreadSheetView(full, indices, null);
	}
	else {
	  result = full.getHeader();
	  for (int index: indices)
	    result.addRow().assign(full.getRow(index));
	}
	break;
      case VISIBLE:
        indices = new int[getRowCount()];
        for (i = 0; i < getRowCount(); i++)
          indices[i] = selectionRowToModelRow(i);
	if (view) {
	  result = new SpreadSheetView(full, indices, null);
	}
	else {
	  result = full.getHeader();
	  for (int index: indices)
	    result.addRow().assign(full.getRow(index));
	}
	break;
      default:
	throw new IllegalStateException("Unhandled row range: " + range);
    }

    return result;
  }

  /**
   * Inverts the selected rows.
   */
  public void invertRowSelection() {
    Range	range;
    int[] 	indices;

    range = new Range();
    range.setMax(getRowCount());
    range.setIndices(getSelectedRows());
    range.setInverted(true);
    indices = range.getIntIndices();
    clearSelection();
    for (int index : indices)
      addRowSelectionInterval(index, index);
  }

    /**
     * Returns the index of the first selected row, -1 if no row is selected.
     * @return the index of the first selected row
     */
    public int getSelectedRow() {
      int	result;

      result = super.getSelectedRow();
      if (result > -1) {
	if (result >= getRowCount()) {
	  System.out.println("invalid: " + result);
	  result = -1;
	}
      }

      return result;
    }

  /**
   * Returns the indices of all selected rows.
   *
   * @return an array of integers containing the indices of all selected rows,
   *         or an empty array if no row is selected
   * @see #getSelectedRow
   */
  public int[] getSelectedRows() {
    TIntList	result;
    int		i;

    result = new TIntArrayList(super.getSelectedRows());
    i      = 0;
    while (i < result.size()) {
      if (result.get(i) >= getRowCount()) {
	System.out.println("invalid: " + result.get(i));
	result.removeAt(i);
      }
      else
	i++;
    }

    return result.toArray();
  }

  /**
   * Returns the column widths.
   *
   * @return		the current widths
   */
  public int[] getColumnWidths() {
    int[]	result;
    int		i;

    result = new int[getColumnModel().getColumnCount()];
    for (i = 0; i < getColumnModel().getColumnCount(); i++)
      result[i] = getColumnModel().getColumn(i).getWidth();

    return result;
  }

  /**
   * Sets the column widths.
   *
   * @param value	the widths
   */
  public void setColumnWidths(int[] value) {
    int		i;

    for (i = 0; i < value.length && i < getColumnModel().getColumnCount(); i++) {
      getColumnModel().getColumn(i).setWidth(value[i]);
      getColumnModel().getColumn(i).setPreferredWidth(value[i]);
    }
  }

  /**
   * Returns the filechooser for saving the table as spreadsheet.
   *
   * @return		the filechooser
   */
  protected synchronized SpreadSheetFileChooser getFileChooser() {
    if (m_FileChooser == null) {
      m_FileChooser = new SpreadSheetFileChooser();
      m_FileChooser.setMultiSelectionEnabled(false);
    }

    return m_FileChooser;
  }

  /**
   * Pops up a save dialog for saving the data to a file.
   *
   * @param range	the type of data to save
   */
  protected void saveAs(TableRowRange range) {
    int 		retVal;
    File file;
    SpreadSheetWriter 	writer;

    retVal = getFileChooser().showSaveDialog(GUIHelper.getParentComponent(this));
    if (retVal != SpreadSheetFileChooser.APPROVE_OPTION)
      return;
    file = getFileChooser().getSelectedFile();
    writer = getFileChooser().getWriter();
    if (!writer.write(toSpreadSheet(range), file)) {
      GUIHelper.showErrorMessage(
	  GUIHelper.getParentComponent(this),
	  "Failed to save spreadsheet to the following file:\n" + file);
    }
  }
}
