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
 * ScatterPlotPolygonPaintlet.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.paintlet;

import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.stats.scatterplot.AbstractScatterPlot;

import java.awt.Graphics;

/**
 * Paints the currently selected polygon in the scatter plot.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ScatterPlotPolygonPaintlet
  extends AbstractOverlayPaintlet {

  private static final long serialVersionUID = 374355591590336935L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Paints the currently selected polygon in the scatter plot.";
  }

  /**
   * Draw the overlay onto the scatter plot, only calculates the
   * value to position in this method
   *
   * @param g Graphics drawn on
   */
  @Override
  protected void drawData(Graphics g) {
    int[]		x;
    int[]		y;
    int			i;
    AbstractScatterPlot	parent;
    AxisPanel		axisX;
    AxisPanel		axisY;

    parent = (AbstractScatterPlot) m_Panel;
    if (parent == null)
      return;
    if (parent.getSelection().isEmpty())
      return;

    g.setColor(m_Color);

    axisX = m_Panel.getPlot().getAxis(Axis.BOTTOM);
    axisY = m_Panel.getPlot().getAxis(Axis.LEFT);
    x = new int[parent.getSelection().size()];
    y = new int[x.length];
    for (i = 0; i < x.length; i++) {
      x[i] = axisX.valueToPos(parent.getSelection().get(i).getX());
      y[i] = axisY.valueToPos(parent.getSelection().get(i).getY());
    }

    if (x.length == 1)
      g.drawOval(x[0] - 1, y[0] - 1, 3, 3);
    else if (x.length == 2)
      g.drawLine(x[0], y[0], x[1], y[1]);
    else
      g.drawPolygon(x, y, x.length);
  }
}
