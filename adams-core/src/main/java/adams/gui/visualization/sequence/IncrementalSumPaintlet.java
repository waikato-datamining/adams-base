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
 * IncrementalSumPaintlet.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.sequence;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import adams.data.sequence.XYSequence;
import adams.data.sequence.XYSequencePoint;
import adams.gui.event.PaintEvent.PaintMoment;
import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.plot.Axis;

/**
 * A paintlet for painting a line plot of a sequence.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9308 $
 */
public class IncrementalSumPaintlet
  extends LinePaintlet {

  /** for serialization. */
  private static final long serialVersionUID = 8242948176244747138L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Paintlet that sums up the individual Y values of a sequence and "
	+ "plots these data points as line. The data points represent "
	+ "percentages and are scaled to the maximum on the Y axis, in order "
	+ "to be visible in the plot.";
  }

  /**
   * Draws the data with the given color.
   *
   * @param g		the graphics context
   * @param moment	the paint moment
   * @param data	the data to draw
   * @param color	the color to draw in
   * @param marker	the type of marker to draw
   */
  @Override
  protected void drawData(Graphics g, PaintMoment moment, XYSequence data, Color color, MarkerShape marker) {
    List<XYSequencePoint>	points;
    XYSequencePoint		curr;
    int				i;
    XYSequence			newList;
    XYSequencePoint		newPoint;
    double			sum;
    double			factor;
    AxisPanel			axisY;

    points  = data.toList();
    newList = (XYSequence) data.getHeader();
    sum     = 0;
    for (i = 0; i < points.size(); i++) {
      curr = (XYSequencePoint) points.get(i);
      sum += curr.getY();
      newPoint = (XYSequencePoint) curr.getClone();
      newPoint.setY(sum);
      newList.add(newPoint);
    }
    points = newList.toList();
    axisY  = getPanel().getPlot().getAxis(Axis.LEFT);
    factor = 1 / sum * axisY.getMaximum();
    for (i = 0; i < points.size(); i++) {
      curr = (XYSequencePoint) points.get(i);
      curr.setY(curr.getY() * factor);
    }
    
    super.drawData(g, moment, newList, color, marker);
  }
}
