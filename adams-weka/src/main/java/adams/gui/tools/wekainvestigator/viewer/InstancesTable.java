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

package adams.gui.tools.wekainvestigator.viewer;

import adams.core.Range;
import adams.gui.core.GUIHelper;
import adams.gui.core.SortableAndSearchableTable;
import adams.gui.dialog.ApprovalDialog;
import weka.core.Instances;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

/**
 * Table for displaying Instances objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InstancesTable
  extends SortableAndSearchableTable {

  private static final long serialVersionUID = -1408763296714340976L;

  /** the renderer to use. */
  protected AttributeValueCellRenderer m_Renderer = new AttributeValueCellRenderer();

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
    int		i;

    if (model instanceof InstancesTableModel) {
      super.setModel(model);
    }
    else {
      throw new IllegalArgumentException(
	"Model must be derived from " + InstancesTableModel.class.getName() + ", provided: " + model.getClass().getName());
    }
  }

  /**
   * Returns the underlying data.
   *
   * @return		the data
   */
  public Instances getInstances() {
    return ((InstancesTableModel) getModel()).getInstances();
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
    JPopupMenu	menu;
    JMenuItem	menuitem;
    final int	col;

    menu = new JPopupMenu();
    col  = tableHeader.columnAtPoint(e.getPoint());

    menuitem = new JMenuItem("Rename...");
    menuitem.addActionListener((ActionEvent ae) -> {
      String newName = GUIHelper.showInputDialog(
	InstancesTable.this, "Please enter new name", getInstances().attribute(col).name());
      if (newName != null)
	((InstancesTableModel) getUnsortedModel()).renameAttributeAt(col, newName);
    });
    menu.add(menuitem);

    menuitem = new JMenuItem("Delete");
    menuitem.addActionListener((ActionEvent ae) -> {
      int retVal = GUIHelper.showConfirmMessage(InstancesTable.this, "Delete attribute '" + getInstances().attribute(col).name() + "'?");
      if (retVal == ApprovalDialog.APPROVE_OPTION)
	((InstancesTableModel) getUnsortedModel()).deleteAttributeAt(col);
    });
    menu.add(menuitem);

    // TODO undo

    menu.show(this, e.getX(), e.getY());
  }

  /**
   * Shows a popup menu for the cells.
   *
   * @param e		the event
   */
  protected void showCellPopup(MouseEvent e) {
    JPopupMenu	menu;
    JMenuItem	menuitem;
    final int	col;
    final int	row;
    final int[]	selRows;

    menu    = new JPopupMenu();
    col     = tableHeader.columnAtPoint(e.getPoint());
    row     = rowAtPoint(e.getPoint());
    selRows = getSelectedRows();

    menuitem = new JMenuItem("Delete");
    menuitem.addActionListener((ActionEvent ae) -> {
      Range range = new Range();
      range.setMax(getRowCount());
      range.setIndices(selRows);
      String msg = "Delete row";
      if (selRows.length > 1)
	msg += "s";
      msg += " " + range.getRange() + "?";
      int retVal = GUIHelper.showConfirmMessage(InstancesTable.this, msg);
      if (retVal == ApprovalDialog.APPROVE_OPTION) {
	int[] actRows = new int[selRows.length];
	for (int i = 0; i < selRows.length; i++)
	  actRows[i] = getActualRow(selRows[i]);
	((InstancesTableModel) getUnsortedModel()).deleteInstances(actRows);
      }
    });
    menu.add(menuitem);

    // TODO undo

    menu.show(this, e.getX(), e.getY());
  }
}
