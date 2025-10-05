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

import adams.core.annotation.MixedCopyright;
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

  /** Defines the calculation method for the density map. */
  public enum DensityMode {
    HISTOGRAM,
    KDE
  }
  
  /** the number of bins on the X axis. */
  protected int m_XBins;
  
  /** the number of bins on the Y axis. */
  protected int m_YBins;
  
  /** the density map. */
  protected double[][] m_DensityMap;
  
  /** the paintscale in use. */
  protected PaintScale m_PaintScale;
  
  /** whether the density was calculated. */
  protected boolean m_DensityCalculated;
  
  /** the maximum density calculated. */
  protected double m_MaxDensity;
  
  /** the minimum density calculated. */
  protected double m_MinDensity;
  
  /** the current dataset. */
  protected XYDataset m_CurrentDataset;
  
  /** the minimum in the data (X axis). */
  protected double m_XMinData;

  /** the maximum in the data (X axis). */
  protected double m_XMaxData;

  /** the minimum in the data (Y axis). */
  protected double m_YMinData;

  /** the maximum in the data (Y axis). */
  protected double m_YMaxData;

  /** the range for the X axis. */
  protected double m_XRange;

  /** the range for the Y axis. */
  protected double m_YRange;

  /** the mode for the plot. */
  protected DensityMode m_DensityMode;

  /** Standard deviation (sigma) for the Gaussian kernel. */
  protected double m_Bandwidth; //

  /** the colors to use for the paintscale. */
  protected Color[] m_Colors;

  /**
   * Constructs a new density plot renderer.
   * @param xBins The number of bins to divide the domain (X) axis into.
   * @param yBins The number of bins to divide the range (Y) axis into.
   * @param colors The colors to use
   * @param mode The initial density calculation mode (BINNING or KDE).
   */
  public DensityPlotXYItemRenderer(int xBins, int yBins, Color[] colors, DensityMode mode, double bandwidth) {
    if ((xBins <= 0) || (yBins <= 0))
      throw new IllegalArgumentException("Number of bins must be positive.");
    if (colors == null)
      throw new IllegalArgumentException("Colors cannot be null.");

    m_XBins             = xBins;
    m_YBins             = yBins;
    m_DensityMode       = mode;
    m_Colors            = colors;
    m_Bandwidth         = bandwidth;
    m_DensityCalculated = false;
    m_MaxDensity        = 0.0;
    m_MinDensity        = Double.MAX_VALUE;

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

    result = new LookupPaintScale(min, max, Color.BLUE);
    inc    = (max - min) / (m_Colors.length - 1);
    for (i = 0; i < m_Colors.length; i++)
      result.add(min + i*inc, m_Colors[i]);

    return result;
  }

  /**
   * Calculates the 2D density map from the dataset using the configured mode.
   * This method also stores the axis bounds for use in getItemPaint.
   * @param dataset The dataset containing the (x, y) points.
   * @param plot The plot, used to get axis information.
   */
  protected void calculateDensity(XYDataset dataset, XYPlot plot) {
    ValueAxis 	domainAxis;
    ValueAxis 	rangeAxis;

    if ((dataset == null) || (plot == null))
      return;

    m_CurrentDataset = dataset;

    // Get the effective data range for binning
    domainAxis = plot.getDomainAxis();
    rangeAxis = plot.getRangeAxis();

    // Store bounds globally for use in getItemPaint
    m_XMinData = domainAxis.getLowerBound();
    m_XMaxData = domainAxis.getUpperBound();
    m_YMinData = rangeAxis.getLowerBound();
    m_YMaxData = rangeAxis.getUpperBound();
    m_XRange   = m_XMaxData - m_XMinData;
    m_YRange   = m_YMaxData - m_YMinData;

    // Initialize density map and max density
    m_DensityMap = new double[m_XBins][m_YBins];
    m_MaxDensity = 0.0;
    m_MinDensity = Double.MAX_VALUE;

    // calculate the map
    switch (m_DensityMode) {
      case HISTOGRAM:
	calculateBinningDensity(dataset);
	break;
      case KDE:
	calculateKdeDensity(dataset);
	break;
      default:
	throw new IllegalStateException("Unhandled mode: " + m_DensityMode);
    }

    m_PaintScale        = createPaintScale(m_MinDensity, m_MaxDensity);
    m_DensityCalculated = true;
  }

  /**
   * Calculates density using simple binning (counting points per cell).
   */
  protected void calculateBinningDensity(XYDataset dataset) {
    int		series;
    int 	itemCount;
    int 	item;
    double 	xValue;
    double 	yValue;
    int 	xIndex;
    int 	yIndex;
    int 	yMapIndex;

    for (series = 0; series < dataset.getSeriesCount(); series++) {
      itemCount = dataset.getItemCount(series);
      for (item = 0; item < itemCount; item++) {
	xValue = dataset.getXValue(series, item);
	yValue = dataset.getYValue(series, item);

	// Skip points outside the current axis range
	if ((xValue < m_XMinData) || (xValue >= m_XMaxData) || (yValue < m_YMinData) || (yValue >= m_YMaxData))
	  continue;

	// Determine X bin index (clamped to prevent ArrayIndexOutOfBounds)
	xIndex = (int) Math.floor(((xValue - m_XMinData) / m_XRange) * m_XBins);
	xIndex = Math.min(xIndex, m_XBins - 1); // Clamp max
	xIndex = Math.max(xIndex, 0);         // Clamp min

	// Determine Y bin index (clamped to prevent ArrayIndexOutOfBounds)
	yIndex = (int) Math.floor(((yValue - m_YMinData) / m_YRange) * m_YBins);
	yIndex = Math.min(yIndex, m_YBins - 1); // Clamp max
	yIndex = Math.max(yIndex, 0);         // Clamp min

	// Note: The Y axis maps lower values to the bottom, but the array
	// maps index 0 to the top, so we reverse the Y index for correct visual mapping.
	yMapIndex = (m_YBins - 1) - yIndex;

	// Increment density count
	m_DensityMap[xIndex][yMapIndex]++;
	m_MaxDensity = Math.max(m_MaxDensity, m_DensityMap[xIndex][yMapIndex]);
	m_MinDensity = Math.min(m_MinDensity, m_DensityMap[xIndex][yMapIndex]);
      }
    }
  }

  /**
   * Calculates density using 2D Gaussian Kernel Density Estimation.
   */
  protected void calculateKdeDensity(XYDataset dataset) {
    double 	xBinSizeData;
    double 	yBinSizeData;
    double 	sigmaSquared;
    double 	twoSigmaSquared;
    int 	series;
    int 	itemCount;
    int 	item;
    double 	x_i;
    double 	y_i;
    int		i;
    int		j;
    double 	x_j;
    double 	y_j;
    double 	dx;
    double 	dy;
    double 	dSquared;
    double 	kernelValue;

    xBinSizeData     = m_XRange / m_XBins;
    yBinSizeData     = m_YRange / m_YBins;
    sigmaSquared     = m_Bandwidth * m_Bandwidth;
     twoSigmaSquared = 2 * sigmaSquared;

    for (series = 0; series < dataset.getSeriesCount(); series++) {
      itemCount = dataset.getItemCount(series);
      for (item = 0; item < itemCount; item++) {
	x_i = dataset.getXValue(series, item);
	y_i = dataset.getYValue(series, item);

	// Skip points outside the current axis range
	if ((x_i < m_XMinData) || (x_i > m_XMaxData) || (y_i < m_YMinData) || (y_i > m_YMaxData))
	  continue;

	// Iterate over the entire grid
	for (i = 0; i < m_XBins; i++) {
	  for (j = 0; j < m_YBins; j++) {
	    // Calculate the center of the current bin (i, j) in data coordinates
	    // X center: xMinData + (i + 0.5) * bin width
	    x_j = m_XMinData + (i + 0.5) * xBinSizeData;

	    // Y center (reversed index): yMaxData - (j + 0.5) * bin height
	    y_j = m_YMaxData - (j + 0.5) * yBinSizeData;

	    // Squared distance between data point (x_i, y_i) and bin center (x_j, y_j)
	    dx       = x_i - x_j;
	    dy       = y_i - y_j;
	    dSquared = dx * dx + dy * dy;

	    // Apply Gaussian kernel: exp(-d^2 / (2 * sigma^2))
	    kernelValue = Math.exp(-dSquared / twoSigmaSquared);

	    // Sum the contributions to the bin
	    m_DensityMap[i][j] += kernelValue;
	  }
	}
      }
    }

    // Find the new maximum density value after all points have contributed
    for (i = 0; i < m_XBins; i++) {
      for (j = 0; j < m_YBins; j++) {
	m_MaxDensity = Math.max(m_MaxDensity, m_DensityMap[i][j]);
	m_MinDensity = Math.min(m_MinDensity, m_DensityMap[i][j]);
      }
    }
  }

  /**
   * Resets the calculation flag. Should be called when the dataset, axes, or mode changes.
   */
  public void invalidateDensityMap() {
    m_DensityCalculated = false;
    m_DensityMap        = null;
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
    double 	xValue;
    double 	yValue;
    int 	xIndex;
    int 	yIndex;
    int 	yMapIndex;
    double 	density;

    // If density map hasn't been calculated, fall back to default paint
    if (!m_DensityCalculated || m_CurrentDataset == null || m_XRange == 0 || m_YRange == 0)
      return super.getItemPaint(series, item);

    try {
      xValue = m_CurrentDataset.getXValue(series, item);
      yValue = m_CurrentDataset.getYValue(series, item);

      // --- 1. Map (x, y) to a Bin Index ---

      // Check if point is outside the calculated bounds (should generally be handled by JFreeChart clipping)
      // If point is outside the initial calculated bounds, return transparent/default
      if (xValue < m_XMinData || xValue >= m_XMaxData || yValue < m_YMinData || yValue >= m_YMaxData)
	return super.getItemPaint(series, item);

      // Determine X bin index
      xIndex = (int) Math.floor(((xValue - m_XMinData) / m_XRange) * m_XBins);
      xIndex = Math.min(xIndex, m_XBins - 1);
      xIndex = Math.max(xIndex, 0);

      // Determine Y bin index (requires reversing for array lookup)
      yIndex = (int) Math.floor(((yValue - m_YMinData) / m_YRange) * m_YBins);
      yIndex = Math.min(yIndex, m_YBins - 1);
      yIndex = Math.max(yIndex, 0);

      yMapIndex = (m_YBins - 1) - yIndex;

      // --- 2. Retrieve Density and Calculate Paint ---
      density = m_DensityMap[xIndex][yMapIndex];

      if (density > 0.0)
	return m_PaintScale.getPaint(density);

    }
    catch (Exception e) {
      System.err.println("Error calculating item paint based on density: " + e.getMessage());
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
  public Color[] getColors() {
    return m_Colors;
  }

  /**
   * Sets the colors to use.
   *
   * @param value	the colors
   */
  public void setColors(Color[] value) {
    if (value == null)
      throw new IllegalArgumentException("Colors cannot be null.");
    m_Colors = value;
    invalidateDensityMap();
    fireChangeEvent();
  }

  /**
   * Returns the current density calculation mode (BINNING or KDE).
   *
   * @return The density mode.
   */
  public DensityMode getDensityMode() {
    return m_DensityMode;
  }

  /**
   * Sets the density calculation mode.
   *
   * @param densityMode The new density mode.
   */
  public void setDensityMode(DensityMode densityMode) {
    if (m_DensityMode != densityMode) {
      m_DensityMode = densityMode;
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
    return m_Bandwidth;
  }

  /**
   * Sets the bandwidth (sigma) for the Gaussian kernel. Requires invalidation.
   *
   * @param bandwidth The new bandwidth. Must be greater than 0.
   */
  public void setBandwidth(double bandwidth) {
    if (bandwidth <= 0.0)
      throw new IllegalArgumentException("Bandwidth must be positive.");
    if (m_Bandwidth != bandwidth) {
      m_Bandwidth = bandwidth;
      if (m_DensityMode == DensityMode.KDE) {
	invalidateDensityMap();
	fireChangeEvent();
      }
    }
  }
}
