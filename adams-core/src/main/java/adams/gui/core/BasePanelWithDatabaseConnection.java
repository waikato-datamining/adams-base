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
 * BasePanelWithDatabaseConnection.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnectionHandler;
import adams.event.DatabaseConnectionChangeEvent;
import adams.event.DatabaseConnectionChangeEvent.EventType;
import adams.event.DatabaseConnectionChangeListener;

/**
 * Ancestor for panels that depend on a database connection.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class BasePanelWithDatabaseConnection
  extends BasePanel
  implements DatabaseConnectionHandler, DatabaseConnectionChangeListener {

  /** for serialization. */
  private static final long serialVersionUID = 399941129570919254L;

  /** the database connection. */
  protected AbstractDatabaseConnection m_DatabaseConnection;

  /**
   * Initializes the members.
   */
  protected void initialize() {
    super.initialize();

    m_DatabaseConnection = getDefaultDatabaseConnection();
  }

  /**
   * Returns the default database connection.
   *
   * @return		the default database connection
   */
  protected abstract AbstractDatabaseConnection getDefaultDatabaseConnection();

  /**
   * Returns the currently used database connection object, can be null.
   *
   * @return		the current object
   */
  public AbstractDatabaseConnection getDatabaseConnection() {
    return m_DatabaseConnection;
  }

  /**
   * Sets the database connection object to use.
   *
   * @param value	the object to use
   */
  public void setDatabaseConnection(AbstractDatabaseConnection value) {
    m_DatabaseConnection.removeChangeListener(this);
    m_DatabaseConnection = value;
    m_DatabaseConnection.addChangeListener(this);
  }

  /**
   * A change in the database connection occurred.
   *
   * @param e		the event
   */
  public void databaseConnectionStateChanged(DatabaseConnectionChangeEvent e) {
    if (e.getType() == EventType.CONNECT)
      setDatabaseConnection(e.getDatabaseConnection());
  }
}
