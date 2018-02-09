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
 * StringToRectangle.java
 * Copyright (C) 2017-2018 University of Waikato, Hamilton, NZ
 */

package adams.data.conversion;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseRectangle;

/**
 <!-- globalinfo-start -->
 * Converts a string into a adams.core.base.BaseRectangle object, using 'x y w h' or 'x0 y0 x1 y1'.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-use-xy &lt;boolean&gt; (property: useXY)
 * &nbsp;&nbsp;&nbsp;If enabled, then the format 'x0 y0 x1 y1' is used instead of 'x y w h'.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class StringToRectangle
  extends AbstractConversionFromString {

  private static final long serialVersionUID = -5567881103146881202L;

  /** whether to use a second XY instead of width and height. */
  protected boolean m_UseXY;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Converts a string into a " + BaseRectangle.class.getName() + " object, using 'x y w h' or 'x0 y0 x1 y1'.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "use-xy", "useXY",
      false);
  }

  /**
   * Sets whether the format 'x0 y0 x1 y1' is used instead of 'x y w h'.
   *
   * @param value	true if XY based
   */
  public void setUseXY(boolean value) {
    m_UseXY = value;
    reset();
  }

  /**
   * Returns whether the format 'x0 y0 x1 y1' is used instead of 'x y w h'.
   *
   * @return		true if XY based
   */
  public boolean getUseXY() {
    return m_UseXY;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useXYTipText() {
    return "If enabled, then the format 'x0 y0 x1 y1' is used instead of 'x y w h'.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "useXY", (m_UseXY ? "x0 y0 x1 y1" : "x y w h"), "format: ");
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return BaseRectangle.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    return new BaseRectangle((String) m_Input, m_UseXY);
  }
}
