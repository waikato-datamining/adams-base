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
 * LoggingSupporter.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.core.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Interface for classes that support logging.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface LoggingSupporter {
  
  /**
   * Returns the logger in use.
   * 
   * @return		the logger
   */
  public Logger getLogger();
  
  /**
   * Returns whether logging is enabled.
   * 
   * @return		true if at least {@link Level#INFO}
   */
  public boolean isLoggingEnabled();
}
