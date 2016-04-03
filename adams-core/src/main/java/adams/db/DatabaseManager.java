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
 * DatabaseManager.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.db;

import adams.core.base.BasePassword;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Manages the database URL/connection object relations.
 *
 * @author  fracpete (fracpete at waikato dot ac dot 
 * @version $Revision$
 * @param <T> the type of database this manager is for
 */
public class DatabaseManager<T extends AbstractDatabaseConnection>
  implements Serializable {

  /** for serialization. */
  private static final long serialVersionUID = -8832349882994980783L;

  /** the database this manager is for. */
  protected String m_DatabaseName;

  /** for storing the database connection objects. */
  protected HashMap<String,T> m_Connections;

  /** the default database connection. */
  protected T m_DefaultDatabaseConnection;

  /** for storing all database connection objects. */
  protected static HashMap<String,AbstractDatabaseConnection> m_AllConnections;
  static {
    m_AllConnections = new HashMap<>();
  }

  /**
   * Initializes the manager.
   *
   * @param dbName	the name of the database this manager is for
   */
  public DatabaseManager(String dbName) {
    super();

    m_DatabaseName = dbName;
    m_Connections  = new HashMap<String,T>();
  }

  /**
   * Returns the name of the database this manager is handling.
   *
   * @return		the database name
   */
  public String getDatabaseName() {
    return m_DatabaseName;
  }

  /**
   * Generates a URL that includes the user name.
   *
   * @param dbcon	the database connection object to create the URL for
   * @return		the complete URL
   */
  public String createURL(T dbcon) {
    return createURL(dbcon.getURL(), dbcon.getUser(), dbcon.getPassword());
  }

  /**
   * Generates a URL that includes the user name.
   *
   * @param url		the JDBC URL
   * @param user	the database user
   * @param password	the database password
   * @return		the complete URL
   */
  public String createURL(String url, String user, BasePassword password) {
    return user + ":" + password + "@" + url;
  }

  /**
   * Checks whether a database object for the specified URL is
   * available.
   *
   * @param url		the URL to check
   * @param user	the database user
   * @param password	the database password
   * @return		true if a database object is available
   */
  public boolean has(String url, String user, BasePassword password) {
    if (url == null)
      return false;
    else
      return m_Connections.containsKey(createURL(url, user, password));
  }

  /**
   * Gets the database object for the specified URL.
   *
   * @param url		the URL to get the database for
   * @param user	the database user
   * @param password	the database password
   * @return		the database object if available, otherwise null
   */
  public T get(String url, String user, BasePassword password) {
    if (url == null)
      return null;
    else
      return m_Connections.get(createURL(url, user, password));
  }

  /**
   * Adds the database object (uses the current URL as key).
   *
   * @param dbcon	the database object to add
   * @return		the previous database, null if no previous one stored
   */
  public T add(T dbcon) {
    String	url;

    if (dbcon == null)
      return null;

    dbcon.setOwner(this);

    url = createURL(dbcon);
    m_AllConnections.put(url, dbcon);

    return m_Connections.put(url, dbcon);
  }

  /**
   * Sets the default database connection.
   *
   * @param value	the default connection object
   */
  public void setDefault(T value) {
    m_DefaultDatabaseConnection = value;
    if (value != null) {
      m_DefaultDatabaseConnection.setOwner(this);
      add(value);
    }
  }

  /**
   * Returns the default database connection.
   *
   * @return		the default
   */
  public T getDefault() {
    return m_DefaultDatabaseConnection;
  }

  /**
   * Returns an iterator over all databases.
   *
   * @return		the iterator
   */
  public Iterator<T> iterator() {
    return m_Connections.values().iterator();
  }

  /**
   * Returns a short string representation of the manager.
   *
   * @return		the string representation
   */
  @Override
  public String toString() {
    return getDatabaseName() + ": " + m_Connections.keySet();
  }

  /**
   * Returns the connection objects.
   *
   * @return		the connection objects
   */
  public static List<AbstractDatabaseConnection> getConnectionObjects() {
    return new ArrayList<>(m_AllConnections.values());
  }

  /**
   * Returns active (ie currently connected) connection objects.
   *
   * @return		the connection objects
   */
  public static List<AbstractDatabaseConnection> getActiveConnectionObjects() {
    Collection<AbstractDatabaseConnection>	conns;
    List<AbstractDatabaseConnection> 		result;

    result = new ArrayList<>();
    conns  = m_AllConnections.values();
    for (AbstractDatabaseConnection dbcon: conns) {
      if (dbcon.isConnected())
	result.add(dbcon);
    }

    return result;
  }
}
