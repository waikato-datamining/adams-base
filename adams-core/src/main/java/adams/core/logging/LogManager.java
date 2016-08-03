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
 * LogManager.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core.logging;

import java.util.Enumeration;
import java.util.Vector;

/**
 * Simple LogManager that avoids the memory leak that the
 * {@link java.util.logging.LogManager} has by not keeping any references to any
 * {@link Logger} objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LogManager {

  /** the singleton instance of the log manager. */
  protected static LogManager m_LogManager;

  /**
   * Initializes the log manager.
   */
  protected LogManager() {
    super();
  }

  /**
   * Returns a logger for the name.
   *
   * @param name	the name of the logger
   * @return		the logger
   */
  public synchronized Logger getLogger(String name) {
    return new Logger(name);
  }

  /**
   * Returns all logger names in the cache.
   *
   * @return		the logger names
   */
  public Enumeration<String> getLoggerNames() {
    return new Vector<String>().elements();
  }

  /**
   * Returns the singleton.
   *
   * @return		the log manager
   */
  public static synchronized LogManager getLogManager() {
    if (m_LogManager == null)
      m_LogManager = new LogManager();
    return m_LogManager;
  }
}
