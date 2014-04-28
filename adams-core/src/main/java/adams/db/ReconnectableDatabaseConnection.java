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
 * ReconnectableDatabaseConnection.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.db;

import adams.core.base.BasePassword;

/**
 * For database connection classes that offer reconnecting to another database.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface ReconnectableDatabaseConnection {

  /**
   * Reconnects with the given parameters if they differ from the current
   * settings.
   *
   * @param url		the JDBC URL
   * @param user	the database user
   * @param password	the database password
   * @return		true if the reconnect was successful (or not necessary)
   */
  public boolean reconnect(String url, String user, BasePassword password);
}
