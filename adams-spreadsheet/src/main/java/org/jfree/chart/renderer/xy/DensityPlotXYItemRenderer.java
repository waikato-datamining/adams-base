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
 * DensityPlotXYItemRenderer.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package org.jfree.chart.renderer.xy;

import adams.core.ObjectCopyHelper;
import adams.core.annotation.MixedCopyright;
import adams.core.logging.LoggingHelper;
import adams.gui.visualization.core.ColorGradientGenerator;
import adams.gui.visualization.core.KernelDensityEstimation;
import adams.gui.visualization.core.KernelDensityEstimation.Mode;
import adams.gui.visualization.core.KernelDensityEstimation.RenderState;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.LookupPaintScale;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.data.xy.XYDataset;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.util.logging.Level;

/**
 * A custom XYItemRenderer that colors the individual data points (shapes)
 * based on the calculated data density (either Binning or KDE).
 * It extends XYLineAndShapeRenderer and overrides getItemPaint to inject the
 * density-derived color for each point.
 */
@MixedCopyright(
  author = "Gemini 2.5 Flash",
  note = "Original code based on Gemini-generated output before being customized and extended."
)
public class DensityPlotXYItemRenderer 
  extends XYLineAndShapeRenderer {

  private static final long serialVersionUID = 4459317362748178074L;

  /** the paintscale in use. */
  protected PaintScale m_PaintScale;
  
  /** whether the density was calculated. */
  protected boolean m_DensityCalculated;

  /** the render state. */
  protected RenderState m_RenderState;

  /** the current dataset. */
  protected XYDataset m_CurrentDataset;

  /** for calculating the density. */
  protected KernelDensityEstimation m_KDE;

  /**
   * Constructs a new density plot renderer.
   * @param numBins The number of bins to divide the domain axes into.
   * @param generator the color generator
   * @param mode The initial density calculation mode (HISTOGRAM or KDE).
   */
  public DensityPlotXYItemRenderer(int numBins, ColorGradientGenerator generator, Mode mode, double bandwidth) {
    if (numBins <= 0)
      throw new IllegalArgumentException("Number of bins must be positive.");
    if (generator == null)
      throw new IllegalArgumentException("Color generator cannot be null.");

    m_KDE = new KernelDensityEstimation();
    m_KDE.setMode(mode);
    m_KDE.setNumBins(numBins);
    m_KDE.setBandwidth(bandwidth);
    m_KDE.setGenerator(ObjectCopyHelper.copyObject(generator));
    m_DensityCalculated = false;

    setSeriesShapesVisible(0, true);
    setSeriesLinesVisible(0, false);
  }

  /**
   * Creates the paintscale to use.
   *
   * @param min 	the minimum to use
   * @param max 	the maximum to use
   * @return		the scale
   */
  protected PaintScale createPaintScale(double min, double max) {
    LookupPaintScale 	result;
    int			i;
    double		inc;

    // in case we only have a single point
    if (max <= min)
      max = min + 1e06;

    result = new LookupPaintScale(min, max, Color.BLUE);
    inc    = (max - min) / (m_RenderState.colors.length - 1);
    for (i = 0; i < m_RenderState.colors.length; i++)
      result.add(min + i*inc, m_RenderState.colors[i]);

    return result;
  }

  /**
   * Calculates the 2D density map from the dataset using the configured mode.
   * This method also stores the axis bounds for use in getItemPaint.
   * @param dataset The dataset containing the (x, y) points.
   * @param plot The plot, used to get axis information.
   */
  protected void calculateDensity(XYDataset dataset, XYPlot plot) {
    int		series;
    int 	itemCount;
    int 	item;
    TDoubleList	x;
    TDoubleList	y;

    if ((dataset == null) || (plot == null))
      return;

    m_CurrentDataset = dataset;

    x = new TDoubleArrayList();
    y = new TDoubleArrayList();
    for (series = 0; series < dataset.getSeriesCount(); series++) {
      itemCount = dataset.getItemCount(series);
      for (item = 0; item < itemCount; item++) {
	x.add(dataset.getXValue(series, item));
	y.add(dataset.getYValue(series, item));
      }
    }

    m_RenderState       = m_KDE.calculate(x.toArray(), y.toArray());
    m_PaintScale        = createPaintScale(m_RenderState.binMin, m_RenderState.binMax);
    m_DensityCalculated = true;
  }

  /**
   * Resets the calculation flag. Should be called when the dataset, axes, or mode changes.
   */
  public void invalidateDensityMap() {
    m_DensityCalculated = false;
    m_RenderState       = null;
    m_CurrentDataset    = null;
  }

  /**
   * Overrides the base method to provide the density-derived color for the shape/line.
   * This method is called by the superclass during its drawItem implementation.
   * * @param series the series index.
   *
   * @param item the item index.
   * @return The Paint object corresponding to the item's density.
   */
  @Override
  public Paint getItemPaint(int series, int item) {
    double 	density;

    // If density map hasn't been calculated, fall back to default paint
    if (!m_DensityCalculated || (m_RenderState.xRange == 0) || (m_RenderState.yRange == 0))
      return super.getItemPaint(series, item);

    try {
      density = m_RenderState.getDensity(m_CurrentDataset.getXValue(series, item), m_CurrentDataset.getYValue(series, item));
      if (density > 0.0)
	return m_PaintScale.getPaint(density);
    }
    catch (Exception e) {
      LoggingHelper.global().log(Level.SEVERE, "Error calculating item paint based on density:", e);
    }

    return super.getItemPaint(series, item);
  }


  /**
   * The drawItem method now only ensures the density map is calculated and then
   * delegates to the superclass to draw the shapes using our custom getItemPaint.
   * (Signature preserved for compatibility)
   */
  @Override
  public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea,
		       PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis,
		       XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {

    // --- 1. Recalculate Density Map if needed (Only run once per draw cycle) ---
    // Note: The density calculation needs axis bounds, which is why it runs here.
    if (!m_DensityCalculated)
      calculateDensity(dataset, plot);

    // --- 2. Delegate drawing to superclass ---
    // The superclass (XYLineAndShapeRenderer) will call our overridden getItemPaint()
    // before drawing the shape for the current (series, item).
    super.drawItem(g2, state, dataArea, info, plot, domainAxis, rangeAxis,
      dataset, series, item, crosshairState, pass);
  }

  /**
   * Returns the current colors being used.
   *
   * @return the colors
   */
  public ColorGradientGenerator getGenerator() {
    return m_KDE.getGenerator();
  }

  /**
   * Sets the colors to use.
   *
   * @param value	the colors
   */
  public void setGenerator(ColorGradientGenerator value) {
    if (value == null)
      throw new IllegalArgumentException("Color gradient generator cannot be null.");
    m_KDE.setGenerator(ObjectCopyHelper.copyObject(value));
    invalidateDensityMap();
    fireChangeEvent();
  }

  /**
   * Returns the current density calculation mode (BINNING or KDE).
   *
   * @return The density mode.
   */
  public Mode getDensityMode() {
    return m_KDE.getMode();
  }

  /**
   * Sets the density calculation mode.
   *
   * @param value The new density mode.
   */
  public void setDensityMode(Mode value) {
    if (m_KDE.getMode() != value) {
      m_KDE.setMode(value);
      invalidateDensityMap();
      fireChangeEvent();
    }
  }

  /**
   * Returns the bandwidth (sigma) used for KDE. Ignored in BINNING mode.'
   *
   * @return The bandwidth.
   */
  public double getBandwidth() {
    return m_KDE.getBandwidth();
  }

  /**
   * Sets the bandwidth (sigma) for the Gaussian kernel. Requires invalidation.
   *
   * @param bandwidth The new bandwidth. Must be greater than 0.
   */
  public void setBandwidth(double bandwidth) {
    if (bandwidth <= 0.0)
      throw new IllegalArgumentException("Bandwidth must be positive.");
    if (m_KDE.getBandwidth() != bandwidth) {
      m_KDE.setBandwidth(bandwidth);
      if (m_KDE.getMode() == Mode.KDE) {
	invalidateDensityMap();
	fireChangeEvent();
      }
    }
  }
}
