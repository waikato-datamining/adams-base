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
 * ExternalCommandWithProgrammaticArguments.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.core.command;

/**
 * Interface for external commands that support programmatic options that
 * get folded into the command.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface ExternalCommandWithProgrammaticArguments
  extends ExternalCommand {

  /**
   * Sets the additional arguments to append to the command.
   *
   * @param value	the arguments
   */
  public void setAdditionalArguments(String[] value);

  /**
   * Returns the additional arguments to append to the command.
   *
   * @return	the arguments
   */
  public String[] getAdditionalArguments();
}
