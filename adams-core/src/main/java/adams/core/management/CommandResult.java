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
 * CommandResult.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.core.management;

import adams.core.Utils;

import java.io.Serializable;

/**
 * Container class for storing command, exit code and stdout/stderr output.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class CommandResult
  implements Serializable {

  private static final long serialVersionUID = 3385654490914424873L;

  /** the command that was executed. */
  public String[] command;

  /** the exit code of the command. */
  public int exitCode;

  /** the output on stdout, if any. */
  public String stdout;

  /** the output on stderr, if any. */
  public String stderr;

  /**
   * Initializes the container.
   *
   * @param command	the command that was executed
   * @param exitCode	the exit code of the command
   */
  public CommandResult(String[] command, int exitCode) {
    this(command, exitCode, null, null);
  }

  /**
   * Initializes the container.
   *
   * @param command	the command that was executed
   * @param exitCode	the exit code of the command
   * @param stdout	the output on stdout, can be null
   * @param stderr	the output on stderr, can be null
   */
  public CommandResult(String[] command, int exitCode, String stdout, String stderr) {
    this.command = command.clone();
    this.exitCode = exitCode;
    this.stdout = stdout;
    this.stderr = stderr;
  }

  /**
   * The container as a string representation.
   *
   * @return		the string representation
   */
  public String toString() {
    return "command=" + Utils.flatten(command, " ")
      + ", exitCode=" + exitCode
      + (stdout != null ? (", stdout=" + stdout) : "")
      + (stderr != null ? (", stderr=" + stderr) : "");
  }
}
