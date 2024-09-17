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
 * GeometryUtils.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.data.geometry;

import adams.data.statistics.StatUtils;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility functions for JTS related geometry operations.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class GeometryUtils {

  /**
   * Converts the polygon into a JTS polygon (for proper intersects).
   *
   * @param polygon 	the AWT polygon to convert
   * @return		the JTS polygon
   */
  public static org.locationtech.jts.geom.Polygon toGeometry(Polygon polygon) {
    return toGeometry(StatUtils.toNumberArray(polygon.xpoints), StatUtils.toNumberArray(polygon.ypoints));
  }

  /**
   * Converts the rectangle into a JTS polygon (for proper intersects).
   *
   * @param rectangle 	the rectangle to convert
   * @return		the JTS polygon
   */
  public static org.locationtech.jts.geom.Polygon toGeometry(Rectangle rectangle) {
    Double[]	x;
    Double[]	y;

    x = new Double[]{
      rectangle.getX(),
      rectangle.getX() + rectangle.getWidth() - 1,
      rectangle.getX() + rectangle.getWidth() - 1,
      rectangle.getX(),
      rectangle.getX(),
    };
    y = new Double[]{
      rectangle.getY(),
      rectangle.getY(),
      rectangle.getY() + rectangle.getHeight() - 1,
      rectangle.getY() + rectangle.getHeight() - 1,
      rectangle.getY(),
    };

    return toGeometry(x, y);
  }

  /**
   * Converts the polygon into a JTS polygon (for proper intersects).
   *
   * @param x 		the x coordinates
   * @param y 		the y coordinates
   * @return		the JTS polygon
   */
  public static org.locationtech.jts.geom.Polygon toGeometry(int[] x, int[] y) {
    return toGeometry(StatUtils.toNumberArray(x), StatUtils.toNumberArray(y));
  }

  /**
   * Converts the polygon into a JTS polygon (for proper intersects).
   *
   * @param x 		the x coordinates
   * @param y 		the y coordinates
   * @return		the JTS polygon
   */
  public static org.locationtech.jts.geom.Polygon toGeometry(double[] x, double[] y) {
    return toGeometry(StatUtils.toNumberArray(x), StatUtils.toNumberArray(y));
  }

  /**
   * Converts the polygon into a JTS polygon (for proper intersects).
   *
   * @param x 		the x coordinates
   * @param y 		the y coordinates
   * @return		the JTS polygon
   */
  public static org.locationtech.jts.geom.Polygon toGeometry(Number[] x, Number[] y) {
    org.locationtech.jts.geom.Polygon	result;
    LinearRing 				ring;
    List<Coordinate> 			coords;
    GeometryFactory 			factory;
    int					i;

    factory = new GeometryFactory();
    coords = new ArrayList<>();
    for (i = 0; i < x.length; i++)
      coords.add(new Coordinate(x[i].doubleValue(), y[i].doubleValue()));
    coords.add(new Coordinate(x[0].doubleValue(), y[0].doubleValue()));
    ring = new LinearRing(new CoordinateArraySequence(coords.toArray(new Coordinate[0])), factory);
    result = new org.locationtech.jts.geom.Polygon(ring, null, factory);

    return result;
  }

  /**
   * Returns the boundaries of the JTS polygon.
   *
   * @param polygon	the polygon to get the bounds for
   * @return		the bounds, empty rectangle if failed to compute
   */
  public static Rectangle polygonBounds(org.locationtech.jts.geom.Polygon polygon) {
    Rectangle				result;
    Geometry 				envelope;
    org.locationtech.jts.geom.Polygon	bbox;
    Coordinate[]			coords;
    Point 				point;

    result   = new Rectangle();
    envelope = polygon.getEnvelope();
    if (envelope instanceof org.locationtech.jts.geom.Polygon) {
      bbox = (org.locationtech.jts.geom.Polygon) polygon.getEnvelope();
      coords = bbox.getCoordinates();
      result = new Rectangle(
	  (int) coords[0].x,
	  (int) coords[0].y,
	  (int) (coords[2].x - coords[0].x + 1),
	  (int) (coords[2].y - coords[0].y + 1));
    }
    else if (envelope instanceof Point) {
      point = (Point) envelope;
      if (!point.isEmpty())
	result = new Rectangle((int) point.getX(), (int) point.getY(), 0, 0);
    }
    return result;
  }


  /**
   * Turns the JTS polygon into a AWT one.
   *
   * @param polygon	the JTS polygon to convert
   * @return		the AWT polygon
   */
  public static Polygon toPolygon(org.locationtech.jts.geom.Polygon polygon) {
    Coordinate[]	coords;
    int[]		polyX;
    int[]		polyY;
    int 		i;

    coords = polygon.getCoordinates();
    polyX = new int[coords.length];
    polyY = new int[coords.length];
    for (i = 0; i < coords.length; i++) {
      polyX[i] = (int) coords[i].x;
      polyY[i] = (int) coords[i].y;
    }

    return new Polygon(polyX, polyY, polyX.length);
  }

  /**
   * Scales the polygon using the supplied scale factors.
   *
   * @param polygon	the polygon to scale
   * @param scaleX	the scale factor for the X axis
   * @param scaleY	the scale factor for the Y axis
   * @return		the scaled polygon
   */
  public static org.locationtech.jts.geom.Polygon scale(org.locationtech.jts.geom.Polygon polygon, double scaleX, double scaleY) {
    org.locationtech.jts.geom.Polygon	result;
    Coordinate[]			coordsOld;
    Coordinate[]			coordsNew;
    LinearRing 				ring;
    GeometryFactory 			factory;
    int					i;

    factory   = new GeometryFactory();
    coordsOld = polygon.getCoordinates();
    coordsNew = new Coordinate[coordsOld.length];
    for (i = 0; i < coordsOld.length; i++)
      coordsNew[i] = new Coordinate(coordsOld[i].getX() * scaleX, coordsOld[i].getY() * scaleY);
    ring   = new LinearRing(new CoordinateArraySequence(coordsNew), factory);
    result = new org.locationtech.jts.geom.Polygon(ring, null, factory);

    return result;

  }
}
