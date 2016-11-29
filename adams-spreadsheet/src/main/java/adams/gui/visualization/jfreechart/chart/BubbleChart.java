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

/**
 * BubbleChart.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.jfreechart.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYZDataset;

/**
 * Generates bubble chart from XYZ data (Z is size of bubble).
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BubbleChart
  extends AbstractChartGeneratorWithAxisLabels<XYZDataset> {

  private static final long serialVersionUID = -4759011723765395176L;

  /** the plot orientation. */
  protected Orientation m_Orientation;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Creates a bubble chart from X-Y-Z data (Z is size of bubble).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "orientation", "orientation",
      Orientation.VERTICAL);
  }

  /**
   * Sets the orientation for the plot.
   *
   * @param value	the orientation
   */
  public void setOrientation(Orientation value) {
    m_Orientation = value;
    reset();
  }

  /**
   * Returns the orientation for the plot.
   *
   * @return		the orientation
   */
  public Orientation getOrientation() {
    return m_Orientation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String orientationTipText() {
    return "The orientation of the plot.";
  }

  /**
   * Performs the actual generation of the chart.
   *
   * @param data	the data to use
   * @return		the chart
   */
  @Override
  protected JFreeChart doGenerate(XYZDataset data) {
    return ChartFactory.createBubbleChart(m_Title, m_LabelX, m_LabelY, data, m_Orientation.getOrientation(), m_Legend, m_ToolTips, false);
  }
}
