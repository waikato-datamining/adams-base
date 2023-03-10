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
 * VertexShape.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.object.objectannotations.outline;

import java.awt.Graphics2D;

/**
 * Shapes for plotting vertices.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public enum VertexShape {
  /** no shape. */
  NONE,
  /** a square box. */
  BOX,
  /** a filled square box. */
  BOX_FILLED,
  /** a circle. */
  CIRCLE,
  /** a filled circle. */
  CIRCLE_FILLED,
  /** a triangle. */
  TRIANGLE,
  /** a filled triangle. */
  TRIANGLE_FILLED;

  /**
   * Plots the shape at the specified position.
   *
   * @param g		the graphics context
   * @param posX	the x position
   * @param posY	the y position
   * @param extent	the size of the marker
   */
  public void plot(Graphics2D g, int posX, int posY, int extent) {
    int[] 	x;
    int[] 	y;

    if (this == NONE)
      return;

    switch (this) {
      case BOX:
	g.drawRect(posX - (extent / 2), posY - (extent / 2), extent - 1, extent - 1);
        break;
      case BOX_FILLED:
	g.fillRect(posX - (extent / 2), posY - (extent / 2), extent - 1, extent - 1);
        break;
      case CIRCLE:
	g.drawArc(posX - (extent / 2), posY - (extent / 2), extent - 1, extent - 1, 0, 360);
        break;
      case CIRCLE_FILLED:
	g.fillArc(posX - (extent / 2), posY - (extent / 2), extent - 1, extent - 1, 0, 360);
	break;
      case TRIANGLE:
      case TRIANGLE_FILLED:
	x = new int[3];
	y = new int[3];
	x[0] = posX - (extent / 2);
	y[0] = posY + (extent / 2);
	x[1] = x[0] + extent;
	y[1] = y[0];
	x[2] = posX;
	y[2] = y[0] - extent;
	if (this == TRIANGLE)
	  g.drawPolygon(x, y, 3);
	else
	  g.fillPolygon(x, y, 3);
        break;
      default:
        throw new IllegalStateException("Unhandled vertex shape: " + this);
    }
  }
}
