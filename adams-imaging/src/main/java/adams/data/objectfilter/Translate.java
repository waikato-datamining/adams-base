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
 * Translate.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.data.objectfilter;

import adams.core.QuickInfoHelper;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;

/**
 <!-- globalinfo-start -->
 * Translates the objects by the specified X and Y.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-x &lt;int&gt; (property: X)
 * &nbsp;&nbsp;&nbsp;The translation on the X axis.
 * &nbsp;&nbsp;&nbsp;default: 0
 * </pre>
 *
 * <pre>-y &lt;int&gt; (property: Y)
 * &nbsp;&nbsp;&nbsp;The translation on the Y axis.
 * &nbsp;&nbsp;&nbsp;default: 0
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Translate
  extends AbstractObjectFilter {

  private static final long serialVersionUID = -2181381799680316619L;

  /** the scale factor for x/width. */
  protected int m_X;

  /** the scale factor for y/height. */
  protected int m_Y;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Translates the objects by the specified X and Y.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "x", "X",
      0);

    m_OptionManager.add(
      "y", "Y",
      0);
  }

  /**
   * Sets the translation on the X axis.
   *
   * @param value	the translation
   */
  public void setX(int value) {
    if (getOptionManager().isValid("X", value)) {
      m_X = value;
      reset();
    }
  }

  /**
   * Returns the translation on the X axis.
   *
   * @return		the translation
   */
  public int getX() {
    return m_X;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String XTipText() {
    return "The translation on the X axis.";
  }

  /**
   * Sets the translation on the Y axis.
   *
   * @param value	the translation
   */
  public void setY(int value) {
    if (getOptionManager().isValid("scaleY", value)) {
      m_Y = value;
      reset();
    }
  }

  /**
   * Returns the translation on the Y axis.
   *
   * @return		the translation
   */
  public int getY() {
    return m_Y;
  }

  /**
   * Returns the tip teyt for this property.
   *
   * @return 		tip teyt for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String YTipText() {
    return "The translation on the Y axis.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "X", m_X, "x: ");
    result += QuickInfoHelper.toString(this, "Y", m_Y, ", y: ");

    return result;
  }

  /**
   * Filters the image objects.
   *
   * @param objects	the located objects
   * @return		the updated list of objects
   */
  @Override
  protected LocatedObjects doFilter(LocatedObjects objects) {
    LocatedObjects	result;
    LocatedObject	newObj;

    result = new LocatedObjects();
    for (LocatedObject obj: objects) {
      newObj = new LocatedObject(
        obj.getImage(),
	obj.getX() + m_X,
	obj.getY() + m_Y,
	obj.getWidth(),
	obj.getHeight(),
	obj.getMetaData());
      result.add(newObj);
    }

    return result;
  }
}
