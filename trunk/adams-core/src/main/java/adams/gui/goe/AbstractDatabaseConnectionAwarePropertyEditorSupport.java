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
 * AbstractDatabaseConnectionAwarePropertyEditorSupport.java
 * Copyright (C) 2010-2011 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.goe;

import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnectionHandler;
import adams.event.DatabaseConnectionChangeListener;

/**
 * Ancestor for GOE editors that are DatabaseConnectionChangeListeners and
 * need to de-register correctly.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractDatabaseConnectionAwarePropertyEditorSupport
  extends AbstractPropertyEditorSupport
  implements DatabaseConnectionHandler {

  /** the database connection. */
  protected AbstractDatabaseConnection m_DatabaseConnection;

  /**
   * For initializing members.
   */
  protected void initialize() {
    super.initialize();

    m_DatabaseConnection = getDefaultDatabaseConnection();
  }

  /**
   * Returns the default database connection.
   *
   * @return		the connection
   */
  protected abstract AbstractDatabaseConnection getDefaultDatabaseConnection();

  /**
   * Returns the change listener that needs to be de-registered.
   *
   * @return		the listener
   */
  protected abstract DatabaseConnectionChangeListener getDatabaseConnectionChangeListener();

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
    m_DatabaseConnection = value;
    if (m_CustomEditor instanceof DatabaseConnectionHandler)
      ((DatabaseConnectionHandler) m_CustomEditor).setDatabaseConnection(m_DatabaseConnection);
  }

  /**
   * Cleans up when the dialog is closed.
   */
  protected void cleanUp() {
    m_DatabaseConnection.removeChangeListener(getDatabaseConnectionChangeListener());
  }
}
