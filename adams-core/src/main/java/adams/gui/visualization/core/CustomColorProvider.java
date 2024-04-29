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
 * CustomColorProvider.java
 * Copyright (C) 2011-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.core;

import java.awt.Color;

/**
 <!-- globalinfo-start -->
 * Allows the user to define colors.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
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
 */
public class CustomColorProvider
  extends AbstractCustomColorProvider {

  /** for serialization. */
  private static final long serialVersionUID = -6184352647827352221L;

  /**
   * Default constructors.
   */
  public CustomColorProvider() {
    super();
  }

  /**
   * Allows setting the colors immediately.
   *
   * @param colors	the colors to use
   */
  public CustomColorProvider(Color[] colors) {
    this();
    setColors(colors);
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Allows the user to define colors.";
  }

  /**
   * Returns the default colors to use.
   *
   * @return		the colors
   */
  protected Color[] getDefaultColors() {
    return new Color[]{
      Color.BLUE,
      Color.CYAN,
      Color.GREEN,
      Color.MAGENTA,
      Color.ORANGE,
      Color.PINK,
      Color.RED
    };
  }

  /**
   * Returns whether the allow-darkening option is enabled.
   *
   * @return		true if enabled
   */
  protected boolean isDarkeningEnabled() {
    return true;
  }
}
