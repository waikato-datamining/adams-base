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
 * AbstractTableFacade.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.db;

import adams.core.logging.LoggingHelper;
import adams.core.logging.LoggingObject;

import java.util.logging.Level;

/**
 * Ancestor for database table facades.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractTableFacade
  extends LoggingObject {

  private static final long serialVersionUID = 8540313627448845885L;

  /** name of the table. */
  protected String m_TableName;

  /** whether debugging is turned on. */
  protected boolean m_Debug;

  /** connection to database. */
  protected AbstractDatabaseConnection m_DatabaseConnection;

  /**
   * Constructor.
   *
   * @param dbcon	the database context to use
   * @param tableName	the name of the table
   */
  public AbstractTableFacade(AbstractDatabaseConnection dbcon, String tableName) {
    super();

    m_TableName          = tableName;
    m_DatabaseConnection = dbcon;

    updatePrefix();
  }

  /**
   * Updates the prefix of the console object output streams.
   */
  protected void updatePrefix() {
    String	prefix;

    prefix   = getClass().getName() + "(" + getDatabaseConnection().toStringShort() + "/" + getDatabaseConnection().hashCode() + ")";
    m_Logger = LoggingHelper.getLogger(prefix);
    m_Logger.setLevel(getDebug() ? Level.INFO : Level.OFF);
  }

  /**
   * Returns the database connection this table is for.
   *
   * @return		the database connection
   */
  public AbstractDatabaseConnection getDatabaseConnection() {
    return m_DatabaseConnection;
  }

  /**
   * Get name of table.
   *
   * @return	table name
   */
  public String getTableName() {
    return m_TableName;
  }

  /**
   * Sets whether debugging is enabled, outputs more on the console.
   *
   * @param value	if true debugging is enabled
   */
  public void setDebug(boolean value) {
    m_Debug = value;
    getLogger().setLevel(value ? Level.INFO : Level.WARNING);
  }

  /**
   * Returns whether debugging is enabled.
   *
   * @return		true if debugging is enabled
   */
  public boolean getDebug() {
    return m_Debug;
  }
}
