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
 * SelectionShapePainter.java
 * Copyright (C) 2018-2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.image.selectionshape;

import adams.core.option.OptionHandler;
import adams.gui.visualization.image.ImagePanel.PaintPanel;

import java.awt.Graphics;
import java.awt.Point;
import java.util.List;

/**
 * Interface for classes that can paint the selection.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface SelectionShapePainter
  extends OptionHandler {

  /**
   * Returns whether the current point is at leats the specified distance
   * away from the last one.
   *
   * @param owner	the panel this shape is used with
   * @param trace	the trace so far
   * @param p		the point to check
   * @return		true if can be added
   */
  public boolean canAddTracePoint(PaintPanel owner, List<Point> trace, Point p);

  /**
   * Paints the selection shape.
   *
   * @param owner	the panel this shape is used with
   * @param g		the graphics context
   * @param topLeft 	the top-left corner
   * @param bottomRight	the bottom-right corner
   * @param trace	the mouse movement trace
   */
  public void paintSelectionShape(PaintPanel owner, Graphics g, Point topLeft, Point bottomRight, List<Point> trace);
}
