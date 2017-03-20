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
 * AbstractConnection.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.scripting.connection;

import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;
import adams.scripting.command.RemoteCommand;
import adams.scripting.command.RemoteCommandWithResponse;

/**
 * Ancestor for connections.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractConnection
  extends AbstractOptionHandler
  implements Connection {

  private static final long serialVersionUID = 7968903903323685090L;

  /**
   * Hook method that checks the request command and setup for sending it.
   *
   * @param cmd		the command to check
   * @return		null if successful, otherwise error message
   */
  protected String checkRequest(RemoteCommand cmd) {
    if (cmd == null)
      return "No request command provided!";
    if (!cmd.isRequest())
      return "Command is not a request:\n" + cmd.toString();

    return null;
  }

  /**
   * Sends the request command.
   *
   * @param cmd		the command to send
   * @return		null if successful, otherwise error message
   */
  protected abstract String doSendRequest(RemoteCommand cmd);

  /**
   * Sends the request command.
   *
   * @param cmd		the command to send
   * @return		null if successful, otherwise error message
   */
  public String sendRequest(RemoteCommand cmd) {
    String	result;

    result = checkRequest(cmd);
    if (result == null) {
      cmd.beforeSendRequest();
      result = doSendRequest(cmd);
      cmd.afterSendRequest(result);
    }

    return result;
  }

  /**
   * Hook method that checks the response command and setup for sending it.
   *
   * @param cmd		the command to check
   * @return		null if successful, otherwise error message
   */
  protected String checkResponse(RemoteCommand cmd) {
    if (cmd == null)
      return "No response command provided!";
    if (cmd.isRequest())
      return "Command is not a response:\n" + cmd.toString();

    return null;
  }

  /**
   * Sends the response command.
   *
   * @param cmd		the command to send
   * @return		null if successful, otherwise error message
   */
  protected abstract String doSendResponse(RemoteCommand cmd);

  /**
   * Sends the response command.
   *
   * @param cmd		the command to send
   * @return		null if successful, otherwise error message
   */
  public String sendResponse(RemoteCommand cmd) {
    String	result;

    result = checkResponse(cmd);
    if (result == null) {
      if (cmd instanceof RemoteCommandWithResponse)
	((RemoteCommandWithResponse) cmd).beforeSendResponse();
      result = doSendResponse(cmd);
      if (cmd instanceof RemoteCommandWithResponse)
	((RemoteCommandWithResponse) cmd).afterSendResponse(result);
    }

    return result;
  }

  /**
   * Compares this connection with the other one using the commandline string.
   *
   * @param o		the other connection
   * @return		less than zero, equal to zero or greater than zero if
   * 			the commandline string is less, equal or greater than
   * 			this one
   */
  @Override
  public int compareTo(Connection o) {
    return OptionUtils.getCommandLine(this).compareTo(OptionUtils.getCommandLine(o));
  }

  /**
   * Checks whether the object is the same as this one.
   *
   * @param o		the other object
   * @return		true if also a connection object and the same commandline
   * @see		#compareTo(Connection)
   */
  public boolean equals(Object o) {
    return (o instanceof Connection) && (compareTo((Connection) o) == 0);
  }

  /**
   * Cleans up data structures, frees up memory.
   * <br>
   * Default implementation does nothing.
   */
  @Override
  public void cleanUp() {

  }

  /**
   * Just returns the commandline.
   *
   * @return		the commandline
   */
  @Override
  public String toString() {
    return OptionUtils.getCommandLine(this);
  }
}
