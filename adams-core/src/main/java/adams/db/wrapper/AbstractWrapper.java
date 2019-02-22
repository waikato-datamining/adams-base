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
 * AbstractWrapper.java
 * Copyright (C) 2018-2019 University of Waikato, Hamilton, NZ
 */

package adams.db.wrapper;

import adams.core.logging.LoggingObject;
import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnectionProvider;
import adams.db.TableInterface;

/**
 * Ancestor for wrapper table classes.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractWrapper<W extends TableInterface>
  extends LoggingObject
  implements TableInterface, DatabaseConnectionProvider {

  private static final long serialVersionUID = 2584491122555160871L;

  /** the database connection. */
  protected AbstractDatabaseConnection m_Connection;

  /** the wrapped backend. */
  protected W m_Wrapped;

  /** the table name. */
  protected String m_TableName;

  /**
   * Initializes the wrapper.
   *
   * @param dbcon	the database connection
   * @param wrapped 	the wrapped backend
   */
  protected AbstractWrapper(AbstractDatabaseConnection dbcon, W wrapped) {
    this(dbcon, wrapped, wrapped.getTableName());
  }

  /**
   * Initializes the wrapper.
   *
   * @param dbcon	the database connection
   * @param wrapped 	the wrapped backend
   */
  protected AbstractWrapper(AbstractDatabaseConnection dbcon, W wrapped, String tableName) {
    super();
    m_Connection = dbcon;
    m_Wrapped    = wrapped;
    m_TableName  = tableName;
  }

  /**
   * Returns the currently used database connection object, can be null.
   *
   * @return		the current object
   */
  public AbstractDatabaseConnection getDatabaseConnection() {
    return m_Connection;
  }

  /**
   * Get name of table.
   *
   * @return		table name
   */
  public String getTableName() {
    return m_TableName;
  }

  /**
   * Returns the wrapped backend.
   *
   * @return		the backend
   */
  public W getWrapped() {
    return m_Wrapped;
  }
}
