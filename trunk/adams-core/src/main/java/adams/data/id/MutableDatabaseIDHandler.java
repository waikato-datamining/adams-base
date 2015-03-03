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
 * MutableDatabaseIDHandler.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */
package adams.data.id;


/**
 * Interface for classes that allow the database ID to be set as well.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface MutableDatabaseIDHandler
  extends DatabaseIDHandler {

  /**
   * Sets the database ID.
   *
   * @param value	the database ID
   */
  public void setDatabaseID(int value);
}
