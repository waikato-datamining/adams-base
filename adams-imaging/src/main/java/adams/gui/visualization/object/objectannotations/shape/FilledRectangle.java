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
 * FilledRectangle.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.object.objectannotations.shape;

import adams.flow.transformer.locateobjects.LocatedObject;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * Plots a filled rectangle.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class FilledRectangle
  extends AbstractShapePlotter {

  private static final long serialVersionUID = 5516830542182177734L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Plots a filled rectangle.";
  }

  /**
   * Plots the outline.
   *
   * @param object the object to plot
   * @param color  the color to use
   * @param g      the graphics context
   */
  @Override
  protected void doPlotShape(LocatedObject object, Color color, Graphics2D g) {
    Rectangle rect;

    rect = object.getRectangle();
    g.setColor(color);
    g.fillRect(rect.x, rect.y, rect.width, rect.height);
  }
}
