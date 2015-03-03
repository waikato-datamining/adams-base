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
 * Main.java
 * Copyright (C) 2009-2011 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui;

import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnection;
import adams.env.Environment;
import adams.gui.application.AbstractApplicationFrame;

/**
 * GUI for ADAMS.
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-home &lt;java.lang.String&gt; (property: home)
 * &nbsp;&nbsp;&nbsp;The directory to use as the project's home directory, overriding the automatically
 * &nbsp;&nbsp;&nbsp;determined one.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-title &lt;java.lang.String&gt; (property: applicationTitle)
 * &nbsp;&nbsp;&nbsp;The title for the application.
 * &nbsp;&nbsp;&nbsp;default: ADAMS
 * </pre>
 *
 * <pre>-driver &lt;java.lang.String&gt; (property: driver)
 * &nbsp;&nbsp;&nbsp;The Java classname of the driver.
 * &nbsp;&nbsp;&nbsp;default: com.mysql.jdbc.Driver
 * </pre>
 *
 * <pre>-url &lt;java.lang.String&gt; (property: URL)
 * &nbsp;&nbsp;&nbsp;The JDBC database URL to connect to.
 * </pre>
 *
 * <pre>-user &lt;java.lang.String&gt; (property: user)
 * &nbsp;&nbsp;&nbsp;The name of the database user.
 * </pre>
 *
 * <pre>-password &lt;java.lang.String&gt; (property: password)
 * &nbsp;&nbsp;&nbsp;The password of the database user.
 * </pre>
 *
 * <pre>-user-mode &lt;BASIC|EXPERT|DEVELOPER|DEBUGGER&gt; (property: userMode)
 * &nbsp;&nbsp;&nbsp;The user mode, which determines the visibility of the menu items.
 * &nbsp;&nbsp;&nbsp;default: BASIC
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Main
  extends AbstractApplicationFrame {

  /** for serialization. */
  private static final long serialVersionUID = -5800519559483605870L;

  /**
   * Returns the default database connection.
   *
   * @return		the default database connection
   */
  protected AbstractDatabaseConnection getDefaultDatabaseConnection() {
    return DatabaseConnection.getSingleton();
  }

  /**
   * Returns the filename that stores the menu layout.
   *
   * @return		the filename
   */
  protected String getMenuLayoutFile() {
    return "adams/gui/Main.props";
  }

  /**
   * Returns the default title of the application.
   *
   * @return		the default title
   */
  protected String getDefaultApplicationTitle() {
    return "ADAMS";
  }

  /**
   * Closes the application.
   */
  protected void closeApplication() {
    m_DbConn.disconnect();
    super.closeApplication();
  }

  /**
   * starts the application.
   *
   * @param args	the commandline arguments
   */
  public static void main(String[] args) {
    runApplication(Environment.class, Main.class, args);
  }
}
