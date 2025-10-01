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

  /** the generator to use. */
  protected ColorGradientGenerator m_Generator;

  /** the number of bins to generate on X and Y. */
  protected int m_NumBins;

  /** the generated colors. */
  protected transient Color[] m_Colors;

  /** the bins. */
  protected transient int[][] m_Bins;

  /** the starting values for X bins. */
  protected transient double[] m_StartX;

  /** the starting values for Y bins. */
  protected transient double[] m_StartY;

  /** the smallest bin value. */
  protected transient int m_BinMin;

  /** the largest bin value. */
  protected transient int m_BinMax;

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
      "num-bins", "numBins",
      50, 1, null);

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

    m_Colors = null;
    m_Bins   = null;
    m_StartX = null;
    m_StartY = null;

    if (points.isEmpty())
      return;

    // colors
    m_Colors = m_Generator.generate();

    // init bins
    m_Bins   = new int[m_NumBins][m_NumBins];
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
    for (i = 0; i < x.length; i++) {
      xIndex = (int) ((x[i] - m_XMin) / (m_XMax - m_XMin) * (m_NumBins - 1));
      yIndex = (int) ((y[i] - m_YMin) / (m_YMax - m_YMin) * (m_NumBins - 1));
      m_Bins[yIndex][xIndex]++;
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
    int		count;

    result = defColor;

    if (m_Bins != null) {
      x      = point.getX();
      y      = point.getY();
      xIndex = (int) ((x - m_XMin) / (m_XMax - m_XMin) * (m_NumBins - 1));
      yIndex = (int) ((y - m_YMin) / (m_YMax - m_YMin) * (m_NumBins - 1));
      count  = m_Bins[yIndex][xIndex];
      index  = (int) (((double) count - m_BinMin) / (m_BinMax - m_BinMin) * (m_Colors.length - 1));
      result = m_Colors[index];
    }

    return result;
  }
}
