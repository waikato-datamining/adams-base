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
 * AbstractErrorPostProcessor.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.control.errorpostprocessor;

import adams.core.option.AbstractOptionHandler;
import adams.flow.core.Actor;
import adams.flow.core.ErrorHandler;

/**
 * Ancestor for post-processors for error messages.
 * To be used by error handlers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractErrorPostProcessor
  extends AbstractOptionHandler
  implements ErrorPostProcessor {

  private static final long serialVersionUID = -7893131952932448760L;

  /**
   * Hook method for checks.
   * <br>
   * Default implementation ensures that error handler, source actor and
   * error message are present.
   *
   * @param handler	the error handler that this call comes from
   * @param source	the source actor where the error originated
   * @param type	the type of error
   * @param msg		the error message
   */
  protected void check(ErrorHandler handler, Actor source, String type, String msg) {
    if (handler == null)
      throw new IllegalStateException("No error handler provided!");
    if (source == null)
      throw new IllegalStateException("No source actor provided!");
    if (msg == null)
      throw new IllegalStateException("No error message provided!");
  }

  /**
   * Performs the actual post-processing of the error.
   *
   * @param handler	the error handler that this call comes from
   * @param source	the source actor where the error originated
   * @param type	the type of error
   * @param msg		the error message
   */
  protected abstract void doPostProcessError(ErrorHandler handler, Actor source, String type, String msg);

  /**
   * Post-processes the error.
   *
   * @param handler	the error handler that this call comes from
   * @param source	the source actor where the error originated
   * @param type	the type of error
   * @param msg		the error message
   */
  public void postProcessError(ErrorHandler handler, Actor source, String type, String msg) {
    check(handler, source, type, msg);
    doPostProcessError(handler, source, type, msg);
  }
}
