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
 * AbstractOutputFormatter.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.core.command.output;

import adams.core.command.ExternalCommand;
import adams.core.command.OutputType;
import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor for output formatters.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractOutputFormatter
  extends AbstractOptionHandler
  implements OutputFormatter {

  private static final long serialVersionUID = -6998188915252308900L;

  /**
   * Determines whether the output should be formatted and forwarded to the command.
   *
   * @param command	the command to forward the output to
   * @param stdout	whether the output occurred on stdout
   * @return		true if to forward the output to the command
   */
  protected boolean canFormatOutput(ExternalCommand command, boolean stdout) {
    return ((command.getOutputType() == OutputType.STDOUT) && stdout)
      || ((command.getOutputType() == OutputType.STDERR) && !stdout)
      || (command.getOutputType() == OutputType.BOTH);
  }

  /**
   * Formats the output received from the command. Feeds the formatted data back into the
   * ExternalCommand instance.
   *
   * @param command  the external command to feed the output back into
   * @param stdout   whether the output was from stdout or stderr
   * @param blocking whether the output was received via blocking or async execution
   * @param output   the output to format
   */
  protected abstract void doFormatOutput(ExternalCommand command, boolean stdout, boolean blocking, String output);

  /**
   * Formats the output received from the command. Feeds the formatted data back into the
   * ExternalCommand instance.
   *
   * @param command  the external command to feed the output back into
   * @param stdout   whether the output was from stdout or stderr
   * @param blocking whether the output was received via blocking or async execution
   * @param output   the output to format
   */
  @Override
  public void formatOutput(ExternalCommand command, boolean stdout, boolean blocking, String output) {
    if (canFormatOutput(command, stdout))
      doFormatOutput(command, stdout, blocking, output);
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
  }
}
