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
 * RgbInColorProvider.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.core;

import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;
import adams.gui.core.ColorHelper;

import java.awt.Color;

/**
 <!-- globalinfo-start -->
 * Looks for an RGB color definition in the name, eg #ff0000. If none found or no name provided to search, the default color is returned.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-default-color &lt;java.awt.Color&gt; (property: defaultColor)
 * &nbsp;&nbsp;&nbsp;The default color to use if no color located or no name provided to match 
 * &nbsp;&nbsp;&nbsp;against.
 * &nbsp;&nbsp;&nbsp;default: #000000
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RgbInColorProvider
  extends AbstractOptionHandler
  implements ColorProviderWithNameSupport {

  private static final long serialVersionUID = 5693610200784035720L;

  /** the default color. */
  protected Color m_DefaultColor;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Looks for an RGB color definition in the name, eg #ff0000. If none found "
	+ "or no name provided to search, the default color is returned.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"default-color", "defaultColor",
	Color.BLACK);
  }

  /**
   * Sets the default color to use.
   *
   * @param value	the default color to use
   */
  public void setDefaultColor(Color value) {
    m_DefaultColor = value;
    reset();
    resetColors();
  }

  /**
   * Returns the default color in use.
   *
   * @return		the default color in use
   */
  public Color getDefaultColor() {
    return m_DefaultColor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String defaultColorTipText() {
    return "The default color to use if no color located or no name provided to match against.";
  }

  /**
   * Just returns the default color.
   *
   * @return		the default color
   */
  @Override
  public Color next() {
    return m_DefaultColor;
  }

  /**
   * Ignored.
   */
  @Override
  public void resetColors() {
  }

  /**
   * Ignored.
   */
  @Override
  public void recycle(Color c) {
  }

  /**
   * Ignored.
   */
  @Override
  public void exclude(Color c) {
  }

  /**
   * Returns the next color for this name.
   *
   * @param name	the name to use
   * @return		the next color
   */
  @Override
  public synchronized Color next(String name) {
    Color		result;
    int			i;
    StringBuilder	color;
    char		c;

    result = m_DefaultColor;

    if (name.contains("#")) {
      name  = name.toLowerCase();
      color = new StringBuilder();
      for (i = name.indexOf('#'); i < name.length(); i++) {
	c = name.charAt(i);
	if (c == '#')
	  color.append(c);
	else if ((c >= '0') && (c <= '9'))
	  color.append(c);
	else if ((c >= 'a') && (c <= 'f'))
	  color.append(c);
	if (color.length() == 7)
	  break;
      }
      result = ColorHelper.valueOf(color.toString(), m_DefaultColor);
    }

    return result;
  }

  /**
   * Returns a shallow copy of itself.
   *
   * @return		the shallow copy
   */
  @Override
  public ColorProvider shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  @Override
  public ColorProvider shallowCopy(boolean expand) {
    return (ColorProvider) OptionUtils.shallowCopy(this, expand);
  }
}
