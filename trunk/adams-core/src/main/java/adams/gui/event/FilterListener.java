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

/**
 * FilterListener.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.event;

import adams.data.container.DataContainer;

/**
 * Interface for classes that want to listen to events that trigger filtering.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of data the filter handles
 */
public interface FilterListener<T extends DataContainer> {

  /**
   * Gets called when the filtering should be executed.
   *
   * @param e		the event
   */
  public void filter(FilterEvent<T> e);
}
