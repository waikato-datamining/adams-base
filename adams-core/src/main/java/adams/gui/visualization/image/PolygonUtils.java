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

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
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
}
