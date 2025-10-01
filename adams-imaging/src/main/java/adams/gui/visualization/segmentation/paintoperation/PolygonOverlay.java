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
 * PolygonOverlay.java
 * Copyright (C) 2023-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.segmentation.paintoperation;

import adams.core.Utils;
import adams.gui.visualization.segmentation.tool.PolygonFill;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;

/**
 * Paints an overlay.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class PolygonOverlay
  extends AbstractPaintOperation {

  private static final long serialVersionUID = -4358423705617028678L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Paints the current polygon points of the " + Utils.classToString(PolygonFill.class) + ".";
  }

  /**
   * Performs a paint operation.
   *
   * @param g the graphics context
   */
  @Override
  protected void doPerformPaint(Graphics2D g) {
    Polygon 	poly;
    float 	stroke;
    PolygonFill pf;
    boolean	canPaint;
    int		x;
    int		y;
    int		s;

    pf       = (PolygonFill) getOwner();
    poly     = pf.getPolygon();
    canPaint = (poly != null) || (!pf.getPoints().isEmpty());
    if (!canPaint)
      return;

    stroke = 1.0f;
    if (g.getStroke() instanceof BasicStroke)
      stroke = ((BasicStroke) g.getStroke()).getLineWidth();
    g.setStroke(new BasicStroke(pf.getPolygonStroke()));
    g.setColor(pf.getPolygonColor());

    // markers
    s = pf.getMarkerSize();
    if (s > 1) {
      for (Point p : pf.getPoints()) {
	x = (int) p.getX();
	y = (int) p.getY();
	g.drawLine(x - s / 2, y, x + s / 2, y);
	g.drawLine(x, y - s / 2, x, y + s / 2);
      }
    }

    if (poly != null)
      g.drawPolygon(poly);

    g.setStroke(new BasicStroke(stroke));
  }
}
