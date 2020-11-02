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
 * AbstractShapeTool.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.segmentation.tool;

import adams.gui.core.MouseUtils;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Ancestor for shaped tools.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractShapeTool
  extends AbstractTool {

  private static final long serialVersionUID = -4106386739843618810L;

  /** the last point that was drawn on. */
  protected Point m_LastPoint;

  /**
   * Draws the currently selected shape at the specified location.
   *
   * @param p		the location
   */
  protected void doDrawShape(Point p) {
    doDrawShape(Arrays.asList(p));
  }

  /**
   * Draws the currently selected shape at the specified locations.
   *
   * @param points	the locations
   */
  protected abstract void doDrawShape(List<Point> points);

  /**
   * Draws the currently selected shape at the specified location.
   * Skips drawing if no active layer.
   * Updates the canvas after a successful draw.
   *
   * @param p		the location
   */
  protected void drawShape(Point p) {
    if (!hasActiveLayer())
      return;
    doDrawShape(p);
    getCanvas().getOwner().getManager().update();
  }

  /**
   * Draws the shape along the line between the two points.
   *
   * @param from	the starting point
   * @param to		the end point
   */
  protected void drawShape(Point from, Point to) {
    double	diffX;
    double	diffY;
    int		steps;
    int		i;
    int		x;
    int		y;
    Point	curr;
    Point	prev;
    List<Point> points;

    if (!hasActiveLayer())
      return;

    diffX  = to.x - from.x;
    diffY  = to.y - from.y;
    steps  = (int) Math.max(Math.abs(diffX), Math.abs(diffY));
    curr   = from;
    points = new ArrayList<>();
    points.add(from);
    for (i = 1; i < steps - 1; i++) {
      prev = curr;
      x    = (int) Math.round(from.x + diffX / steps * i);
      y    = (int) Math.round(from.y + diffY / steps * i);
      curr = new Point(x, y);
      if (!curr.equals(prev))
        points.add(curr);
    }
    if (!points.get(points.size() - 1).equals(to))
      points.add(to);

    doDrawShape(points);

    getCanvas().getOwner().getManager().update();
  }

  /**
   * Creates the mouse listener to use.
   *
   * @return		the listener, null if not applicable
   */
  @Override
  protected ToolMouseAdapter createMouseListener() {
    if (m_Listener == null) {
      m_Listener = new ToolMouseAdapter(this) {
	@Override
	public void mousePressed(MouseEvent e) {
	  if (MouseUtils.isLeftClick(e)) {
	    drawShape(e.getPoint());
	    m_LastPoint = e.getPoint();
	    e.consume();
	  }
	  else {
	    super.mouseClicked(e);
	  }
	}
	@Override
	public void mouseReleased(MouseEvent e) {
	  super.mouseReleased(e);
	  m_LastPoint = null;
	}
      };
    }
    return m_Listener;
  }

  /**
   * Creates the mouse motion listener to use.
   *
   * @return		the listener, null if not applicable
   */
  @Override
  protected ToolMouseMotionAdapter createMouseMotionListener() {
    if (m_MotionListener == null) {
      m_MotionListener = new ToolMouseMotionAdapter(this) {
	@Override
	public void mouseDragged(MouseEvent e) {
	  if (getOwner().getCanvas().isLeftMouseDown()) {
	    if (m_LastPoint == null)
	      drawShape(e.getPoint());
	    else
	      drawShape(m_LastPoint, e.getPoint());
	    m_LastPoint = e.getPoint();
	    e.consume();
	  }
	  else {
	    super.mouseDragged(e);
	  }
	}
      };
    }
    return m_MotionListener;
  }
}
