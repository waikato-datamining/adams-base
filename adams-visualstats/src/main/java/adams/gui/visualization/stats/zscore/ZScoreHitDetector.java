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
 * ZScoreHitDetector.java
 * Copyright (C) 2011-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.zscore;

import adams.data.spreadsheet.SpreadSheetUtils;
import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.plot.AbstractDistanceBasedHitDetector;
import adams.gui.visualization.core.plot.Axis;

import java.awt.event.MouseEvent;
import java.text.DecimalFormat;

/**
 * Hit detector for the zscore visualisation.
 *
 * @author msf8
 */
public class ZScoreHitDetector
extends AbstractDistanceBasedHitDetector<double[], String> {

  /** for serialization */
  private static final long serialVersionUID = -5768575571226254067L;

  /** zscore plot for detection */
  protected ZScore m_Owner;

  /**
   * Constructor
   * @param owner		ZScore for detection
   */
  public ZScoreHitDetector(ZScore owner) {
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
    return "Hit detector for the zscore visualisation";
  }

  /**
   * Get the zscore plot for detection
   * @return			Z-score which owns this
   */
  public ZScore getOwner() {
    return m_Owner;
  }

  protected double[] isHit(MouseEvent e) {
    double distance = 1000;
    int val = 0;
    //All calculated using pixel distances rather than differences in data
    //values so easy ro apply to all plots even with
    //vastly different scales
    AxisPanel bottom = m_Owner.getPlot().getAxis(Axis.BOTTOM);
    AxisPanel left = m_Owner.getPlot().getAxis(Axis.LEFT);
    //mouse positions
    double x = e.getX();
    double y = e.getY();
    double[] data = SpreadSheetUtils.getNumericColumn(m_Owner.getData(), m_Owner.getIndex());
    for(int i = 0; i< data.length; i++) {
      //distance from point to mouse position
      double thisDistance = Math.sqrt(Math.pow((x-bottom.valueToPos(i)), 2) + Math.pow(y-left.valueToPos(data[i]), 2));
      //if least distance
      if(thisDistance < distance) {
	distance = thisDistance;
	val = i;
      }
    }
    //If close enough to a point
    if(distance < 7){
      return new double[]{val, data[val]};
    }
    else
      return null;
  }

  protected String processHit(MouseEvent e, double[] hit) {
    if(hit == null){
      return null;
    }
    else {
      DecimalFormat df = new DecimalFormat("#");
      //Returns data position and data value
      return "data point:" + df.format(hit[0]) + ", value:" + hit[1];
    }
  }
}