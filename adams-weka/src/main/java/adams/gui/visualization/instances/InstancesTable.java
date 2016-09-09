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
 * InstancesTable.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.instances;

import adams.core.Range;
import adams.data.statistics.ArrayHistogram;
import adams.flow.control.Flow;
import adams.flow.control.StorageName;
import adams.flow.core.Actor;
import adams.flow.source.StorageValue;
import adams.flow.transformer.ArrayToSequence;
import adams.flow.transformer.MakePlotContainer;
import adams.gui.chooser.WekaFileChooser;
import adams.gui.core.BasePopupMenu;
import adams.gui.core.GUIHelper;
import adams.gui.core.SortableAndSearchableTable;
import adams.gui.core.UndoHandlerWithQuickAccess;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.goe.GenericObjectEditorDialog;
import adams.gui.visualization.statistics.HistogramFactory;
import gnu.trove.list.array.TDoubleArrayList;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Undoable;
import weka.core.converters.AbstractFileSaver;

import javax.swing.JMenuItem;
import javax.swing.SwingWorker;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Table for displaying Instances objects.
 * Supports simple undo by default, but can make use of a
 * {@link UndoHandlerWithQuickAccess} as well.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InstancesTable
  extends SortableAndSearchableTable
  implements Undoable {

  private static final long serialVersionUID = -1408763296714340976L;

  /** the renderer to use. */
  protected AttributeValueCellRenderer m_Renderer;

  /** the filechooser for exporting data. */
  protected WekaFileChooser m_FileChooser;

  /** the last plot in use. */
  protected adams.flow.sink.SimplePlot m_LastPlot;

  /** the last histogram in use. */
  protected ArrayHistogram m_LastHistogram;

  /**
   * Initializes the table with the data.
   *
   * @param data	the data to display
   */
  public InstancesTable(Instances data) {
    this(new InstancesTableModel(data));
  }

  /**
   * Initializes the table with the model.
   *
   * @param model	the model to use
   */
  public InstancesTable(InstancesTableModel model) {
    super(model);
  }

  /**
   * Initializes the widget.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    m_FileChooser = new WekaFileChooser();
    m_Renderer    = new AttributeValueCellRenderer();
    setAutoResizeMode(SortableAndSearchableTable.AUTO_RESIZE_OFF);
    addHeaderPopupMenuListener((MouseEvent e) -> showHeaderPopup(e));
    addCellPopupMenuListener((MouseEvent e) -> showCellPopup(e));
  }

  /**
   * Sets the model to use.
   *
   * @param model        the model to display
   */
  @Override
  public synchronized void setModel(TableModel model) {
    if (model instanceof InstancesTableModel)
      super.setModel(model);
    else
      throw new IllegalArgumentException(
	"Model must be derived from " + InstancesTableModel.class.getName() + ", provided: " + model.getClass().getName());
  }

  /**
   * Sets the undo handler to use.
   *
   * @param value	the handler, null if to turn off
   */
  public void setUndoHandler(UndoHandlerWithQuickAccess value) {
    ((InstancesTableModel) getUnsortedModel()).setUndoHandler(value);
  }

  /**
   * Returns the undo handler in use.
   *
   * @return		the handler, null if none set
   */
  public UndoHandlerWithQuickAccess getUndoHandler() {
    return ((InstancesTableModel) getUnsortedModel()).getUndoHandler();
  }

  /**
   * returns whether undo support is enabled
   *
   * @return true if undo support is enabled
   */
  @Override
  public boolean isUndoEnabled() {
    return ((InstancesTableModel) getUnsortedModel()).isUndoEnabled();
  }

  /**
   * sets whether undo support is enabled
   *
   * @param enabled whether to enable/disable undo support
   */
  @Override
  public void setUndoEnabled(boolean enabled) {
    ((InstancesTableModel) getUnsortedModel()).setUndoEnabled(enabled);
  }

  /**
   * removes the undo history
   */
  @Override
  public void clearUndo() {
    ((InstancesTableModel) getUnsortedModel()).clearUndo();
  }

  /**
   * returns whether an undo is possible, i.e. whether there are any undo points
   * saved so far
   *
   * @return returns TRUE if there is an undo possible
   */
  @Override
  public boolean canUndo() {
    return ((InstancesTableModel) getUnsortedModel()).canUndo();
  }

  /**
   * undoes the last action
   */
  @Override
  public void undo() {
    ((InstancesTableModel) getModel()).undo();
    setOptimalColumnWidth();
  }

  /**
   * adds an undo point to the undo history, if the undo support is enabled
   *
   * @see #isUndoEnabled()
   * @see #setUndoEnabled(boolean)
   */
  @Override
  public void addUndoPoint() {
    ((InstancesTableModel) getModel()).addUndoPoint();
  }

  /**
   * returns whether the model is read-only
   *
   * @return true if model is read-only
   */
  public boolean isReadOnly() {
    return ((InstancesTableModel) getUnsortedModel()).isReadOnly();
  }

  /**
   * sets whether the model is read-only
   *
   * @param value if true the model is set to read-only
   */
  public void setReadOnly(boolean value) {
    ((InstancesTableModel) getUnsortedModel()).setReadOnly(value);
  }

  /**
   * sets the data
   *
   * @param data the data to use
   */
  public void setInstances(Instances data) {
    setModel(new InstancesTableModel(data));
  }

  /**
   * returns the data
   *
   * @return the current data
   */
  public Instances getInstances() {
    return ((InstancesTableModel) getUnsortedModel()).getInstances();
  }

  /**
   * Returns the renderer for this cell.
   *
   * @param row		the row
   * @param column	the column
   * @return		the renderer
   */
  @Override
  public TableCellRenderer getCellRenderer(int row, int column) {
    return m_Renderer;
  }

  /**
   * Plots the specified column.
   *
   * @param col		the column to plot
   */
  protected void plotColumn(int col) {
    final List<Double> 		list;
    GenericObjectEditorDialog 	setup;
    int				i;
    final String		title;
    SwingWorker 		worker;
    Instances			data;
    Instance			inst;

    // let user customize plot
    if (GUIHelper.getParentDialog(this) != null)
      setup = new GenericObjectEditorDialog(GUIHelper.getParentDialog(this), ModalityType.DOCUMENT_MODAL);
    else
      setup = new GenericObjectEditorDialog(GUIHelper.getParentFrame(this), true);
    setup.setDefaultCloseOperation(GenericObjectEditorDialog.DISPOSE_ON_CLOSE);
    setup.getGOEEditor().setClassType(Actor.class);
    setup.getGOEEditor().setCanChangeClassInDialog(false);
    if (m_LastPlot == null)
      m_LastPlot = new adams.flow.sink.SimplePlot();
    setup.setCurrent(m_LastPlot);
    setup.setLocationRelativeTo(GUIHelper.getParentComponent(this));
    setup.setVisible(true);
    if (setup.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return;
    m_LastPlot = (adams.flow.sink.SimplePlot) setup.getCurrent();

    // get data from spreadsheet
    list = new ArrayList<>();
    data = getInstances();
    for (i = 0; i < data.numInstances(); i++) {
      inst = data.instance(i);
      if (!inst.isMissing(col))
	list.add(inst.value(col));
    }

    // generate plot
    title = "Attribute #" + (col + 1) + "/" + data.attribute(col).name();
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

	adams.flow.sink.SimplePlot plot = (adams.flow.sink.SimplePlot) m_LastPlot.shallowCopy();
	plot.setShortTitle(true);
	plot.setName(title);
        plot.setX(-2);
        plot.setY(-2);
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
   * Displays a histogram for the specified column.
   *
   * @param col		the column
   */
  protected void histogram(int col) {
    TDoubleArrayList 			list;
    HistogramFactory.SetupDialog	setup;
    HistogramFactory.Dialog		dialog;
    int					i;
    Instances				data;
    Instance				inst;

    // let user customize histogram
    if (GUIHelper.getParentDialog(this) != null)
      setup = HistogramFactory.getSetupDialog(GUIHelper.getParentDialog(this), ModalityType.DOCUMENT_MODAL);
    else
      setup = HistogramFactory.getSetupDialog(GUIHelper.getParentFrame(this), true);
    setup.setDefaultCloseOperation(HistogramFactory.SetupDialog.DISPOSE_ON_CLOSE);
    if (m_LastHistogram == null)
      m_LastHistogram = new ArrayHistogram();
    setup.setCurrent(m_LastHistogram);
    setup.setLocationRelativeTo(GUIHelper.getParentComponent(this));
    setup.setVisible(true);
    if (setup.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return;
    m_LastHistogram = (ArrayHistogram) setup.getCurrent();

    // get data from spreadsheet
    list = new TDoubleArrayList();
    data = getInstances();
    for (i = 0; i < data.numInstances(); i++) {
      inst = data.instance(i);
      if (!inst.isMissing(col))
	list.add(inst.value(col));
    }

    // calculate histogram
    m_LastHistogram.clear();

    // display histogram
    if (GUIHelper.getParentDialog(this) != null)
      dialog = HistogramFactory.getDialog(GUIHelper.getParentDialog(this), ModalityType.MODELESS);
    else
      dialog = HistogramFactory.getDialog(GUIHelper.getParentFrame(this), false);
    dialog.setDefaultCloseOperation(HistogramFactory.Dialog.DISPOSE_ON_CLOSE);
    dialog.add(m_LastHistogram, list.toArray(), "Attribute #" + (col + 1) + "/" + data.attribute(col).name());
    dialog.setLocationRelativeTo(GUIHelper.getParentComponent(this));
    dialog.setVisible(true);
  }

  /**
   * Shows a popup menu for the header.
   *
   * @param e		the event
   */
  protected void showHeaderPopup(MouseEvent e) {
    BasePopupMenu		menu;

    menu = createHeaderPopup(e);
    menu.showAbsolute(getTableHeader(), e);
  }

  /**
   * Shows a popup menu for the header.
   *
   * @param e		the event
   * @return		the menu
   */
  protected BasePopupMenu createHeaderPopup(MouseEvent e) {
    BasePopupMenu		menu;
    JMenuItem			menuitem;
    final int			col;
    final InstancesTableModel	instModel;

    menu      = new BasePopupMenu();
    col       = tableHeader.columnAtPoint(e.getPoint());
    instModel = (InstancesTableModel) getUnsortedModel();

    if (instModel.isUndoEnabled()) {
      menuitem = new JMenuItem("Undo", GUIHelper.getIcon("undo.gif"));
      menuitem.setEnabled(canUndo());
      menuitem.addActionListener((ActionEvent ae) -> instModel.undo());
      menu.add(menuitem);
      menu.addSeparator();
    }

    menuitem = new JMenuItem("Rename...", GUIHelper.getEmptyIcon());
    menuitem.addActionListener((ActionEvent ae) -> {
      String newName = GUIHelper.showInputDialog(
	InstancesTable.this, "Please enter new name", getInstances().attribute(col - 1).name());
      if (newName != null) {
	instModel.renameAttributeAt(col, newName);
	setOptimalColumnWidth();
      }
    });
    menu.add(menuitem);

    menuitem = new JMenuItem("Delete", GUIHelper.getIcon("delete.gif"));
    menuitem.addActionListener((ActionEvent ae) -> {
      int retVal = GUIHelper.showConfirmMessage(InstancesTable.this, "Delete attribute '" + getInstances().attribute(col - 1).name() + "'?");
      if (retVal == ApprovalDialog.APPROVE_OPTION) {
	instModel.deleteAttributeAt(col);
	setOptimalColumnWidth();
      }
    });
    menu.add(menuitem);

    menu.addSeparator();

    menuitem = new JMenuItem("Plot attribute...", GUIHelper.getIcon("plot.gif"));
    menuitem.setEnabled(getInstances().attribute(col - 1).isNumeric());
    menuitem.addActionListener((ActionEvent ae) -> plotColumn(col - 1));
    menu.add(menuitem);

    menuitem = new JMenuItem("Histogram...", GUIHelper.getIcon("histogram.png"));
    menuitem.setEnabled(getInstances().attribute(col - 1).isNumeric());
    menuitem.addActionListener((ActionEvent ae) -> histogram(col - 1));
    menu.add(menuitem);

    return menu;
  }

  /**
   * Shows a popup menu for the cells.
   *
   * @param e		the event
   */
  protected void showCellPopup(MouseEvent e) {
    BasePopupMenu 	menu;

    menu = createCellPopup(e);
    menu.showAbsolute(this, e);
  }

  /**
   * Creates a popup menu for the cells.
   *
   * @param e		the event
   * @return		the menu
   */
  protected BasePopupMenu createCellPopup(MouseEvent e) {
    BasePopupMenu 		menu;
    JMenuItem			menuitem;
    final int[]			selRows;
    final InstancesTableModel	instModel;
    final Range 		range;

    menu      = new BasePopupMenu();
    selRows   = getSelectedRows();
    instModel = (InstancesTableModel) getUnsortedModel();
    range = new Range();
    range.setMax(getRowCount());
    range.setIndices(selRows);

    if (instModel.isUndoEnabled()) {
      menuitem = new JMenuItem("Undo", GUIHelper.getIcon("undo.gif"));
      menuitem.setEnabled(canUndo());
      menuitem.addActionListener((ActionEvent ae) -> instModel.undo());
      menu.add(menuitem);
    }

    menuitem = new JMenuItem("Invert selection", GUIHelper.getEmptyIcon());
    menuitem.addActionListener((ActionEvent ae) -> invertRowSelection());
    menu.add(menuitem);

    menu.addSeparator();

    menuitem = new JMenuItem("Delete", GUIHelper.getIcon("delete.gif"));
    menuitem.setEnabled(selRows.length > 0);
    menuitem.addActionListener((ActionEvent ae) -> {
      String msg = "Delete row";
      if (selRows.length > 1)
	msg += "s";
      msg += " " + range.getRange() + "?";
      int retVal = GUIHelper.showConfirmMessage(InstancesTable.this, msg);
      if (retVal != ApprovalDialog.APPROVE_OPTION)
	return;
	int[] actRows = new int[selRows.length];
      for (int i = 0; i < selRows.length; i++)
	actRows[i] = getActualRow(selRows[i]);
      instModel.deleteInstances(actRows);
    });
    menu.add(menuitem);

    menuitem = new JMenuItem("Export...", GUIHelper.getIcon("save.gif"));
    menuitem.setEnabled(selRows.length > 0);
    menuitem.addActionListener((ActionEvent ae) -> {
      int retVal = m_FileChooser.showSaveDialog(InstancesTable.this);
      if (retVal != WekaFileChooser.APPROVE_OPTION)
	return;
      AbstractFileSaver saver = m_FileChooser.getWriter();
      File file = m_FileChooser.getSelectedFile();
      Instances original = getInstances();
      Instances data = new Instances(original, 0);
      for (int i = 0; i < selRows.length; i++)
	data.add((Instance) original.instance(getActualRow(selRows[i])).copy());
      try {
	saver.setFile(file);
	saver.setInstances(data);
	saver.writeBatch();
      }
      catch (Exception ex) {
	GUIHelper.showErrorMessage(
	  InstancesTable.this, "Failed to export data to: " + file, ex);
      }
    });
    menu.add(menuitem);

    return menu;
  }
}
