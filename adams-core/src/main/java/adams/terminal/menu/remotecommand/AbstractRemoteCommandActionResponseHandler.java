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
 * AbstractRemoteCommandActionResponseHandler.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.terminal.menu.remotecommand;

import adams.scripting.responsehandler.AbstractResponseHandler;

/**
 * Custom handler for intercepting the responses from remote command actions.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractRemoteCommandActionResponseHandler<T extends AbstractRemoteCommandAction>
  extends AbstractResponseHandler {

  private static final long serialVersionUID = 6205405220037007365L;

  /** the owner. */
  protected T m_Command;

  /**
   * Initializes the handler.
   *
   * @param command	the command this handler belongs to
   */
  public AbstractRemoteCommandActionResponseHandler(T command) {
    super();
    m_Command = command;
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Ties into " + AbstractRemoteCommandAction.class.getName() + " derived commands.";
  }

  /**
   * Returns the command this handler belongs to.
   *
   * @return		the command, null if none set
   */
  public T getCommand() {
    return m_Command;
  }
}
