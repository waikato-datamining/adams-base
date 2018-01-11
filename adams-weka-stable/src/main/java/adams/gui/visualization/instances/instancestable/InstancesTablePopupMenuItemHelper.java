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
 * InstancesTablePopupMenuItemHelper.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.instances.instancestable;

import adams.core.ClassLister;
import adams.gui.core.ConsolePanel;
import adams.gui.core.GUIHelper;
import adams.gui.visualization.instances.InstancesTable;
import weka.core.Instances;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Helper class for constructing popup menus for the InstancesTable.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InstancesTablePopupMenuItemHelper {

  /**
   * Returns a sorted list of popup menu items for the specified superclass.
   *
   * @param cls		the superclass
   * @return		the list
   */
  protected static List<InstancesTablePopupMenuItem> getItems(Class cls) {
    List<InstancesTablePopupMenuItem>		result;
    String[]					classes;

    result = new ArrayList<>();
    classes = ClassLister.getSingleton().getClassnames(cls);
    for (String c : classes) {
      try {
	result.add((InstancesTablePopupMenuItem) Class.forName(c).newInstance());
      }
      catch (Exception e) {
	ConsolePanel.getSingleton().append("Failed to instantiate InstancesTable menu item: " + c, e);
      }
    }

    if (result.size() > 1)
      Collections.sort(result);

    return result;
  }

  /**
   * Adds the appropriate action to the menuitem.
   *
   * @param table	the table this menu is for
   * @param isRow	whether this is for a row or a column
   * @param actRow	the current actual row
   * @param selRow 	the selected row in the table
   * @param column	the current column
   * @param data	the instances to use
   * @param menuitem	the menuitem to add the action to
   * @param item	the menu item scheme
   */
  protected static void addAction(final InstancesTable table, final Instances data, boolean isRow, final int actRow, final int selRow, final int column, final JMenuItem menuitem, final InstancesTablePopupMenuItem item) {
    if (isRow) {
      if (item instanceof PlotRow) {
	menuitem.addActionListener((ActionEvent e) -> ((PlotRow) item).plotRow(table, data, actRow, selRow));
      }
      else if (item instanceof ProcessRow) {
	menuitem.addActionListener((ActionEvent e) -> ((ProcessRow) item).processRow(table, data, actRow, selRow));
      }
      else if (item instanceof ProcessCell) {
	menuitem.addActionListener((ActionEvent e) -> ((ProcessCell) item).processCell(table, data, actRow, selRow, column));
      }
    }
    else {
      if (item instanceof PlotColumn) {
	menuitem.addActionListener((ActionEvent e) -> ((PlotColumn) item).plotColumn(table, data, column));
      }
      else if (item instanceof ProcessColumn) {
	menuitem.addActionListener((ActionEvent e) -> ((ProcessColumn) item).processColumn(table, data, column));
      }
    }
  }

  /**
   * Adds the available menu items to the menu.
   *
   * @param table	the table this menu is for
   * @param isRow	whether this is for a row or a column
   * @param actRow	the current actual row
   * @param selRow 	the selected row in the table
   * @param column	the current column
   * @param menu	the menu to add the items to
   * @param items	the available schemes
   */
  protected static void addToPopupMenu(InstancesTable table, boolean isRow, int actRow, int selRow, int column, JPopupMenu menu, List<InstancesTablePopupMenuItem> items) {
    JMenuItem		menuitem;
    Instances		data;

    if (items.size() == 0)
      return;

    data = table.getInstances();

    if (menu.getComponent(menu.getComponentCount() - 1) instanceof JMenuItem)
      menu.addSeparator();
    for (InstancesTablePopupMenuItem item: items) {
      menuitem = new JMenuItem(item.getMenuItem());
      if (item.getIconName() != null)
        menuitem.setIcon(GUIHelper.getIcon(item.getIconName()));
      addAction(table, data, isRow, actRow, selRow, column, menuitem, item);
      menu.add(menuitem);
    }
  }

  /**
   * Adds the available menu items to the menu.
   *
   * @param table	the table this menu is for
   * @param menu	the menu to add the items to
   * @param isRow	whether this is for a row or a column
   * @param actRow	the current actual row
   * @param selRow 	the selected row in the table
   * @param column	the current column
   */
  public static void addToPopupMenu(InstancesTable table, JPopupMenu menu, boolean isRow, int actRow, int selRow, int column) {
    menu.addSeparator();
    if (isRow) {
      addToPopupMenu(table, true, actRow, selRow, column, menu, getItems(PlotRow.class));
      addToPopupMenu(table, true, actRow, selRow, column, menu, getItems(ProcessRow.class));
      addToPopupMenu(table, true, actRow, selRow, column, menu, getItems(ProcessCell.class));
    }
    else {
      addToPopupMenu(table, false, actRow, selRow, column, menu, getItems(PlotColumn.class));
      addToPopupMenu(table, false, actRow, selRow, column, menu, getItems(ProcessColumn.class));
    }
  }
}
