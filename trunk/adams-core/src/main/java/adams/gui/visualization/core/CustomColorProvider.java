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

/**
 * DefaultColorProvider.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.core;

import java.awt.Color;
import java.util.Arrays;

/**
 <!-- globalinfo-start -->
 * Allows the user to define colors.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-color &lt;java.awt.Color&gt; [-color ...] (property: colors)
 * &nbsp;&nbsp;&nbsp;The colors to use.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-darkening (property: allowDarkening)
 * &nbsp;&nbsp;&nbsp;If enabled, colors are re-used in a darker version.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CustomColorProvider
  extends AbstractColorProvider {

  /** for serialization. */
  private static final long serialVersionUID = -6184352647827352221L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Allows the user to define colors.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"color", "colors",
	new Color[]{
	    Color.BLUE,
	    Color.CYAN,
	    Color.GREEN,
	    Color.MAGENTA,
	    Color.ORANGE,
	    Color.PINK,
	    Color.RED
	});

    m_OptionManager.add(
	"darkening", "allowDarkening",
	false);
  }

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
   * Sets whether to allow the darkening of colors.
   *
   * @param value	if true colors are re-used in darker versions
   */
  public void setAllowDarkening(boolean value) {
    m_AllowDarkening = value;
    reset();
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
    return "If enabled, colors are re-used in a darker version.";
  }
}
