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
 * DatabaseConnectionProvider.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.standalone;

import adams.flow.core.Actor;

/**
 * Interface for database connection providers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface AbstractDatabaseConnectionProvider
  extends Actor {

  /**
   * Returns the database connection in use. Reconnects the database, to make
   * sure that the database connection is the correct one.
   *
   * @return		the connection object
   */
  public adams.db.AbstractDatabaseConnection getConnection();
}
