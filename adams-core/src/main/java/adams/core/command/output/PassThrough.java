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
 * PassThrough.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.core.command.output;

import adams.core.command.ExternalCommand;

/**
 * Simply forwards the received output without any processing.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class PassThrough
  extends AbstractOutputFormatter {

  private static final long serialVersionUID = 8986415437957022973L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Simply forwards the received output without any processing.";
  }

  /**
   * Returns what output type the formatter generates.
   *
   * @param blocking returns the type when used on blocking mode output
   * @return the type
   */
  @Override
  public Class generates(boolean blocking) {
    return String.class;
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
  @Override
  protected void doFormatOutput(ExternalCommand command, boolean stdout, boolean blocking, String output) {
    command.addFormattedOutput(output);
  }
}
