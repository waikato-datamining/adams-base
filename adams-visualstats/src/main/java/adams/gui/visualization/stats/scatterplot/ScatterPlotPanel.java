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
 * ScatterPlotPanel.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.scatterplot;

import weka.core.Instances;
import adams.data.statistics.StatUtils;
import adams.gui.visualization.core.PlotPanel;
import adams.gui.visualization.core.axis.FancyTickGenerator;
import adams.gui.visualization.core.axis.Visibility;
import adams.gui.visualization.core.plot.Axis;

/**
 * Panel for displaying scatter plot data.
 *
 * @author msf8
 * @version $Revision$
 */
public class ScatterPlotPanel
extends PlotPanel{

  /** for serialization */
  private static final long serialVersionUID = 107298737463861170L;

  /** Instances to be plotted */
  private Instances m_Instances;

  /** index of attribute for x axis */
  private int m_XIndex = 0;

  /** index of attribute for y axis */
  private int m_YIndex = 0;

  @Override
  protected void initGUI() {
    super.initGUI();
    setAxisVisibility(Axis.LEFT, Visibility.VISIBLE);
    setAxisVisibility(Axis.BOTTOM, Visibility.VISIBLE);
    m_AxisLeft.setNumberFormat("#.##");
    m_AxisBottom.setNumberFormat("#.##");
  }

  /**
   * set the instances for the scatter plot panel
   * @param inst			Instances to be plotted
   */
  protected void setinstances(Instances inst) {
    m_Instances = inst;
  }

  /**
   * Set up the scatter plot panel
   * called by the calling class when all the fields have been set
   */
  public void reset() {
    double[] m_DataX = m_Instances.attributeToDoubleArray(m_XIndex);
    double[] m_DataY = m_Instances.attributeToDoubleArray(m_YIndex);
    double xMin = StatUtils.min(m_DataX);
    double xMax = StatUtils.max(m_DataX);
    double yMin = StatUtils.min(m_DataY);
    double yMax = StatUtils.max(m_DataY);

    m_AxisLeft.setMinimum(yMin);
    m_AxisLeft.setMaximum(yMax);
    m_AxisBottom.setMinimum(xMin);
    m_AxisBottom.setMaximum(xMax);
    //set margins for the axis
    m_AxisLeft.setBottomMargin(0.10);
    m_AxisLeft.setTopMargin(0.10);
    m_AxisBottom.setBottomMargin(0.10);
    m_AxisBottom.setTopMargin(0.10);
    //set axis names
    m_AxisLeft.setAxisName(m_Instances.attribute(m_YIndex).name());
    m_AxisBottom.setAxisName(m_Instances.attribute(m_XIndex).name());
    
    m_AxisLeft.setTickGenerator(new FancyTickGenerator());
    m_AxisBottom.setTickGenerator(new FancyTickGenerator());
    m_AxisLeft.setNthValueToShow(1);
    m_AxisBottom.setNthValueToShow(2);

    validate();
    repaint();
  }

  /**
   * Set index of x attribute
   * @param val			index of x attribute
   */
  public void setX(int val) {
    m_XIndex = val;
    reset();
  }

  /**
   * set the index of the y attribute
   * @param val			index of y attribute
   */
  public void setY(int val) {
    m_YIndex = val;
    reset();
  }
}