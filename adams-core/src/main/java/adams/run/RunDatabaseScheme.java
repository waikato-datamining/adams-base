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
 * RunDatabaseScheme.java
 * Copyright (C) 2008-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.run;

import adams.core.base.BasePassword;
import adams.db.AbstractIndexedTable;
import adams.db.DatabaseConnection;
import adams.db.DatabaseConnectionEstablisher;
import adams.db.DatabaseConnectionParameterHandler;

/**
 * Abstract ancestor for RunSchemes that need to access the database.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class RunDatabaseScheme
  extends RunScheme
  implements DatabaseConnectionEstablisher, DatabaseConnectionParameterHandler {

  /** for serialization. */
  private static final long serialVersionUID = -9114743801790739455L;

  /** database connection. */
  protected transient DatabaseConnection m_DbConn;

  /** the JDBC URL to connect to. */
  protected String m_URL;

  /** the database user to use for connecting. */
  protected String m_User;

  /** the password of the database user. */
  protected BasePassword m_Password;

  /**
   * initializes member variables.
   */
  @Override
  protected void initialize() {
    m_DbConn = null;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"url", "URL",
	DatabaseConnection.getSingleton().getDefaultURL(), false);

    m_OptionManager.add(
	"user", "user",
	DatabaseConnection.getSingleton().getDefaultUser(), false);

    m_OptionManager.add(
	"password", "password",
	DatabaseConnection.getSingleton().getDefaultPassword(), false);
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
   * Establishes the database connection.
   */
  public void establishDatabaseConnection() {
    m_DbConn = DatabaseConnection.getSingleton(m_URL, m_User, m_Password);
  }

  /**
   * Performs some initializations before the actual run.
   * Connects to the database.
   *
   * @throws Exception 	if something goes wrong
   */
  @Override
  protected void preRun() throws Exception {
    super.preRun();

    getLogger().info("- Connecting to database");
    establishDatabaseConnection();
    getLogger().info("- Initializing tables");
    AbstractIndexedTable.initTables(m_DbConn);
  }

  /**
   * Performs some output/cleanup after the actual run.
   * Disconnects from the database.
   *
   * @throws Exception 	if something goes wrong
   */
  @Override
  protected void postRun() throws Exception {
    super.postRun();

    getLogger().info("- Disconnecting from database");
    m_DbConn.disconnect();
    m_DbConn = null;
  }
}
