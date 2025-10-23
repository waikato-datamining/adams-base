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
 * KernelDensityEstimation.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.core;

import adams.core.option.AbstractOptionHandler;
import adams.data.statistics.StatUtils;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * For calculating kernel-density estimates for plots.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class KernelDensityEstimation
  extends AbstractOptionHandler {

  private static final long serialVersionUID = 8894339332143266645L;

  /**
   * The mode for calculating the density.
   */
  public enum Mode {
    HISTOGRAM,
    KDE
  }

  /**
   * The rendering state.
   */
  public static class RenderState {

    /** the generated colors. */
    public Color[] colors;

    /** the number of bins. */
    public int numBins;

    /** the bins. */
    public double[][] bins;

    /** the starting values for X bins. */
    public double[] startX;

    /** the starting values for Y bins. */
    public double[] startY;

    /** the smallest bin value. */
    public double binMin;

    /** the largest bin value. */
    public double binMax;

    /** the minimum x value. */
    public double xMin;

    /** the maximum x value. */
    public double xMax;

    /** the minimum x value. */
    public double yMin;

    /** the maximum x value. */
    public double yMax;

    /** the X bin width. */
    public double xWidth;

    /** the Y bin width. */
    public double yWidth;

    /** the X range. */
    public double xRange;

    /** the Y range. */
    public double yRange;

    /** for storing additional information. */
    public Map<String,Object> additional = new HashMap<>();

    /**
     * Returns the density for the data point.
     *
     * @param x    	the x coordinate
     * @param y 	the y coordinate
     * @return 		the density
     */
    public double getDensity(double x, double y) {
      double 	result;
      int 	xi;
      int 	yi;

      xi = (int) ((x - xMin) / (xMax - xMin) * (numBins - 1));
      yi = (int) ((y - yMin) / (yMax - yMin) * (numBins - 1));
      result = bins[yi][xi];

      return result;
    }

    /**
     * Returns the color index for the data point.
     *
     * @param x    	the x coordinate
     * @param y 	the y coordinate
     * @return 		the color
     */
    public int getIndex(double x, double y) {
      int result;
      double 	density;

      density = getDensity(x, y);
      result  = (int) ((density - binMin) / (binMax - binMin) * (colors.length - 1));

      return result;
    }

    /**
     * Returns the color to use for the data point.
     *
     * @param x    	the x coordinate
     * @param y 	the y coordinate
     * @return 		the color
     */
    public Color getColor(double x, double y) {
      Color	result;
      int	index;

      index  = getIndex(x, y);
      result = colors[index];

      return result;
    }
  }

  /** the mode to use. */
  protected Mode m_Mode;

  /** the number of bins to generate on X and Y. */
  protected int m_NumBins;

  /** the bandwidth. */
  protected double m_Bandwidth;

  /** the generator to use. */
  protected ColorGradientGenerator m_Generator;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "For calculating kernel-density estimates for plots.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

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
   * Calculates the render state.
   *
   * @param x		the x values
   * @param y 		the y values
   */
  public RenderState calculate(double[] x, double[] y) {
    RenderState 	result;
    int			i;
    int			xi;
    int			yi;
    double		h2;
    double 		norm;
    double 		xCenter;
    double 		yCenter;
    double 		sum;
    double 		dx;
    double 		dy;
    double 		r2;

    result = new RenderState();

    // colors
    result.colors = m_Generator.generate();

    // init bins
    result.numBins = m_NumBins;
    result.bins    = new double[m_NumBins][m_NumBins];
    result.startX  = new double[m_NumBins];
    result.startY  = new double[m_NumBins];
    result.xMin    = StatUtils.min(x);
    result.xMax    = StatUtils.max(x);
    result.xRange  = result.xMax - result.xMin;
    result.xWidth  = result.xRange / m_NumBins;
    result.yMin    = StatUtils.min(y);
    result.yMax    = StatUtils.max(y);
    result.yRange  = result.yMax - result.yMin;
    result.yWidth  = result.yRange / m_NumBins;
    for (i = 0; i < m_NumBins; i++) {
      result.startX[i] = result.xMin + i* result.xWidth;
      result.startY[i] = result.yMin + i* result.yWidth;
    }

    // fill bins
    if (m_Mode == Mode.HISTOGRAM) {
      for (i = 0; i < x.length; i++) {
	xi = (int) ((x[i] - result.xMin) / (result.xMax - result.xMin) * (m_NumBins - 1));
	yi = (int) ((y[i] - result.yMin) / (result.yMax - result.yMin) * (m_NumBins - 1));
	result.bins[yi][xi]++;
      }
    }
    else {
      // Gaussian kernel KDE evaluated at bin centers
      h2 = m_Bandwidth * m_Bandwidth;
      // normalization factor for 2D Gaussian: 1 / (2π h^2 n)
      norm = 1.0 / (2.0 * Math.PI * h2 * x.length);

      for (xi = 0; xi < m_NumBins; xi++) {
	xCenter = result.xMin + (xi + 0.5) * (result.xMax - result.xMin) / m_NumBins;
	for (yi = 0; yi < m_NumBins; yi++) {
	  yCenter = result.yMin + (yi + 0.5) * (result.yMax - result.yMin) / m_NumBins;
	  sum = 0.0;
	  for (i = 0; i < x.length; i++) {
	    dx = xCenter - x[i];
	    dy = yCenter - y[i];
	    r2 = dx * dx + dy * dy;
	    sum += Math.exp(-0.5 * r2 / h2); // Gaussian kernel unnormalized
	  }
	  result.bins[yi][xi] = sum * norm;
	}
      }
    }

    // determine min/max
    result.binMin = Integer.MAX_VALUE;
    result.binMax = 0;
    for (yi = 0; yi < m_NumBins; yi++) {
      for (xi = 0; xi < m_NumBins; xi++) {
	result.binMin = Math.min(result.binMin, result.bins[yi][xi]);
	result.binMax = Math.max(result.binMax, result.bins[yi][xi]);
      }
    }
    
    return result;
  }
}
