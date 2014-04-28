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
 * XYPaintlet.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.canvas;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import adams.gui.event.PaintEvent.PaintMoment;
import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.plot.Axis;

/**
 * Simply paints ciircles for X and Y coordinates.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class XYPaintlet
  extends AbstractDataPoolPaintlet {

  /** for serialization. */
  private static final long serialVersionUID = -7454325864276285119L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Simply paints dots for the X and Y coordinates.";
  }

  /**
   * Returns the classes that the paintlet accepts.
   * 
   * @return		the classes of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Double[].class};
  }

  /**
   * Sets the minimum and maximum of the axes.
   */
  @Override
  public void prepareUpdate() {
    double	minX;
    double	maxX;
    double	minY;
    double	maxY;
    Double[]	coords;
    
    minX = Double.MAX_VALUE;
    maxX = Double.MIN_VALUE;
    minY = Double.MAX_VALUE;
    maxY = Double.MIN_VALUE;
    for (Object o: getDataPool()) {
      coords = (Double[]) o;
      if (coords[0] < minX)
	minX = coords[0];
      if (coords[0] > maxX)
	maxX = coords[0];
      if (coords[1] < minY)
	minY = coords[1];
      if (coords[1] > maxY)
	maxY = coords[1];
    }
    
    getPlot().getAxis(Axis.BOTTOM).setMinimum(minX);
    getPlot().getAxis(Axis.BOTTOM).setMaximum(maxX);
    getPlot().getAxis(Axis.LEFT).setMinimum(minY);
    getPlot().getAxis(Axis.LEFT).setMaximum(maxY);
  }

  /**
   * The paint routine of the paintlet.
   *
   * @param g		the graphics context to use for painting
   * @param moment	what {@link PaintMoment} is currently being painted
   */
  @Override
  public void performPaint(Graphics g, PaintMoment moment) {
    Double[]	coords;
    Graphics2D	g2d;
    AxisPanel	x;
    AxisPanel	y;

    x   = getPlot().getAxis(Axis.BOTTOM);
    y   = getPlot().getAxis(Axis.LEFT);
    g2d = (Graphics2D) g;
    g2d.setColor(Color.RED);
    g2d.setStroke(new BasicStroke(2.0f));
    for (Object o: getDataPool()) {
      coords = (Double[]) o;
      g2d.drawOval(x.valueToPos(coords[0]), y.valueToPos(coords[1]), 4, 4);
    }
  }
}
