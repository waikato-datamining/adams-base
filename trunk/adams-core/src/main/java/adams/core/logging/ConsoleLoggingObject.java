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
 * ConsoleLoggingObject.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.core.logging;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A basic object with logging support in the console.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ConsoleLoggingObject
  implements Serializable {

  /** for serialization. */
  private static final long serialVersionUID = 2530679166402508994L;

  /** the logger in use. */
  protected transient Logger m_Logger;
  
  /**
   * Initializes the object.
   */
  public ConsoleLoggingObject() {
    initializeLogger();
  }
  
  /**
   * Initializes the logger.
   * <p/>
   * Default implementation uses the class name.
   */
  protected void initializeLogger() {
    m_Logger = LoggingHelper.getConsoleLogger(getClass());
  }
  
  /**
   * Returns the logger in use.
   * 
   * @return		the logger
   */
  public synchronized Logger getLogger() {
    if (m_Logger == null)
      initializeLogger();
    return m_Logger;
  }
  
  /**
   * Returns whether logging is enabled.
   * 
   * @return		true if not {@link Level#OFF}
   */
  public boolean isLoggingEnabled() {
    return (getLogger().getLevel() != Level.OFF);
  }
}
