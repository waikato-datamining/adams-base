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
 * Default.java
 * Copyright (C) 2025 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.segmentation.layer.overlaylayeraction;

import adams.gui.visualization.segmentation.layer.OverlayLayer;

/**
 * Switches to default settings.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Default
  extends AbstractOverlayLayerAction {

  /**
   * The name to display in the menu.
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "Use default";
  }

  /**
   * Returns the name of the icon to use.
   *
   * @return		the name, null for empty icon
   */
  @Override
  public String getIconName() {
    return "new.gif";
  }

  /**
   * Performs the action.
   *
   * @param origin	the origin of the action
   */
  @Override
  public void performAction(OverlayLayer origin) {
    origin.useDefault();
  }
}
