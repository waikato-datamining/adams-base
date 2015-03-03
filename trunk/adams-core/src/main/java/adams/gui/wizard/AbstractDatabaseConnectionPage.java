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
 * AbstractDatabaseConnectionPage.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.wizard;

import adams.core.Properties;
import adams.db.AbstractDatabaseConnection;
import adams.gui.core.PropertiesParameterPanel.PropertyType;

/**
 * Ancestor for pages that allow the user to enter database connection 
 * parameters.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9915 $
 */
public abstract class AbstractDatabaseConnectionPage
  extends ParameterPanelPage {

  /** for serialization. */
  private static final long serialVersionUID = -7633802524155866313L;

  /**
   * Ancestor for page checks that check the connection with the current
   * connection parameters.
   * 
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision: 9915 $
   */
  public static class DatabaseConnectionPageCheck 
    implements PageCheck<AbstractDatabaseConnectionPage> {
    
    /** for serialization. */
    private static final long serialVersionUID = 5859663043469959157L;
    
    /**
     * Checks the page.
     * 
     * @param page	the page to check
     * @return		true if check passed
     */
    @Override
    public boolean checkPage(AbstractDatabaseConnectionPage page) {
      AbstractDatabaseConnection 	conn;
      
      conn = page.getDatabaseConnection();
      
      try {
	return conn.connect();
      }
      catch (Exception e) {
	System.err.println("Failed to connect!");
	e.printStackTrace();
	return false;
      }
    }
  }
  
  /** key for connection URL. */
  public static final String CONNECTION_URL = "connectionURL";

  /** key for connection user. */
  public static final String CONNECTION_USER = "connectionUser";

  /** key for connection password. */
  public static final String CONNECTION_PASSWORD = "connectionPassword";
  
  /**
   * Default constructor.
   */
  public AbstractDatabaseConnectionPage() {
    this("Connection");
  }
  
  /**
   * Initializes the page with the given page name.
   * 
   * @param pageName	the page name to use
   */
  public AbstractDatabaseConnectionPage(String pageName) {
    super();
    setPageName(pageName);
  }
  
  /**
   * Initializes the widets.
   */
  @Override
  protected void initGUI() {
    Properties			props;
    AbstractDatabaseConnection	conn;
    
    super.initGUI();
    
    setDescription(
	"Please enter the database connection parameters.\n"
	+ "A JDBC connection URL has the following format:\n"
	+ "  jdbc:<protocol>://<url or ip>[:port]/[database]\n"
	+ "For instance, a MySQL database looks like this:\n"
	+ "  jdbc:mysql://localhost:3306/cms");
    getParameterPanel().addPropertyType(CONNECTION_URL, PropertyType.STRING);
    getParameterPanel().setLabel(CONNECTION_URL, "JDBC URL");
    getParameterPanel().addPropertyType(CONNECTION_USER, PropertyType.STRING);
    getParameterPanel().setLabel(CONNECTION_USER, "User");
    getParameterPanel().addPropertyType(CONNECTION_PASSWORD, PropertyType.PASSWORD);
    getParameterPanel().setLabel(CONNECTION_PASSWORD, "Password");
    getParameterPanel().setPropertyOrder(new String[]{
	CONNECTION_URL,
	CONNECTION_USER,
	CONNECTION_PASSWORD,
    });
    
    conn  = getDefaultDatabaseConnection();
    props = new Properties();
    props.setProperty(CONNECTION_URL, conn.getURL());
    props.setProperty(CONNECTION_USER, conn.getUser());
    props.setPassword(CONNECTION_PASSWORD, conn.getPassword());
    setProperties(props);
  }
  
  /**
   * Returns the default database connection to use for filling in the
   * parameters.
   * 
   * @return		the default connection
   */
  protected abstract AbstractDatabaseConnection getDefaultDatabaseConnection();
  
  /**
   * Returns a database connection using the user-supplied parameters.
   * 
   * @return		the connection
   */
  protected abstract AbstractDatabaseConnection getDatabaseConnection();
}
