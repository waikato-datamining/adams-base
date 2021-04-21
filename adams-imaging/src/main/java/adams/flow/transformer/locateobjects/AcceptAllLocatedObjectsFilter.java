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
 * AcceptAllLocatedObjectsFilter.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.locateobjects;

import java.io.Serializable;

/**
 * Dummy filter that accepts all objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class AcceptAllLocatedObjectsFilter
  implements Serializable, LocatedObjectFilter {

  private static final long serialVersionUID = -4254766410476769680L;

  /**
   * Whether to accept the located object.
   *
   * @param obj 	the object to check
   * @return 		always true
   */
  @Override
  public boolean accept(LocatedObject obj) {
    return true;
  }
}
