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
 * StickPaintlet.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.sequence;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import adams.data.sequence.XYSequence;
import adams.data.sequence.XYSequencePoint;
import adams.data.sequence.XYSequenceUtils;
import adams.gui.event.PaintEvent.PaintMoment;
import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.plot.Axis;

/**
 <!-- globalinfo-start -->
 * Paintlet for painting a stick plot for a sequence.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-stroke-thickness &lt;float&gt; (property: strokeThickness)
 * &nbsp;&nbsp;&nbsp;The thickness of the stroke.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.01
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StickPaintlet
  extends AbstractXYSequencePaintlet
  implements PaintletWithCustomDataSupport {

  /** for serialization. */
  private static final long serialVersionUID = 8242948176244747138L;

  /** whether to paint all the data points (no optimization). */
  protected boolean m_PaintAll;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Paintlet for painting a stick plot for a sequence.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "paint-all", "paintAll",
	    false);
  }

  /**
   * Returns whether marker shapes are disabled.
   *
   * @return		true if marker shapes are disabled
   */
  public boolean getPaintAll() {
    return m_PaintAll;
  }

  /**
   * Sets whether to draw markers or not.
   *
   * @param value	if true then marker shapes won't be drawn
   */
  public void setPaintAll(boolean value) {
    m_PaintAll = value;
    memberChanged();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String paintAllTipText() {
    return "If set to true, all data points will be painted, regardless whether they are visible or not.";
  }

  /**
   * Returns a new instance of the hit detector to use.
   *
   * @return		the hit detector
   */
  @Override
  public AbstractXYSequencePointHitDetector newHitDetector() {
    return new StickHitDetector(getSequencePanel());
  }
  /**
   * Draws the custom data with the given color.
   *
   * @param g		the graphics context
   * @param moment	the paint moment
   * @param data	the data to draw
   * @param color	the color to draw in
   */
  public void drawCustomData(Graphics g, PaintMoment moment, XYSequence data, Color color) {
    List<XYSequencePoint>	points;
    XYSequencePoint		curr;
    int				currX;
    int				currY;
    int				prevX;
    AxisPanel			axisX;
    AxisPanel			axisY;
    int				i;
    int				start;
    int				end;

    points = data.toList();
    axisX  = getPanel().getPlot().getAxis(Axis.BOTTOM);
    axisY  = getPanel().getPlot().getAxis(Axis.LEFT);

    // paint all points
    g.setColor(color);

    // find the start and end points for painting
    if (m_PaintAll) {
      start = 0;
      end   = data.size() - 1;
    }
    else {
      start = XYSequenceUtils.findClosestX(points, Math.floor(axisX.getMinimum()));
      if (start > 0)
	start--;
      end = XYSequenceUtils.findClosestX(points, Math.ceil(axisX.getMaximum()));
      if (end < data.size() - 1)
	end++;
    }

    currX  = Integer.MIN_VALUE;
    currY  = Integer.MIN_VALUE;
    prevX  = axisX.valueToPos(points.get(start).getX());

    for (i = start; i <= end; i++) {
      curr = (XYSequencePoint) points.get(i);

      // determine coordinates
      currX = axisX.valueToPos(XYSequencePoint.toDouble(curr.getX()));
      if (!m_PaintAll) {
	if ((i != start) && (i != end) && (currX == prevX))
	  continue;
      }
      currY = axisY.valueToPos(XYSequencePoint.toDouble(curr.getY()));

      // draw line
      g.drawLine(currX, axisY.valueToPos(0), currX, currY);

      prevX = currX;
    }
  }

  /**
   * The paint routine of the paintlet.
   *
   * @param g		the graphics context to use for painting
   * @param moment	what {@link PaintMoment} is currently being painted
   */
  @Override
  public void performPaint(Graphics g, PaintMoment moment) {
    int		i;
    XYSequence	data;

    // paint all points
    synchronized(getActualContainerManager()) {
      for (i = 0; i < getActualContainerManager().count(); i++) {
	if (!getActualContainerManager().isVisible(i))
	  continue;
	data = getActualContainerManager().get(i).getData();
	if (data.size() == 0)
	  continue;
	synchronized(data) {
	  drawCustomData(g, moment, data, getColor(i));
	}
      }
    }
  }
}
