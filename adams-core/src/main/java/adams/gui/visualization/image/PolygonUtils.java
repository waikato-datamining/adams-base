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
 * PolygonUtils.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.image;

import adams.core.License;
import adams.core.annotation.MixedCopyright;
import adams.data.statistics.StatUtils;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for polygons.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class PolygonUtils {

  /**
   * Returns the bounding box for the polygon.
   *
   * @param points	the polygon points to calculate the bounding box for
   * @return		the polygon
   */
  public static Polygon toPolygon(List<Point> points) {
    TIntList	x;
    TIntList	y;

    x = new TIntArrayList();
    y = new TIntArrayList();
    for (Point p: points) {
      x.add(p.x);
      y.add(p.y);
    }

    return new Polygon(x.toArray(), y.toArray(), x.size());
  }

  /**
   * Turns the rectangle into a polygon.
   *
   * @param rect 	the polygon to convert
   * @return		the polygon
   */
  public static Polygon toPolygon(Rectangle rect) {
    int[]	x;
    int[]	y;

    x = new int[]{rect.x, rect.x + rect.width - 1, rect.x + rect.width - 1,  rect.x};
    y = new int[]{rect.y, rect.y,                  rect.y + rect.height - 1, rect.y + rect.height - 1};

    return new Polygon(x, y, x.length);
  }

  /**
   * Turns the polygon into a list of points.
   *
   * @param poly	the polygon
   * @return		the list of points
   */
  public static List<Point> toPoints(Polygon poly) {
    List<Point>	result;
    int		i;

    result = new ArrayList<>();

    for (i = 0; i < poly.npoints; i++)
      result.add(new Point(poly.xpoints[i], poly.ypoints[i]));

    return result;
  }

  /**
   * Returns the bounding box for the polygon.
   *
   * @param points	the polygon points to calculate the bounding box for
   * @return		the bbox
   */
  public static Rectangle boundingBox(List<Point> points) {
    int		minx;
    int		maxx;
    int		miny;
    int		maxy;

    minx = Integer.MAX_VALUE;
    maxx = 0;
    miny = Integer.MAX_VALUE;
    maxy = 0;

    for (Point p: points) {
      minx = Math.min(minx, p.x);
      maxx = Math.max(maxx, p.y);
      miny = Math.min(miny, p.x);
      maxy = Math.max(maxy, p.y);
    }

    return new Rectangle(minx, miny, maxx - minx + 1, maxy - miny + 1);
  }

  /**
   * Returns the minimum distance between a line segment AB and a point E.
   *
   * @param A	the start of the line segment
   * @param B	the end of the line segment
   * @param E	the point to calculate the min distance to
   * @return	the minimum distance
   */
  @MixedCopyright(
    license = License.CC_BY_SA_3,
    author = "rrlinus",
    url = "https://www.geeksforgeeks.org/minimum-distance-from-a-point-to-the-line-segment-using-vectors/"
  )
  public static double minDistance(Point A, Point B, Point E)
  {
    // vector AB
    Point AB = new Point();
    AB.x = B.x - A.x;
    AB.y = B.y - A.y;

    // vector BP
    Point BE = new Point();
    BE.x = E.x - B.x;
    BE.y = E.y - B.y;

    // vector AP
    Point AE = new Point();
    AE.x = E.x - A.x;
    AE.y = E.y - A.y;

    // Variables to store dot product
    double AB_BE, AB_AE;

    // Calculating the dot product
    AB_BE = (AB.x * BE.x + AB.y * BE.y);
    AB_AE = (AB.x * AE.x + AB.y * AE.y);

    // Minimum distance from
    // point E to the line segment
    double reqAns = 0;

    // Case 1
    if (AB_BE > 0)
    {
      // Finding the magnitude
      double y = E.y - B.y;
      double x = E.x - B.x;
      reqAns = Math.sqrt(x * x + y * y);
    }
    // Case 2
    else if (AB_AE < 0) {
      double y = E.y - A.y;
      double x = E.x - A.x;
      reqAns = Math.sqrt(x * x + y * y);
    }
    // Case 3
    else {
      // Finding the perpendicular distance
      double x1 = AB.x;
      double y1 = AB.y;
      double x2 = AE.x;
      double y2 = AE.y;
      double mod = Math.sqrt(x1 * x1 + y1 * y1);
      reqAns = Math.abs(x1 * y2 - y1 * x2) / mod;
    }

    return reqAns;
  }

  /**
   * Adds the vertex to the polygon at the appropriate position.
   *
   * @param poly	the polygon to add to
   * @param vertex 	the vertex to insert
   * @return		the new polygon
   */
  public static Polygon addVertext(Polygon poly, Point vertex) {
    Polygon		result;
    List<Point>		points;
    Point		A;
    Point		B;
    int			i;
    double[]		dist;
    TIntList		x;
    TIntList		y;

    // calculate min distance to all line segments
    points = toPoints(poly);
    dist   = new double[points.size()];
    for (i = 0; i < points.size(); i++) {
      if (i == points.size() - 1) {
        A = points.get(i);
        B = points.get(0);
      }
      else {
        A = points.get(i);
        B = points.get(i + 1);
      }
      dist[i] = minDistance(A, B, vertex);
    }

    // insert between points of line segment with smallest distance
    i = StatUtils.minIndex(dist);
    x = new TIntArrayList(poly.xpoints);
    y = new TIntArrayList(poly.ypoints);
    if (i == points.size() - 1) {
      x.add(vertex.x);
      y.add(vertex.y);
    }
    else {
      x.insert(i + 1, vertex.x);
      y.insert(i + 1, vertex.y);
    }

    // create new polygon
    result = new Polygon(x.toArray(), y.toArray(), x.size());
    return result;
  }
}
