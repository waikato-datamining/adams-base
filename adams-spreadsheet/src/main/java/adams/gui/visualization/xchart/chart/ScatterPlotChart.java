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
 * ScatterPlotChart.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.xchart.chart;

import adams.gui.visualization.xchart.dataset.ChartUtils;
import adams.gui.visualization.xchart.dataset.Datasets;
import adams.gui.visualization.xchart.dataset.XYDataset;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;

/**
 * Generates an XY scatter plot.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ScatterPlotChart
  extends AbstractXYChartGenerator {

  private static final long serialVersionUID = -6627859170147678938L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates an XY scatter plot.";
  }

  /**
   * Applies styling to the chart.
   *
   * @param data	the data to will be added
   * @param chart	the chart to style
   */
  @Override
  protected void styleChart(XYChart chart, Datasets<XYDataset> data) {
    super.styleChart(chart, data);
    chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter);
  }

  /**
   * Hook method for after the chart has been generated.
   *
   * @param chart	the chart to add the data to
   * @param data	the data to add
   */
  protected void postGenerate(XYChart chart, Datasets<XYDataset> data) {
    super.postGenerate(chart, data);
    if (ChartUtils.hasDiagonal(chart))
      chart.getSeriesMap().get(ChartUtils.KEY_DIAGONAL).setXYSeriesRenderStyle(XYSeriesRenderStyle.Line);
  }
}
