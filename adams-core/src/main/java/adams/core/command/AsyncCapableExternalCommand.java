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
 * AsyncCapableExternalCommand.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.core.command;

/**
 * Interface for external commands that support asynchronous mode.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface AsyncCapableExternalCommand
  extends ExternalCommand {

  /**
   * Sets whether to execute in blocking or async fashion.
   *
   * @param value	true for blocking
   */
  public void setBlocking(boolean value);

  /**
   * Returns whether to execute in blocking or async fashion.
   *
   * @return		true for blocking
   */
  public boolean getBlocking();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String blockingTipText();

  /**
   * Adds the line received on stdout from the command.
   *
   * @param line	the line to add
   */
  public void addStdOut(String line);

  /**
   * Adds the line received on stderr from the command.
   *
   * @param line	the line to add
   */
  public void addStdErr(String line);
}
