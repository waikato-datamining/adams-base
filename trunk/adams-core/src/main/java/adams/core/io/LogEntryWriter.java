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
 * LogEntryWriter.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.core.io;

import adams.core.Utils;
import adams.db.LogEntry;

/**
 * Helper class for writing log entries to a file (in CSV format).
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LogEntryWriter {

  /**
   * Starts a new log file (writes the header).
   *
   * @param filename	the log file to start
   * @return		true if successfully started
   */
  public static boolean rewrite(String filename) {
    String	entry;

    entry =   "\"" + "Host" + "\","
            + "\"" + "IP" + "\","
            + "\"" + "DB-ID" + "\","
            + "\"" + "Generation" + "\","
            + "\"" + "Type" + "\","
            + "\"" + "Status" + "\","
            + "\"" + "Source" + "\","
            + "\"" + "Message" + "\"";

    return FileUtils.writeToFile(filename, entry, false);
  }

  /**
   * Appends the given log entry.
   *
   * @param filename	the file to store the log entry in
   * @param log		the entry to store
   * @return		true if successfully written
   */
  public static boolean write(String filename, LogEntry log) {
    String	entry;

    entry =   "\"" + log.getHost() + "\","
            + "\"" + log.getIP() + "\","
            + log.getDatabaseID() + ","
            + "\"" + log.getGenerationAsString() + "\","
            + "\"" + log.getType() + "\","
            + "\"" + log.getStatus() + "\","
            + "\"" + Utils.backQuoteChars(log.getMessage()) + "\"";

    return FileUtils.writeToFile(filename, entry, true);
  }
}
