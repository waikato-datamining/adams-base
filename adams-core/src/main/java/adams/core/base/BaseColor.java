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
 * BaseColor.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

import java.awt.Color;

import adams.gui.core.ColorHelper;

/**
 * Wrapper for a Color object to be editable in the GOE.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseColor
  extends BaseObject {

  /** for serialization. */
  private static final long serialVersionUID = -5853830144343397434L;

  /** the default value. */
  public final static String DEFAULT = "#000000";
  
  /**
   * Initializes the string with length 0.
   */
  public BaseColor() {
    this(DEFAULT);
  }

  /**
   * Initializes the object with the hex string to parse.
   *
   * @param s		the hex string to parse
   */
  public BaseColor(String s) {
    super(s);
  }

  /**
   * Initializes the object with the color.
   *
   * @param color	the color to initialize with
   */
  public BaseColor(Color color) {
    super(ColorHelper.toHex(color));
  }

  /**
   * Initializes the internal object.
   */
  @Override
  protected void initialize() {
    m_Internal = DEFAULT;
  }

  /**
   * Checks whether the string value is a valid presentation for this class.
   *
   * @param value	the string value to check
   * @return		true if valid
   */
  @Override
  public boolean isValid(String value) {
    return (ColorHelper.valueOf(value, null) != null);
  }

  /**
   * Sets the string value.
   *
   * @param value	the string value
   */
  @Override
  public void setValue(String value) {
    if (isValid(value))
      m_Internal = ColorHelper.toHex(ColorHelper.valueOf(value));
  }

  /**
   * Returns the current string value.
   *
   * @return		the string value
   */
  @Override
  public String getValue() {
    return (String) m_Internal;
  }
  
  /**
   * Returns a {@link Color} object.
   * 
   * @return		the color
   */
  public Color toColorValue() {
    return ColorHelper.valueOf((String) m_Internal);
  }

  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return		the tool tip
   */
  @Override
  public String getTipText() {
    return "A color definition.";
  }
}
