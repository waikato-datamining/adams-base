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
 * ColorChooserPanel.java
 * Copyright (C) 2008-2019 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.chooser;

import adams.gui.core.ColorHelper;

import javax.swing.JColorChooser;
import java.awt.Color;

/**
 * A panel that contains a text field with the current Color (in hex notation)
 * and a button for bringing up a Color dialog.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ColorChooserPanel
  extends AbstractChooserPanel<Color> {

  /** for serialization. */
  private static final long serialVersionUID = -8755020252465094120L;

  /**
   * Initializes the panel with BLACK.
   */
  public ColorChooserPanel() {
    this(Color.BLACK);
  }

  /**
   * Initializes the panel with the given color.
   *
   * @param color	the color to use
   */
  public ColorChooserPanel(Color color) {
    super();

    setCurrent(color);
  }

  /**
   * Returns the number of columns in the selection text field.
   *
   * @return		the number of columns
   */
  protected int getSelectionColumns() {
    return 10;
  }

  /**
   * Returns the tooltip for the text field.
   *
   * @return		the tooltip
   */
  protected String textFieldToolTipText() {
    return "<html>"
      + "Formats:\n"
      + "<ul>\n"
      + "  <li>hex notation: #(AA)RRGGBB with AA/RR/GG/BB being hexadecimal strings</li>\n"
      + "  <li>RGB notation: (A,)R,G,B with A/R/G/B from 0-255</li>\n"
      + "  </li>predefined names (case-insensitive): black, blue, cyan, darkgray,"
      + "  darkgrey, gray, grey, green, lightgray, lightgrey, magenta, orange,"
      + "  pink, red, white, yellow</li>\n"
      + "</ul>\n"
      + "</html>";
  }

  /**
   * Performs the actual choosing of an object.
   *
   * @return		the chosen object or null if none chosen
   */
  protected Color doChoose() {
    return JColorChooser.showDialog(m_Self, "Select color", getCurrent());
  }

  /**
   * Converts the value into its string representation.
   *
   * @param value	the value to convert
   * @return		the generated string
   */
  protected String toString(Color value) {
    return ColorHelper.toHex(value);
  }

  /**
   * Converts the string representation into its object representation.
   *
   * @param value	the string value to convert
   * @return		the generated object
   */
  protected Color fromString(String value) {
    return ColorHelper.valueOf(value);
  }

  /**
   * Checks whether the string value is valid and can be parsed.
   *
   * @param value	the value to check
   * @return		true if valid
   */
  protected boolean isValid(String value) {
    return (value != null) && (ColorHelper.valueOf(value, null) != null);
  }
}
