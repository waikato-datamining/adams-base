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
 * DatabaseConnection.java
 * Copyright (C) 2008-2012 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.db;

import adams.core.base.BasePassword;
import adams.env.DatabaseConnectionDefinition;

/**
 * DatabaseConnection manages the interface to the database back-end.
 * Currently set up for MYSQL.
 *
 *  @author  dale (dale at waikato dot ac dot nz)
 *  @version $Revision$
 */
public class DatabaseConnection
  extends AbstractDatabaseConnection {

  /** for serialization. */
  private static final long serialVersionUID = -3625820307854172417L;

  /** the props file. */
  public final static String FILENAME = "DatabaseConnection.props";

  /** for managing the database connections. */
  private static DatabaseManager<DatabaseConnection> m_DatabaseManager;
  static {
    m_DatabaseManager = new DatabaseManager<DatabaseConnection>("adams");
    DatabaseConnection dbcon = new DatabaseConnection();
    m_DatabaseManager.setDefault(DatabaseConnection.getSingleton(dbcon.getURL(), dbcon.getUser(), dbcon.getPassword()));
  }

  /**
   * Local Database Constructor.
   */
  public DatabaseConnection() {
    super();
  }

  /**
   * Local Database Constructor. Initialise the JDBC driver, and attempt
   * connection to the database specified in the URL, with the given username
   * and password.
   *
   * @param driver      the JDBC driver
   * @param url         the JDBC URL
   * @param user        the user to connect with
   * @param password    the password for the user
   */
  public DatabaseConnection(String url, String user, BasePassword password) {
    super(url, user, password);
  }

  /**
   * Returns the properties key to use for retrieving the properties.
   *
   * @return		the key
   */
  @Override
  protected String getDefinitionKey() {
    return DatabaseConnectionDefinition.KEY;
  }

  /**
   * Returns the global database connection object. If not instantiated yet, it
   * will automatically try to connect to the database server.
   *
   * @param url		the database URL
   * @param user	the database user
   * @param password	the database password
   * @return		the singleton
   */
  public static synchronized DatabaseConnection getSingleton(String url, String user, BasePassword password) {
    if (!m_DatabaseManager.has(url, user, password)) {
      m_DatabaseManager.add(new DatabaseConnection(url, user, password));
    }
    else {
      if (!m_DatabaseManager.get(url, user, password).isConnected()) {
	try {
	  m_DatabaseManager.get(url, user, password).connect();
	}
	catch (Exception e) {
	  e.printStackTrace();
	}
      }
    }

    return m_DatabaseManager.get(url, user, password);
  }

  /**
   * Returns the global database connection object. If not instantiated yet, it
   * can automatically try to connect to the database server, depending on the
   * default in the props file (SUFFIX_CONNECTONSTARTUP).
   *
   * @return		the singleton
   * @see		#getConnectOnStartUp()
   * @see		AbstractDatabaseConnection#SUFFIX_CONNECTONSTARTUP
   */
  public static synchronized DatabaseConnection getSingleton() {
    return m_DatabaseManager.getDefault();
  }
}
