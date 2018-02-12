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
 * Copyright (C) 2017-2018 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

import java.awt.Rectangle;
import java.awt.geom.Point2D;

/**
 * Wrapper for a rectangle object to be editable in the GOE.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class BaseRectangle
  extends AbstractBaseString {

  /** for serialization. */
  private static final long serialVersionUID = -5853830144343397434L;

  /**
   * Initializes the string with "0 0 0 0".
   */
  public BaseRectangle() {
    this("0 0 1 1", false);
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public BaseRectangle(String s) {
    this(s, false);
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   * @param isXY	whether in format 'x0 y0 x1 y1' (true) or 'x y w h' (false)
   */
  public BaseRectangle(String s, boolean isXY) {
    super(isXY ? fromXY(s) : s);
  }

  /**
   * Initializes the object with the location to use.
   *
   * @param location	the location to use
   */
  public BaseRectangle(Rectangle location) {
    this(
      location.x, location.y,
      location.width, location.height, false);
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
    this(x, y, w, h, false);
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param x		the x of the top-left corner
   * @param y		the y of the top-left corner
   * @param w_or_x	the width or x of bottom-right corner
   * @param h_or_y	the height or y of bottom-right corner
   * @param isXY	whether in format 'x0 y0 x1 y1' (true) or 'x y w h' (false)
   */
  public BaseRectangle(int x, int y, int w_or_x, int h_or_y, boolean isXY) {
    this(
      x
	+ " "
	+ y
	+ " "
	+ (isXY ? w_or_x-x+1 : w_or_x)
	+ " "
	+ (isXY ? h_or_y-y+1 : h_or_y));
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param x		the x of the top-left corner
   * @param y		the y of the top-left corner
   * @param w		the width
   * @param h		the height
   */
  public BaseRectangle(double x, double y, double w, double h) {
    this(x, y, w, h, false);
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param x		the x of the top-left corner
   * @param y		the y of the top-left corner
   * @param w_or_x	the width or x of bottom-right corner
   * @param h_or_y	the height or y of bottom-right corner
   * @param isXY	whether in format 'x0 y0 x1 y1' (true) or 'x y w h' (false)
   */
  public BaseRectangle(double x, double y, double w_or_x, double h_or_y, boolean isXY) {
    this(
      x
	+ " "
	+ y
	+ " "
	+ (isXY ? w_or_x-x+1 : w_or_x)
	+ " "
	+ (isXY ? h_or_y-y+1 : h_or_y));
  }

  /**
   * Checks whether the string value is a valid presentation for this class.
   *
   * @param value	the string value to check
   * @return		true if non-null
   */
  @Override
  public boolean isValid(String value) {
    return isValidFormat(value);
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
   * Returns the rectangle in 'x0 y0 x1 y1' format.
   *
   * @return		the string value
   */
  public String xyValue() {
    String 	result;
    Rectangle 	rect;
    boolean	allInt;

    rect = rectangleValue();
    allInt = (rect.getX() == (int) rect.getX())
      && (rect.getY() == (int) rect.getY())
      && (rect.getWidth() == (int) rect.getWidth())
      && (rect.getHeight() == (int) rect.getHeight());
    if (allInt)
      result = ((int) rect.getX())
	+ " "
	+ ((int) rect.getY())
	+ " "
	+ ((int) (rect.getX() + rect.getWidth() - 1))
	+ " "
	+ ((int) (rect.getY() + rect.getHeight() - 1));
    else
      result = rect.getX()
	+ " "
	+ rect.getY()
	+ " "
	+ (rect.getX() + rect.getWidth() - 1)
	+ " "
	+ (rect.getY() + rect.getHeight() - 1);

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

  /**
   * Checks whether the string value is a valid presentation for this class.
   *
   * @param value	the string value to check
   * @return		true if non-null
   */
  public static boolean isValidFormat(String value) {
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
   * Parses the string in XY format and returns it in xywh format.
   * Assumes there are 4 numbers present.
   *
   * @param x0y0x1y1	the string to parse
   * @return		the converted string
   * @see		#isValidFormat(String)
   */
  public static String fromXY(String x0y0x1y1) {
    String[]  	parts;
    double[]	values;
    int		i;

    x0y0x1y1 = x0y0x1y1.replaceAll("  ", " ");
    parts = x0y0x1y1.split(" ");
    if (parts.length != 4)
      throw new IllegalArgumentException("Expected four numbers in string: " + x0y0x1y1);
    values = new double[4];
    for (i = 0; i < parts.length; i++)
      values[i] = Double.parseDouble(parts[i]);
    values[2] = values[2] - values[0] + 1;  // width
    values[3] = values[3] - values[1] + 1;  // height
    return values[0] + " " + values[1] + " " + values[2] + " " + values[3];
  }
}
