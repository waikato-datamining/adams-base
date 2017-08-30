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
 * BaseRectangle.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

import java.awt.Rectangle;
import java.awt.geom.Point2D;

/**
 * Wrapper for a rectangle object to be editable in the GOE.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseRectangle
  extends AbstractBaseString {

  /** for serialization. */
  private static final long serialVersionUID = -5853830144343397434L;

  /**
   * Initializes the string with length 0.
   */
  public BaseRectangle() {
    this("0 0 0 0");
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public BaseRectangle(String s) {
    super(s);
  }

  /**
   * Initializes the object with the location to use.
   *
   * @param location	the location to use
   */
  public BaseRectangle(Rectangle location) {
    this(
      location.x, location.y,
      location.width, location.height);
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param x		the x of the top-left corner
   * @param y		the y of the top-left corner
   * @param w		the width
   * @param h		the height
   */
  public BaseRectangle(int x, int y, int w, int h) {
    this(x + " " + y + " " + w + " " + h);
  }

  /**
   * Checks whether the string value is a valid presentation for this class.
   *
   * @param value	the string value to check
   * @return		true if non-null
   */
  @Override
  public boolean isValid(String value) {
    String[]  parts;

    value = value.replaceAll("  ", " ");
    parts = value.split(" ");
    if (parts.length != 4)
      return false;

    try {
      for (String part: parts)
        Double.parseDouble(part);
    }
    catch (Exception e) {
      return false;
    }

    return true;
  }

  /**
   * Returns the location as array (x, y, w, h).
   *
   * @return		the location array
   */
  public double[] doubleValue() {
    double[]	result;
    String[]	parts;
    int		i;

    parts = getValue().split(" ");
    result = new double[4];
    if (parts.length != 4)
      return result;

    for (i = 0; i < parts.length; i++)
      result[i] = Double.parseDouble(parts[i]);

    return result;
  }

  /**
   * Returns the location as array (x, y, w, h).
   *
   * @return		the location array
   */
  public int[] intValue() {
    int[]	result;
    String[]	parts;
    int		i;

    parts = getValue().split(" ");
    result = new int[4];
    if (parts.length != 4)
      return result;

    for (i = 0; i < parts.length; i++)
      result[i] = (int) Double.parseDouble(parts[i]);

    return result;
  }

  /**
   * Returns the quadrilateral location.
   *
   * @return		the quadrilateral
   */
  public Rectangle rectangleValue() {
    Rectangle	result;
    int[]	parts;

    parts = intValue();
    result = new Rectangle(
      parts[0], parts[1],
      parts[2], parts[3]);

    return result;
  }

  /**
   * Returns the center of the quadrilateral.
   *
   * @return		the center
   */
  public Point2D centerValue() {
    Rectangle		rect;

    rect = rectangleValue();
    return new Point2D.Double(rect.getCenterX(), rect.getCenterY());
  }

  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return		the tool tip
   */
  @Override
  public String getTipText() {
    return "Rectangle location string.";
  }
}
