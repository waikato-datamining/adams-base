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
 * RectanglePainter.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.image.selectionshape;

import adams.gui.visualization.image.ImagePanel.PaintPanel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.List;

/**
 * Paints a simple rectangle, using the top-left and bottom-right coordinates.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RectanglePainter
  extends AbstractSelectionShapePainter
  implements ColorSelectionShapePainter {

  private static final long serialVersionUID = 2794117849848652523L;

  /** the color to use. */
  protected Color m_Color;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Paints a simple rectangle, using the top-left and bottom-right coordinates.";
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
   * Returns whether the current point is at leats the specified distance
   * away from the last one.
   *
   * @param owner	the panel this shape is used with
   * @param trace	the trace so far
   * @param p		the point to check
   * @return		true if can be added
   */
  public boolean canAddTracePoint(PaintPanel owner, List<Point> trace, Point p) {
    return false;
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
  protected void doPaintSelectionShape(PaintPanel owner, Graphics g, Point topLeft, Point bottomRight, List<Point> trace) {
    int	topX;
    int	bottomX;
    int	topY;
    int	bottomY;
    int	tmp;

    g.setColor(m_Color);

    topX    = (int) owner.mouseToPixelLocation(topLeft).getX();
    topY    = (int) owner.mouseToPixelLocation(topLeft).getY();
    bottomX = (int) owner.mouseToPixelLocation(bottomRight).getX();
    bottomY = (int) owner.mouseToPixelLocation(bottomRight).getY();

    // swap necessary?
    if (topX > bottomX) {
      tmp     = topX;
      topX    = bottomX;
      bottomX = tmp;
    }
    if (topY > bottomY) {
      tmp     = topY;
      topY    = bottomY;
      bottomY = tmp;
    }

    g.drawRect(
      topX,
      topY,
      (bottomX - topX + 1),
      (bottomY - topY + 1));
  }
}
