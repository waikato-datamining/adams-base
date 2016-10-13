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
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

import adams.core.Range;
import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetSupporter;
import adams.gui.chooser.SpreadSheetFileChooser;
import adams.gui.event.PopupMenuListener;
import adams.gui.event.RemoveItemsEvent;
import adams.gui.event.RemoveItemsListener;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Vector;

/**
 * A specialized JTable that allows double-clicking on header for resizing to
 * optimal width.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseTable
  extends JTable
  implements SpreadSheetSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -2360462659067336490L;

  /** for setting the optimal column width. */
  protected JTableHelper m_TableHelper;

  /** the listeners for items to be removed. */
  protected HashSet<RemoveItemsListener> m_RemoveItemsListeners;

  /** the popup menu listeners for the header row. */
  protected HashSet<PopupMenuListener> m_HeaderPopupMenuListeners;

  /** the popup menu listeners for the cells. */
  protected HashSet<PopupMenuListener> m_CellPopupMenuListeners;

  /** whether to show a simple cell popup menu. */
  protected boolean m_ShowSimpleCellPopupMenu;

  /** the simple cell popup menu listener. */
  protected PopupMenuListener m_SimpleCellPopupMenuListener;

  /**
   * Constructs a default <code>BaseTable</code> that is initialized with a default
   * data model, a default column model, and a default selection
   * model.
   */
  public BaseTable() {
    super();
    initGUI();
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
    m_RemoveItemsListeners     = new HashSet<>();
    m_HeaderPopupMenuListeners = new HashSet<>();
    m_CellPopupMenuListeners   = new HashSet<>();
    m_ShowSimpleCellPopupMenu  = false;

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
    JMenuItem		menuitem;
    final int		row;
    final int		col;

    menu = new BasePopupMenu();
    row  = rowAtPoint(e.getPoint());
    col  = columnAtPoint(e.getPoint());

    menuitem = new JMenuItem("Copy cell", GUIHelper.getIcon("copy_cell.gif"));
    menuitem.addActionListener((ActionEvent ae) -> {
      if ((row == -1) || (col == -1))
        return;
      Object value = getValueAt(row, col);
      if (value != null)
        ClipboardHelper.copyToClipboard("" + value);
    });
    menu.add(menuitem);

    menuitem = new JMenuItem("Copy row", GUIHelper.getIcon("copy_row.gif"));
    menuitem.addActionListener((ActionEvent ae) -> {
      if (row == -1)
        return;
      StringBuilder content = new StringBuilder();
      for (int i = 0; i < getColumnCount(); i++) {
        Object value = getValueAt(row, i);
        if (i > 0)
          content.append("\t");
        if (value != null)
          content.append(value.toString());
      }
      ClipboardHelper.copyToClipboard(content.toString());
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

    menuitem = new JMenuItem("Save table as...", GUIHelper.getIcon("save.gif"));
    menuitem.addActionListener((ActionEvent ae) -> {
      SpreadSheetFileChooser chooser = new SpreadSheetFileChooser();
      int retVal = chooser.showSaveDialog(BaseTable.this);
      if (retVal != SpreadSheetFileChooser.APPROVE_OPTION)
        return;
      SpreadSheetWriter writer = chooser.getWriter();
      if (!writer.write(toSpreadSheet(), chooser.getSelectedFile()))
        GUIHelper.showErrorMessage(BaseTable.this, "Failed to save table to '" + chooser.getSelectedFile() + "'!");
    });
    menu.add(menuitem);

    return menu;
  }

  /**
   * Returns the underlying sheet.
   *
   * @return		the spread sheet
   */
  @Override
  public SpreadSheet toSpreadSheet() {
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
}
