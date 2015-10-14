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
 * AbstractDataContainerDbWriter.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.Constants;
import adams.data.container.DataContainer;
import adams.data.id.DatabaseIDHandler;
import adams.db.DataProvider;
import adams.flow.core.Actor;

/**
 * Interface for actors that import data containers into the database.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of data to write to the database
 */
public interface DataContainerDbWriter<T extends DataContainer & DatabaseIDHandler>
  extends Actor {

  /**
   * Returns the data provider to use for storing the container in the database.
   *
   * @param cont	the current container
   * @return		the data provider
   */
  public DataProvider<T> getDataProvider(T cont);

  /**
   * Returns whether the container already exists in the database.
   *
   * @param provider	the provider to use for checking
   * @param cont	the container to look for
   * @return		true if already stored in database
   */
  public boolean exists(DataProvider provider, T cont);

  /**
   * Removes the container from the database.
   *
   * @param provider	the provider to use for removing
   * @param cont	the container to remove
   * @return		true if successfully removed
   */
  public boolean remove(DataProvider provider, T cont);

  /**
   * Adds the container to the database.
   *
   * @param provider	the provider to use
   * @param cont	the container to store
   * @return		the database ID, {@link Constants#NO_ID} if failed
   */
  public Integer add(DataProvider provider, T cont);

  /**
   * Loads the container from the database.
   *
   * @param provider	the provider to use
   * @param cont	the container to store
   * @return		the container, null if failed to load
   */
  public T load(DataProvider provider, T cont);
}
