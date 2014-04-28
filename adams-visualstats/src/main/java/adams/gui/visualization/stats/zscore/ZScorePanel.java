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
 * ZScorePanel.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.zscore;

import adams.data.statistics.StatUtils;
import weka.core.Instances;
import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.PlotPanel;
import adams.gui.visualization.core.axis.Visibility;
import adams.gui.visualization.core.plot.Axis;

/**
 * A panel that contains the a z score graph.
 *
 * @author msf8
 * @version $Revision$
 */
public class ZScorePanel
extends PlotPanel{

  /** for serialization */
  private static final long serialVersionUID = -4445527264369086318L;

  /** instances to be displayed */
  protected Instances m_Instances;

  /**Index of the attribute plotted */
  protected int m_Index;

  /**
   * Set the instances of the z score panel
   * @param inst			Instances containing data
   */
  protected void setInstances(Instances inst) {
    m_Instances = inst;
  }

  protected void initGUI() {
    super.initGUI();
    setAxisVisibility(Axis.LEFT, Visibility.VISIBLE);
    setAxisVisibility(Axis.BOTTOM, Visibility.VISIBLE);
    m_AxisLeft.setNumberFormat("#.##");
    m_AxisBottom.setNumberFormat("#");
  }

  /**
   * Sets up the graph, called when all the fields have been set
   */
  public void reset() {
    double[] m_Data = m_Instances.attributeToDoubleArray(m_Index);
    int xMin = 0;
    int xMax = m_Data.length;
    double yMin = StatUtils.min(m_Data);
    double yMax = StatUtils.max(m_Data);

    m_AxisLeft.setMinimum(yMin);
    m_AxisLeft.setMaximum(yMax);
    m_AxisBottom.setMinimum(xMin);
    m_AxisBottom.setMaximum(xMax);

    m_AxisLeft.setBottomMargin(0.15);
    m_AxisLeft.setTopMargin(0.15);

    m_AxisLeft.setAxisName(m_Instances.attribute(m_Index).name());
    validate();
    repaint();
  }

  /**
   * Sets the index of the attribute to be plotted
   * @param val			int index of attribute
   */
  public void setIndex(int val) {
    m_Index = val;
  }

  /**
   * Get the y axis
   * @return			y axis of this z score panel
   */
  public AxisPanel getLeft() {
    return m_AxisLeft;
  }

  /**
   * Get the x axis
   * @return			x axis of this z score panel
   */
  public AxisPanel getBottom() {
    return m_AxisBottom;
  }
}