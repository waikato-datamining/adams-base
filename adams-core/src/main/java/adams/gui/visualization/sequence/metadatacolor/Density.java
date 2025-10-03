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

import adams.data.sequence.XYSequencePoint;
import adams.data.statistics.StatUtils;
import adams.gui.visualization.core.BiColorGenerator;
import adams.gui.visualization.core.ColorGradientGenerator;

import java.awt.Color;
import java.util.List;

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

  /** the mode to use. */
  protected Mode m_Mode;

  /** the number of bins to generate on X and Y. */
  protected int m_NumBins;

  /** the bandwidth. */
  protected double m_Bandwidth;

  /** the generator to use. */
  protected ColorGradientGenerator m_Generator;

  /** the generated colors. */
  protected transient Color[] m_Colors;

  /** the bins. */
  protected transient double[][] m_Bins;

  /** the starting values for X bins. */
  protected transient double[] m_StartX;

  /** the starting values for Y bins. */
  protected transient double[] m_StartY;

  /** the smallest bin value. */
  protected transient double m_BinMin;

  /** the largest bin value. */
  protected transient double m_BinMax;

  /** the minimum x value. */
  protected transient double m_XMin;

  /** the maximum x value. */
  protected transient double m_XMax;

  /** the minimum x value. */
  protected transient double m_YMin;

  /** the maximum x value. */
  protected transient double m_YMax;

  /** the X bin width. */
  protected transient double m_XWidth;

  /** the Y bin width. */
  protected transient double m_YWidth;

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

    m_Colors = null;
    m_Bins   = null;
    m_StartX = null;
    m_StartY = null;
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
    double[]	x;
    double[]	y;
    int		i;
    int 	xIndex;
    int 	yIndex;
    int		xi;
    int		yi;

    m_Colors = null;
    m_Bins   = null;
    m_StartX = null;
    m_StartY = null;

    if (points.isEmpty())
      return;

    // colors
    m_Colors = m_Generator.generate();

    // init bins
    m_Bins   = new double[m_NumBins][m_NumBins];
    m_StartX = new double[m_NumBins];
    m_StartY = new double[m_NumBins];
    x        = new double[points.size()];
    y        = new double[points.size()];
    for (i = 0; i < points.size(); i++) {
      x[i] = points.get(i).getX();
      y[i] = points.get(i).getY();
    }
    m_XMin = StatUtils.min(x);
    m_XMax = StatUtils.max(x);
    m_XWidth = (m_XMax - m_XMin) / m_NumBins;
    m_YMin = StatUtils.min(y);
    m_YMax = StatUtils.max(y);
    m_YWidth = (m_YMax - m_YMin) / m_NumBins;
    for (i = 0; i < m_NumBins; i++) {
      m_StartX[i] = m_XMin + i* m_XWidth;
      m_StartY[i] = m_YMin + i* m_YWidth;
    }

    // fill bins
    if (m_Mode == Mode.HISTOGRAM) {
      for (i = 0; i < x.length; i++) {
	xIndex = (int) ((x[i] - m_XMin) / (m_XMax - m_XMin) * (m_NumBins - 1));
	yIndex = (int) ((y[i] - m_YMin) / (m_YMax - m_YMin) * (m_NumBins - 1));
	m_Bins[yIndex][xIndex]++;
      }
    }
    else {
      // Gaussian kernel KDE evaluated at bin centers
      double h = m_Bandwidth;
      double h2 = h * h;
      // normalization factor for 2D Gaussian: 1 / (2π h^2 n)
      double norm = 1.0 / (2.0 * Math.PI * h2 * points.size());

      for (xi = 0; xi < m_NumBins; xi++) {
	double xCenter = m_XMin + (xi + 0.5) * (m_XMax - m_XMin) / m_NumBins;
	for (yi = 0; yi < m_NumBins; yi++) {
	  double yCenter = m_YMin + (yi + 0.5) * (m_YMax - m_YMin) / m_NumBins;
	  double sum = 0.0;
	  for (i = 0; i < x.length; i++) {
	    double dx = xCenter - x[i];
	    double dy = yCenter - y[i];
	    double r2 = dx * dx + dy * dy;
	    sum += Math.exp(-0.5 * r2 / h2); // Gaussian kernel unnormalized
	  }
	  m_Bins[xi][yi] = sum * norm;
	}
      }
    }

    // determine min/max
    m_BinMin = Integer.MAX_VALUE;
    m_BinMax = 0;
    for (yIndex = 0; yIndex < m_NumBins; yIndex++) {
      for (xIndex = 0; xIndex < m_NumBins; xIndex++) {
	m_BinMin = Math.min(m_BinMin, m_Bins[yIndex][xIndex]);
	m_BinMax = Math.max(m_BinMax, m_Bins[yIndex][xIndex]);
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

    result = defColor;

    if (m_Bins != null) {
      x      = point.getX();
      y      = point.getY();
      xIndex = (int) ((x - m_XMin) / (m_XMax - m_XMin) * (m_NumBins - 1));
      yIndex = (int) ((y - m_YMin) / (m_YMax - m_YMin) * (m_NumBins - 1));
      count  = m_Bins[yIndex][xIndex];
      index  = (int) ((count - m_BinMin) / (m_BinMax - m_BinMin) * (m_Colors.length - 1));
      result = m_Colors[index];
    }

    return result;
  }
}
