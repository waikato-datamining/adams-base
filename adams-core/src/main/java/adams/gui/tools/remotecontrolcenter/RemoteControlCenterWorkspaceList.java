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
 * RemoteControlCenterWorkspaceList.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.remotecontrolcenter;

import adams.gui.workspace.AbstractWorkspaceListPanel;

/**
 * Workspace list for the remote control center.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RemoteControlCenterWorkspaceList
  extends AbstractWorkspaceListPanel<RemoteControlCenterPanel> {

  private static final long serialVersionUID = -2341620264742492727L;

  /**
   * Returns the default title to use for dialogs.
   *
   * @return		the title
   */
  @Override
  protected String getDefaultDialogTitle() {
    return "Remote control center";
  }
}
