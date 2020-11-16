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
 * AbstractCustomColorProvider.java
 * Copyright (C) 2020 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.core;

import java.awt.Color;
import java.util.Arrays;

/**
 * Ancestor for custom color providers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractCustomColorProvider
  extends AbstractColorProvider {

  /** for serialization. */
  private static final long serialVersionUID = -6184352647827352221L;

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "color", "colors",
      getDefaultColors());

    if (isDarkeningEnabled()) {
      m_OptionManager.add(
	"darkening", "allowDarkening",
	false);
    }
  }

  /**
   * Returns the default colors to use.
   *
   * @return		the colors
   */
  protected abstract Color[] getDefaultColors();

  /**
   * Sets the colors to use.
   *
   * @param value	the colors to use
   */
  public void setColors(Color[] value) {
    m_DefaultColors.clear();
    m_DefaultColors.addAll(Arrays.asList(value));
    reset();
    resetColors();
  }

  /**
   * Returns the colors in use.
   *
   * @return		the colors in use
   */
  public Color[] getColors() {
    return m_DefaultColors.toArray(new Color[m_DefaultColors.size()]);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorsTipText() {
    return "The colors to use.";
  }

  /**
   * Returns whether the allow-darkening option is enabled.
   *
   * @return		true if enabled
   */
  protected abstract boolean isDarkeningEnabled();

  /**
   * Sets whether to allow the darkening of colors.
   *
   * @param value	if true colors are re-used in darker versions
   */
  public void setAllowDarkening(boolean value) {
    if (isDarkeningEnabled()) {
      m_AllowDarkening = value;
      reset();
    }
  }

  /**
   * Returns whether to allow the darkening of colors.
   *
   * @return		true if colors are re-used in darker versions
   */
  public boolean getAllowDarkening() {
    return m_AllowDarkening;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String allowDarkeningTipText() {
    return !isDarkeningEnabled() ? "DISABLED" : "If enabled, colors are re-used in a darker version.";
  }
}
