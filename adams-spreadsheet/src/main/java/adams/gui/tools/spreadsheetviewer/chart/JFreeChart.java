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
 * JFreeChart.java
 * Copyright (C) 2016-2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.spreadsheetviewer.chart;

import adams.core.option.OptionUtils;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.control.Flow;
import adams.flow.sink.JFreeChartPlot;
import adams.gui.visualization.jfreechart.chart.XYLineChart;
import adams.gui.visualization.jfreechart.dataset.AbstractDatasetGenerator;
import adams.gui.visualization.jfreechart.dataset.DefaultXY;
import adams.gui.visualization.jfreechart.shape.AbstractShapeGenerator;
import adams.gui.visualization.jfreechart.shape.Default;

import java.awt.Color;

/**
 * Uses JFreeChart to display the data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class JFreeChart
  extends AbstractChartGenerator {

  private static final long serialVersionUID = 4891980788339254294L;

  /** the dataset generator. */
  protected AbstractDatasetGenerator m_Dataset;

  /** the chart generator. */
  protected adams.gui.visualization.jfreechart.chart.AbstractChartGenerator m_Chart;

  /** the shape generator. */
  protected AbstractShapeGenerator m_Shape;

  /** the color for the plot. */
  protected Color m_PlotColor;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses JFreeChart to display the data.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "dataset", "dataset",
      new DefaultXY());

    m_OptionManager.add(
      "chart", "chart",
      new XYLineChart());

    m_OptionManager.add(
      "shape", "shape",
      new Default());

    m_OptionManager.add(
      "plot-color", "plotColor",
      Color.BLUE);
  }

  /**
   * Sets the dataset generator.
   *
   * @param value	the generator
   */
  public void setDataset(AbstractDatasetGenerator value) {
    m_Dataset = value;
    reset();
  }

  /**
   * Returns the dataset generator.
   *
   * @return		the generator
   */
  public AbstractDatasetGenerator getDataset() {
    return m_Dataset;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String datasetTipText() {
    return "The dataset generator to use.";
  }

  /**
   * Sets the chart generator.
   *
   * @param value	the generator
   */
  public void setChart(adams.gui.visualization.jfreechart.chart.AbstractChartGenerator value) {
    m_Chart = value;
    reset();
  }

  /**
   * Returns the chart generator.
   *
   * @return		the generator
   */
  public adams.gui.visualization.jfreechart.chart.AbstractChartGenerator getChart() {
    return m_Chart;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String chartTipText() {
    return "The chart generator to use.";
  }

  /**
   * Sets the shape generator.
   *
   * @param value	the generator
   */
  public void setShape(AbstractShapeGenerator value) {
    m_Shape = value;
    reset();
  }

  /**
   * Returns the shape generator.
   *
   * @return		the generator
   */
  public AbstractShapeGenerator getShape() {
    return m_Shape;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String shapeTipText() {
    return "The shape generator to use for the data point markers.";
  }

  /**
   * Sets the color for the plot.
   *
   * @param value	the color
   */
  public void setPlotColor(Color value) {
    m_PlotColor = value;
    reset();
  }

  /**
   * Returns the color for the plot.
   *
   * @return		the color
   */
  public Color getPlotColor() {
    return m_PlotColor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String plotColorTipText() {
    return "The color for the plot.";
  }

  /**
   * Adds the chart generation to the flow. The flow already contains
   * forwarding of spreadsheet and selecting subset of rows.
   *
   * @param flow	the flow to extend
   * @param name	the name of the tab/sheet
   * @param sheet	the spreadsheet to generate the flow for
   */
  @Override
  protected void addChartGeneration(Flow flow, String name, SpreadSheet sheet) {
    JFreeChartPlot	plot;

    plot = new JFreeChartPlot();
    plot.setDataset((AbstractDatasetGenerator) OptionUtils.shallowCopy(m_Dataset));
    plot.setChart((adams.gui.visualization.jfreechart.chart.AbstractChartGenerator) OptionUtils.shallowCopy(m_Chart));
    plot.setShape((AbstractShapeGenerator) OptionUtils.shallowCopy(m_Shape));
    plot.setPlotColor(m_PlotColor);
    plot.setWidth(m_Width);
    plot.setHeight(m_Height);
    flow.add(plot);
  }
}
