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
 * ErrorCrossHitDetector.java
 * Copyright (C) 2014-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink.sequenceplotter;

import adams.data.sequence.XYSequence;
import adams.data.sequence.XYSequencePoint;
import adams.data.sequence.XYSequenceUtils;
import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.sequence.AbstractXYSequencePointHitDetector;
import adams.gui.visualization.sequence.XYSequencePaintlet;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Detects selections of error crosses.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ErrorCrossHitDetector
  extends AbstractXYSequencePointHitDetector {

  /** for serialization. */
  private static final long serialVersionUID = -3363546923840405674L;

  /**
   * Initializes the hit detector (constructor only for GOE) with no owner.
   */
  public ErrorCrossHitDetector() {
    this(null);
  }

  /**
   * Initializes the hit detector.
   *
   * @param owner	the paintlet that uses this detector
   */
  public ErrorCrossHitDetector(XYSequencePaintlet owner) {
    super(owner);
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Detects selections of error crosses.";
  }

  /**
   * Returns the default minimum pixel difference.
   *
   * @return		the minimum
   */
  protected int getDefaultMinimumPixelDifference() {
    return 1;
  }

  /**
   * Checks for a hit.
   *
   * @param e		the MouseEvent (for coordinates)
   * @return		the associated object with the hit, otherwise null
   */
  @Override
  protected List<XYSequencePoint> isHit(MouseEvent e) {
    List<XYSequencePoint>	result;
    double			y;
    double			x;
    double			diffY;
    double			diffX;
    double			diffPixel;
    int				i;
    XYSequence			s;
    XYSequencePoint		sp;
    AxisPanel			axisBottom;
    AxisPanel			axisLeft;
    int				index;
    List<XYSequencePoint>	points;
    int				diameter;
    boolean			logging;
    ErrorCrossPaintlet		paintlet;

    if (m_Owner == null)
      return null;

    result     = new ArrayList<>();
    axisBottom = m_Owner.getPlot().getAxis(Axis.BOTTOM);
    axisLeft   = m_Owner.getPlot().getAxis(Axis.LEFT);
    y          = axisLeft.posToValue(e.getY());
    x          = axisBottom.posToValue(e.getX());
    paintlet   = null;
    if (m_Owner instanceof ErrorCrossPaintlet)
      paintlet = (ErrorCrossPaintlet) m_Owner;
    logging    = isLoggingEnabled();

    for (i = 0; i < m_Owner.getSequencePanel().getContainerManager().count(); i++) {
      if (!m_Owner.getSequencePanel().getContainerManager().get(i).isVisible())
	continue;

      // check for hit
      s      = m_Owner.getSequencePanel().getContainerManager().get(i).getData();
      points = s.toList();

      if (logging)
	getLogger().info("\n" + s.getID() + ":");

      index = XYSequenceUtils.findClosestX(points, x);
      if (index == -1)
	continue;
      sp = points.get(index);

      if (paintlet != null)
	diameter = paintlet.getDiameter(axisBottom, axisLeft, sp);
      else
	diameter = 1;
      
      diffX     = sp.getX() - x;
      diffPixel = Math.abs(axisBottom.valueToPos(diffX) - axisBottom.valueToPos(0));
      if (logging)
	getLogger().info("diff x=" + diffPixel);
      if (diffPixel > m_MinimumPixelDifference + (diameter / 2))
	continue;
      diffY     = sp.getY() - y;
      diffPixel = Math.abs(axisLeft.valueToPos(diffY) - axisLeft.valueToPos(0));
      if (logging)
	getLogger().info("diff y=" + diffPixel);
      if (diffPixel > m_MinimumPixelDifference + (diameter / 2))
	continue;

      // add hit
      if (logging)
	getLogger().info("hit!");
      result.add(sp);
    }

    if (result.size() > 0)
      return result;
    else
      return null;
  }
}
