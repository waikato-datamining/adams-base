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
 * AbstractPlotSelectedRows.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.instances.instancestable;

import adams.core.Utils;
import adams.core.option.AbstractOptionHandler;
import adams.gui.core.GUIHelper;
import adams.gui.visualization.instances.InstancesTable;
import weka.core.Instances;

/**
 * Ancestor for plugins that plot rows.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractPlotSelectedRows
  extends AbstractOptionHandler
  implements PlotSelectedRows {

  private static final long serialVersionUID = 7979833588446267882L;

  /** the menu item caption. */
  protected String m_MenuItemText;

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "menu-item-text", "menuItemText",
      "");
  }

  /**
   * Sets the (optional) custom menu item text.
   *
   * @param value 	the text
   */
  public void setMenuItemText(String value) {
    m_MenuItemText = value;
    reset();
  }

  /**
   * Returns the (optional) cuistom menu item text.
   *
   * @return 		the text
   */
  public String getMenuItemText() {
    return m_MenuItemText;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String menuItemTextTipText() {
    return "The (optional) custom text for the menu item.";
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
   * Returns the default name for the menu item.
   *
   * @return            the name
   */
  protected abstract String getDefaultMenuItem();

  /**
   * Returns the name for the menu item.
   *
   * @return            the name
   */
  @Override
  public String getMenuItem() {
    if ((m_MenuItemText == null) || m_MenuItemText.isEmpty())
      return getDefaultMenuItem();
    else
      return m_MenuItemText;
  }

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
   * Hook method for checks before attempting processing.
   *
   * @param table	the source table
   * @param data	the spreadsheet to use as basis
   * @param actRows	the rows in the spreadsheet
   * @param selRows 	the selected rows in the table
   * @return		null if passed, otherwise error message
   */
  protected String check(InstancesTable table, Instances data, int[] actRows, int[] selRows) {
    if (table == null)
      return "No source table available!";
    if (data == null)
      return "No Instances available!";
    if (actRows.length == 0)
      return "No rows selected!";
    return null;
  }

  /**
   * Plots the specified rows.
   *
   * @param table	the source table
   * @param data	the Instances to use as basis
   * @param actRows	the actual rows in the Instances
   * @param selRows	the selected rows in the table
   * @return		true if successful
   */
  protected abstract boolean doPlotSelectedRows(InstancesTable table, Instances data, int[] actRows, int[] selRows);

  /**
   * Plots the specified rows.
   *
   * @param table	the source table
   * @param data	the Instances to use as basis
   * @param actRows	the actual rows in the Instances
   * @param selRows	the selected rows in the table
   * @return		true if successful
   */
  public boolean processSelectedRows(InstancesTable table, Instances data, int[] actRows, int[] selRows) {
    boolean	result;
    String	error;

    error = check(table, data, actRows, selRows);
    result = (error == null);
    if (result)
      result = doPlotSelectedRows(table, data, actRows, selRows);
    else
      GUIHelper.showErrorMessage(table, "Failed to plot rows (0-based indices): " + Utils.arrayToString(actRows) + "\n" + error);

    return result;
  }
}
