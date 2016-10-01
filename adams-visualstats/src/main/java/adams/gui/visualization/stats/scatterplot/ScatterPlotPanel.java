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
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.scatterplot;

import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetUtils;
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
  protected SpreadSheet m_Data;

  /** index of attribute for x axis */
  protected int m_XIndex;

  /** index of attribute for y axis */
  protected int m_YIndex;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_XIndex = 0;
    m_YIndex = 0;
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();
    setAxisVisibility(Axis.LEFT, Visibility.VISIBLE);
    setAxisVisibility(Axis.BOTTOM, Visibility.VISIBLE);
    m_AxisLeft.setNumberFormat("#.##");
    m_AxisBottom.setNumberFormat("#.##");
  }

  /**
   * set the data for the scatter plot panel.
   *
   * @param value	data to be plotted
   */
  public void setData(SpreadSheet value) {
    m_Data = value;
  }

  /**
   * Returns the data for the plot.
   *
   * @return		the data
   */
  public SpreadSheet getData() {
    return m_Data;
  }

  /**
   * Set up the scatter plot panel
   * called by the calling class when all the fields have been set
   */
  public void reset() {
    double[] dataX = SpreadSheetUtils.getNumericColumn(m_Data, m_XIndex);
    double[] dataY = SpreadSheetUtils.getNumericColumn(m_Data, m_YIndex);
    double xMin = StatUtils.min(dataX);
    double xMax = StatUtils.max(dataX);
    double yMin = StatUtils.min(dataY);
    double yMax = StatUtils.max(dataY);

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
    m_AxisLeft.setAxisName(m_Data.getColumnName(m_YIndex));
    m_AxisBottom.setAxisName(m_Data.getColumnName(m_XIndex));
    
    m_AxisLeft.setTickGenerator(new FancyTickGenerator());
    m_AxisBottom.setTickGenerator(new FancyTickGenerator());
    m_AxisLeft.setNthValueToShow(1);
    m_AxisBottom.setNthValueToShow(2);

    validate();
    repaint();
  }

  /**
   * Set index of x attribute.
   *
   * @param val		index of x attribute
   */
  public void setX(int val) {
    m_XIndex = val;
    reset();
  }

  /**
   * Returns the index of the X attribute.
   *
   * @return		the index
   */
  public int getX() {
    return m_XIndex;
  }

  /**
   * set the index of the y attribute.
   *
   * @param val		index of y attribute
   */
  public void setY(int val) {
    m_YIndex = val;
    reset();
  }

  /**
   * Returns the index of the Y attribute.
   *
   * @return		the index
   */
  public int getY() {
    return m_YIndex;
  }
}