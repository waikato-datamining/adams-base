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
 * Point2DToString.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.data.conversion;

import java.awt.geom.Point2D;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Point2DToString
  extends AbstractConversionToString {

  private static final long serialVersionUID = -7485249047688540453L;

  /** whether to round to integers. */
  protected boolean m_UseIntegers;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns a " + Point2D.class.getName() + " into a string of the format 'x y'.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "use-integers", "useIntegers",
	    false);
  }

  /**
   * Sets whether to use integers in the output or doubles.
   *
   * @param value	true if to use integers
   */
  public void setUseIntegers(boolean value) {
    m_UseIntegers = value;
    reset();
  }

  /**
   * Returns whether to use integers or doubles in the output.
   *
   * @return 		true if to use integers
   */
  public boolean getUseIntegers() {
    return m_UseIntegers;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useIntegersTipText() {
    return "If enabled, rounded integers are output instead of doubles.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return Point2D.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    String	result;
    Point2D	point;

    point  = (Point2D) m_Input;
    if (m_UseIntegers)
      result = (int) Math.round(point.getX()) + " " + (int) Math.round(point.getY());
    else
      result = point.getX() + " " + point.getY();

    return result;
  }
}
