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
 * Invert.java
 * Copyright (C) 2020-2025 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.segmentation.layer.overlaylayeraction;

import adams.gui.visualization.segmentation.layer.OverlayLayer;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Inverts background and foreground.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Invert
  extends AbstractOverlayLayerAction {

  /**
   * The name to display in the menu.
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "Invert";
  }

  /**
   * Returns the name of the icon to use.
   *
   * @return		the name, null for empty icon
   */
  @Override
  public String getIconName() {
    return "invert.png";
  }

  /**
   * Updates the image using the current color.
   */
  protected void swap(BufferedImage image) {
    int		black;
    int		other;
    int[]	pixels;
    int		i;

    if (image == null)
      return;

    black  = Color.BLACK.getRGB();
    pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
    other  = black;
    for (i = 0; i < pixels.length; i++) {
      if (pixels[i] != black) {
        other = pixels[i];
        break;
      }
    }
    for (i = 0; i < pixels.length; i++) {
      if (pixels[i] == black)
	pixels[i] = other;
      else
        pixels[i] = black;
    }
    image.setRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
  }

  /**
   * Performs the action.
   *
   * @param origin	the origin of the action
   */
  @Override
  public void performAction(OverlayLayer origin) {
    origin.getManager().addUndoPoint(getName());
    swap(origin.getImage());
    origin.getManager().update();
  }
}
