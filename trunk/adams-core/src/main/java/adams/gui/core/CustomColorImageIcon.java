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
 * CustomColorImageIcon.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * A specialized ImageIcon that simply fills in a rectangle with the specified
 * color.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CustomColorImageIcon
  extends ImageIcon {

  /**
   * Initializes the icon.
   *
   * @param width	the width
   * @param height	the height
   * @param color	the color for filling
   */
  public CustomColorImageIcon(int width, int height, Color color) {
    super(createImage(width, height, color));
  }

  /**
   * Creates a BufferedImage with the specified dimensions and fills it with
   * the color.
   *
   * @param width	the width
   * @param height	the height
   * @param color	the color for filling
   * @return		the image
   */
  public static BufferedImage createImage(int width, int height, Color color) {
    BufferedImage	result;
    Graphics		g;

    result = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
    g      = result.getGraphics();
    g.setColor(color);
    g.fillRect(0, 0, width, height);
    g.dispose();

    return result;
  }
}
