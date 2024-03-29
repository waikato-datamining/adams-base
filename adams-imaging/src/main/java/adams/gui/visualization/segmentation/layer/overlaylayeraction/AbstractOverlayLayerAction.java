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
 * AbstractOverlayLayerAction.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.segmentation.layer.overlaylayeraction;

import adams.gui.core.ImageManager;
import adams.gui.visualization.segmentation.layer.OverlayLayer;

import javax.swing.ImageIcon;

/**
 * Ancestor for {@link OverlayLayer} actions.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractOverlayLayerAction {

  /**
   * The name to display in the menu.
   *
   * @return		the name
   */
  public abstract String getName();

  /**
   * Returns the icon to use in the menu.
   *
   * @return		the icon
   */
  public ImageIcon getIcon() {
    if (getIconName() == null)
      return ImageManager.getEmptyIcon();
    else
      return ImageManager.getIcon(getIconName());
  }

  /**
   * Returns the name of the icon to use.
   *
   * @return		the name, null for empty icon
   */
  public abstract String getIconName();

  /**
   * Performs the action.
   *
   * @param origin	the origin of the action
   */
  public abstract void performAction(OverlayLayer origin);
}
