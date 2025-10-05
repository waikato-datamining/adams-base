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
 * ScatterPlot.java
 * Copyright (C) 2016-2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.jfreechart.chart;

import adams.gui.visualization.core.BiColorGenerator;
import adams.gui.visualization.core.ColorGradientGenerator;
import adams.gui.visualization.jfreechart.dataset.ChartUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.LookupPaintScale;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.renderer.xy.DensityPlotXYItemRenderer;
import org.jfree.chart.renderer.xy.DensityPlotXYItemRenderer.DensityMode;
import org.jfree.data.xy.XYDataset;

import java.awt.Color;

/**
 * Generates a scatter plot from XY data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DensityScatterPlot
  extends AbstractChartGeneratorWithAxisLabels<XYDataset> {

  private static final long serialVersionUID = -4759011723765395176L;

  /** how to calculate the density. */
  protected DensityMode m_Mode;

  /** the number of bins to generate on X and Y. */
  protected int m_NumBins;

  /** the bandwidth. */
  protected double m_Bandwidth;

  /** the color gradient generator. */
  protected ColorGradientGenerator m_Generator;

  /** the plot orientation. */
  protected Orientation m_Orientation;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Creates a simple scatter plot from X-Y data.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "mode", "mode",
      DensityMode.HISTOGRAM);

    m_OptionManager.add(
      "num-bins", "numBins",
      50, 1, null);

    m_OptionManager.add(
      "bandwidth", "bandwidth",
      1.0, 0.0, null);

    m_OptionManager.add(
      "generator", "generator",
      new BiColorGenerator());

    m_OptionManager.add(
      "orientation", "orientation",
      Orientation.VERTICAL);
  }

  /**
   * Sets the mode to use.
   *
   * @param value	the mode
   */
  public void setMode(DensityMode value) {
    m_Mode = value;
    reset();
  }

  /**
   * Returns the mode to use.
   *
   * @return		the mode
   */
  public DensityMode getMode() {
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
   * Creates the paintscale to use.
   *
   * @return		the scale
   */
  protected PaintScale createPaintScale(double min, double max) {
    LookupPaintScale	result;
    Color[]		colors;
    int			i;
    double		inc;

    result = new LookupPaintScale(min, max, Color.BLUE);
    colors = m_Generator.generate();
    inc    = (max - min) / (colors.length - 1);
    for (i = 0; i < colors.length; i++)
      result.add(min + i*inc, colors[i]);

    return result;
  }

  /**
   * Performs the actual generation of the chart.
   *
   * @param data	the data to use
   * @return		the chart
   */
  @Override
  protected JFreeChart doGenerate(XYDataset data) {
    JFreeChart 			result;
    NumberAxis 			xAxis;
    NumberAxis 			yAxis;
    XYPlot 			plot;
    XYToolTipGenerator 		toolTipGenerator;
    int				diagonal;
    DensityPlotXYItemRenderer 	renderer;
    int				i;

    xAxis = new NumberAxis(m_LabelX);
    xAxis.setAutoRangeIncludesZero(false);
    yAxis = new NumberAxis(m_LabelY);
    yAxis.setAutoRangeIncludesZero(false);

    plot = new XYPlot(data, xAxis, yAxis, null);

    toolTipGenerator = null;
    if (m_ToolTips)
      toolTipGenerator = new StandardXYToolTipGenerator();

    renderer = new DensityPlotXYItemRenderer(m_NumBins, m_NumBins, m_Generator.generate(), m_Mode, m_Bandwidth);
    renderer.setBandwidth(m_Bandwidth);
    renderer.setDefaultToolTipGenerator(toolTipGenerator);
    renderer.setURLGenerator(null);
    diagonal = ChartUtils.getDiagonalIndex(data);
    if (diagonal != -1) {
      for (i = 0; i < data.getSeriesCount(); i++) {
	if (i == diagonal) {
	  renderer.setSeriesLinesVisible(i, true);
	  renderer.setSeriesShapesVisible(i, false);
	}
	else {
	  renderer.setSeriesLinesVisible(i, false);
	  renderer.setSeriesShapesVisible(i, true);
	}
      }
    }
    plot.setRenderer(renderer);
    plot.setOrientation(m_Orientation.getOrientation());

    result = new JFreeChart(m_Title, JFreeChart.DEFAULT_TITLE_FONT, plot, m_Legend);
    ChartFactory.getChartTheme().apply(result);
    return result;
  }
}
