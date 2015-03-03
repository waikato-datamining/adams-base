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
 * TableManager.java
 * Copyright (C) 2011-2012 University of Waikato, Hamilton, New Zealand
 */
package adams.db;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Iterator;

import adams.event.DatabaseConnectionChangeEvent;
import adams.event.DatabaseConnectionChangeListener;
import adams.event.DatabaseConnectionChangeEvent.EventType;

/**
 * Manages the database URL/table relations.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of table this manager is for
 */
public class TableManager<T extends SQL>
  implements Serializable {

  /** for serialization. */
  private static final long serialVersionUID = 7054134442727870486L;

  /** the table this manager is for. */
  protected String m_TableName;

  /** for storing the table objects. */
  protected Hashtable<String,T> m_Tables;

  /** the database manager to use for default connection. */
  protected DatabaseManager m_DatabaseManager;

  /**
   * Initializes the manager.
   *
   * @param tableName	the name of the table this manager is for
   * @param manager	the database manager to obtain the default database
   * 			connection from
   */
  public TableManager(String tableName, DatabaseManager manager) {
    super();

    m_TableName       = tableName;
    m_Tables          = new Hashtable<String,T>();
    m_DatabaseManager = manager;
  }

  /**
   * Returns the name of the table this manager is handling.
   *
   * @return		the table name
   */
  public String getTableName() {
    return m_TableName;
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
   * Checks whether a table object for the specified database connection is
   * available.
   *
   * @param dbcon	the connection to check
   * @return		true if a table object is available
   */
  public boolean has(AbstractDatabaseConnection dbcon) {
    if (dbcon == null)
      dbcon = getDatabaseManager().getDefault();
    return m_Tables.containsKey(createURL(dbcon));
  }

  /**
   * Gets the table object for the specified database connection.
   *
   * @param dbcon	the connection to get the table for
   * @return		the table object if available, otherwise null
   */
  public T get(AbstractDatabaseConnection dbcon) {
    if (dbcon == null)
      dbcon = getDatabaseManager().getDefault();
    return m_Tables.get(createURL(dbcon));
  }

  /**
   * Adds the table object for the specified database connection.
   *
   * @param dbcon	the connection to add the table for
   * @param table	the table object to add
   * @return		the previous table, null if no previous one stored
   */
  public T add(AbstractDatabaseConnection dbcon, T table) {
    T	result;

    if (dbcon == null) {
      if (hasDatabaseManager())
	dbcon = getDatabaseManager().getDefault();
      else
	return null;
    }

    result = m_Tables.put(createURL(dbcon), table);

    dbcon.addChangeListener(new DatabaseConnectionChangeListener() {
      public void databaseConnectionStateChanged(DatabaseConnectionChangeEvent e) {
	if (e.getType() == EventType.DISCONNECT) {
	  e.getDatabaseConnection().removeChangeListener(this);
	  m_Tables.remove(createURL(e.getDatabaseConnection()));
	}
      }
    });

    return result;
  }

  /**
   * Returns an iterator over all tables.
   *
   * @return		the iterator
   */
  public Iterator<T> iterator() {
    return m_Tables.values().iterator();
  }

  /**
   * Returns a short string representation of the manager.
   *
   * @return		the string representation
   */
  public String toString() {
    return getTableName() + ": " + m_Tables.keySet();
  }
}
