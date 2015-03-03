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
 * AbstractReconnectableDatabaseConnection.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.db;

import java.util.logging.Level;

import adams.core.base.BasePassword;

/**
 * Ancestor for database connection classes that offer reconnecting to
 * another URL.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractReconnectableDatabaseConnection
  extends AbstractDatabaseConnection
  implements ReconnectableDatabaseConnection {

  /** for serialization. */
  private static final long serialVersionUID = -5575792002262680914L;

  /**
   * Constructor, uses the default settings.
   */
  protected AbstractReconnectableDatabaseConnection() {
    super();
  }

  /**
   * Reconnects with the given parameters if they differ from the current
   * settings.
   *
   * @param url		the JDBC URL
   * @param user	the database user
   * @param password	the database password
   * @return		true if the reconnect was successful (or not necessary)
   */
  @Override
  public synchronized boolean reconnect(String url, String user, BasePassword password) {
    boolean	result;

    result = true;

    // do we have to reconnect?
    if (    !url.equals(getURL())
	 || !user.equals(getUser())
	 || !password.equals(getPassword())) {

      if (isConnected())
	disconnect();

      setURL(url);
      setUser(user);
      setPassword(password);

      try {
	result = connect();
      }
      catch (Exception e) {
	result = false;
	getLogger().log(Level.SEVERE, "Failed to connect", e);
      }
    }
    else {
      if (!isConnected()) {
	try {
	  result = connect();
	}
	catch (Exception e) {
	  result = false;
	  getLogger().log(Level.SEVERE, "Failed to connect (2nd attempt)", e);
	}
      }
    }

    return result;
  }
}
