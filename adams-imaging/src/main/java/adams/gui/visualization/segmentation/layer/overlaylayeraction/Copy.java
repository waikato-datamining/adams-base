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
 * Copy.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.segmentation.layer.overlaylayeraction;

import adams.gui.core.GUIHelper;
import adams.gui.visualization.segmentation.layer.OverlayLayer;

/**
 * Creates a copy of an existing layer.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Copy
  extends AbstractOverlayLayerAction {

  /**
   * The name to display in the menu.
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "Copy...";
  }

  /**
   * Returns the name of the icon to use.
   *
   * @return		the name, null for empty icon
   */
  @Override
  public String getIconName() {
    return "copy.gif";
  }

  /**
   * Performs the action.
   *
   * @param origin	the origin of the action
   */
  @Override
  public void performAction(OverlayLayer origin) {
    String		newName;
    OverlayLayer	newLayer;

    newName = GUIHelper.showInputDialog(
      origin.getRootPane(),
      "Please enter new name:",
      origin.getManager().suggestName(origin.getName()));
    if (newName == null)
      return;
    if (origin.getManager().hasLayer(newName)) {
      GUIHelper.showErrorMessage(origin.getRootPane(), "A layer with the name '" + newName + "' already exists!");
      return;
    }

    newLayer = origin.getManager().addOverlay(newName, origin.getColor().darker().darker(), origin.getAlpha(), origin.getBinaryImage());
    newLayer.setRemovable(origin.isRemovable());
    newLayer.setActionsAvailable(origin.hasActionsAvailable());
    origin.getManager().update();
  }
}
