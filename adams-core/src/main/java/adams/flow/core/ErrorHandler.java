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
 * ErrorHandler.java
 * Copyright (C) 2012-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.core;

import adams.flow.control.Flow;

/**
 * Interface for classes that handle errors in the flow.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface ErrorHandler {

  /**
   * Handles the given error message with the flow that the actor belongs to,
   * if the flow has error logging turned on. Might stop the flow as well.
   *
   * @param source	the actor this error originated from
   * @param type	the type of error
   * @param msg		the error message to log
   * @return		null if error has been handled, otherwise the error message
   * @see		Flow#getLogErrors()
   * @see		Flow#getErrorHandling()
   */
  public String handleError(Actor source, String type, String msg);
}
