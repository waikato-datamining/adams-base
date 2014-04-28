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
 * Copyright (C) 2008-2010 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.chooser;

import java.awt.Color;

import javax.swing.JColorChooser;

import adams.gui.core.ColorHelper;

/**
 * A panel that contains a text field with the current Color (in hex notation)
 * and a button for bringing up a Color dialog.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
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
}
