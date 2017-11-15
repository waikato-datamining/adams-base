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
 * AbstractObjectFilter.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.data.objectfilter;

import adams.core.option.AbstractOptionHandler;
import adams.flow.transformer.locateobjects.LocatedObjects;

/**
 * Ancestor for image object filters.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractObjectFilter
  extends AbstractOptionHandler
  implements ObjectFilter {

  private static final long serialVersionUID = -5536282098947025494L;


  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return null;
  }

  /**
   * Hook method for checking the object list before processing it.
   *
   * @param objects	the object list to check
   * @return		null if successful, otherwise error message
   */
  protected String check(LocatedObjects objects) {
    if (objects == null)
      return "No objects provided!";
    return null;
  }

  /**
   * Filters the image objects.
   *
   * @param objects	the objects to filter
   * @return		the updated object list
   */
  protected abstract LocatedObjects doFilter(LocatedObjects objects);

  /**
   * Filters the image objects.
   *
   * @param objects	the objects to filter
   * @return		the updated object list
   */
  public LocatedObjects filter(LocatedObjects objects) {
    String		msg;

    msg = check(objects);
    if (msg != null)
      throw new IllegalStateException(msg);

    return doFilter(objects);
  }
}
