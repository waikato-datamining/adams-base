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
 * ColorBlind24Provider.java
 * Copyright (C) 2020 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.core;

import adams.gui.core.ColorHelper;

import java.awt.Color;

/**
 <!-- globalinfo-start -->
 * Color provider for color blind people, using 24 colors.<br>
 * See:<br>
 * http:&#47;&#47;mkweb.bcgsc.ca&#47;colorblind&#47;palettes.mhtml<br>
 * http:&#47;&#47;mkweb.bcgsc.ca&#47;colorblind&#47;palettes&#47;24.color.blindness.palette.txt
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
 * &nbsp;&nbsp;&nbsp;default: #003d30, #005745, #00735c, #009175, #00af8e, #00cba7, #00ebc1, #86ffde, #00306f, #00489e, #005fcc, #0079fa, #009ffa, #00c2f9, #00e5f8, #7cfffa, #004002, #005a01, #007702, #009503, #00b408, #00d302, #00f407, #afff2a
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ColorBlind24Provider
  extends AbstractCustomColorProvider {

  /** for serialization. */
  private static final long serialVersionUID = -6184352647827352221L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Color provider for color blind people, using 24 colors.\n"
      + "See:\n"
      + "http://mkweb.bcgsc.ca/colorblind/palettes.mhtml\n"
      + "http://mkweb.bcgsc.ca/colorblind/palettes/24.color.blindness.palette.txt";
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
      ColorHelper.valueOf("#003D30"),
      ColorHelper.valueOf("#005745"),
      ColorHelper.valueOf("#00735C"),
      ColorHelper.valueOf("#009175"),
      ColorHelper.valueOf("#00AF8E"),
      ColorHelper.valueOf("#00CBA7"),
      ColorHelper.valueOf("#00EBC1"),
      ColorHelper.valueOf("#86FFDE"),
      ColorHelper.valueOf("#00306F"),
      ColorHelper.valueOf("#00489E"),
      ColorHelper.valueOf("#005FCC"),
      ColorHelper.valueOf("#0079FA"),
      ColorHelper.valueOf("#009FFA"),
      ColorHelper.valueOf("#00C2F9"),
      ColorHelper.valueOf("#00E5F8"),
      ColorHelper.valueOf("#7CFFFA"),
      ColorHelper.valueOf("#004002"),
      ColorHelper.valueOf("#005A01"),
      ColorHelper.valueOf("#007702"),
      ColorHelper.valueOf("#009503"),
      ColorHelper.valueOf("#00B408"),
      ColorHelper.valueOf("#00D302"),
      ColorHelper.valueOf("#00F407"),
      ColorHelper.valueOf("#AFFF2A"),
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
