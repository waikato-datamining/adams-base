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
 * AbstractEnhancingSingleHandler.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.core.logging;

import java.util.logging.Handler;

/**
 * Ancestor for log handlers that enhance a single handler.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractEnhancingSingleHandler
  extends AbstractLogHandler
  implements EnhancingSingleHandler {

  /** the base handler. */
  protected Handler m_Handler;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    setHandler(getDefaultHandler());
  }

  protected abstract Handler getDefaultHandler();

  /**
   * Sets the handler to use for outputting the log records.
   *
   * @param value	the handler
   */
  public void setHandler(Handler value) {
    m_Handler = value;
    reset();
  }

  /**
   * Returns the handler to use for outputting the log records.
   *
   * @return		the handler
   */
  public Handler getHandler() {
    return m_Handler;
  }
}
