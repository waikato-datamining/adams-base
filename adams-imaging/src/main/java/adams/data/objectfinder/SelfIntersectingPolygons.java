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
 * SelfIntersectingPolygons.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.data.objectfinder;

import adams.data.geometry.GeometryUtils;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

/**
 * Locates objects with self-intersecting polygons, i.e., invalid ones.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class SelfIntersectingPolygons
  extends AbstractObjectFinder {

  private static final long serialVersionUID = 374893945810599322L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Locates objects with self-intersecting polygons, i.e., invalid ones.";
  }

  /**
   * Performs the actual finding of the objects in the list.
   *
   * @param objects the list of objects to process
   * @return the indices
   */
  @Override
  protected int[] doFind(LocatedObjects objects) {
    TIntList	result;

    result = new TIntArrayList();

    for (LocatedObject obj: objects) {
      if (!obj.hasPolygon())
	continue;
      if (GeometryUtils.selfIntersects(obj.getPolygon()))
	result.add(obj.getIndex());
    }

    if (isLoggingEnabled())
      getLogger().info("# self intersecting objects: " + result.size());

    return result.toArray();
  }
}
