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
 * Markers.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.segmentation.layer;

import adams.core.logging.CustomLoggingLevelObject;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages markers.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Markers
  extends CustomLoggingLevelObject {

  private static final long serialVersionUID = 3127191904578591061L;

  /**
   * Enum for the marker shape to plot around the marker points.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   */
  public enum Shape {
    /** nothing. */
    NONE,
    /** a square box. */
    BOX,
    /** a circle. */
    CIRCLE,
    /** a triangle. */
    TRIANGLE
  }

  /** the owner. */
  protected LayerManager m_Owner;

  /** the marker points to draw. */
  protected List<Point> m_Points;

  /** the marker shape. */
  protected Shape m_Shape;

  /** the color for the markers. */
  protected Color m_Color;

  /** the maximum width/height of the shape to plot around the marker points. */
  protected int m_Extent;

  /**
   * Initializes the marker handling.
   */
  public Markers(LayerManager owner) {
    m_Owner  = owner;
    m_Points = new ArrayList<>();
    m_Shape  = Shape.CIRCLE;
    m_Color  = Color.RED;
    m_Extent = 7;
  }

  /**
   * Returns the owning layer manager.
   *
   * @return		the owner
   */
  public LayerManager getOwner() {
    return m_Owner;
  }

  /**
   * Removes all marker points.
   */
  public void clear() {
    m_Points.clear();
    update(true);
  }

  /**
   * Returns the number of marker points.
   *
   * @return		the number of points
   */
  public int size() {
    return m_Points.size();
  }

  /**
   * Adds the marker point.
   *
   * @param p		the point to add
   */
  public void add(Point p) {
    m_Points.add(p);
    update();
  }

  /**
   * Sets the shape to use for markers.
   *
   * @param value	the shape
   */
  public void setShape(Shape value) {
    m_Shape = value;
    update();
  }

  /**
   * Returns the shape in use for markers.
   *
   * @return		the shape
   */
  public Shape getShape() {
    return m_Shape;
  }

  /**
   * Sets the color to use for markers.
   *
   * @param value	the color
   */
  public void setColor(Color value) {
    m_Color = value;
    update();
  }

  /**
   * Returns the color in use for markers.
   *
   * @return		the color
   */
  public Color getColor() {
    return m_Color;
  }

  /**
   * Sets the size of the markers.
   *
   * @param value	the extent
   */
  public void setExtent(int value) {
    m_Extent = value;
    update();
  }

  /**
   * Returns the size of the markers.
   *
   * @return		the extent
   */
  public int getExtent() {
    return m_Extent;
  }

  /**
   * Draws the markers.
   *
   * @param g		the graphics context
   */
  public void drawMarkers(Graphics2D g) {
    int		currX;
    int		currY;
    int 	prevX;
    int 	prevY;

    if (m_Points.size() == 0)
      return;

    g.setColor(m_Color);

    prevX = 0;
    prevY = 0;

    for (Point p: m_Points) {
      currX = (int) p.getX();
      currY = (int) p.getY();

      if (m_Shape != Shape.NONE) {
	if (Math.sqrt(Math.pow(currX - prevX, 2) + Math.pow(currY - prevY, 2)) > m_Extent * 2) {
	  if (m_Shape == Shape.BOX) {
	    g.drawRect(
	      currX - (m_Extent / 2),
	      currY - (m_Extent / 2),
	      m_Extent - 1,
	      m_Extent - 1);
	  }
	  else if (m_Shape == Shape.CIRCLE) {
	    g.drawArc(
	      currX - (m_Extent / 2),
	      currY - (m_Extent / 2),
	      m_Extent - 1,
	      m_Extent - 1,
	      0,
	      360);
	  }
	  else if (m_Shape == Shape.TRIANGLE) {
	    int[] x = new int[3];
	    int[] y = new int[3];
	    x[0] = currX - (m_Extent / 2);
	    y[0] = currY + (m_Extent / 2);
	    x[1] = x[0] + m_Extent;
	    y[1] = y[0];
	    x[2] = currX;
	    y[2] = y[0] - m_Extent;
	    g.drawPolygon(x, y, 3);
	  }

	  prevX = currX;
	  prevY = currY;
	}
      }
    }
  }

  /**
   * Triggers an update (if any markers).
   */
  public void update() {
    update(false);
  }

  /**
   * Triggers an update.
   */
  public void update(boolean force) {
    if (getOwner() != null) {
      if (force || (m_Points.size() > 0))
	getOwner().update();
    }
  }
}
