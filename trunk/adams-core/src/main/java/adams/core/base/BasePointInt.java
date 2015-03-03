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
 * BasePointInt.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

import java.awt.Point;

import adams.core.Utils;

/**
 * Wrapper for a {@link Point} object (integers) to be editable in the GOE.
 * Format: x;y
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 10214 $
 */
public class BasePointInt
  extends AbstractBaseString {

  /** for serialization. */
  private static final long serialVersionUID = -3504062141216626521L;

  /** the separator. */
  public final static String SEPARATOR = ";";
  
  /** the default value. */
  public final static String DEFAULT = "0" + SEPARATOR + "0";
  
  /**
   * Initializes the string with length 0.
   */
  public BasePointInt() {
    this(DEFAULT);
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public BasePointInt(String s) {
    super(s);
  }

  /**
   * Initializes the object with the given value.
   *
   * @param value	the value to use
   */
  public BasePointInt(Point value) {
    this(toString(value));
  }

  /**
   * Initializes the object with the given value.
   *
   * @param x		the X value to use
   * @param y		the Y value to use
   */
  public BasePointInt(int x, int y) {
    this(new Point(x, y));
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
   * @return		true if parseable boolean
   */
  @Override
  public boolean isValid(String value) {
    if (value == null)
      return false;
    return (parse(value) != null);
  }

  /**
   * Returns the {@link Point} value.
   *
   * @return		the point value
   */
  public Point pointValue() {
    return parse((String) m_Internal);
  }

  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return		the tool tip
   */
  @Override
  public String getTipText() {
    return "An integer point (x;y).";
  }
  
  /**
   * Turns a {@link Point} object into a string.
   * 
   * @param d		the object to convert
   * @return		the generated string
   */
  public static String toString(Point d) {
    return d.x + SEPARATOR + d.y;
  }
  
  /**
   * Parses the string ("x;y").
   * 
   * @param s		the string to parse
   * @return		the generated {@link Point} object, null if failed to parse
   */
  public Point parse(String s) {
    Point	result;
    String[]	parts;
    
    result = null;
    
    if (s.indexOf(SEPARATOR) > 0) {
      parts = s.split(SEPARATOR);
      if (parts.length == 2) {
	if (Utils.isInteger(parts[0]) && Utils.isInteger(parts[1]))
	  result = new Point(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
      }
    }
    
    return result;
  }
}
