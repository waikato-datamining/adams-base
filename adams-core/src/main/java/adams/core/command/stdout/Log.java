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
 * Log.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.core.command.stdout;

import adams.core.logging.LoggingLevel;

/**
 * Outputs the data received from the command's stdout via its logger instance.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Log
  extends AbstractStdOutProcessor {

  private static final long serialVersionUID = -2194306680981658479L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs the data received from the command's stdout via its logger instance.";
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
   * Processes the stdout output received when in async mode.
   *
   * @param output the output to process
   */
  @Override
  public void processAsync(String output) {
    if (isLoggingEnabled())
      getLogger().info(output);
  }

  /**
   * Processes the stdout output received when in blocking mode.
   *
   * @param output the output to process
   */
  @Override
  public void processBlocking(String output) {
    processAsync(output);
  }
}
