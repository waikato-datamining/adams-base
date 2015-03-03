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
 * CustomLoggingLevelObject.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.core.logging;

/**
 * Allows setting of logging level, in contrast to {@link adams.core.logging.LoggingObject}.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7676 $
 */
public class CustomLoggingLevelObject
  extends LoggingObject
  implements LoggingLevelHandler {

  private static final long serialVersionUID = -4771277060514061223L;

  /**
   * Sets the logging level.
   *
   * @param value 	the level
   */
  public synchronized void setLoggingLevel(LoggingLevel value) {
    m_LoggingLevel = value;
    m_Logger       = null;
  }
}
