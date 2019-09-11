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
import adams.gui.core.TableRowRange;
import adams.gui.visualization.instances.instancestable.InstancesTablePopupMenuItemHelper.TableState;
import weka.core.Attribute;

/**
 * Allows the user to change the weight of the selected attribute.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ChangeAttributeWeight
  extends AbstractProcessColumn {

  private static final long serialVersionUID = 8866236994813131751L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows the user to change the weight of the selected attribute.";
  }

  /**
   * Checks whether the row range can be handled.
   *
   * @param range	the range to check
   * @return		true if handled
   */
  @Override
  public boolean handlesRowRange(TableRowRange range) {
    return true;
  }

  /**
   * Returns the name for the menu item.
   *
   * @return            the name
   */
  @Override
  public String getMenuItem() {
    return "Change attribute weight...";
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
   * Processes the specified column.
   *
   * @param state	the table state
   * @return		true if successful
   */
  @Override
  protected boolean doProcessColumn(TableState state) {
    double	weight;
    String 	newWeightStr;
    double	newWeight;
    Attribute 	att;

    if (state.actCol == -1)
      return false;

    att       = state.table.getInstances().attribute(state.actCol);
    weight    = att.weight();
    newWeightStr = GUIHelper.showInputDialog(
      state.table.getParent(),
      "Please enter new weight for '" + att.name() + "': ",
      "" + weight);
    if (!Utils.isDouble(newWeightStr)) {
      GUIHelper.showErrorMessage(state.table.getParent(), "Weight has to be a valid numnber, provided: " + newWeightStr);
      return false;
    }

    newWeight = Utils.toDouble(newWeightStr);
    if (weight != newWeight) {
      state.table.addUndoPoint();
      att.setWeight(newWeight);
    }

    return true;
  }
}
