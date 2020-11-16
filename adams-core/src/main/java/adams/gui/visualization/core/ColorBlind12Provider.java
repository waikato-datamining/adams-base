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
 * ColorBlind12Provider.java
 * Copyright (C) 2020 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.core;

import adams.gui.core.ColorHelper;

import java.awt.Color;

/**
 <!-- globalinfo-start -->
 * Color provider for color blind people, using 12 colors.<br>
 * See:<br>
 * http:&#47;&#47;mkweb.bcgsc.ca&#47;colorblind&#47;palettes.mhtml<br>
 * http:&#47;&#47;mkweb.bcgsc.ca&#47;colorblind&#47;palettes&#47;12.color.blindness.palette.txt
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-color &lt;java.awt.Color&gt; [-color ...] (property: colors)
 * &nbsp;&nbsp;&nbsp;The colors to use.
 * &nbsp;&nbsp;&nbsp;default: #9f0162, #009f81, #ff5aaf, #00fccf, #8400cd, #008df9, #00c2f9, #ffb2fd, #a40122, #e20134, #ff6e3a, #ffc33b
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ColorBlind12Provider
  extends AbstractCustomColorProvider {

  /** for serialization. */
  private static final long serialVersionUID = -6184352647827352221L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Color provider for color blind people, using 12 colors.\n"
      + "See:\n"
      + "http://mkweb.bcgsc.ca/colorblind/palettes.mhtml\n"
      + "http://mkweb.bcgsc.ca/colorblind/palettes/12.color.blindness.palette.txt";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_CheckTooDark = false;
  }

  /**
   * Returns the default colors to use.
   *
   * @return		the colors
   */
  protected Color[] getDefaultColors() {
    return new Color[]{
      ColorHelper.valueOf("#9F0162"),
      ColorHelper.valueOf("#009F81"),
      ColorHelper.valueOf("#FF5AAF"),
      ColorHelper.valueOf("#00FCCF"),
      ColorHelper.valueOf("#8400CD"),
      ColorHelper.valueOf("#008DF9"),
      ColorHelper.valueOf("#00C2F9"),
      ColorHelper.valueOf("#FFB2FD"),
      ColorHelper.valueOf("#A40122"),
      ColorHelper.valueOf("#E20134"),
      ColorHelper.valueOf("#FF6E3A"),
      ColorHelper.valueOf("#FFC33B"),
    };
  }

  /**
   * Returns whether the allow-darkening option is enabled.
   *
   * @return		true if enabled
   */
  protected boolean isDarkeningEnabled() {
    return false;
  }
}
