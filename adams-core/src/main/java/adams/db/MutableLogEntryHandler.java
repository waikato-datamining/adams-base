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
 * LogEntryMutableHandler.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.db;

/**
 * Interface for classes that allow the internally stored list of LogEntry
 * records to be modified from outside.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface MutableLogEntryHandler
  extends LogEntryHandler {

  /**
   * Removes all currently stored LogEntry records.
   */
  public void clearLogEntries();

  /**
   * Adds the LogEntry record to the internal list.
   *
   * @param entry	the record to add
   */
  public void addLogEntry(LogEntry entry);

  /**
   * Returns the specified LogEntry record.
   *
   * @param index	the index of the record to return
   * @return		the requested LogEntry
   */
  public LogEntry getLogEntry(int index);

  /**
   * Removes the specified LogEntry record from the internal list.
   *
   * @param index	the index of the record to delete
   * @return		the deleted LogEntry
   */
  public LogEntry removeLogEntry(int index);
}
