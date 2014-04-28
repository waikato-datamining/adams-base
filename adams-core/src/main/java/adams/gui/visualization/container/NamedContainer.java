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
 * NamedContainer.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.container;

import adams.data.id.MutableIDHandler;

/**
 * Interface for containers that are have a unique ID string.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface NamedContainer
  extends MutableIDHandler {

  /**
   * Sets the container's ID.
   *
   * @param value	the new ID
   */
  public void setID(String value);

  /**
   * Returns the container's ID.
   *
   * @return		the ID
   */
  public String getID();

  /**
   * Returns the displayed container's ID.
   *
   * @return		the displayed ID
   */
  public String getDisplayID();
}