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
 * DoubleMatrixToBufferedImage.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.data.image.AbstractImageContainer;
import adams.data.image.BufferedImageContainer;
import adams.gui.visualization.core.BiColorGenerator;
import adams.gui.visualization.core.ColorGradientGenerator;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 <!-- globalinfo-start -->
 * Turns a matrix of double values into a BufferedImage.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-generator &lt;adams.gui.visualization.core.ColorGradientGenerator&gt; (property: generator)
 * &nbsp;&nbsp;&nbsp;The generator to use for creating the gradient colors.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.core.BiColorGenerator
 * </pre>
 *
 * <pre>-missing-value-color &lt;java.awt.Color&gt; (property: missingValueColor)
 * &nbsp;&nbsp;&nbsp;The color to use for missing values (ie NaN values).
 * &nbsp;&nbsp;&nbsp;default: #00ffffff
 * </pre>
 *
 * <pre>-use-fixed-range &lt;boolean&gt; (property: useFixedRange)
 * &nbsp;&nbsp;&nbsp;Whether to use pre-defined min&#47;max values or ones determined from the heatmap
 * &nbsp;&nbsp;&nbsp;itself.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-min &lt;double&gt; (property: min)
 * &nbsp;&nbsp;&nbsp;The minimum to use in case of using a fixed range.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * </pre>
 *
 * <pre>-max &lt;double&gt; (property: max)
 * &nbsp;&nbsp;&nbsp;The maximum to use in case of using a fixed range.
 * &nbsp;&nbsp;&nbsp;default: 100.0
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class DoubleMatrixToBufferedImage
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = 2535421741524997185L;

  /** the generator to use. */
  protected ColorGradientGenerator m_Generator;

  /** the color for missing values. */
  protected Color m_MissingValueColor;

  /** whether to use a fixed min/max. */
  protected boolean m_UseFixedRange;

  /** the fixed min. */
  protected double m_Min;

  /** the fixed max. */
  protected double m_Max;

  /** the gradient colors. */
  protected Color[] m_GradientColors;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns a matrix of double values into a BufferedImage.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "generator", "generator",
      new BiColorGenerator());

    m_OptionManager.add(
      "missing-value-color", "missingValueColor",
      new Color(255, 255, 255, 0));

    m_OptionManager.add(
      "use-fixed-range", "useFixedRange",
      false);

    m_OptionManager.add(
      "min", "min",
      0.0);

    m_OptionManager.add(
      "max", "max",
      100.0);
  }

  /**
   * Resets the object.
   */
  @Override
  protected void reset() {
    super.reset();

    m_GradientColors = null;
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
   * Sets the color for missing values.
   *
   * @param value	the color
   */
  public void setMissingValueColor(Color value) {
    m_MissingValueColor = value;
    reset();
  }

  /**
   * Returns the color for missing values.
   *
   * @return		the color
   */
  public Color getMissingValueColor() {
    return m_MissingValueColor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String missingValueColorTipText() {
    return "The color to use for missing values (ie NaN values).";
  }

  /**
   * Sets whether to use a fixed range.
   *
   * @param value	true if to use fixed range
   */
  public void setUseFixedRange(boolean value) {
    m_UseFixedRange = value;
    reset();
  }

  /**
   * Returns whether to use a fixed range.
   *
   * @return		true if to use fixed range
   */
  public boolean getUseFixedRange() {
    return m_UseFixedRange;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useFixedRangeTipText() {
    return "Whether to use pre-defined min/max values or ones determined from the heatmap itself.";
  }

  /**
   * Sets the minimum in case of using a fixed range.
   *
   * @param value	the minimum
   */
  public void setMin(double value) {
    m_Min = value;
    reset();
  }

  /**
   * Returns the minimum in case of using a fixed range.
   *
   * @return		the minimum
   */
  public double getMin() {
    return m_Min;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String minTipText() {
    return "The minimum to use in case of using a fixed range.";
  }

  /**
   * Sets the maximum in case of using a fixed range.
   *
   * @param value	the maximum
   */
  public void setMax(double value) {
    m_Max = value;
    reset();
  }

  /**
   * Returns the maximum in case of using a fixed range.
   *
   * @return		the maximum
   */
  public double getMax() {
    return m_Max;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxTipText() {
    return "The maximum to use in case of using a fixed range.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return Double[][].class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return AbstractImageContainer.class;
  }

  /**
   * Generates the gradient colors.
   *
   * @return		the colors
   */
  protected Color[] getGradientColors() {
    if (m_GradientColors == null)
      m_GradientColors = m_Generator.generate();

    return m_GradientColors;
  }

  /**
   * Checks whether the data can be processed.
   *
   * @return		null if checks passed, otherwise error message
   */
  @Override
  protected String checkData() {
    String	result;

    result = super.checkData();

    if (result == null) {
      if (m_UseFixedRange) {
	if (m_Min >= m_Max)
	  result = "Max must be greater than Min: max=" + m_Max + ", min=" + m_Min;
      }
    }

    return result;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    BufferedImageContainer	result;
    BufferedImage		image;
    Color[]			colors;
    Double[][] 			data;
    int				width;
    int				height;
    double			min;
    double			max;
    double			range;
    int				x;
    int				y;
    Graphics2D			g;
    Color			color;

    data = (Double[][]) m_Input;
    if (data.length == 0)
      throw new IllegalStateException("No data in double matrix!");

    height = data.length;
    width  = data[0].length;
    if (m_UseFixedRange) {
      min = m_Min;
      max = m_Max;
    }
    else {
      min = Double.MAX_VALUE;
      max = -Double.MAX_VALUE;
      for (y = 0; y < height; y++) {
	for (x = 0; x < width; x++) {
	  min = Math.min(min, data[y][x]);
	  max = Math.max(max, data[y][x]);
	}
      }
      if (max == min) {
	max = min + 1.0;
	getLogger().warning("Max/min are the same, using min=" + min + ", max=" + max + " instead!");
      }
    }
    colors = getGradientColors();
    range  = max - min;

    image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    g      = image.createGraphics();
    for (y = 0; y < height; y++) {
      for (x = 0; x < width; x++) {
	if (Double.isNaN(data[y][x]))
	  color = m_MissingValueColor;
	else
	  color = colors[(int) (((data[y][x] - min) / range) * (colors.length - 2)) + 1];
	g.setColor(color);
	g.drawLine(x, y, x, y);
      }
    }

    result = new BufferedImageContainer();
    result.setImage(image);

    return result;
  }
}
