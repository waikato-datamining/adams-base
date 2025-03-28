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
 * TimeseriesPointHitDetector.java
 * Copyright (C) 2011-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.timeseries;

import adams.data.timeseries.Timeseries;
import adams.data.timeseries.TimeseriesPoint;
import adams.data.timeseries.TimeseriesUtils;
import adams.gui.visualization.container.AbstractContainer;
import adams.gui.visualization.container.NamedContainer;
import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.plot.AbstractDistanceBasedHitDetector;
import adams.gui.visualization.core.plot.Axis;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Detects selections of timeseries points in the timeseries panel.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class TimeseriesPointHitDetector
  extends AbstractDistanceBasedHitDetector<List<TimeseriesPoint>, String> {

  /** for serialization. */
  private static final long serialVersionUID = 7459498872766468963L;

  /** the owner of this detector. */
  protected TimeseriesPanel m_Owner;

  /**
   * Initializes the hit detector.
   *
   * @param owner	the panel that uses this detector
   */
  public TimeseriesPointHitDetector(TimeseriesPanel owner) {
    super();

    m_Owner = owner;
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Detects selections of timeseries points in the timeseries panel.";
  }

  /**
   * Returns the owner.
   *
   * @return		the owning panel
   */
  public TimeseriesPanel getOwner() {
    return m_Owner;
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
  protected List<TimeseriesPoint> isHit(MouseEvent e) {
    List<TimeseriesPoint>	result;
    double			val;
    long			time;
    double			diffTemp;
    double			diffTime;
    double			diffPixel;
    int				i;
    Timeseries			s;
    TimeseriesPoint		tp;
    TimeseriesPoint		tp2;
    AxisPanel			axisBottom;
    AxisPanel			axisLeft;
    int[]			indices;
    int				index;
    double			dist;
    List<TimeseriesPoint>	points;
    TimeseriesContainerModel	model;

    result     = new ArrayList<>();
    axisBottom = m_Owner.getPlot().getAxis(Axis.BOTTOM);
    axisLeft   = m_Owner.getPlot().getAxis(Axis.LEFT);
    val        = axisLeft.posToValue((int) e.getY());
    time       = (long) axisBottom.posToValue((int) e.getX());
    model      = (TimeseriesContainerModel) m_Owner.getContainerList().getContainerModel();

    for (i = 0; i < model.getRowCount(); i++) {
      if (!((TimeseriesContainer) model.getContainerAt(i)).isVisible())
	continue;

      // check for hit
      s       = ((TimeseriesContainer) model.getContainerAt(i)).getData();
      points  = s.toList();
      indices = TimeseriesUtils.findEnclosingTimestamps(points, new Date(time));

      if (getDebug())
	getLogger().info("\n" + s.getID() + ":");

      // do we have only one point available?
      if ((indices[0] == -1) || (indices[1] == -1)) {
	index = TimeseriesUtils.findClosestTimestamp(points, new Date(time));
	if (index == -1)
	  continue;
	tp = points.get(index);

	// do X and Y fit?
	diffTime  = tp.getTimestamp().getTime() - time;
	diffPixel = Math.abs(axisBottom.valueToPos(diffTime) - axisBottom.valueToPos(0));
	if (getDebug())
	  getLogger().info("diff time=" + diffPixel);
	if (diffPixel > m_MinimumPixelDifference)
	  continue;
	diffTemp  = tp.getValue() - val;
	diffPixel = Math.abs(axisLeft.valueToPos(diffTemp) - axisLeft.valueToPos(0));
	if (getDebug())
	  getLogger().info("diff val=" + diffPixel);
	if (diffPixel > m_MinimumPixelDifference)
	  continue;
      }
      else {
	tp  = points.get(indices[0]);
	tp2 = points.get(indices[1]);
	dist = distance(
	    	new Point2D.Double(axisBottom.valueToPos(tp.getTimestamp().getTime()), axisLeft.valueToPos(tp.getValue())),
	    	new Point2D.Double(axisBottom.valueToPos(tp2.getTimestamp().getTime()), axisLeft.valueToPos(tp2.getValue())),
	    	new Point2D.Double(e.getX(), e.getY()));
	if (getDebug())
	  getLogger().info("dist line=" + dist);
	if (dist > m_MinimumPixelDifference)
	  continue;
      }

      // add hit
      if (getDebug())
	getLogger().info("hit!");
      result.add(tp);
    }

    if (result.size() > 0)
      return result;
    else
      return null;
  }

  /**
   * Performs the action when a hit is detected.
   *
   * @param e		the MouseEvent (for coordinates)
   * @param hit		the object that got determined by the hit
   * @return		the generated appendix for the tiptext
   */
  @Override
  protected String processHit(MouseEvent e, List<TimeseriesPoint> hit) {
    String			result;
    int				i;
    Timeseries			tp;
    AbstractContainer 		cont;

    result = " (";
    for (i = 0; i < hit.size(); i++) {
      if (i > 0)
	result += ", ";
      tp  = (Timeseries) hit.get(i).getParent();
      cont = m_Owner.getContainerManager().newContainer(tp);
      if (cont instanceof NamedContainer)
	result += ((NamedContainer) cont).getDisplayID();
    }
    result += ")";

    return result;
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    m_Owner = null;

    super.cleanUp();
  }
}
