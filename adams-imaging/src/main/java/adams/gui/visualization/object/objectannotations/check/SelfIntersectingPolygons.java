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

package adams.gui.visualization.object.objectannotations.check;

import adams.core.MessageCollection;
import adams.flow.transformer.locateobjects.GeometryUtils;
import adams.flow.transformer.locateobjects.LocatedObjects;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

/**
 * Looks for self-intersecting polygons.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class SelfIntersectingPolygons
  extends AbstractAnnotationCheck {

  private static final long serialVersionUID = 8234740862404287907L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Looks for self-intersecting polygons";
  }

  /**
   * Checks the annotations.
   *
   * @param objects the annotations to check
   * @return null if checks passed, otherwise error message
   */
  @Override
  protected String doCheckAnnotations(LocatedObjects objects) {
    MessageCollection result;
    int[]		indices;
    int			i;

    result  = new MessageCollection();
    indices = findInvalidAnnotationsIndices(objects);
    for (i = 0; i < indices.length; i++)
      result.add("Object #" + (indices[i]+1) + " has a self-intersecting polygon: " + objects.get(indices[i]));

    if (result.isEmpty())
      return null;
    else
      return result.toString();
  }

  /**
   * Checks the annotations and returns the indices of the invalid ones.
   *
   * @param objects the annotations to check
   * @return the invalid indices, 0-length array if no invalid ones
   */
  @Override
  protected int[] doFindInvalidAnnotationsIndices(LocatedObjects objects) {
    TIntList	result;
    int		i;

    result = new TIntArrayList();
    for (i = 0; i < objects.size(); i++) {
      if (objects.get(i).hasPolygon() && GeometryUtils.selfIntersects(objects.get(i).getPolygon()))
	result.add(i);
    }

    return result.toArray();
  }
}
