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

package adams.scripting.responsehandler;

import adams.core.logging.LoggingLevel;
import adams.core.option.OptionUtils;
import adams.scripting.command.RemoteCommand;

/**
 * Outputs the responses using the logger.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LoggingHandler
  extends AbstractResponseHandler {

  private static final long serialVersionUID = 8309252227142210365L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs the responses in the console.";
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
   * Handles successful responses.
   *
   * @param cmd		the command with the response
   */
  @Override
  public void responseSuccessful(RemoteCommand cmd) {
    getLogger().info("Successful response: " + OptionUtils.getCommandLine(cmd) + "\n" + cmd);
  }

  /**
   * Handles failed responses.
   *
   * @param cmd		the command with the response
   * @param msg		message, can be null
   */
  @Override
  public void responseFailed(RemoteCommand cmd, String msg) {
    getLogger().info("Failed response: " + OptionUtils.getCommandLine(cmd) + "\nMessage: " + msg + "\n" + cmd);
  }
}
