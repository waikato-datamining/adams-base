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
 * LogTSQLite.java
 * Copyright (C) 2016-2019 University of Waikato, Hamilton, NZ
 */

package adams.db.sqlite;

import adams.db.AbstractDatabaseConnection;
import adams.db.TableManager;

/**
 * SQLite implementation.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class LogT
  extends adams.db.generic.LogT {

  private static final long serialVersionUID = 6309576140124918549L;

  /** the table manager. */
  protected static TableManager<LogT> m_TableManager;

  /**
   * The constructor.
   *
   * @param dbcon the database context this table is used in
   */
  public LogT(AbstractDatabaseConnection dbcon) {
    super(dbcon);
  }

  /**
   * Initializes the table. Used by the "InitializeTables" tool.
   *
   * @param dbcon	the database context
   */
  public static synchronized void initTable(AbstractDatabaseConnection dbcon) {
    getSingleton(dbcon).init();
  }

  /**
   * Returns the singleton of the table.
   *
   * @param dbcon	the database connection to get the singleton for
   * @return		the singleton
   */
  public static synchronized LogT getSingleton(AbstractDatabaseConnection dbcon) {
    if (m_TableManager == null)
      m_TableManager = new TableManager<>(TABLE_NAME, dbcon.getOwner());
    if (!m_TableManager.has(dbcon))
      m_TableManager.add(dbcon, new LogT(dbcon));

    return m_TableManager.get(dbcon);
  }
}
