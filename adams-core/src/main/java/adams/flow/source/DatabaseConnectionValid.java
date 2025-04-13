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
 * DatabaseConnectionValid.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnection;
import adams.flow.core.ActorUtils;
import adams.flow.core.Token;

import java.sql.Connection;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class DatabaseConnectionValid
  extends AbstractSimpleSource {

  private static final long serialVersionUID = 6287213264978639545L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Checks whether the current database connection is still valid.";
  }

  /**
   * Returns the default database connection.
   *
   * @return 		the default database connection
   */
  protected AbstractDatabaseConnection getDefaultDatabaseConnection() {
    return new DatabaseConnection();
  }

  /**
   * Determines the database connection in the flow.
   *
   * @return		the database connection to use
   */
  protected AbstractDatabaseConnection getDatabaseConnection() {
    return ActorUtils.getDatabaseConnection(
      this, adams.flow.standalone.DatabaseConnectionProvider.class, getDefaultDatabaseConnection());
  }

  /**
   * Executes the flow item.
   *
   * @return null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    AbstractDatabaseConnection	dbconn;
    boolean			ok;
    Connection			conn;

    result = null;
    ok     = true;
    dbconn = null;

    try {
      dbconn = getDatabaseConnection();
      if (dbconn == null)
	result = "No database connection available!";
    }
    catch (Exception e) {
      result = handleException("Failed to obtained database connection!", e);
      ok     = false;
    }

    if (ok && (dbconn != null)) {
      conn = dbconn.getConnection(true);
      ok = (conn != null);
      if (ok) {
	try {
	  ok = conn.isValid(3);
	}
	catch (Exception e) {
	  result = handleException("Failed to check whether database connection is valid!", e);
	  ok = false;
	}
      }
    }

    if (isLoggingEnabled())
      getLogger().info("Database connection ok: " + ok);

    m_OutputToken = new Token(ok);

    return result;
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{Boolean.class};
  }
}
