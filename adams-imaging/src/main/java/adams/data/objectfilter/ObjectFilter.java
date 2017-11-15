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

import adams.core.QuickInfoSupporter;
import adams.core.option.OptionHandler;
import adams.flow.transformer.locateobjects.LocatedObjects;

/**
 * Interface for image object filters.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface ObjectFilter
  extends OptionHandler, QuickInfoSupporter {

  /**
   * Filters the image objects.
   *
   * @param objects	the objects to filter
   * @return		the updated object list
   */
  public LocatedObjects filter(LocatedObjects objects);
}
