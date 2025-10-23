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
 * AbstractXYChartGenerator.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.xchart.chart;

import adams.gui.visualization.core.ColorProvider;
import adams.gui.visualization.core.ColorProviderHandler;
import adams.gui.visualization.core.DefaultColorProvider;
import adams.gui.visualization.xchart.dataset.ChartUtils;
import adams.gui.visualization.xchart.dataset.Datasets;
import adams.gui.visualization.xchart.dataset.XYDataset;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Ancestor for XY charts.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractXYChartGenerator
  extends AbstractChartGeneratorWithAxisLabels<XYChart, XYDataset>
  implements ColorProviderHandler {

  private static final long serialVersionUID = -6627859170147678938L;

  /** the marker size. */
  protected int m_MarkerSize;

  /** the background color. */
  protected Color m_BackgroundColor;

  /** the color provider for generating the colors (if more than one series). */
  protected ColorProvider m_ColorProvider;

  /** the color for the diagonal plot. */
  protected Color m_DiagonalColor;
  
  /** the zoom selection color. */
  protected Color m_ZoomSelectionColor;
  
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
      "color-provider", "colorProvider",
      new DefaultColorProvider());

    m_OptionManager.add(
      "diagonal-color", "diagonalColor",
      Color.BLACK);

    m_OptionManager.add(
      "zoom-selection-color", "zoomSelectionColor",
      new Color(0,0,192,128));
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
   * Sets the color provider to use for XY charts.
   *
   * @param value	the color provider
   */
  public void setColorProvider(ColorProvider value) {
    m_ColorProvider = value;
    reset();
  }

  /**
   * Returns the color provider to use for XY charts.
   *
   * @return		the color provider
   */
  public ColorProvider getColorProvider() {
    return m_ColorProvider;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String colorProviderTipText() {
    return "The color provider to use for XY plots.";
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
  protected void styleChart(XYChart chart, Datasets<XYDataset> data) {
    List<Color>		colors;
    int			numColors;

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

    colors = new ArrayList<>();
    if (ChartUtils.hasDiagonal(data)) {
      numColors = data.size() - 1;
      colors.add(m_DiagonalColor);
    }
    else {
      numColors = data.size();
    }
    colors.addAll(Arrays.asList(m_ColorProvider.generate(numColors)));
    chart.getStyler().setSeriesColors(colors.toArray(new Color[0]));
  }

  /**
   * Adds the data to the chart.
   *
   * @param chart	the chart to add the data to
   * @param data	the data to add
   */
  @Override
  protected void addData(XYChart chart, Datasets<XYDataset> data) {
    for (XYDataset series : data)
      chart.addSeries(series.getName(), series.getX(), series.getY());
  }
}
