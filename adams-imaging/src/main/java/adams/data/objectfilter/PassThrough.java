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
 * PassThrough.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.data.objectfilter;

import adams.flow.transformer.locateobjects.LocatedObjects;

/**
 * Dummy, just returns the incoming object list.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PassThrough
  extends AbstractObjectFilter {

  private static final long serialVersionUID = -2181381799680316619L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Dummy, just returns the incoming object list.";
  }

  /**
   * Filters the image objects.
   *
   * @param objects	the located objects
   * @return		the updated list of objects
   */
  @Override
  protected LocatedObjects doFilter(LocatedObjects objects) {
    return objects;
  }
}
