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
 * ErrorPostProcessor.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.control.errorpostprocessor;

import adams.flow.core.Actor;
import adams.flow.core.ErrorHandler;

/**
 * Interface for post-processors for error messages.
 * To be used by error handlers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface ErrorPostProcessor {

  /**
   * Post-processes the error.
   *
   * @param handler	the error handler that this call comes from
   * @param source	the source actor where the error originated
   * @param type	the type of error
   * @param msg		the error message
   */
  public void postProcessError(ErrorHandler handler, Actor source, String type, String msg);
}
