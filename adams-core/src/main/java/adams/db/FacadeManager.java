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
 * FacadeManager.java
 * Copyright (C) 2011-2012 University of Waikato, Hamilton, New Zealand
 */
package adams.db;

import adams.event.DatabaseConnectionChangeEvent;
import adams.event.DatabaseConnectionChangeEvent.EventType;
import adams.event.DatabaseConnectionChangeListener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Manages the database URL/facade relations.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 5952 $
 * @param <T> the type of facade this manager is for
 */
public class FacadeManager<T extends AbstractTableFacade>
  implements Serializable {

  /** for serialization. */
  private static final long serialVersionUID = 7054134442727870486L;

  /** the facade this manager is for. */
  protected String m_FacadeName;

  /** for storing the facade objects. */
  protected HashMap<String,T> m_Facades;

  /** the database manager to use for default connection. */
  protected DatabaseManager m_DatabaseManager;

  /**
   * Initializes the manager.
   *
   * @param facadeName	the name of the facade this manager is for
   * @param manager	the database manager to obtain the default database
   * 			connection from
   */
  public FacadeManager(String facadeName, DatabaseManager manager) {
    super();

    m_FacadeName       = facadeName;
    m_Facades          = new HashMap<>();
    m_DatabaseManager = manager;
  }

  /**
   * Returns the name of the facade this manager is handling.
   *
   * @return		the facade name
   */
  public String getFacadeName() {
    return m_FacadeName;
  }

  /**
   * Checks whether a database manager is available.
   *
   * @return		true if a database manager is available
   */
  public boolean hasDatabaseManager() {
    return (m_DatabaseManager != null);
  }

  /**
   * Returns the database manager in use.
   *
   * @return		the database manager
   */
  public DatabaseManager getDatabaseManager() {
    return m_DatabaseManager;
  }

  /**
   * Generates a URL that includes the user name.
   *
   * @param dbcon	the database connection
   * @return		the complete URL
   */
  protected String createURL(AbstractDatabaseConnection dbcon) {
    return dbcon.getUser() + ":" + dbcon.getPassword() + "@" + dbcon.getURL();
  }

  /**
   * Checks whether a facade object for the specified database connection is
   * available.
   *
   * @param dbcon	the connection to check
   * @return		true if a facade object is available
   */
  public boolean has(AbstractDatabaseConnection dbcon) {
    if (dbcon == null)
      dbcon = getDatabaseManager().getDefault();
    return m_Facades.containsKey(createURL(dbcon));
  }

  /**
   * Gets the facade object for the specified database connection.
   *
   * @param dbcon	the connection to get the facade for
   * @return		the facade object if available, otherwise null
   */
  public T get(AbstractDatabaseConnection dbcon) {
    if (dbcon == null)
      dbcon = getDatabaseManager().getDefault();
    return m_Facades.get(createURL(dbcon));
  }

  /**
   * Adds the facade object for the specified database connection.
   *
   * @param dbcon	the connection to add the facade for
   * @param facade	the facade object to add
   * @return		the previous facade, null if no previous one stored
   */
  public T add(AbstractDatabaseConnection dbcon, T facade) {
    T	result;

    if (dbcon == null) {
      if (hasDatabaseManager())
	dbcon = getDatabaseManager().getDefault();
      else
	return null;
    }

    result = m_Facades.put(createURL(dbcon), facade);

    dbcon.addChangeListener(new DatabaseConnectionChangeListener() {
      public void databaseConnectionStateChanged(DatabaseConnectionChangeEvent e) {
	if (e.getType() == EventType.DISCONNECT) {
	  e.getDatabaseConnection().removeChangeListener(this);
	  m_Facades.remove(createURL(e.getDatabaseConnection()));
	}
      }
    });

    return result;
  }

  /**
   * Returns an iterator over all facades.
   *
   * @return		the iterator
   */
  public Iterator<T> iterator() {
    return m_Facades.values().iterator();
  }

  /**
   * Returns a short string representation of the manager.
   *
   * @return		the string representation
   */
  public String toString() {
    return getFacadeName() + ": " + m_Facades.keySet();
  }
}
