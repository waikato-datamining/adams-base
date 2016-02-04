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
 * AllowAll.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.scripting.permissionhandler;

import adams.scripting.command.RemoteCommand;

/**
 * Allows all commands.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class AllowAll
  extends AbstractPermissionHandler {

  private static final long serialVersionUID = 4390768873427078067L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows all commands.";
  }

  /**
   * Checks whether the command is permitted.
   *
   * @param cmd		the command to check
   * @return		true if permitted
   */
  @Override
  public boolean permitted(RemoteCommand cmd) {
    return true;
  }
}
