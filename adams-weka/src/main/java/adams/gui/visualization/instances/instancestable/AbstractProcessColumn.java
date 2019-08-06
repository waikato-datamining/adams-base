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
 * AbstractProcessColumn.java
 * Copyright (C) 2016-2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.instances.instancestable;

import adams.core.option.AbstractOptionHandler;
import adams.gui.core.GUIHelper;
import adams.gui.visualization.instances.instancestable.InstancesTablePopupMenuItemHelper.TableState;
import weka.core.Instances;

/**
 * Ancestor for plugins that process a column.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractProcessColumn
  extends AbstractOptionHandler
  implements ProcessColumn {

  private static final long serialVersionUID = -1050881505327794503L;

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
   * Hook method for checks before attempting processing.
   *
   * @param state	the table state
   * @return		null if passed, otherwise error message
   */
  protected String check(TableState state) {
    Instances data;

    if (state.table == null)
      return "No source table available!";
    if (state.actCol < 0)
      return "Negative column index!";
    data = state.table.getInstances();
    if (state.actCol >= data.numAttributes())
      return "Column index too large: " + (state.actCol + 1) + " > " + data.numAttributes();
    return null;
  }

  /**
   * Processes the specified column.
   *
   * @param state	the table state
   * @return		true if successful
   */
  protected abstract boolean doProcessColumn(TableState state);

  /**
   * Processes the specified column.
   *
   * @param state	the table state
   * @return		true if successful
   */
  public boolean processColumn(TableState state) {
    boolean	result;
    String	error;

    error = check(state);
    result = (error == null);
    if (result)
      result = doProcessColumn(state);
    else
      GUIHelper.showErrorMessage(state.table, "Failed to process column #" + (state.actCol+1) + "\n" + error);

    return result;
  }
}
