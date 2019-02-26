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
 * DbBackend.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.db.mysql;

import adams.db.AbstractDatabaseConnection;
import adams.db.AbstractDbBackend;
import adams.db.JDBC;
import adams.db.LogIntf;
import adams.db.SQLIntf;
import adams.db.generic.SQL;

/**
 * MySQL database backend.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DbBackend
  extends AbstractDbBackend {

  private static final long serialVersionUID = -6206414041321415520L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "MySQL database backend.";
  }

  /**
   * Returns the generic SQL handler.
   *
   * @param conn	the database connection
   * @return		the handler
   */
  public SQLIntf getSQL(AbstractDatabaseConnection conn) {
    if (!JDBC.isMySQL(conn))
      throw new IllegalStateException("Not a MySQL JDBC URL: " + conn.getURL());
    return SQL.singleton(conn);
  }

  /**
   * Returns the handler for the log table.
   *
   * @param conn	the database connection
   * @return		the handler
   */
  @Override
  public LogIntf getLog(AbstractDatabaseConnection conn) {
    if (!JDBC.isMySQL(conn))
      throw new IllegalStateException("Not a MySQL JDBC URL: " + conn.getURL());
    return LogT.getSingleton(conn);
  }
}
