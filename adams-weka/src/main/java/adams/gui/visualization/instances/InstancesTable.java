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
import adams.gui.core.UndoHandlerWithQuickAccess;
import adams.gui.dialog.ApprovalDialog;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Undoable;
import weka.core.converters.AbstractFileSaver;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;

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
   * Shows a popup menu for the header.
   *
   * @param e		the event
   */
  protected void showHeaderPopup(MouseEvent e) {
    JPopupMenu			menu;
    JMenuItem			menuitem;
    final int			col;
    final InstancesTableModel	instModel;

    menu      = new JPopupMenu();
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

    menu.show(this, e.getX(), e.getY());
  }

  /**
   * Shows a popup menu for the cells.
   *
   * @param e		the event
   */
  protected void showCellPopup(MouseEvent e) {
    BasePopupMenu 	menu;

    menu = createCellPopup(e);
    menu.show(this, e.getX(), e.getY());
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
