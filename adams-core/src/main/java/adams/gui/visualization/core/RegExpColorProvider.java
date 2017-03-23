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
 * RegExpColorProvider.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.core;

import adams.core.base.BaseKeyValuePair;
import adams.core.base.BaseRegExp;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;
import adams.gui.core.ColorHelper;

import java.awt.Color;

/**
 <!-- globalinfo-start -->
 * Uses the provided regular expressions to determine the colors. If no regular expression matches or no name given to match against, the default color is returned.<br>
 * The regular expressions and colors are provided as pairs, with the regexp being the key and the color the value.
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
 * &nbsp;&nbsp;&nbsp;The default color to use if no regular expression matches or no name provided 
 * &nbsp;&nbsp;&nbsp;to match against.
 * &nbsp;&nbsp;&nbsp;default: #000000
 * </pre>
 * 
 * <pre>-color &lt;adams.core.base.BaseKeyValuePair&gt; [-color ...] (property: colors)
 * &nbsp;&nbsp;&nbsp;The key-value pairs of regular expression ('key') and color ('value').
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RegExpColorProvider
  extends AbstractOptionHandler
  implements ColorProviderWithNameSupport {

  private static final long serialVersionUID = 5693610200784035720L;

  /** the default color. */
  protected Color m_DefaultColor;

  /** the regexp/color pairs. */
  protected BaseKeyValuePair[] m_Colors;

  /** the regular expressions. */
  protected BaseRegExp[] m_RegExps;

  /** the regular expressions. */
  protected Color[] m_ColorValues;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Uses the provided regular expressions to determine the colors. If no "
	+ "regular expression matches or no name given to match against, the "
	+ "default color is returned.\n"
	+ "The regular expressions and colors are provided as pairs, with the "
	+ "regexp being the key and the color the value.";
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

    m_OptionManager.add(
	"color", "colors",
	new BaseKeyValuePair[0]);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_RegExps     = null;
    m_ColorValues = null;
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
    return "The default color to use if no regular expression matches or no name provided to match against.";
  }

  /**
   * Sets the regexp/color key-value pairs.
   *
   * @param value	the pairs
   */
  public void setColors(BaseKeyValuePair[] value) {
    m_Colors = value;
    reset();
    resetColors();
  }

  /**
   * Returns the regexp/color key-value pairs.
   *
   * @return		the pairs
   */
  public BaseKeyValuePair[] getColors() {
    return m_Colors;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorsTipText() {
    return "The key-value pairs of regular expression ('key') and color ('value').";
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
    Color	result;
    int		i;

    result = m_DefaultColor;

    // initialize pairs
    if (m_RegExps == null) {
      m_RegExps     = new BaseRegExp[m_Colors.length];
      m_ColorValues = new Color[m_Colors.length];
      for (i = 0; i < m_Colors.length; i++) {
	m_RegExps[i]     = new BaseRegExp(m_Colors[i].getPairKey());
	m_ColorValues[i] = ColorHelper.valueOf(m_Colors[i].getPairValue());
      }
    }

    for (i = 0; i < m_RegExps.length; i++) {
      if (m_RegExps[i].isMatch(name)) {
	result = m_ColorValues[i];
	break;
      }
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
