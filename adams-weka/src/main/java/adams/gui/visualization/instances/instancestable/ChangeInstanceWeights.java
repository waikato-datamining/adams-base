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
 * ChangeAttributeWeight.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.instances.instancestable;

import adams.core.Utils;
import adams.gui.core.GUIHelper;
import adams.gui.visualization.instances.instancestable.InstancesTablePopupMenuItemHelper.TableState;
import gnu.trove.set.TDoubleSet;
import gnu.trove.set.hash.TDoubleHashSet;

/**
 * Allows the user to change the weight of the selected attribute.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ChangeInstanceWeights
  extends AbstractProcessSelectedRows
  implements ProcessRow {

  private static final long serialVersionUID = 8866236994813131751L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows the user to change the weight of the selected instances.";
  }

  /**
   * Returns the name of the icon.
   *
   * @return            the name, null if none available
   */
  public String getIconName() {
    return "weight.png";
  }

  /**
   * Returns the default name for the menu item.
   *
   * @return            the name
   */
  @Override
  protected String getDefaultMenuItem() {
    return "Change instance weights...";
  }

  /**
   * Returns the minimum number of rows that the plugin requires.
   *
   * @return		the minimum
   */
  @Override
  public int minNumRows() {
    return 1;
  }

  /**
   * Returns the maximum number of rows that the plugin requires.
   *
   * @return		the maximum, -1 for none
   */
  @Override
  public int maxNumRows() {
    return -1;
  }

  /**
   * Processes the specified rows.
   *
   * @param state	the table state
   * @return		true if successful
   */
  @Override
  protected boolean doProcessSelectedRows(TableState state) {
    TDoubleSet  	weights;
    double		weight;
    double		newWeight;
    String		newWeightStr;

    // same weights?
    weights = new TDoubleHashSet();
    for (int row: state.actRows)
      weights.add(state.table.getInstances().instance(row).weight());
    if (weights.size() == 1)
      weight = weights.toArray()[0];
    else
      weight = 1.0;
    newWeightStr = GUIHelper.showInputDialog(
      state.table.getParent(),
      "Please enter new weight for selected row" + (state.selRows.length != 1 ? "s" : "") + ": ",
      "" + weight);
    if (!Utils.isDouble(newWeightStr)) {
      GUIHelper.showErrorMessage(state.table.getParent(), "Weight has to be a valid numnber, provided: " + newWeightStr);
      return false;
    }

    newWeight = Utils.toDouble(newWeightStr);
    if (weight != newWeight) {
      state.table.addUndoPoint();
      for (int row: state.actRows)
	state.table.getInstances().instance(row).setWeight(newWeight);
    }


    return true;
  }

  /**
   * Processes the specified row.
   *
   * @param state	the table state
   * @return		true if successful
   */
  @Override
  public boolean processRow(TableState state) {
    return processSelectedRows(state);
  }
}
