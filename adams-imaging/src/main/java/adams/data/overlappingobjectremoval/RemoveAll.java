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
 * RemoveAll.java
 * Copyright (C) 2020-2022 University of Waikato, Hamilton, NZ
 */

package adams.data.overlappingobjectremoval;

import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;

import java.util.Map;
import java.util.Set;

/**
 * Removes all objects that have overlaps.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RemoveAll
  extends AbstractOverlappingObjectRemoval {

  private static final long serialVersionUID = -895136411948961806L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Removes all objects that have overlaps.";
  }

  /**
   * Removes overlapping image objects.
   *
   * @param objects	the objects to clean up
   * @param matches	the matches that were determined by an algorithm, used as basis for removal
   * @return		the updated objects
   */
  @Override
  public LocatedObjects removeOverlaps(LocatedObjects objects, Map<LocatedObject, Map<LocatedObject,Double>> matches) {
    LocatedObjects	result;
    Set<LocatedObject> 	others;

    result = new LocatedObjects();
    for (LocatedObject thisObj : objects) {
      if (!matches.containsKey(thisObj)) {
        result.add(thisObj.getClone());
        continue;
      }
      others = matches.get(thisObj).keySet();
      if (others.size() == 0)
        result.add(thisObj.getClone());
    }

    return result;
  }
}
