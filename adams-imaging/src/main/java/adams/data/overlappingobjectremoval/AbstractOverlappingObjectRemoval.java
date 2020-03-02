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
 * AbstractOverlappingObjectRemoval.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.data.overlappingobjectremoval;

import adams.core.option.AbstractOptionHandler;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;

import java.util.Map;
import java.util.Set;

/**
 * Ancestor for schemes that remove overlapping images objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractOverlappingObjectRemoval
  extends AbstractOptionHandler
  implements OverlappingObjectRemoval {

  private static final long serialVersionUID = 6991707947586230873L;

  /**
   * Removes overlapping image objects.
   *
   * @param objects	the objects to clean up
   * @param matches	the matches that were determined by an algorithm, used as basis for removal
   * @return		the updated objects
   */
  @Override
  public abstract LocatedObjects removeOverlaps(LocatedObjects objects, Map<LocatedObject, Set<LocatedObject>> matches);
}
