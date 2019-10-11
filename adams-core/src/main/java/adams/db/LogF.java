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
 * LogF.java
 * Copyright (C) 2017-2019 University of Waikato, Hamilton, NZ
 */

package adams.db;

import adams.core.logging.LoggingHelper;

import java.util.List;

/**
 * Facade for logging backends.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class LogF
  extends AbstractTableFacade
  implements LogIntf {

  private static final long serialVersionUID = 3061846359366161539L;

  /** the facade manager. */
  protected static FacadeManager<LogF> m_TableManager;

  /** the backend. */
  protected LogIntf m_DB;

  /**
   * Constructor.
   *
   * @param dbcon the database context to use
   */
  public LogF(AbstractDatabaseConnection dbcon) {
    super(dbcon, TABLE_NAME);

    m_DB = AbstractDbBackend.getSingleton().getLog(dbcon);
  }

  /**
   * Loads a log entry from db, using the database ID.
   *
   * @param auto_id	the auto_id to retrieve
   * 			and field from
   * @return		the log entry, null if not found
   */
  public LogEntry load(long auto_id) {
    if (isLoggingEnabled())
      getLogger().info(LoggingHelper.getMethodName() + ": auto_id=" + auto_id);
    return m_DB.load(auto_id);
  }

  /**
   * Loads log entries from the database that match the conditions.
   *
   * @param cond	the conditions for the entries to match
   * @return		the log entries
   */
  public List<LogEntry> load(LogEntryConditions cond) {
    if (isLoggingEnabled())
      getLogger().info(LoggingHelper.getMethodName() + ": cond=" + cond);
    return m_DB.load(cond);
  }

  /**
   * Looks for a log entry in the DB, looking for the auto_id.
   *
   * @param log		the log entry
   * @return		true if a log entry already exists
   */
  public boolean exists(LogEntry log) {
    if (isLoggingEnabled())
      getLogger().info(LoggingHelper.getMethodName() + ": log=" + log);
    return m_DB.exists(log);
  }

  /**
   * Adds a log entry.
   *
   * @param log		the log entry to add
   * @return  		true if insert successful or already present
   */
  public boolean add(LogEntry log) {
    if (isLoggingEnabled())
      getLogger().info(LoggingHelper.getMethodName() + ": log=" + log);
    return m_DB.add(log);
  }

  /**
   * Updates a log entry.
   *
   * @param log		the log entry to update
   * @return  		true if update successful or false if not present
   */
  public boolean update(LogEntry log) {
    if (isLoggingEnabled())
      getLogger().info(LoggingHelper.getMethodName() + ": log=" + log);
    return m_DB.update(log);
  }

  /**
   * Removes a log entry from the DB.
   *
   * @param log		the log entry
   * @return		true if successful
   */
  public boolean remove(LogEntry log) {
    if (isLoggingEnabled())
      getLogger().info(LoggingHelper.getMethodName() + ": log=" + log);
    return m_DB.remove(log);
  }

  /**
   * Returns the singleton of the facade.
   *
   * @param dbcon	the database connection to get the singleton for
   * @return		the singleton
   */
  public static synchronized LogF getSingleton(AbstractDatabaseConnection dbcon) {
    if (m_TableManager == null)
      m_TableManager = new FacadeManager<>(TABLE_NAME, dbcon.getOwner());
    if (!m_TableManager.has(dbcon))
      m_TableManager.add(dbcon, new LogF(dbcon));

    return m_TableManager.get(dbcon);
  }
}
