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
 * ConsoleObject.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.core.logging;

import adams.core.SizeOf;
import adams.core.SizeOfHandler;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A basic object with logging support.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LoggingObject
  implements Serializable, SizeOfHandler, LoggingSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 2530679166402508994L;

  /** the logging level. */
  protected LoggingLevel m_LoggingLevel;

  /** the logger in use. */
  protected transient Logger m_Logger;
  
  /**
   * Initializes the object.
   */
  public LoggingObject() {
    initializeLogging();
  }
  
  /**
   * Pre-configures the logging.
   */
  protected void initializeLogging() {
    m_LoggingLevel = LoggingLevel.WARNING;
  }
  
  /**
   * Initializes the logger.
   * <br><br>
   * Default implementation uses the class name.
   */
  protected void configureLogger() {
    m_Logger = LoggingHelper.getLogger(getClass());
    m_Logger.setLevel(m_LoggingLevel.getLevel());
  }
  
  /**
   * Returns the logger in use.
   * 
   * @return		the logger
   */
  public synchronized Logger getLogger() {
    if (m_Logger == null)
      configureLogger();
    return m_Logger;
  }

  /**
   * Returns the logging level.
   *
   * @return 		the level
   */
  public LoggingLevel getLoggingLevel() {
    return m_LoggingLevel;
  }
  
  /**
   * Returns whether logging is enabled.
   * 
   * @return		true if at least {@link Level#INFO}
   */
  public boolean isLoggingEnabled() {
    return LoggingHelper.isAtLeast(m_LoggingLevel.getLevel(), Level.INFO);
  }

  /**
   * Returns the size of the object.
   *
   * @return		the size of the object
   */
  public int sizeOf() {
    return SizeOf.sizeOf(this);
  }
}
