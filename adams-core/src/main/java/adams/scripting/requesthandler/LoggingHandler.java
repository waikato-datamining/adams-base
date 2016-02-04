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
 * LoggingHandler.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.scripting.requesthandler;

import adams.core.logging.LoggingLevel;
import adams.scripting.command.RemoteCommand;

/**
 * Outputs request using the logger.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LoggingHandler
  extends AbstractRequestHandler {

  private static final long serialVersionUID = -575781398900766054L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs the request using the logger.";
  }

  /**
   * Returns the default logging level to use.
   *
   * @return		the logging level
   */
  @Override
  protected LoggingLevel getDefaultLoggingLevel() {
    return LoggingLevel.INFO;
  }

  /**
   * Handles successfuly requests.
   *
   * @param cmd		the command with the request
   */
  @Override
  public void requestSuccessful(RemoteCommand cmd) {
    getLogger().info("Successful request\n" + cmd);
  }

  /**
   * Handles failed requests.
   *
   * @param cmd		the command with the request
   * @param msg		the optional error message, can be null
   */
  @Override
  public void requestFailed(RemoteCommand cmd, String msg) {
    getLogger().info("Failed request: " + msg + "\n" + cmd);
  }

  /**
   * Handles rejected requests.
   *
   * @param cmd		the command with the request
   */
  @Override
  public void requestRejected(RemoteCommand cmd) {
    getLogger().info("Rejected request\n" + cmd);
  }
}
