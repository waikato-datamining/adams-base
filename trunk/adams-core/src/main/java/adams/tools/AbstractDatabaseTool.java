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
 * AbstractDatabaseTool.java
 * Copyright (C) 2008-2011 University of Waikato, Hamilton, New Zealand
 */

package adams.tools;

import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnectionHandler;

/**
 * Abstract ancestor for tools that need database access.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractDatabaseTool
  extends AbstractTool
  implements DatabaseConnectionHandler {

  /** for serialization. */
  private static final long serialVersionUID = -8882774800775467939L;

  /** database connection. */
  protected transient AbstractDatabaseConnection m_dbc;

  /**
   * initializes member variables.
   */
  protected void initialize() {
    super.initialize();

    m_dbc = getDefaultDatabaseConnection();
  }

  /**
   * Returns the default database connection.
   *
   * @return		the database connection
   */
  protected abstract AbstractDatabaseConnection getDefaultDatabaseConnection();

  /**
   * Sets the database connection to use.
   *
   * @param value	the database connection
   */
  public void setDatabaseConnection(AbstractDatabaseConnection value) {
    m_dbc = value;
  }

  /**
   * Returns the current database connection.
   *
   * @return		the database connection
   */
  public AbstractDatabaseConnection getDatabaseConnection() {
    return m_dbc;
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    m_dbc = null;
  }
}
