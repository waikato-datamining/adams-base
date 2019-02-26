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
 * LogT.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.db.mirrored;

import adams.core.UniqueIDs;
import adams.db.AbstractDatabaseConnection;
import adams.db.AbstractDbBackend;
import adams.db.LogEntry;
import adams.db.LogEntryConditions;
import adams.db.LogIntf;
import adams.db.wrapper.AbstractWrapper;
import adams.db.wrapper.WrapperManager;

import java.util.List;

/**
 * Allows mirroring to another database.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class LogT
  extends AbstractWrapper<LogIntf>
  implements LogIntf {

  private static final long serialVersionUID = -4601570259375411398L;

  /** the table manager. */
  protected static WrapperManager<LogT> m_TableManager;

  /** the non-mirrored backend. */
  protected LogIntf m_DB;

  /** object for blocking polling/removal of fully processed. */
  protected final Long m_Updating;

  /**
   * Initializes the mirroring.
   *
   * @param dbcon	the database connection
   * @param wrapped	the mirror
   */
  protected LogT(AbstractDatabaseConnection dbcon, LogIntf wrapped) {
    super(dbcon, wrapped);
    m_DB       = ((DbBackend) AbstractDbBackend.getSingleton()).getNonMirroredBackend().getLog(dbcon);
    m_Updating = UniqueIDs.nextLong();
  }

  /**
   * Loads a log entry from db, using the database ID.
   *
   * @param auto_id	the auto_id to retrieve
   * 			and field from
   * @return		the log entry, null if not found
   */
  @Override
  public LogEntry load(long auto_id) {
    return m_DB.load(auto_id);
  }

  /**
   * Loads log entries from the database that match the conditions.
   *
   * @param cond	the conditions for the entries to match
   * @return		the log entries
   */
  @Override
  public List<LogEntry> load(LogEntryConditions cond) {
    return m_DB.load(cond);
  }

  /**
   * Looks for a log entry in the DB, looking for the auto_id.
   *
   * @param log		the log entry
   * @return		true if a log entry already exists
   */
  @Override
  public boolean exists(LogEntry log) {
    return m_DB.exists(log);
  }

  /**
   * Adds a log entry.
   *
   * @param log		the log entry to add
   * @return  		true if insert successful or already present
   */
  @Override
  public boolean add(LogEntry log) {
    return m_DB.add(log);
  }

  /**
   * Updates a log entry.
   *
   * @param log		the log entry to update
   * @return  		true if update successful or false if not present
   */
  @Override
  public boolean update(LogEntry log) {
    synchronized(m_Updating) {
      getWrapped().update(log);
      return m_DB.update(log);
    }
  }

  /**
   * Removes a log entry from the DB.
   *
   * @param log		the log entry
   * @return		true if successful
   */
  @Override
  public boolean remove(LogEntry log) {
    synchronized(m_Updating) {
      getWrapped().remove(log);
      return m_DB.remove(log);
    }
  }

  /**
   * Returns the singleton of the table.
   *
   * @param dbcon	the database connection to get the singleton for
   * @param mirror 	the mirror
   * @return		the singleton
   */
  public static synchronized LogT getSingleton(AbstractDatabaseConnection dbcon, LogIntf mirror) {
    if (m_TableManager == null)
      m_TableManager = new WrapperManager<>(TABLE_NAME, dbcon.getOwner());
    if (!m_TableManager.has(dbcon))
      m_TableManager.add(dbcon, new LogT(dbcon, mirror));

    return m_TableManager.get(dbcon);
  }
}
