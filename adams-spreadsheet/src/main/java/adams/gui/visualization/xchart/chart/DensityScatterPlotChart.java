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
 * DensityScatterPlotChart.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.xchart.chart;

import adams.core.ObjectCopyHelper;
import adams.gui.visualization.core.BiColorGenerator;
import adams.gui.visualization.core.ColorGradientGenerator;
import adams.gui.visualization.core.KernelDensityEstimation;
import adams.gui.visualization.core.KernelDensityEstimation.Mode;
import adams.gui.visualization.core.KernelDensityEstimation.RenderState;
import adams.gui.visualization.xchart.dataset.ChartUtils;
import adams.gui.visualization.xchart.dataset.Datasets;
import adams.gui.visualization.xchart.dataset.XYDataset;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates an density-based XY scatter plot.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class DensityScatterPlotChart
  extends AbstractChartGeneratorWithAxisLabels<XYChart, XYDataset> {

  private static final long serialVersionUID = -6627859170147678938L;

  /** the marker size. */
  protected int m_MarkerSize;

  /** the background color. */
  protected Color m_BackgroundColor;

  /** the color for the diagonal plot. */
  protected Color m_DiagonalColor;

  /** the zoom selection color. */
  protected Color m_ZoomSelectionColor;

  /** how to calculate the density. */
  protected Mode m_Mode;

  /** the number of bins to generate on X and Y. */
  protected int m_NumBins;

  /** the bandwidth. */
  protected double m_Bandwidth;

  /** the generator to use. */
  protected ColorGradientGenerator m_Generator;

  /** the generated render state. */
  protected transient RenderState m_RenderState;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates an density-based XY scatter plot.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "marker-size", "markerSize",
      7, 0, null);

    m_OptionManager.add(
      "background-color", "backgroundColor",
      Color.WHITE);

    m_OptionManager.add(
      "diagonal-color", "diagonalColor",
      Color.BLACK);

    m_OptionManager.add(
      "zoom-selection-color", "zoomSelectionColor",
      new Color(0,0,192,128));

    m_OptionManager.add(
      "mode", "mode",
      Mode.HISTOGRAM);

    m_OptionManager.add(
      "num-bins", "numBins",
      50, 1, null);

    m_OptionManager.add(
      "bandwidth", "bandwidth",
      1.0, 0.0, null);

    m_OptionManager.add(
      "generator", "generator",
      new BiColorGenerator());
  }

  /**
   * Sets the marker size.
   *
   * @param value	the size
   */
  public void setMarkerSize(int value) {
    m_MarkerSize = value;
    reset();
  }

  /**
   * Returns the marker size.
   *
   * @return		the size
   */
  public int getMarkerSize() {
    return m_MarkerSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String markerSizeTipText() {
    return "The size to use for the markers.";
  }

  /**
   * Sets the background color for the plot.
   *
   * @param value	the color
   */
  public void setBackgroundColor(Color value) {
    m_BackgroundColor = value;
    reset();
  }

  /**
   * Returns the background color for the plot.
   *
   * @return		the color
   */
  public Color getBackgroundColor() {
    return m_BackgroundColor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String backgroundColorTipText() {
    return "The background color for the plot.";
  }

  /**
   * Sets the color for the diagonal (ie second data series if present) of XY charts.
   *
   * @param value	the color
   */
  public void setDiagonalColor(Color value) {
    m_DiagonalColor = value;
    reset();
  }

  /**
   * Returns the color for the diagonal (ie second data series if present) of XY charts.
   *
   * @return		the color
   */
  public Color getDiagonalColor() {
    return m_DiagonalColor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String diagonalColorTipText() {
    return "The color for the diagonal (ie second data series if present) of XY charts.";
  }

  /**
   * Sets the color for the zoom selection.
   *
   * @param value	the color
   */
  public void setZoomSelectionColor(Color value) {
    m_ZoomSelectionColor = value;
    reset();
  }

  /**
   * Returns the color for the zoom selection.
   *
   * @return		the color
   */
  public Color getZoomSelectionColor() {
    return m_ZoomSelectionColor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String zoomSelectionColorTipText() {
    return "The color for the zoom selection.";
  }

  /**
   * Sets the mode to use.
   *
   * @param value	the mode
   */
  public void setMode(Mode value) {
    m_Mode = value;
    reset();
  }

  /**
   * Returns the mode to use.
   *
   * @return		the mode
   */
  public Mode getMode() {
    return m_Mode;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String modeTipText() {
    return "The mode to use for generating the density.";
  }

  /**
   * Sets the number of bins to generate on X and Y axis.
   *
   * @param value	the number of bins
   */
  public void setNumBins(int value) {
    if (getOptionManager().isValid("numBins", value)) {
      m_NumBins = value;
      reset();
    }
  }

  /**
   * Returns the number of bins to generate on X and Y axis.
   *
   * @return		the number of bins
   */
  public int getNumBins() {
    return m_NumBins;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numBinsTipText() {
    return "The number of bins to generate on X and Y axis.";
  }

  /**
   * Sets the bandwidth for kernel density estimates.
   *
   * @param value	the bandwidth
   */
  public void setBandwidth(double value) {
    if (getOptionManager().isValid("bandwidth", value)) {
      m_Bandwidth = value;
      reset();
    }
  }

  /**
   * Returns the bandwidth for kernel density estimates.
   *
   * @return		the bandwidth
   */
  public double getBandwidth() {
    return m_Bandwidth;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String bandwidthTipText() {
    return "The bandwidth for kernel density estimates.";
  }

  /**
   * Sets the color generator.
   *
   * @param value	the generator
   */
  public void setGenerator(ColorGradientGenerator value) {
    m_Generator = value;
    reset();
  }

  /**
   * Returns the color generator.
   *
   * @return		the generator
   */
  public ColorGradientGenerator getGenerator() {
    return m_Generator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String generatorTipText() {
    return "The generator to use for creating the gradient colors.";
  }

  /**
   * Builds the chart.
   *
   * @return		the chart
   */
  @Override
  protected XYChart buildChart() {
    return new XYChartBuilder()
	     .title(m_Title)
	     .xAxisTitle(m_LabelX)
	     .yAxisTitle(m_LabelY)
	     .build();
  }

  /**
   * Applies styling to the chart.
   *
   * @param data	the data to will be added
   * @param chart	the chart to style
   */
  @Override
  protected void styleChart(XYChart chart, Datasets<XYDataset> data) {
    List<Color> 		colors;
    KernelDensityEstimation	kde;
    TDoubleList			x;
    TDoubleList			y;
    Map<Integer,XYDataset> 	map;
    int				i;
    int				colorIndex;
    Datasets<XYDataset> 	datasets;

    datasets = new Datasets<>();

    chart.getStyler().setChartTitleVisible(!m_Title.isEmpty());
    chart.getStyler().setXAxisTitleVisible(!m_LabelX.isEmpty());
    chart.getStyler().setYAxisTitleVisible(!m_LabelY.isEmpty());
    chart.getStyler().setLegendVisible(m_Legend);
    chart.getStyler().setLegendPosition(m_LegendPosition);
    chart.getStyler().setZoomEnabled(true);
    chart.getStyler().setZoomResetByDoubleClick(false);
    chart.getStyler().setZoomResetByButton(true);
    chart.getStyler().setZoomSelectionColor(m_ZoomSelectionColor);
    chart.getStyler().setChartBackgroundColor(m_BackgroundColor);
    chart.getStyler().setMarkerSize(m_MarkerSize);

    // collect data
    x = new TDoubleArrayList();
    y = new TDoubleArrayList();
    for (XYDataset dataset: data) {
      if (ChartUtils.isDiagonal(dataset)) {
	datasets.add(dataset);
	continue;
      }
      x.add(dataset.getX());
      y.add(dataset.getY());
    }

    // calculate KDE
    kde = new KernelDensityEstimation();
    kde.setMode(m_Mode);
    kde.setNumBins(m_NumBins);
    kde.setBandwidth(m_Bandwidth);
    kde.setGenerator(ObjectCopyHelper.copyObject(m_Generator));
    m_RenderState = kde.calculate(x.toArray(), y.toArray());

    // generate colors
    colors = new ArrayList<>();
    if (ChartUtils.hasDiagonal(data))
      colors.add(m_DiagonalColor);
    colors.addAll(Arrays.asList(m_RenderState.colors));
    chart.getStyler().setSeriesColors(colors.toArray(new Color[0]));
    chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter);

    // create datasets, one per color index
    map = new HashMap<>();
    for (i = 0; i < x.size(); i++) {
      colorIndex = m_RenderState.getIndex(x.get(i), y.get(i));
      if (!map.containsKey(colorIndex))
	map.put(colorIndex, new XYDataset("KDE-" + colorIndex));
      map.get(colorIndex).add(x.get(i), y.get(i));
    }
    for (int n: map.keySet())
      datasets.add(map.get(n));
    m_RenderState.additional.put("datasets", datasets);
  }

  /**
   * Adds the data to the chart.
   *
   * @param chart the chart to add the data to
   * @param data  the data to add
   */
  @Override
  protected void addData(XYChart chart, Datasets<XYDataset> data) {
    // use regenerated datasets instead
    Datasets<XYDataset> datasets = (Datasets<XYDataset>) m_RenderState.additional.get("datasets");
    for (XYDataset series : datasets)
      chart.addSeries(series.getName(), series.getX(), series.getY());
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
