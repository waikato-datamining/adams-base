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

package adams.db.autodetect;

import adams.db.AbstractDatabaseConnection;
import adams.db.AbstractDbBackend;
import adams.db.JDBC;
import adams.db.LogIntf;
import adams.db.SQLIntf;

/**
 * Auto-detection database backend. Detects: MySQL, SQLite, PostgreSQL.
 * Otherwise uses the generic SQL/LogT instances.
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
    return "Auto-detect Spectral backend.\n"
      + "Detects: MySQL, PostgreSQL, SQLite.\n"
      + "Otherwise uses generic SQL/LogT.";
  }

  /**
   * Returns whether this connection is supported.
   *
   * @param conn	the database connection
   * @return		always true
   */
  @Override
  public boolean isSupported(AbstractDatabaseConnection conn) {
    return true;
  }

  /**
   * Returns the generic SQL handler.
   *
   * @param conn	the database connection
   * @return		the handler
   */
  public SQLIntf getSQL(AbstractDatabaseConnection conn) {
    return adams.db.generic.SQL.singleton(conn);
  }

  /**
   * Returns the handler for the log table.
   *
   * @param conn	the database connection
   * @return		the handler
   */
  @Override
  public LogIntf getLog(AbstractDatabaseConnection conn) {
    if (JDBC.isMySQL(conn))
      return adams.db.mysql.LogT.getSingleton(conn);
    else if (JDBC.isPostgreSQL(conn))
      return adams.db.postgresql.LogT.getSingleton(conn);
    else if (JDBC.isSQLite(conn))
      return adams.db.sqlite.LogT.getSingleton(conn);
    else
      return adams.db.generic.LogT.singleton(conn);
  }
}
