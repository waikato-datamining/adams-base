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
import adams.data.spreadsheet.SpreadSheetSupporter;
import adams.data.statistics.ArrayHistogram;
import adams.flow.control.Flow;
import adams.flow.control.StorageName;
import adams.flow.core.AbstractActor;
import adams.flow.sink.SimplePlot;
import adams.flow.source.StorageValue;
import adams.flow.transformer.ArrayToSequence;
import adams.flow.transformer.MakePlotContainer;
import adams.gui.chooser.SpreadSheetFileChooser;
import adams.gui.event.PopupMenuListener;
import adams.gui.goe.GenericObjectEditorDialog;
import adams.gui.visualization.core.PopupMenuCustomizer;
import adams.gui.visualization.statistics.HistogramFactory;
import gnu.trove.list.array.TDoubleArrayList;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingWorker;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A specialized table for displaying a SpreadSheet table model.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetTable
  extends SortableAndSearchableTable 
  implements SpreadSheetSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 1333317577811620786L;

  /** the customizer for the table header popup menu. */
  protected PopupMenuCustomizer m_HeaderPopupMenuCustomizer;

  /** the customizer for the table cells popup menu. */
  protected PopupMenuCustomizer m_CellPopupMenuCustomizer;

  /** the file chooser for saving the spreadsheet. */
  protected SpreadSheetFileChooser m_FileChooser;
  
  /** the last {@link ArrayHistogram} setup. */
  protected ArrayHistogram m_Histogram;

  /** the last {@link SimplePlot} setup. */
  protected SimplePlot m_Plot;
  
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
    m_Histogram                 = new ArrayHistogram();
    m_Plot = new SimplePlot();

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
    JPopupMenu		menu;
    JMenuItem		menuitem;
    final int		col;
    final boolean	asc;

    menu = new JPopupMenu();
    col  = columnAtPoint(e.getPoint());
    
    menuitem = new JMenuItem("Copy column name", GUIHelper.getEmptyIcon());
    menuitem.setEnabled((getShowRowColumn() && (col > 0) || !getShowRowColumn()));
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	int actCol = col;
	if (getShowRowColumn())
	  actCol--;
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
    menuitem.setEnabled((getShowRowColumn() && (col > 0) || !getShowRowColumn()));
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	int actCol = col;
	if (getShowRowColumn())
	  actCol--;
	SpreadSheet sheet = ((SpreadSheetTableModel) getUnsortedModel()).toSpreadSheet();
	String newName = sheet.getColumnName(actCol);
	newName = GUIHelper.showInputDialog(getParent(), "Please enter new column name", newName);
	if (newName == null)
	  return;
	sheet = sheet.getClone();
	sheet.getHeaderRow().getCell(actCol).setContent(newName);
	setModel(new SpreadSheetTableModel(sheet));
      }
    });
    menu.add(menuitem);

    asc  = !e.isShiftDown();
    if (asc)
      menuitem = new JMenuItem("Sort (asc)", GUIHelper.getIcon("sort-ascending.png"));
    else
      menuitem = new JMenuItem("Sort (desc)", GUIHelper.getIcon("sort-descending.png"));
    menuitem.setEnabled((getShowRowColumn() && (col > 0)) || !getShowRowColumn());
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	SpreadSheet sheet = toSpreadSheet();
	if (getShowRowColumn())
	  sheet.sort(col - 1, asc);
	else
	  sheet.sort(col, asc);
	setModel(new SpreadSheetTableModel(sheet));
      }
    });
    menu.add(menuitem);

    menu.addSeparator();

    menuitem = new JMenuItem("Remove column", GUIHelper.getIcon("delete.gif"));
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	SpreadSheet sheet = toSpreadSheet();
	if (getShowRowColumn())
	  sheet.removeColumn(col - 1);
	else
	  sheet.removeColumn(col);
	setModel(new SpreadSheetTableModel(sheet));
      }
    });
    menu.add(menuitem);

    menu.addSeparator();

    menuitem = new JMenuItem("Plot", GUIHelper.getIcon("plot.gif"));
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	plot(toSpreadSheet(), true, getShowRowColumn() ? col - 1 : col);
      }
    });
    menu.add(menuitem);

    menuitem = new JMenuItem("Histogram", GUIHelper.getIcon("histogram.png"));
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	histogram(toSpreadSheet(), true, getShowRowColumn() ? col - 1 : col);
      }
    });
    menu.add(menuitem);

    if (m_HeaderPopupMenuCustomizer != null)
      m_HeaderPopupMenuCustomizer.customizePopupMenu(e, menu);

    menu.show(getTableHeader(), e.getX(), e.getY());
  }

  /**
   * Shows a popup menu for the cells.
   *
   * @param e		the event that triggered the menu
   */
  protected void showCellPopupMenu(final MouseEvent e) {
    JPopupMenu	menu;
    JMenuItem	menuitem;
    JMenu	submenu;

    menu = new JPopupMenu();

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
	int row = rowAtPoint(e.getPoint());
	if (row == -1)
	  return;
	int col = columnAtPoint(e.getPoint());
	if (col == -1)
	  return;
	GUIHelper.copyToClipboard("" + getValueAt(row, col));
      }
    });
    menu.add(menuitem);

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

    menu.addSeparator();

    menuitem = new JMenuItem("Plot", GUIHelper.getIcon("plot.gif"));
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent ae) {
	int row = rowAtPoint(e.getPoint());
	if (row == -1)
	  return;
	plot(toSpreadSheet(), false, row);
      }
    });
    menu.add(menuitem);

    menuitem = new JMenuItem("Histogram", GUIHelper.getIcon("histogram.png"));
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent ae) {
	int row = rowAtPoint(e.getPoint());
	if (row == -1)
	  return;
	histogram(toSpreadSheet(), false, row);
      }
    });
    menu.add(menuitem);

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

    if (m_CellPopupMenuCustomizer != null)
      m_CellPopupMenuCustomizer.customizePopupMenu(e, menu);

    menu.show(this, e.getX(), e.getY());
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
   * Sorts the spreadsheet with the given comparator.
   *
   * @param comparator	the row comparator to use
   */
  public void sort(RowComparator comparator) {
    toSpreadSheet().sort(comparator);
    ((SpreadSheetTableModel) getUnsortedModel()).fireTableDataChanged();
  }

  /**
   * Allows the user to generate a histogram from either a row or a column.
   * 
   * @param sheet	the spreadsheet to use
   * @param isColumn	whether the to use column or row
   * @param index	the index of the row/column
   */
  protected void histogram(SpreadSheet sheet, boolean isColumn, int index) {
    TDoubleArrayList			list;
    HistogramFactory.SetupDialog	setup;
    HistogramFactory.Dialog		dialog;
    int					i;
    
    // let user customize histogram
    if (GUIHelper.getParentDialog(this) != null)
      setup = HistogramFactory.getSetupDialog(GUIHelper.getParentDialog(this), ModalityType.DOCUMENT_MODAL);
    else
      setup = HistogramFactory.getSetupDialog(GUIHelper.getParentFrame(this), true);
    setup.setDefaultCloseOperation(HistogramFactory.SetupDialog.DISPOSE_ON_CLOSE);
    setup.setCurrent(m_Histogram);
    setup.setVisible(true);
    if (setup.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return;
    m_Histogram = (ArrayHistogram) setup.getCurrent();

    // get data from spreadsheet
    list = new TDoubleArrayList();
    if (isColumn) {
      for (i = 0; i < sheet.getRowCount(); i++) {
	if (sheet.hasCell(i, index) && sheet.getCell(i, index).isNumeric())
	  list.add(sheet.getCell(i, index).toDouble());
      }
    }
    else {
      for (i = 0; i < sheet.getColumnCount(); i++) {
	if (sheet.hasCell(index, i) && sheet.getCell(index, i).isNumeric())
	  list.add(sheet.getCell(index, i).toDouble());
      }
    }
    
    // calculate histogram
    m_Histogram.clear();
    
    // display histogram
    if (GUIHelper.getParentDialog(this) != null)
      dialog = HistogramFactory.getDialog(GUIHelper.getParentDialog(this), ModalityType.MODELESS);
    else
      dialog = HistogramFactory.getDialog(GUIHelper.getParentFrame(this), false);
    dialog.setDefaultCloseOperation(HistogramFactory.Dialog.DISPOSE_ON_CLOSE);
    if (isColumn)
      dialog.add(m_Histogram, list.toArray(), "Column " + (index + 1) + "/" + sheet.getColumnName(index));
    else
      dialog.add(m_Histogram, list.toArray(), "Row " + (index + 1));
    dialog.setVisible(true);
  }

  /**
   * Allows the user to generate a plot from either a row or a column.
   *
   * @param sheet	the spreadsheet to use
   * @param isColumn	whether the to use column or row
   * @param index	the index of the row/column
   */
  protected void plot(SpreadSheet sheet, boolean isColumn, int index) {
    final List<Double> 		list;
    GenericObjectEditorDialog	setup;
    int				i;
    final String		title;
    SwingWorker 		worker;

    // let user customize plot
    if (GUIHelper.getParentDialog(this) != null)
      setup = new GenericObjectEditorDialog(GUIHelper.getParentDialog(this), ModalityType.DOCUMENT_MODAL);
    else
      setup = new GenericObjectEditorDialog(GUIHelper.getParentFrame(this), true);
    setup.setDefaultCloseOperation(HistogramFactory.SetupDialog.DISPOSE_ON_CLOSE);
    setup.getGOEEditor().setClassType(AbstractActor.class);
    setup.getGOEEditor().setCanChangeClassInDialog(false);
    setup.setCurrent(m_Plot);
    setup.setVisible(true);
    if (setup.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return;
    m_Plot = (SimplePlot) setup.getCurrent();

    // get data from spreadsheet
    list = new ArrayList<Double>();
    if (isColumn) {
      for (i = 0; i < sheet.getRowCount(); i++) {
	if (sheet.hasCell(i, index) && sheet.getCell(i, index).isNumeric())
	  list.add(sheet.getCell(i, index).toDouble());
      }
    }
    else {
      for (i = 0; i < sheet.getColumnCount(); i++) {
	if (sheet.hasCell(index, i) && sheet.getCell(index, i).isNumeric())
	  list.add(sheet.getCell(index, i).toDouble());
      }
    }

    // generate plot
    if (isColumn)
      title = "Column " + (index + 1) + "/" + sheet.getColumnName(index);
    else
      title = "Row " + (index + 1);

    worker = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
	Flow flow = new Flow();

	StorageValue sv = new StorageValue();
	sv.setStorageName(new StorageName("values"));
	flow.add(sv);

	ArrayToSequence a2s = new ArrayToSequence();
	flow.add(a2s);

	MakePlotContainer mpc = new MakePlotContainer();
	mpc.setPlotName(title);
	flow.add(mpc);

	SimplePlot plot = (SimplePlot) m_Plot.shallowCopy();
	plot.setShortTitle(true);
	plot.setName(title);
	flow.add(plot);

	flow.setUp();
	flow.getStorage().put(new StorageName("values"), list.toArray(new Double[list.size()]));
	flow.execute();
	flow.wrapUp();
	return null;
      }
    };
    worker.execute();
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
}