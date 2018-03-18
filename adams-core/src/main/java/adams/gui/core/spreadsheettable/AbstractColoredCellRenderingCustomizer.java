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
 * AbstractCellRenderingCustomizer.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.core.spreadsheettable;

import java.awt.Color;

/**
 * Ancestor for cell rendering customizers with predefined foreground/background colors.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractColoredCellRenderingCustomizer
  extends AbstractCellRenderingCustomizer {

  private static final long serialVersionUID = -4927739958774377498L;

  /** whether to use negative background. */
  protected boolean m_UseBackgroundNegative;

  /** the custom background color for negative values. */
  protected Color m_BackgroundNegative;

  /** whether to use positive background. */
  protected boolean m_UseBackgroundPositive;

  /** the custom background color for positive values. */
  protected Color m_BackgroundPositive;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "use-background-negative", "useBackgroundNegative",
      getDefaultUseBackgroundNegative());

    m_OptionManager.add(
      "background-negative", "backgroundNegative",
      getDefaultBackgroundNegative());

    m_OptionManager.add(
      "use-background-positive", "useBackgroundPositive",
      getDefaultUseBackgroundPositive());

    m_OptionManager.add(
      "background-positive", "backgroundPositive",
      getDefaultBackgroundPositive());
  }

  /**
   * Returns the default for using the negative background.
   *
   * @return		the default
   */
  protected boolean getDefaultUseBackgroundNegative() {
    return false;
  }

  /**
   * Sets whether to use the negative background color.
   *
   * @param value	true if to use
   */
  public void setUseBackgroundNegative(boolean value) {
    m_UseBackgroundNegative = value;
    reset();
  }

  /**
   * Returns whether to use the negative background color.
   *
   * @return		true if to use
   */
  public boolean getUseBackgroundNegative() {
    return m_UseBackgroundNegative;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public  String useBackgroundNegativeTipText() {
    return "If enabled, makes use of the negative background color.";
  }

  /**
   * Returns the default negative background.
   *
   * @return		the default
   */
  protected Color getDefaultBackgroundNegative() {
    return Color.RED;
  }

  /**
   * Sets the negative background color.
   *
   * @param value	the color
   */
  public void setBackgroundNegative(Color value) {
    m_BackgroundNegative = value;
    reset();
  }

  /**
   * Returns the negative background color.
   *
   * @return		the color
   */
  public Color getBackgroundNegative() {
    return m_BackgroundNegative;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public  String backgroundNegativeTipText() {
    return "The negative background color to use (if enabled).";
  }

  /**
   * Returns the default for using the positive background.
   *
   * @return		the default
   */
  protected boolean getDefaultUseBackgroundPositive() {
    return false;
  }

  /**
   * Sets whether to use the positive background color.
   *
   * @param value	true if to use
   */
  public void setUseBackgroundPositive(boolean value) {
    m_UseBackgroundPositive = value;
    reset();
  }

  /**
   * Returns whether to use the positive background color.
   *
   * @return		true if to use
   */
  public boolean getUseBackgroundPositive() {
    return m_UseBackgroundPositive;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public  String useBackgroundPositiveTipText() {
    return "If enabled, makes use of the positive background color.";
  }

  /**
   * Returns the default positive background.
   *
   * @return		the default
   */
  protected Color getDefaultBackgroundPositive() {
    return Color.WHITE;
  }

  /**
   * Sets the positive background color.
   *
   * @param value	the color
   */
  public void setBackgroundPositive(Color value) {
    m_BackgroundPositive = value;
    reset();
  }

  /**
   * Returns the positive background color.
   *
   * @return		the color
   */
  public Color getBackgroundPositive() {
    return m_BackgroundPositive;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public  String backgroundPositiveTipText() {
    return "The positive background color to use (if enabled).";
  }
}
