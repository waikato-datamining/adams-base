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
 * DefaultConnection.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.scripting.connection;

import adams.core.Utils;
import adams.scripting.command.CommandUtils;
import adams.scripting.command.RemoteCommand;
import adams.scripting.command.RemoteCommandWithResponse;

import java.net.Socket;
import java.nio.charset.Charset;

/**
 * Sends the command to the specified host:port.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DefaultConnection
  extends AbstractConnection {

  private static final long serialVersionUID = -6089174908347724451L;

  /** the  host. */
  protected String m_Host;

  /** the  host port. */
  protected int m_Port;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Sends the command to the specified host:port.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "host", "host",
      "127.0.0.1");

    m_OptionManager.add(
      "port", "port",
      12345, 1, 65535);
  }

  /**
   * Sets the host to send the command to.
   *
   * @param value	the host
   */
  public void setHost(String value) {
    m_Host = value;
    reset();
  }

  /**
   * Returns the host to send the command to.
   *
   * @return		the host
   */
  public String getHost() {
    return m_Host;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String hostTipText() {
    return "The host to send the command to.";
  }

  /**
   * Sets the port to send the command to.
   *
   * @param value	the port
   */
  public void setPort(int value) {
    if (getOptionManager().isValid("port", value)) {
      m_Port = value;
      reset();
    }
  }

  /**
   * Returns the port to send the command to.
   *
   * @return		the port
   */
  public int getPort() {
    return m_Port;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String portTipText() {
    return "The port to send the  to.";
  }

  /**
   * Sends the command to the specified sscripting engine.
   *
   * @param cmd		the command to send
   * @param host	the host to send the command to
   * @param port	the host port
   * @param request	whether Request or Response
   * @return		null if successfully sent, otherwise error message
   */
  protected String send(RemoteCommand cmd, String host, int port, boolean request) {
    String	result;
    String	data;
    Socket 	socket;

    result = null;
    if (request)
      data = cmd.assembleRequest();
    else
      data = ((RemoteCommandWithResponse) cmd).assembleResponse();
    try {
      socket = new Socket(host, port);
      socket.getOutputStream().write(data.getBytes(Charset.forName(CommandUtils.MESSAGE_CHARSET)));
      socket.getOutputStream().flush();
      socket.close();
    }
    catch (Exception e) {
      result = Utils.handleException(
	cmd, "Failed to send " + (request ? "request" : "response") + " to " + host + ":" + port, e);
    }

    return result;
  }

  /**
   * Sends the request command.
   *
   * @param cmd		the command to send
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doSendRequest(RemoteCommand cmd) {
    return send(cmd, m_Host, m_Port, true);
  }

  /**
   * Sends the response command.
   *
   * @param cmd		the command to send
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doSendResponse(RemoteCommand cmd) {
    return send(cmd, m_Host, m_Port, false);
  }
}
