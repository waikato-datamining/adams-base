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
 * LineHitDetector.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.sequence;

import adams.data.sequence.XYSequence;
import adams.data.sequence.XYSequencePoint;
import adams.data.sequence.XYSequenceUtils;
import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.plot.Axis;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Vector;

/**
 * Detects selections of lines.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LineHitDetector
  extends AbstractXYSequencePointHitDetector {

  /** for serialization. */
  private static final long serialVersionUID = -6387662418337280157L;

  /**
   * Initializes the hit detector (constructor only for GOE) with no owner.
   */
  public LineHitDetector() {
    this(null);
  }

  /**
   * Initializes the hit detector.
   *
   * @param owner	the paintlet that uses this detector
   */
  public LineHitDetector(XYSequencePaintlet owner) {
    super(owner);
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Detects selections of lines.";
  }

  /**
   * Checks for a hit.
   * <br><br>
   * For calculating distance between point and line, see <a href="http://local.wasp.uwa.edu.au/~pbourke/geometry/pointline/">here</a>
   *
   * @param e		the MouseEvent (for coordinates)
   * @return		the associated object with the hit, otherwise null
   */
  @Override
  protected Object isHit(MouseEvent e) {
    double			y;
    double			x;
    double			diffY;
    double			diffX;
    double			diffPixel;
    int				i;
    XYSequence			s;
    XYSequencePoint		sp;
    XYSequencePoint		sp2;
    Vector<XYSequencePoint>	result;
    AxisPanel			axisBottom;
    AxisPanel			axisLeft;
    int[]			indices;
    int				index;
    double			dist;
    List<XYSequencePoint>	points;
    boolean			logging;

    result     = new Vector<XYSequencePoint>();
    axisBottom = m_Owner.getPlot().getAxis(Axis.BOTTOM);
    axisLeft   = m_Owner.getPlot().getAxis(Axis.LEFT);
    y          = axisLeft.posToValue(e.getY());
    x          = axisBottom.posToValue(e.getX());
    logging    = isLoggingEnabled();

    for (i = 0; i < m_Owner.getSequencePanel().getContainerManager().count(); i++) {
      if (!m_Owner.getSequencePanel().getContainerManager().get(i).isVisible())
	continue;

      // check for hit
      s      = m_Owner.getSequencePanel().getContainerManager().get(i).getData();
      points = s.toList();

      if (logging)
	getLogger().info("\n" + s.getID() + ":");

      indices = XYSequenceUtils.findEnclosingXs(points, x);
      // do we have only one point available?
      if ((indices[0] == -1) || (indices[1] == -1)) {
	index = XYSequenceUtils.findClosestX(points, x);
	if (index == -1)
	  continue;
	sp = points.get(index);

	diffX     = sp.getX() - x;
	diffPixel = Math.abs(axisBottom.valueToPos(diffX) - axisBottom.valueToPos(0));
	if (logging)
	  getLogger().info("diff x=" + diffPixel);
	if (diffPixel > m_MinimumPixelDifference)
	  continue;
	diffY     = sp.getY() - y;
	diffPixel = Math.abs(axisLeft.valueToPos(diffY) - axisLeft.valueToPos(0));
	if (logging)
	  getLogger().info("diff y=" + diffPixel);
	if (diffPixel > m_MinimumPixelDifference)
	  continue;
      }
      else {
	sp  = points.get(indices[0]);
	sp2 = points.get(indices[1]);
	dist = distance(
	    new Point2D.Double(axisBottom.valueToPos(sp.getX()), axisLeft.valueToPos(sp.getY())),
	    new Point2D.Double(axisBottom.valueToPos(sp2.getX()), axisLeft.valueToPos(sp2.getY())),
	    new Point2D.Double(e.getX(), e.getY()));
	if (logging)
	  getLogger().info("dist line=" + dist);
	if (dist > m_MinimumPixelDifference)
	  continue;
      }

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
