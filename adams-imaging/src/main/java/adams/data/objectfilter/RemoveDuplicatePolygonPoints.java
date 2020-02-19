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
 * RemoveDuplicatePolygonPoints.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.data.objectfilter;

import adams.core.Utils;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Removes duplicate points from polygons, e.g., introduced during scaling.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RemoveDuplicatePolygonPoints
  extends AbstractObjectFilter {

  private static final long serialVersionUID = -7765505197662180885L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Removes duplicate points from polygons, e.g., introduced during scaling.";
  }

  /**
   * Filters the image objects.
   *
   * @param objects	the objects to filter
   * @return		the updated object list
   */
  @Override
  protected LocatedObjects doFilter(LocatedObjects objects) {
    LocatedObjects  	result;
    LocatedObject   	objNew;
    int[] 		oldX;
    int[] 		oldY;
    Point2D		point;
    Set<Point2D>	points;
    int			i;
    List<Integer>	newX;
    List<Integer> 	newY;

    result = new LocatedObjects();
    for (LocatedObject objOld: objects) {
      objNew = objOld.getClone();
      if (objNew.hasPolygon()) {
        oldX   = objOld.getPolygonX();
        oldY   = objOld.getPolygonY();
        newX   = new ArrayList<>();
        newY   = new ArrayList<>();
        points = new HashSet<>();
        for (i = 0; i < oldX.length; i++) {
          point = new Double(oldX[i], oldY[i]);
          if (!points.contains(point)) {
            newX.add(oldX[i]);
            newY.add(oldY[i]);
            points.add(point);
	  }
	}
	objNew.getMetaData().put(LocatedObject.KEY_POLY_X, Utils.flatten(newX.toArray(), ","));
	objNew.getMetaData().put(LocatedObject.KEY_POLY_Y, Utils.flatten(newY.toArray(), ","));
	if (isLoggingEnabled() && (oldX.length != newX.size()))
	  getLogger().info(objOld.getIndexString() + ": " + oldX.length + " -> " + newX.size());
      }
      result.add(objNew);
    }

    return result;
  }
}
