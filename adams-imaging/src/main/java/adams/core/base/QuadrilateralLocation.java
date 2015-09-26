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
 * QuadrilateralLocation.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

import adams.data.statistics.StatUtils;
import georegression.struct.shapes.Quadrilateral_F64;

import java.awt.Rectangle;
import java.awt.geom.Point2D;

/**
 * Wrapper for a quadrilateral location object to be editable in the GOE.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class QuadrilateralLocation
  extends AbstractBaseString {

  /** for serialization. */
  private static final long serialVersionUID = -5853830144343397434L;

  /**
   * Initializes the string with length 0.
   */
  public QuadrilateralLocation() {
    this("0 0 0 0 0 0 0 0");
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public QuadrilateralLocation(String s) {
    super(s);
  }

  /**
   * Initializes the object with the location to use.
   *
   * @param location	the location to use
   */
  public QuadrilateralLocation(Quadrilateral_F64 location) {
    this(
      location.a.x, location.a.y,
      location.b.x, location.b.y,
      location.c.x, location.c.y,
      location.d.x, location.d.y);
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param x0		the x of the first corner
   * @param y0		the y of the first corner
   * @param x1		the x of the second corner
   * @param y1		the y of the second corner
   * @param x2		the x of the third corner
   * @param y2		the y of the third corner
   * @param x3		the x of the fourth corner
   * @param y3		the y of the fourth corner
   */
  public QuadrilateralLocation(double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3) {
    this(x0 + " " + y0 + " " + x1 + " " + y1 + " " + x2 + " " + y2 + " " + x3 + " " + y3);
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
    if (parts.length != 8)
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
   * Returns the location as array (x0, y0, ..., x3, y3).
   *
   * @return		the location array
   */
  public double[] doubleValue() {
    double[]	result;
    String[]	parts;
    int		i;

    parts = getValue().split(" ");
    result = new double[8];
    if (parts.length != 8)
      return result;

    for (i = 0; i < parts.length; i++)
      result[i] = Double.parseDouble(parts[i]);

    return result;
  }

  /**
   * Returns the quadrilateral location.
   *
   * @return		the quadrilateral
   */
  public Quadrilateral_F64 quadrilateralValue() {
    Quadrilateral_F64	result;
    double[]		parts;

    parts = doubleValue();
    result = new Quadrilateral_F64(
      parts[0], parts[1],
      parts[2], parts[3],
      parts[4], parts[5],
      parts[6], parts[7]);

    return result;
  }

  /**
   * Returns the location as rectangle that the quadrilateral object fits in.
   *
   * @return		the rectangle
   */
  public Rectangle rectangleValue() {
    Rectangle 		result;
    double[]		parts;
    double[]		x;
    double[]		y;
    double		left;
    double		right;
    double		top;
    double		bottom;

    parts  = doubleValue();
    x      = new double[]{parts[0], parts[2], parts[4], parts[6]};
    y      = new double[]{parts[1], parts[3], parts[5], parts[7]};
    left   = StatUtils.min(x);
    right  = StatUtils.max(x);
    top    = StatUtils.min(y);
    bottom = StatUtils.max(y);
    result = new Rectangle(
      (int) Math.round(left),
      (int) Math.round(top),
      (int) Math.round(right - left),
      (int) Math.round(bottom - top));

    return result;
  }

  /**
   * Returns the center of the quadrilateral.
   *
   * @return		the center
   */
  public Point2D centerValue() {
    Point2D		result;
    double[]		parts;
    double[]		x;
    double[]		y;
    double		left;
    double		right;
    double		top;
    double		bottom;

    parts  = doubleValue();
    x      = new double[]{parts[0], parts[2], parts[4], parts[6]};
    y      = new double[]{parts[1], parts[3], parts[5], parts[7]};
    left   = StatUtils.min(x);
    right  = StatUtils.max(x);
    top    = StatUtils.min(y);
    bottom = StatUtils.max(y);
    result = new Point2D.Double(
      left + (right - left) / 2,
      top + (bottom - top) / 2);

    return result;
  }

  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return		the tool tip
   */
  @Override
  public String getTipText() {
    return "Quadrilateral location string.";
  }
}
