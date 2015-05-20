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
 * InitializeTables.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.tools;

import adams.db.AbstractDatabaseConnection;
import adams.db.AbstractIndexedTable;
import adams.db.DatabaseConnection;

/**
 <!-- globalinfo-start -->
 * Makes sure that all tables exist and are initialized. Can be called at runtime to re-create tables that got dropped.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D (property: debug)
 *         If set to true, scheme may output additional info to the console.
 * </pre>
 *
 * <pre>-driver &lt;java.lang.String&gt; (property: driver)
 *         The JDBC driver.
 *         default: com.mysql.jdbc.Driver
 * </pre>
 *
 * <pre>-url &lt;java.lang.String&gt; (property: URL)
 *         The database URL.
 * </pre>
 *
 * <pre>-user &lt;java.lang.String&gt; (property: user)
 *         The database user.
 * </pre>
 *
 * <pre>-password &lt;java.lang.String&gt; (property: password)
 *         The password of the database user.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InitializeTables
  extends AbstractDatabaseTool {

  /** for serialization. */
  private static final long serialVersionUID = 1052968728531351369L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
        "Makes sure that all tables exist and are initialized. Can be "
      + "called at runtime to re-create tables that got dropped.";
  }

  /**
   * Returns the default database connection.
   *
   * @return		the database connection
   */
  protected AbstractDatabaseConnection getDefaultDatabaseConnection() {
    return DatabaseConnection.getSingleton();
  }

  /**
   * Attempt to load the file and save to db.
   * Exit java upon failure
   */
  protected void doRun() {
    AbstractIndexedTable.initTables(getDatabaseConnection());
  }
}
