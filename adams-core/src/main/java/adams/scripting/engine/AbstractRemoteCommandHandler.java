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
 * AbstractCommandHandler.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.scripting.engine;

import adams.core.option.AbstractOptionHandler;
import adams.scripting.command.RemoteCommand;

/**
 * Ancestor for classes that handle remote commands within a scripting engine.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractRemoteCommandHandler
  extends AbstractOptionHandler
  implements RemoteCommandHandler {

  /** the owner. */
  protected RemoteScriptingEngine m_Owner;

  /**
   * Sets the owning scripting engine.
   *
   * @param value	the owner
   */
  public void setOwner(RemoteScriptingEngine value) {
    m_Owner = value;
  }

  /**
   * Returns the owning scripting engine.
   *
   * @return		the owner, null if none set
   */
  public RemoteScriptingEngine getOwner() {
    return m_Owner;
  }

  /**
   * Hook method for checks before handling the command.
   *
   * @param cmd		the command to handle
   * @return		null if checks passed, otherwise error message
   */
  protected String check(RemoteCommand cmd) {
    if (m_Owner == null)
      return "No owner set!";
    if (cmd == null)
      return "No remote command provided!";
    return null;
  }

  /**
   * Handles the command.
   *
   * @param cmd		the command to handle
   * @return		null if successful, otherwise error message
   */
  protected abstract String doHandle(RemoteCommand cmd);

  /**
   * Handles the command.
   *
   * @param cmd		the command to handle
   * @return		null if successful, otherwise error message
   */
  public String handle(RemoteCommand cmd) {
    String	result;

    result = check(cmd);
    if (result == null)
      result = doHandle(cmd);

    return result;
  }
}
