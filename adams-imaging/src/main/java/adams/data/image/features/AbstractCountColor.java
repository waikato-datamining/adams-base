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
 * CountColor.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.data.image.features;

import adams.data.featureconverter.HeaderDefinition;
import adams.data.image.BufferedImageContainer;
import adams.data.report.DataType;
import adams.gui.core.ColorHelper;

import java.awt.Color;

/**
 * Ancestor for feature generators that count pixels based on color.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractCountColor
  extends AbstractBufferedImageFeatureGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -8349656592325229512L;

  /** the color to count. */
  protected Color m_Color;

  /** whether to return the percentage instead of absolute count. */
  protected boolean m_UsePercentage;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "color", "color",
      Color.WHITE);

    m_OptionManager.add(
      "use-percentage", "usePercentage",
      false);
  }

  /**
   * Sets the color to count.
   *
   * @param value	the color
   */
  public void setColor(Color value) {
    m_Color = value;
    reset();
  }

  /**
   * Returns the color to count.
   *
   * @return		the color
   */
  public Color getColor() {
    return m_Color;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorTipText() {
    return "The color to count.";
  }

  /**
   * Sets whether to output percentage instead of absolute count.
   *
   * @param value	the color
   */
  public void setUsePercentage(boolean value) {
    m_UsePercentage = value;
    reset();
  }

  /**
   * Returns whether to output percentage instead of absolute count.
   *
   * @return		true if to output percentage
   */
  public boolean getUsePercentage() {
    return m_UsePercentage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String usePercentageTipText() {
    return "If enabled, a percentage is output (0-1) rather than an absolute count.";
  }

  /**
   * Creates the header from a template image.
   *
   * @param img		the image to act as a template
   * @return		the generated header
   */
  @Override
  public HeaderDefinition createHeader(BufferedImageContainer img) {
    HeaderDefinition	result;

    result = new HeaderDefinition();
    result.add("Count-" + ColorHelper.toHex(m_Color), DataType.NUMERIC);

    return result;
  }
}
