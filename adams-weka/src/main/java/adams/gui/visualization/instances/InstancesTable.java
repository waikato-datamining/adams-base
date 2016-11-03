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
import adams.gui.chooser.WekaFileChooser;
import adams.gui.core.BasePopupMenu;
import adams.gui.core.GUIHelper;
import adams.gui.core.SortableAndSearchableTable;
import adams.gui.core.TableRowRange;
import adams.gui.core.UndoHandlerWithQuickAccess;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.visualization.instances.instancestable.InstancesTablePopupMenuItemHelper;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Undoable;
import weka.core.converters.AbstractFileSaver;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

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

  /** for keeping track of the setups being used (classname-{plot|process}-{column|row} - setup). */
  protected HashMap<String,Object> m_LastSetup;

  /** the listeners for changes. */
  protected HashSet<ChangeListener> m_ChangeListeners;

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

    m_FileChooser     = new WekaFileChooser();
    m_Renderer        = new AttributeValueCellRenderer();
    m_LastSetup       = new HashMap<>();
    m_ChangeListeners = new HashSet<>();
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
    notifyChangeListeners();
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
    final int			row;
    final int			actRow;
    final int			col;
    final int			actCol;
    final InstancesTableModel	instModel;

    menu      = new BasePopupMenu();
    row       = rowAtPoint(e.getPoint());
    actRow    = getActualRow(row);
    col       = tableHeader.columnAtPoint(e.getPoint());
    actCol    = col - 1;
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
	notifyChangeListeners();
      }
    });
    menu.add(menuitem);

    menuitem = new JMenuItem("Delete", GUIHelper.getIcon("delete.gif"));
    menuitem.addActionListener((ActionEvent ae) -> {
      int retVal = GUIHelper.showConfirmMessage(InstancesTable.this, "Delete attribute '" + getInstances().attribute(col - 1).name() + "'?");
      if (retVal == ApprovalDialog.APPROVE_OPTION) {
	instModel.deleteAttributeAt(col);
	setOptimalColumnWidth();
	notifyChangeListeners();
      }
    });
    menu.add(menuitem);

    InstancesTablePopupMenuItemHelper.addToPopupMenu(this, menu, false, actRow, actCol);

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
    JMenu			submenu;
    final int			row;
    final int			actRow;
    final int			col;
    final int			actCol;
    final int[]			selRows;
    final InstancesTableModel	instModel;
    final Range 		range;

    menu      = new BasePopupMenu();
    col       = columnAtPoint(e.getPoint());
    actCol    = col - 1;
    row       = rowAtPoint(e.getPoint());
    actRow    = getActualRow(row);
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
      notifyChangeListeners();
    });
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

    InstancesTablePopupMenuItemHelper.addToPopupMenu(this, menu, true, actRow, actCol);

    return menu;
  }

  /**
   * Exports the data.
   *
   * @param range	what data to export
   */
  protected void saveAs(TableRowRange range) {
    int 		retVal;
    AbstractFileSaver 	saver;
    File 		file;
    Instances 		original;
    Instances 		data;
    int[]		selRows;
    int			i;

    retVal = m_FileChooser.showSaveDialog(InstancesTable.this);
    if (retVal != WekaFileChooser.APPROVE_OPTION)
      return;

    saver    = m_FileChooser.getWriter();
    file     = m_FileChooser.getSelectedFile();
    original = getInstances();
    switch (range) {
      case ALL:
	data = original;
	break;

      case SELECTED:
	data    = new Instances(original, 0);
	selRows = getSelectedRows();
	for (i = 0; i < selRows.length; i++)
	  data.add((Instance) original.instance(getActualRow(selRows[i])).copy());
	break;

      case VISIBLE:
	data = new Instances(original, 0);
	for (i = 0; i < getRowCount(); i++)
	  data.add((Instance) original.instance(getActualRow(i)).copy());
	break;

      default:
	throw new IllegalStateException("Unhandled range type: " + range);
    }

    try {
      saver.setFile(file);
      saver.setInstances(data);
      saver.writeBatch();
    }
    catch (Exception ex) {
      GUIHelper.showErrorMessage(
	InstancesTable.this, "Failed to save data (" + range + ") to: " + file, ex);
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
   * Adds the listener to the pool of listeners that get notified when the data
   * changes.
   *
   * @param l		the listener to add
   */
  public void addChangeListener(ChangeListener l) {
    m_ChangeListeners.add(l);
  }

  /**
   * Removes the listener from the pool of listeners that get notified when the data
   * changes.
   *
   * @param l		the listener to remove
   */
  public void removeChangeListener(ChangeListener l) {
    m_ChangeListeners.remove(l);
  }

  /**
   * Notifies all the change listeners.
   */
  protected synchronized void notifyChangeListeners() {
    ChangeEvent		e;

    e = new ChangeEvent(this);
    for (ChangeListener l: m_ChangeListeners)
      l.stateChanged(e);
  }
}
