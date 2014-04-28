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
 * DataContainer.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.data.container;

import java.io.Serializable;

import adams.core.CloneHandler;

/**
 * Generic Interface for data points.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface DataPoint
  extends Serializable, Comparable, CloneHandler {

  /**
   * Sets the container this point belongs to.
   *
   * @param value	the container
   */
  public void setParent(DataContainer value);

  /**
   * Returns the container this point belongs to.
   *
   * @return		the container, can be null
   */
  public DataContainer getParent();

  /**
   * Returns whether the point belongs to a container.
   *
   * @return		true if the point belongs to a container
   */
  public boolean hasParent();

  /**
   * Returns a clone of itself.
   *
   * @return		the clone
   */
  public Object getClone();

  /**
   * Obtains the stored variables from the other data point.
   *
   * @param other	the data point to get the values from
   */
  public void assign(DataPoint other);
}
