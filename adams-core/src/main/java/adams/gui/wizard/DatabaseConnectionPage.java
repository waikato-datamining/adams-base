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
 * DatabaseConnectionPage.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.wizard;

import adams.core.Properties;
import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnection;

/**
 * Handles an ADAMS database connection.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9915 $
 */
public class DatabaseConnectionPage
  extends AbstractDatabaseConnectionPage {

  /** for serialization. */
  private static final long serialVersionUID = -7633802524155866313L;

  /**
   * Page check for checking the connection with the current
   * connection parameters.
   * 
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision: 9915 $
   */
  public static class DatabaseConnectionPageCheck 
    extends AbstractDatabaseConnectionPageCheck {
    
    /** for serialization. */
    private static final long serialVersionUID = 5859663043469959157L;

    /**
     * Creates a new connection object (not connected).
     * 
     * @param props	the connection parameters
     * @return		the connection
     */
    @Override
    protected AbstractDatabaseConnection newDatabaseConnection(Properties props) {
      return new DatabaseConnection(
	  props.getProperty(CONNECTION_URL), 
	  props.getProperty(CONNECTION_USER), 
	  props.getPassword(CONNECTION_PASSWORD));
    }
  }
  
  /**
   * Default constructor.
   */
  public DatabaseConnectionPage() {
    super();
  }
  
  /**
   * Initializes the page with the given page name.
   * 
   * @param pageName	the page name to use
   */
  public DatabaseConnectionPage(String pageName) {
    super(pageName);
  }
  
  /**
   * Returns the default database connection to use for filling in the
   * parameters.
   * 
   * @return		the default connection
   */
  @Override
  protected AbstractDatabaseConnection getDefaultDatabaseConnection() {
    return DatabaseConnection.getSingleton();
  }
}
