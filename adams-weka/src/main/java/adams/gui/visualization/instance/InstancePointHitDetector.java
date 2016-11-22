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
 * InstancePointHitDetector.java
 * Copyright (C) 2010-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.instance;

import adams.data.instance.Instance;
import adams.data.instance.InstancePoint;
import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.plot.AbstractDistanceBasedHitDetector;
import adams.gui.visualization.core.plot.Axis;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Detects selections of instance points in the instance panel.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InstancePointHitDetector
  extends AbstractDistanceBasedHitDetector {

  /** for serialization. */
  private static final long serialVersionUID = 3397379783536355060L;

  /** the owner of this detector. */
  protected InstancePanel m_Owner;

  /**
   * Initializes the hit detector.
   *
   * @param owner	the panel that uses this detector
   */
  public InstancePointHitDetector(InstancePanel owner) {
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
    return "Detects selections of instance points in the instance panel.";
  }

  /**
   * Returns the owner.
   *
   * @return		the owning panel
   */
  public InstancePanel getOwner() {
    return m_Owner;
  }

  /**
   * Determines the enclosing attribute indices for the given x value.
   * If the given x happens to be an exact point, then this points will
   * be stored at index 0. If no index could be determined, then -1 will be
   * stored.
   *
   * @param inst	the underlying instance
   * @param x		the x on the plot
   * @return		the indices
   */
  protected int[] findEnclosingAttributeIndices(Instance inst, double x) {
    int[]	result;
    int		i;

    result = new int[]{-1, -1};

    if (Math.floor(x) == x) {
      result[0] = (int) x;
    }
    else {
      result[0] = (int) Math.floor(x);
      result[1] = (int) Math.ceil(x);
    }

    for (i = 0; i < 2; i++) {
      if ((result[i] < 0) || (result[i] >= inst.size()))
	result[i] = -1;
    }

    return result;
  }

  /**
   * Returns the closest attribute index for the given x.
   *
   * @param inst	the underlying Instance
   * @param x		the x to get the index for
   * @return		the index
   */
  protected int findClosestAttributeIndex(Instance inst, double x) {
    int		result;

    result = (int) Math.round(x);
    if (result < 0)
      result = 0;
    else if (result > inst.size() - 1)
      result = inst.size() - 1;

    return result;
  }

  /**
   * Returns the Instance point at the specified position.
   *
   * @param inst	the Instance data structure to search
   * @param index	the attribute index (original dataset)
   * @return		the InstancePoint, null if not found
   */
  protected InstancePoint findInstancePoint(Instance inst, int index) {
    InstancePoint	result;
    List<InstancePoint>	list;
    int			i;

    result = null;

    list = inst.toList();
    for (i = 0; i < list.size(); i++) {
      if (list.get(i).getX() == index) {
	result = list.get(i);
	break;
      }
    }

    return result;
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
    int				diffPixel;
    int				i;
    Instance			inst;
    InstancePoint		ip;
    InstancePoint		ip2;
    List<InstancePoint>		result;
    AxisPanel			axisBottom;
    AxisPanel			axisLeft;
    int[]			indices;
    int				index;
    double			dist;
    InstanceContainerModel 	model;

    result     = new ArrayList<>();
    axisBottom = m_Owner.getPlot().getAxis(Axis.BOTTOM);
    axisLeft   = m_Owner.getPlot().getAxis(Axis.LEFT);
    y          = axisLeft.posToValue((int) e.getY());
    x          = axisBottom.posToValue((int) e.getX());
    model      = (InstanceContainerModel) m_Owner.getContainerList().getContainerModel();

    for (i = 0; i < model.getRowCount(); i++) {
      if (!model.getContainerAt(i).isVisible())
	continue;

      // check for hit
      inst    = model.getContainerAt(i).getData();
      indices = findEnclosingAttributeIndices(inst, x);

      // do we have only one point available?
      if ((indices[0] < 0) || (indices[1] < 0)) {
	index = findClosestAttributeIndex(inst, x);
	ip    = findInstancePoint(inst, index);
	if (ip == null) {
	  getLogger().info("Failed to determine instance point for attribute index #" + index + ": " + inst);
	  continue;
	}

	// do X and Y fit?
	diffX     = ip.getX() - x;
	diffPixel = Math.abs(axisBottom.valueToPos(diffX) - axisBottom.valueToPos(0));
	getLogger().info("diff timestamp=" + diffPixel);
	if (diffPixel > m_MinimumPixelDifference)
	  continue;
	diffY     = ip.getY() - y;
	diffPixel = Math.abs(axisLeft.valueToPos(diffY) - axisLeft.valueToPos(0));
	getLogger().info("diff abundance=" + diffPixel);
	if (diffPixel > m_MinimumPixelDifference)
	  continue;

	// add hit
	result.add(ip);
      }
      else {
	ip = findInstancePoint(inst, indices[0]);
	if (ip == null) {
	  getLogger().info("Failed to determine instance point for attribute index #" + indices[0] + ": " + inst);
	  continue;
	}

	ip2 = findInstancePoint(inst, indices[1]);
	if (ip2 == null) {
	  getLogger().info("Failed to determine instance point for attribute index #" + indices[1] + ": " + inst);
	  continue;
	}

	dist = distance(
	    	new Point2D.Double(axisBottom.valueToPos(ip.getX()), axisLeft.valueToPos(ip.getY())),
	    	new Point2D.Double(axisBottom.valueToPos(ip2.getX()), axisLeft.valueToPos(ip2.getY())),
	    	new Point2D.Double(e.getX(), e.getY()));
	getLogger().info("dist line=" + dist);
	if (dist > m_MinimumPixelDifference)
	  continue;

	// add hit
	result.add(ip);
      }
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
  protected Object processHit(MouseEvent e, Object hit) {
    String			result;
    List<InstancePoint>		hits;
    int				i;
    Instance 			inst;
    InstanceContainer 		cont;

    hits = (List<InstancePoint>) hit;

    result = "";
    for (i = 0; i < hits.size(); i++) {
      if (i > 0)
	result += ", ";
      inst = (Instance) hits.get(i).getParent();
      cont = m_Owner.getContainerManager().newContainer(inst);
      result += hits.get(i) + " (" + cont.getDisplayID() + ")";
    }

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
