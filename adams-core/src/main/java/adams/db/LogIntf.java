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
 * LogIntf.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.db;

import java.util.List;

/**
 * Interface for tables for storing log messages.
 *
 * @author Fracpete (fracpete at waikato dot ac dot nz)
 */
public interface LogIntf
  extends TableInterface {

  /** the table name. */
  public final static String TABLE_NAME = "logging";

  /**
   * Loads a log entry from db, using the database ID.
   *
   * @param auto_id	the auto_id to retrieve
   * 			and field from
   * @return		the log entry, null if not found
   */
  public LogEntry load(long auto_id);

  /**
   * Loads log entries from the database that match the conditions.
   *
   * @param cond	the conditions for the entries to match
   * @return		the log entries
   */
  public List<LogEntry> load(LogEntryConditions cond);

  /**
   * Looks for a log entry in the DB, looking for the auto_id.
   *
   * @param log		the log entry
   * @return		true if a log entry already exists
   */
  public boolean exists(LogEntry log);

  /**
   * Adds a log entry.
   *
   * @param log		the log entry to add
   * @return  		true if insert successful or already present
   */
  public boolean add(LogEntry log);

  /**
   * Updates a log entry.
   *
   * @param log		the log entry to update
   * @return  		true if update successful or false if not present
   */
  public boolean update(LogEntry log);

  /**
   * Removes a log entry from the DB.
   *
   * @param log		the log entry
   * @return		true if successful
   */
  public boolean remove(LogEntry log);
}
