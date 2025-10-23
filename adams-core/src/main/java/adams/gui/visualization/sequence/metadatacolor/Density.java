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

import adams.core.ObjectCopyHelper;
import adams.data.container.DataContainer;
import adams.data.sequence.XYSequencePoint;
import adams.gui.visualization.core.BiColorGenerator;
import adams.gui.visualization.core.ColorGradientGenerator;
import adams.gui.visualization.core.KernelDensityEstimation;
import adams.gui.visualization.core.KernelDensityEstimation.Mode;
import adams.gui.visualization.core.KernelDensityEstimation.RenderState;

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
    RenderState			state;
    DataContainer		parent;
    double[]			x;
    double[]			y;
    int				i;
    KernelDensityEstimation	kde;

    if (m_Cache == null)
      m_Cache = new HashMap<>();

    if (points.isEmpty())
      return;

    parent = points.get(0).getParent();
    if (m_Cache.containsKey(parent))
      return;

    x = new double[points.size()];
    y = new double[points.size()];
    for (i = 0; i < points.size(); i++) {
      x[i] = points.get(i).getX();
      y[i] = points.get(i).getY();
    }

    kde = new KernelDensityEstimation();
    kde.setMode(m_Mode);
    kde.setNumBins(m_NumBins);
    kde.setBandwidth(m_Bandwidth);
    kde.setGenerator(ObjectCopyHelper.copyObject(m_Generator));
    state = kde.calculate(x, y);
    m_Cache.put(parent, state);
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
    RenderState	state;

    result = defColor;
    state  = null;
    if (m_Cache != null)
      state = m_Cache.get(point.getParent());
    if (state != null)
      result = state.getColor(point.getX(), point.getY());

    return result;
  }
}
