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
 * DataProvider.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.db;

import adams.data.container.DataContainer;

/**
 * Interface for table classes that return the base data type used in a
 * project.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of data
 * @see AbstractIndexedTable
 */
public interface DataProvider<T extends DataContainer>
  extends DatabaseConnectionProvider {

  /**
   * Adds a data container to the database. Returns the created auto-id, and
   * sets it in the data container as well.
   *
   * @param cont  	the container to store in the database
   * @return  	new ID, or null if fail
   */
  public Integer add(T cont);

  /**
   * Checks whether the container exists in the database.
   *
   * @param id		the database ID of the data container
   * @return		true if the container exists
   */
  public boolean exists(int id);

  /**
   * Checks whether the container exists in the database.
   *
   * @param id		the ID of the data container
   * @return		true if the container exists
   */
  public boolean exists(String id);
  
  /**
   * Load a data container with given database ID.
   *
   * @param id		the database ID
   * @return 		the data container, or null if not found
   */
  public T load(int id);
  
  /**
   * Load a data container with given ID.
   *
   * @param id		the ID
   * @return 		the data container, or null if not found
   */
  public T load(String id);

  /**
   * Removes the data container from the database (and the associated report,
   * if any).
   *
   * @param id		the database ID of the container to remove from the database
   * @return		true if no error
   */
  public boolean remove(int id);

  /**
   * Removes the data container from the database (and the associated report,
   * if any).
   *
   * @param id		the ID of the container to remove from the database
   * @return		true if no error
   */
  public boolean remove(String id);
}
