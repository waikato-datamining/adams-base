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
 * RemovePolygons.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.data.objectfilter;

import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;

/**
 * Removes polygon information.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RemovePolygons
  extends AbstractObjectFilter {

  private static final long serialVersionUID = -7765505197662180885L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Removes polygon information.";
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

    result = new LocatedObjects();
    for (LocatedObject objOld: objects) {
      objNew = objOld.getClone();
      if (objNew.hasPolygon()) {
        objNew.getMetaData().remove(LocatedObject.KEY_POLY_X);
        objNew.getMetaData().remove(LocatedObject.KEY_POLY_Y);
      }
      result.add(objNew);
    }

    return result;
  }
}
