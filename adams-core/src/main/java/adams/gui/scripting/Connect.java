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
 * Connect.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.scripting;

import adams.core.base.BasePassword;
import adams.core.logging.LoggingLevel;
import adams.db.AbstractDatabaseConnection;
import adams.db.GlobalSingletonDatabaseConnection;

/**
 <!-- scriptlet-parameters-start -->
 * Action parameters:<br>
 * <pre>   connect &lt;driver&gt; &lt;URL&gt; &lt;user&gt; [password]</pre>
 * <br><br>
 <!-- scriptlet-parameters-end -->
 *
 <!-- scriptlet-description-start -->
 * Description:
 * <pre>   Connects to the database.</pre>
 * <br><br>
 <!-- scriptlet-description-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Connect
  extends AbstractDatabaseScriptlet {

  /** for serialization. */
  private static final long serialVersionUID = 5029475846064359798L;

  /** the action to execute. */
  public final static String ACTION = "connect";

  /**
   * Returns the action string used in the command processor.
   *
   * @return		<!-- scriptlet-action-start -->connect<!-- scriptlet-action-end -->
   */
  @Override
  public String getAction() {
    return ACTION;
  }

  /**
   * Returns a one-line listing of the options of the action.
   *
   * @return		the options or null if none
   */
  @Override
  protected String getOptionsDescription() {
    return "<driver> <URL> <user> [password] [logging-level] [connect-on-startup] [auto-commit]";
  }

  /**
   * Returns the full description of the action.
   *
   * @return		the full description
   */
  @Override
  public String getDescription() {
    return "Connects to the database.";
  }

  /**
   * Processes the options.
   *
   * @param options	additional/optional options for the action
   * @return		null if no error, otherwise error message
   * @throws Exception 	if something goes wrong
   */
  @Override
  public String process(String options) throws Exception {
    String[]			params;
    boolean			connect;
    AbstractDatabaseConnection	conn;

    params = options.split(" ");
    if ((params.length < 2) || (params.length > 6))
      return "Wrong connection parameters!";

    conn    = getDatabaseConnection();
    connect = !conn.isConnected();

    // are we already connected to the correct database?
    if (conn.isConnected()) {
      if (    !conn.getURL().equals(params[0])
	   || !conn.getUser().equals(params[1]) ) {
	conn.disconnect();
	connect = true;
      }
    }

    if (connect) {
      if (!(conn instanceof GlobalSingletonDatabaseConnection)) {
	conn = conn.getClone();
	getOwner().getOwner().setDatabaseConnection(conn);
      }
      conn.setURL(params[0]);
      conn.setUser(params[1]);
      if (params.length >= 3)
	conn.setPassword(new BasePassword(params[2]));
      if (params.length >= 4)
	conn.setLoggingLevel(LoggingLevel.valueOf(params[3]));
      if (params.length >= 5)
	conn.setConnectOnStartUp(Boolean.parseBoolean(params[4]));
      if (params.length >= 6)
	conn.setAutoCommit(Boolean.parseBoolean(params[5]));
      conn.connect();
    }

    if (!conn.isConnected())
      return "Failed to connect to database!";

    return null;
  }
}
