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
 * PermissionHandler.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.scripting.permissionhandler;

import adams.scripting.command.RemoteCommand;

/**
 * Interface for schemes that check whether a command can be executed.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface PermissionHandler {

  /**
   * Checks whether the command is permitted.
   *
   * @param cmd		the command to check
   * @return		true if permitted
   */
  public boolean permitted(RemoteCommand cmd);
}
