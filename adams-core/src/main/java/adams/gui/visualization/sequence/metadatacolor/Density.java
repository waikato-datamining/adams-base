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
 * Density.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.sequence.metadatacolor;

import adams.data.container.DataContainer;
import adams.data.sequence.XYSequencePoint;
import adams.data.statistics.StatUtils;
import adams.gui.visualization.core.BiColorGenerator;
import adams.gui.visualization.core.ColorGradientGenerator;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates colors based on the point density in the grid of numBins x numBins bins.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Density
  extends AbstractMetaDataColor<XYSequencePoint> {

  private static final long serialVersionUID = 83719639628991590L;

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
  }
  
  /** the mode to use. */
  protected Mode m_Mode;

  /** the number of bins to generate on X and Y. */
  protected int m_NumBins;

  /** the bandwidth. */
  protected double m_Bandwidth;

  /** the generator to use. */
  protected ColorGradientGenerator m_Generator;

  /** the cached states. */
  protected transient Map<DataContainer, RenderState> m_Cache;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates colors based on the point density in the grid of numBins x numBins bins.";
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
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Cache = null;
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
   * Initializes the meta-data color scheme.
   *
   * @param points the points to initialize with
   */
  @Override
  public void initialize(List<XYSequencePoint> points) {
    RenderState		state;
    DataContainer	parent;
    double[]		x;
    double[]		y;
    int			i;
    int 		xIndex;
    int 		yIndex;
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

    if (m_Cache == null)
      m_Cache = new HashMap<>();

    if (points.isEmpty())
      return;

    parent = points.get(0).getParent();
    if (m_Cache.containsKey(parent))
      return;

    state = new RenderState();
    m_Cache.put(parent, state);

    // colors
    state.colors = m_Generator.generate();

    // init bins
    state.bins = new double[m_NumBins][m_NumBins];
    state.startX = new double[m_NumBins];
    state.startY = new double[m_NumBins];
    x        = new double[points.size()];
    y        = new double[points.size()];
    for (i = 0; i < points.size(); i++) {
      x[i] = points.get(i).getX();
      y[i] = points.get(i).getY();
    }
    state.xMin = StatUtils.min(x);
    state.xMax = StatUtils.max(x);
    state.xWidth = (state.xMax - state.xMin) / m_NumBins;
    state.yMin = StatUtils.min(y);
    state.yMax = StatUtils.max(y);
    state.yWidth = (state.yMax - state.yMin) / m_NumBins;
    for (i = 0; i < m_NumBins; i++) {
      state.startX[i] = state.xMin + i* state.xWidth;
      state.startY[i] = state.yMin + i* state.yWidth;
    }

    // fill bins
    if (m_Mode == Mode.HISTOGRAM) {
      for (i = 0; i < x.length; i++) {
	xIndex = (int) ((x[i] - state.xMin) / (state.xMax - state.xMin) * (m_NumBins - 1));
	yIndex = (int) ((y[i] - state.yMin) / (state.yMax - state.yMin) * (m_NumBins - 1));
	state.bins[yIndex][xIndex]++;
      }
    }
    else {
      // Gaussian kernel KDE evaluated at bin centers
      h2 = m_Bandwidth * m_Bandwidth;
      // normalization factor for 2D Gaussian: 1 / (2π h^2 n)
      norm = 1.0 / (2.0 * Math.PI * h2 * points.size());

      for (xi = 0; xi < m_NumBins; xi++) {
	xCenter = state.xMin + (xi + 0.5) * (state.xMax - state.xMin) / m_NumBins;
	for (yi = 0; yi < m_NumBins; yi++) {
	  yCenter = state.yMin + (yi + 0.5) * (state.yMax - state.yMin) / m_NumBins;
	  sum = 0.0;
	  for (i = 0; i < x.length; i++) {
	    dx = xCenter - x[i];
	    dy = yCenter - y[i];
	    r2 = dx * dx + dy * dy;
	    sum += Math.exp(-0.5 * r2 / h2); // Gaussian kernel unnormalized
	  }
	  state.bins[xi][yi] = sum * norm;
	}
      }
    }

    // determine min/max
    state.binMin = Integer.MAX_VALUE;
    state.binMax = 0;
    for (yIndex = 0; yIndex < m_NumBins; yIndex++) {
      for (xIndex = 0; xIndex < m_NumBins; xIndex++) {
	state.binMin = Math.min(state.binMin, state.bins[yIndex][xIndex]);
	state.binMax = Math.max(state.binMax, state.bins[yIndex][xIndex]);
      }
    }
  }

  /**
   * Extracts the color from the meta-data.
   *
   * @param point    the point to get the color from
   * @param defColor the default color to use
   * @return the color
   */
  @Override
  public Color getColor(XYSequencePoint point, Color defColor) {
    Color	result;
    int		index;
    double	x;
    double	y;
    int 	xIndex;
    int 	yIndex;
    double	count;
    RenderState	state;

    result = defColor;

    state = null;
    if (m_Cache != null)
      state = m_Cache.get(point.getParent());
    if (state != null) {
      x      = point.getX();
      y      = point.getY();
      xIndex = (int) ((x - state.xMin) / (state.xMax - state.xMin) * (m_NumBins - 1));
      yIndex = (int) ((y - state.yMin) / (state.yMax - state.yMin) * (m_NumBins - 1));
      count  = state.bins[yIndex][xIndex];
      index  = (int) ((count - state.binMin) / (state.binMax - state.binMin) * (state.colors.length - 1));
      result = state.colors[index];
    }

    return result;
  }
}
