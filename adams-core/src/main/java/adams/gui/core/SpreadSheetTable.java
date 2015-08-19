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
 * SpreadSheetTable.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.RowComparator;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.chooser.SpreadSheetFileChooser;
import adams.gui.core.spreadsheettable.SpreadSheetTablePopupMenuItemHelper;
import adams.gui.event.PopupMenuListener;
import adams.gui.visualization.core.PopupMenuCustomizer;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.HashMap;

/**
 * A specialized table for displaying a SpreadSheet table model.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetTable
  extends SortableAndSearchableTable {

  /** for serialization. */
  private static final long serialVersionUID = 1333317577811620786L;

  /** the customizer for the table header popup menu. */
  protected PopupMenuCustomizer m_HeaderPopupMenuCustomizer;

  /** the customizer for the table cells popup menu. */
  protected PopupMenuCustomizer m_CellPopupMenuCustomizer;

  /** the file chooser for saving the spreadsheet. */
  protected SpreadSheetFileChooser m_FileChooser;

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
	model.addTableModelListener(listener);
	listener.tableChanged(new TableModelEvent(model));
      }
    }
  }

  /**
   * Returns the initial setting of whether to set optimal column widths.
   *
   * @return		true
   */
  @Override
  protected boolean initialUseOptimalColumnWidths() {
    return true;
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
    SpreadSheet	result;
    SpreadSheet	full;
    int[] 	indices;
    int		i;
    
    full = ((SpreadSheetTableModel) getUnsortedModel()).toSpreadSheet();
    switch (range) {
      case ALL:
	result = full;
	break;
      case SELECTED:
	result = full.getHeader();
	indices = getSelectedRows();
	for (i = 0; i < indices.length; i++)
	  result.addRow().assign(full.getRow(getActualRow(indices[i])));
	break;
      case VISIBLE:
	result = full.getHeader();
	for (i = 0; i < getRowCount(); i++)
	  result.addRow().assign(full.getRow(getActualRow(i)));
	break;
      default:
	throw new IllegalStateException("Unhandled row range: " + range);
    }
    
    return result;
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
   * Shows a popup menu for the header.
   *
   * @param e		the event that triggered the menu
   */
  protected void showHeaderPopupMenu(MouseEvent e) {
    BasePopupMenu	menu;
    JMenuItem		menuitem;
    final boolean	asc;
    final int           row;
    final int           col;
    final int           actCol;

    menu = new BasePopupMenu();
    row  = rowAtPoint(e.getPoint());
    col  = columnAtPoint(e.getPoint());
    if (getShowRowColumn())
      actCol = col - 1;
    else
      actCol = col;
    
    menuitem = new JMenuItem("Copy column name", GUIHelper.getEmptyIcon());
    menuitem.setEnabled((getShowRowColumn() && (col > 0) || !getShowRowColumn()));
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        GUIHelper.copyToClipboard(((SpreadSheetTableModel) getUnsortedModel()).toSpreadSheet().getColumnName(actCol));
      }
    });
    menu.add(menuitem);
    
    menuitem = new JMenuItem("Copy column", GUIHelper.getIcon("copy.gif"));
    menuitem.setEnabled((getShowRowColumn() && (col > 0) || !getShowRowColumn()));
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        int actCol = col;
        if (getShowRowColumn())
          actCol--;
        SpreadSheet sheet = ((SpreadSheetTableModel) getUnsortedModel()).toSpreadSheet();
        StringBuilder content = new StringBuilder();
        String sep = System.getProperty("line.separator");
        content.append(sheet.getColumnName(actCol) + sep);
        for (int i = 0; i < sheet.getRowCount(); i++) {
          if (!sheet.hasCell(i, actCol) || sheet.getCell(i, actCol).isMissing())
            content.append(sep);
          else
            content.append(sheet.getCell(i, actCol).getContent() + sep);
        }
        GUIHelper.copyToClipboard(content.toString());
      }
    });
    menu.add(menuitem);
    
    menuitem = new JMenuItem("Rename column", GUIHelper.getEmptyIcon());
    menuitem.setEnabled(!isReadOnly() && (getShowRowColumn() && (col > 0) || !getShowRowColumn()));
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	SpreadSheet sheet = ((SpreadSheetTableModel) getUnsortedModel()).toSpreadSheet();
	boolean readOnly = isReadOnly();
	String newName = sheet.getColumnName(actCol);
	newName = GUIHelper.showInputDialog(getParent(), "Please enter new column name", newName);
	if (newName == null)
	  return;
	sheet = sheet.getClone();
	sheet.getHeaderRow().getCell(actCol).setContent(newName);
	setModel(new SpreadSheetTableModel(sheet));
	setReadOnly(readOnly);
	setModified(true);
	((SpreadSheetTableModel) getUnsortedModel()).fireTableDataChanged();
      }
    });
    menu.add(menuitem);

    asc  = !e.isShiftDown();
    if (asc)
      menuitem = new JMenuItem("Sort (asc)", GUIHelper.getIcon("sort-ascending.png"));
    else
      menuitem = new JMenuItem("Sort (desc)", GUIHelper.getIcon("sort-descending.png"));
    menuitem.setEnabled(!isReadOnly() && (getShowRowColumn() && (col > 0)) || !getShowRowColumn());
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        SpreadSheet sheet = toSpreadSheet();
	boolean readOnly = isReadOnly();
        sheet.sort(actCol, asc);
        setModel(new SpreadSheetTableModel(sheet));
	setReadOnly(readOnly);
	setModified(true);
	((SpreadSheetTableModel) getUnsortedModel()).fireTableDataChanged();
      }
    });
    menu.add(menuitem);

    menu.addSeparator();

    menuitem = new JMenuItem("Insert column", GUIHelper.getIcon("insert-column.png"));
    menuitem.setEnabled(!isReadOnly());
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	String colName = GUIHelper.showInputDialog(
	  GUIHelper.getParentComponent(SpreadSheetTable.this),
	  "Please enter the name of the column", "New");
	if (colName == null)
	  return;
        SpreadSheet sheet = toSpreadSheet();
	boolean readOnly = isReadOnly();
        sheet.insertColumn(actCol, colName, SpreadSheet.MISSING_VALUE);
        setModel(new SpreadSheetTableModel(sheet));
	setReadOnly(readOnly);
	setModified(true);
	((SpreadSheetTableModel) getUnsortedModel()).fireTableDataChanged();
      }
    });
    menu.add(menuitem);

    menuitem = new JMenuItem("Remove column", GUIHelper.getIcon("delete-column.png"));
    menuitem.setEnabled(!isReadOnly());
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        SpreadSheet sheet = toSpreadSheet();
	boolean readOnly = isReadOnly();
        sheet.removeColumn(actCol);
        setModel(new SpreadSheetTableModel(sheet));
	setReadOnly(readOnly);
	setModified(true);
	((SpreadSheetTableModel) getUnsortedModel()).fireTableDataChanged();
      }
    });
    menu.add(menuitem);

    SpreadSheetTablePopupMenuItemHelper.addToPopupMenu(this, menu, false, row, actCol);

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
    BasePopupMenu	menu;
    JMenuItem		menuitem;
    JMenu		submenu;
    final int   	row;
    final int[]		rows;
    final int   	col;

    menu = new BasePopupMenu();
    col  = columnAtPoint(e.getPoint());
    row  = rowAtPoint(e.getPoint());
    if (getSelectedRows().length == 0)
      rows = new int[]{row};
    else
      rows = getSelectedRows();

    if (getSelectedRowCount() > 1)
      menuitem = new JMenuItem("Copy rows");
    else
      menuitem = new JMenuItem("Copy row");
    menuitem.setIcon(GUIHelper.getIcon("copy.gif"));
    menuitem.setEnabled(getSelectedRowCount() > 0);
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent ae) {
        copyToClipboard();
      }
    });
    menu.add(menuitem);

    menuitem = new JMenuItem("Copy cell");
    menuitem.setIcon(GUIHelper.getEmptyIcon());
    menuitem.setEnabled(getSelectedRowCount() == 1);
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent ae) {
        if (row == -1)
          return;
        if (col == -1)
          return;
        GUIHelper.copyToClipboard("" + getValueAt(row, col));
      }
    });
    menu.add(menuitem);

    menu.addSeparator();

    menuitem = new JMenuItem("Insert row", GUIHelper.getIcon("insert-row.png"));
    menuitem.setEnabled(!isReadOnly());
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        SpreadSheet sheet = toSpreadSheet();
        boolean readOnly = isReadOnly();
        sheet.insertRow(row);
        setModel(new SpreadSheetTableModel(sheet));
        setReadOnly(readOnly);
        setModified(true);
        ((SpreadSheetTableModel) getUnsortedModel()).fireTableDataChanged();
      }
    });
    menu.add(menuitem);

    menuitem = new JMenuItem("Remove row", GUIHelper.getIcon("delete-row.png"));
    menuitem.setEnabled(!isReadOnly());
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	SpreadSheet sheet = toSpreadSheet();
	boolean readOnly = isReadOnly();
	for (int i = rows.length - 1; i >= 0; i--)
	  sheet.removeRow(rows[i]);
	setModel(new SpreadSheetTableModel(sheet));
	setReadOnly(readOnly);
	setModified(true);
	((SpreadSheetTableModel) getUnsortedModel()).fireTableDataChanged();
      }
    });
    menu.add(menuitem);

    menu.addSeparator();

    submenu = new JMenu("Save");
    submenu.setIcon(GUIHelper.getIcon("save.gif"));
    menu.add(submenu);
    
    menuitem = new JMenuItem("Save all...");
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent ae) {
        saveAs(TableRowRange.ALL);
      }
    });
    submenu.add(menuitem);
    
    menuitem = new JMenuItem("Save selected...");
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent ae) {
        saveAs(TableRowRange.SELECTED);
      }
    });
    submenu.add(menuitem);
    
    menuitem = new JMenuItem("Save visible...");
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent ae) {
        saveAs(TableRowRange.VISIBLE);
      }
    });
    submenu.add(menuitem);

    SpreadSheetTablePopupMenuItemHelper.addToPopupMenu(this, menu, true, row, col);

    menu.addSeparator();

    menuitem = new JCheckBoxMenuItem("Show formulas");
    menuitem.setIcon(GUIHelper.getIcon("formula.png"));
    menuitem.setEnabled(getRowCount() > 0);
    menuitem.setSelected(getShowFormulas());
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent ae) {
        setShowFormulas(!getShowFormulas());
      }
    });
    menu.add(menuitem);

    menuitem = new JCheckBoxMenuItem("Show cell types");
    menuitem.setIcon(GUIHelper.getEmptyIcon());
    menuitem.setEnabled(getRowCount() > 0);
    menuitem.setSelected(getShowCellTypes());
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent ae) {
	setShowCellTypes(!getShowCellTypes());
      }
    });
    menu.add(menuitem);

    if (m_CellPopupMenuCustomizer != null)
      m_CellPopupMenuCustomizer.customizePopupMenu(e, menu);

    menu.showAbsolute(this, e);
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
   * Checks whether a custom background color for negative values has been set.
   *
   * @return		true if custom color set
   */
  public boolean hasNegativeBackground() {
    return ((SpreadSheetTableModel) getUnsortedModel()).hasNegativeBackground();
  }

  /**
   * Sets the custom background color for negative values.
   *
   * @param value	the color, null to unset it
   */
  public void setNegativeBackground(Color value) {
    ((SpreadSheetTableModel) getUnsortedModel()).setNegativeBackground(value);
  }

  /**
   * Returns the custom background color for negative values, if any.
   *
   * @return		the color, null if none set
   */
  public Color getNegativeBackground() {
    return ((SpreadSheetTableModel) getUnsortedModel()).getNegativeBackground();
  }

  /**
   * Checks whether a custom background color for positive values has been set.
   *
   * @return		true if custom color set
   */
  public boolean hasPositiveBackground() {
    return ((SpreadSheetTableModel) getUnsortedModel()).hasPositiveBackground();
  }

  /**
   * Sets the custom background color for positive values.
   *
   * @param value	the color, null to unset it
   */
  public void setPositiveBackground(Color value) {
    ((SpreadSheetTableModel) getUnsortedModel()).setPositiveBackground(value);
  }

  /**
   * Returns the custom background color for positive values, if any.
   *
   * @return		the color, null if none set
   */
  public Color getPositiveBackground() {
    return ((SpreadSheetTableModel) getUnsortedModel()).getPositiveBackground();
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
   * Pops up a save dialog for saving the data to a file.
   * 
   * @param range	the type of data to save
   */
  protected void saveAs(TableRowRange range) {
    int 		retVal;
    File 		file;
    SpreadSheetWriter 	writer;
    
    retVal = getFileChooser().showSaveDialog(GUIHelper.getParentComponent(this));
    if (retVal != SpreadSheetFileChooser.APPROVE_OPTION)
      return;
    file = getFileChooser().getSelectedFile();
    writer = getFileChooser().getWriter();
    if (!writer.write(toSpreadSheet(range), file)) {
      GUIHelper.showErrorMessage(
	  SpreadSheetTable.this,
	  "Failed to save spreadsheet to the following file:\n" + file);
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
}