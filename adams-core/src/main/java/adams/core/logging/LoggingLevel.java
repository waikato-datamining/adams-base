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
 * LoggingLevel.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.core.logging;

import java.util.logging.Level;

/**
 * Enumeration of logging levels.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public enum LoggingLevel {

  /** no logging. */
  OFF(Level.OFF),
  /** only severe warnings. */
  SEVERE(Level.SEVERE),
  /** only warnings and above. */
  WARNING(Level.WARNING),
  /** info messages and above. */
  INFO(Level.INFO),
  /** configuration messages and above. */
  CONFIG(Level.CONFIG),
  /** some debugging and above. */
  FINE(Level.FINE),
  /** more debugging and above. */
  FINER(Level.FINER),
  /** lots of debugging and above. */
  FINEST(Level.FINEST);
  
  /** the associated level. */
  private Level m_Level;

  /** the comparator. */
  protected LevelComparator m_Comparator;

  /**
   * Initializes the enum element.
   * 
   * @param level	the level to associate
   */
  private LoggingLevel(Level level) {
    m_Level      = level;
    m_Comparator = new LevelComparator();
  }
  
  /**
   * Returns the associated logging level.
   * 
   * @return		the level
   */
  public Level getLevel() {
    return m_Level;
  }

  /**
   * Determines the associated LoggingLevel.
   *
   * @param level	the level to get the LoggingLevel for
   * @return		the LoggingLevel, null if not found
   */
  public static LoggingLevel valueOf(Level level) {
    LoggingLevel	result;

    result = null;

    for (LoggingLevel l: values()) {
      if (l.getLevel() == level) {
	result = l;
	break;
      }
    }

    return result;
  }

  /**
   * Checks whether the level meets the minimum.
   *
   * @param min		the minimum level to meet
   * @return		if minimum logging level met
   */
  public boolean isAtLeast(Level min) {
    return (m_Comparator.compare(m_Level, min) >= 0);
  }

  /**
   * Checks whether the level meets the maximum.
   *
   * @param max		the maximum level to meet
   * @return		if maximum logging level met
   */
  public boolean isAtMost(Level max) {
    return (m_Comparator.compare(m_Level, max) <= 0);
  }
}
