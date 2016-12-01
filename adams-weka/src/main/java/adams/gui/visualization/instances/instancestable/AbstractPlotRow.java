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
 * AbstractPlotRow.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.instances.instancestable;

import adams.core.option.AbstractOptionHandler;
import adams.gui.core.GUIHelper;
import adams.gui.visualization.instances.InstancesTable;
import weka.core.Instances;

/**
 * Ancestor for plugins that plot a row.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractPlotRow
  extends AbstractOptionHandler
  implements PlotRow {

  private static final long serialVersionUID = -1128790870421132832L;

  /**
   * For sorting the menu items.
   *
   * @param o       the other item
   * @return        -1 if less than, 0 if equal, +1 if larger than this
   *                menu item name
   */
  @Override
  public int compareTo(InstancesTablePopupMenuItem o) {
    return getMenuItem().compareTo(o.getMenuItem());
  }

  /**
   * Returns the name of the icon.
   *
   * @return            the name, null if none available
   */
  public String getIconName() {
    return null;
  }

  /**
   * Hook method for checks before attempting the plot.
   *
   * @param table	the source table
   * @param data	the instances to use as basis
   * @param actRow	the actual row in the instances
   * @param selRow 	the selected row in the table
   * @return		null if passed, otherwise error message
   */
  protected String check(InstancesTable table, Instances data, int actRow, int selRow) {
    if (table == null)
      return "No source table available!";
    if (data == null)
      return "No instances available!";
    if (actRow < 0)
      return "Negative row index!";
    if (actRow >= data.numInstances())
      return "Row index too large: " + (actRow + 1) + " > " + data.numInstances();
    return null;
  }

  /**
   * Plots the specified row.
   *
   * @param table	the source table
   * @param data	the instances to use as basis
   * @param actRow	        the row in the instances
   * @return		true if successful
   */
  protected abstract boolean doPlotRow(InstancesTable table, Instances data, int actRow);

  /**
   * Plots the specified row.
   *
   * @param table	the source table
   * @param data	the instances to use as basis
   * @param actRow	the actual row in the instances
   * @param selRow 	the selected row in the table
   * @return		true if successful
   */
  public boolean plotRow(InstancesTable table, Instances data, int actRow, int selRow) {
    boolean	result;
    String	error;

    error = check(table, data, actRow, selRow);
    result = (error == null);
    if (result)
      result = doPlotRow(table, data, actRow);
    else
      GUIHelper.showErrorMessage(table, "Failed to plot row #" + (actRow +1) + "\n" + error);

    return result;
  }
}
