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
 * LogEntryHandler.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.db;

import java.util.List;

/**
 * Interface for classes that store LogEntry records.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface LogEntryHandler {

  /**
   * Returns the stored LogEntry records.
   *
   * @return		the stored entries
   */
  public List<LogEntry> getLogEntries();

  /**
   * Returns the number of stored LogEntry records.
   *
   * @return		the number of records
   */
  public int countLogEntries();
}
