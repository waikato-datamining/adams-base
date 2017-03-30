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
 * SimpleTabResponseHandler.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.remotecontrolcenter.panels;

import adams.scripting.command.RemoteCommand;

/**
 * Only displays errors.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SimpleTabResponseHandler
  extends AbstractTabResponseHandler<AbstractRemoteControlCenterTab> {

  private static final long serialVersionUID = 3360233914485812357L;

  /**
   * Initializes the handler.
   *
   * @param tab the tab this handler belongs to
   */
  public SimpleTabResponseHandler(AbstractRemoteControlCenterTab tab) {
    super(tab);
  }

  /**
   * Does nothing.
   *
   * @param cmd		the command with the response
   */
  @Override
  public void responseSuccessful(RemoteCommand cmd) {
  }

  /**
   * Displays error.
   *
   * @param cmd		the command with the response
   * @param msg		message, can be null
   */
  @Override
  public void responseFailed(RemoteCommand cmd, String msg) {
    m_Tab.getOwner().logError(msg + "\n" + cmd, "Response failed");
  }
}
