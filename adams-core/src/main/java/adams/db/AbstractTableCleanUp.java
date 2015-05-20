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
 * AbstractTableCleanUp.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.db;

import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor of classes that clean up tables in some fashion.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractTableCleanUp
  extends AbstractOptionHandler
  implements DatabaseConnectionHandler {

  /** for serialization. */
  private static final long serialVersionUID = -6662554417213854267L;
  
  /** database connection. */
  protected transient AbstractDatabaseConnection m_Connection;

  /**
   * Sets the database connection to use.
   *
   * @param value	the database connection
   */
  public void setDatabaseConnection(AbstractDatabaseConnection value) {
    m_Connection = value;
  }

  /**
   * Returns the current database connection.
   *
   * @return		the database connection
   */
  public AbstractDatabaseConnection getDatabaseConnection() {
    return m_Connection;
  }
  
  /**
   * Performs checks before cleaning up the table.
   * <br><br>
   * Default implementation only checks if connected to database
   * 
   * @return		null if checks successful, otherwise error message
   */
  protected String check() {
    if (!getDatabaseConnection().isConnected())
      return "No connected to database (" + getDatabaseConnection() + ")!";
    
    return null;
  }
  
  /**
   * Performs the actual clean up.
   * 
   * @return		null if successfully cleaned up, otherwise error message
   */
  protected abstract String doCleanUpTable();
  
  /**
   * Cleans up the table.
   * 
   * @return		null if successfully cleaned up, otherwise error message
   */
  public String cleanUpTable() {
    String	result;
    
    result = check();
    if (result == null)
      result = doCleanUpTable();
    
    return result;
  }
}
