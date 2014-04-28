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
 * AbstractDatabaseObjectWithOptionHandling.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.db;

import adams.core.base.BasePassword;
import adams.core.option.AbstractOptionHandler;

/**
 * Abstract ancestor for classes that need to be able to change the database
 * connection with commandline parameters.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractDatabaseObjectWithOptionHandling
  extends AbstractOptionHandler
  implements DatabaseConnectionHandler, DatabaseConnectionEstablisher, DatabaseConnectionParameterHandler {

  /** for serialization. */
  private static final long serialVersionUID = -8800746361445333658L;

  /** database connection. */
  protected transient AbstractDatabaseConnection m_dbc;

  /** the JDBC URL to connect to. */
  protected String m_URL;

  /** the database user to use for connecting. */
  protected String m_User;

  /** the password of the database user. */
  protected BasePassword m_Password;

  /** whether to suppress connecting to the database. */
  protected boolean m_NoDatabaseConnect;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "url", "URL",
	    DatabaseConnection.getSingleton().getDefaultURL(),
	    false);

    m_OptionManager.add(
	    "user", "user",
	    DatabaseConnection.getSingleton().getDefaultUser(),
	    false);

    m_OptionManager.add(
	    "password", "password",
	    DatabaseConnection.getSingleton().getDefaultPassword(),
	    false);

    m_OptionManager.add(
	    "no-connect", "noDatabaseConnect",
	    false);
  }

  /**
   * initializes member variables.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_dbc = null;
  }

  /**
   * Sets the database connection to use.
   *
   * @param value	the database connection
   */
  public void setDatabaseConnection(AbstractDatabaseConnection value) {
    m_dbc = value;

    if (value != null) {
      m_URL      = value.getURL();
      m_User     = value.getUser();
      m_Password = value.getPassword();
    }
    else {
      m_URL      = "";
      m_User     = "";
      m_Password = new BasePassword("");
    }
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
   * Sets the database URL.
   *
   * @param value 	the URL
   */
  public void setURL(String value) {
    m_URL = value;
  }

  /**
   * Returns the database URL being used.
   *
   * @return 		the URL
   */
  public String getURL() {
    return m_URL;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String URLTipText() {
    return "The database URL.";
  }

  /**
   * Sets the database user to use for connecting.
   *
   * @param value 	the user
   */
  public void setUser(String value) {
    m_User = value;
  }

  /**
   * Returns the database user used for connecting.
   *
   * @return 		the user
   */
  public String getUser() {
    return m_User;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String userTipText() {
    return "The database user.";
  }

  /**
   * Sets the password of the database user.
   *
   * @param value 	the password
   */
  public void setPassword(BasePassword value) {
    m_Password = value;
  }

  /**
   * Returns the password of the database user.
   *
   * @return 		the password
   */
  public BasePassword getPassword() {
    return m_Password;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String passwordTipText() {
    return "The password of the database user.";
  }

  /**
   * Sets whether to suppress a database connection or not.
   *
   * @param value 	if true then no database connection will be established
   * 			when establishDatabaseConnection() is called
   * @see		#establishDatabaseConnection()
   */
  public void setNoDatabaseConnect(boolean value) {
    m_NoDatabaseConnect = value;
  }

  /**
   * Returns whether the database connection is suppressed or not.
   *
   * @return 		true if no database connection is established when
   * 			calling establishDatabaseConnection()
   * @see		#establishDatabaseConnection()
   */
  public boolean getNoDatabaseConnect() {
    return m_NoDatabaseConnect;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String noDatabaseConnectTipText() {
    return "If set to true, no database connection is established.";
  }

  /**
   * Returns a new database connection object.
   * 
   * @return		the database connection object
   */
  protected AbstractDatabaseConnection createDatabaseConnection() {
    return DatabaseConnection.getSingleton(m_URL, m_User, m_Password);
  }
  
  /**
   * Establishes the database connection.
   */
  public void establishDatabaseConnection() {
    if (!m_NoDatabaseConnect) {
      if (isLoggingEnabled())
	getLogger().info("Connecting to database");

      m_dbc = createDatabaseConnection();
    }
    else {
      if (isLoggingEnabled())
	getLogger().info("Skipping database connection");
    }
  }
}
