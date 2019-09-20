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
 * PolygonPainter.java
 * Copyright (C) 2018-2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.image.selectionshape;

import adams.gui.visualization.image.ImagePanel.PaintPanel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.List;

/**
 * Paints a polygon from the mouse point trace.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PolygonPainter
  extends AbstractStrokeSelectionShapePainter
  implements ColorSelectionShapePainter {

  private static final long serialVersionUID = 2794117849848652523L;

  /** the color to use. */
  protected Color m_Color;

  /** the minimum distance in pixels that the trace pixels must be apart. */
  protected int m_MinDistance;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Paints a polygon from the mouse point trace.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "color", "color",
      Color.GRAY);

    m_OptionManager.add(
      "min-distance", "minDistance",
      10, 1, null);
  }

  /**
   * Sets the color to use.
   *
   * @param value 	the color
   */
  public void setColor(Color value) {
    m_Color = value;
    reset();
  }

  /**
   * Returns the color to use.
   *
   * @return 		the color
   */
  public Color getColor() {
    return m_Color;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorTipText() {
    return "The color to use for drawing.";
  }

  /**
   * Sets the minimum distance in pixels that a new point must be away from
   * the last trace point.
   *
   * @param value 	the distance
   */
  public void setMinDistance(int value) {
    m_MinDistance = value;
    reset();
  }

  /**
   * Returns the minimum distance in pixels that a new point must be away from
   * the last trace point.
   *
   * @return 		the distance
   */
  public int getMinDistance() {
    return m_MinDistance;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String minDistanceTipText() {
    return "The minimum distance in pixels that a new point must be away from the last trace point.";
  }

  /**
   * Returns whether the current point is at leats the specified distance
   * away from the last one.
   *
   * @param owner	the panel this shape is used with
   * @param trace	the trace so far
   * @param p		the point to check
   * @return		true if can be added
   */
  public boolean canAddTracePoint(PaintPanel owner, List<Point> trace, Point p) {
      Point	last;
      double	dist;

      if (trace.size() == 0)
        return true;

      last = trace.get(trace.size() - 1);
      dist = Math.sqrt(Math.pow(p.getX() - last.getX(), 2) + Math.pow(p.getY() - last.getY(), 2));

      return (dist >= m_MinDistance);
  }

  /**
   * Checks whether painting is possible.
   *
   * @param owner	the panel this shape is used with
   * @param g		the graphics context
   * @param topLeft 	the top-left corner
   * @param bottomRight	the bottom-right corner
   * @param trace	the mouse movement trace
   * @return		true if painting is possible
   */
  @Override
  protected boolean canPaint(PaintPanel owner, Graphics g, Point topLeft, Point bottomRight, List<Point> trace) {
    return owner.isSelecting() && owner.isDragged();
  }

  /**
   * Paints the selection shape.
   *
   * @param owner	the panel this shape is used with
   * @param g		the graphics context
   * @param topLeft 	the top-left corner
   * @param bottomRight	the bottom-right corner
   * @param trace	the mouse movement trace
   */
  @Override
  protected void doPaintSelectionShapeWithStroke(PaintPanel owner, Graphics g, Point topLeft, Point bottomRight, List<Point> trace) {
    int[]	x;
    int[]	y;
    int		i;
    Point	actual;

    g.setColor(m_Color);

    x = new int[trace.size()];
    y = new int[trace.size()];
    for (i = 0; i < trace.size(); i++) {
      actual = owner.mouseToPixelLocation(trace.get(i));
      x[i] = (int) actual.getX();
      y[i] = (int) actual.getY();
    }

    g.drawPolygon(x, y, x.length);
  }
}
