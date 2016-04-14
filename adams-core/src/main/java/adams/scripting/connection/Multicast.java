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
 * Multicast.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.scripting.connection;

import adams.core.MessageCollection;
import adams.scripting.command.RemoteCommand;

/**
 * Sends the same command to all connections.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Multicast
  extends AbstractConnection {

  private static final long serialVersionUID = 6581951716043112610L;

  /** the connections to balance. */
  protected Connection[] m_Connections;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Sends the same command to all connections.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "connection", "connections",
      new Connection[0]);
  }

  /**
   * Sets the connections to use.
   *
   * @param value	the connections
   */
  public void setConnections(Connection[] value) {
    m_Connections = value;
    reset();
  }

  /**
   * Returns the connections to use.
   *
   * @return		the connections
   */
  public Connection[] getConnections() {
    return m_Connections;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String connectionsTipText() {
    return "The connections to send the commands to.";
  }

  /**
   * Hook method that checks the request command and setup for sending it.
   *
   * @param cmd		the command to check
   * @return		null if successful, otherwise error message
   */
  protected String checkRequest(RemoteCommand cmd) {
    String	result;

    result = super.checkRequest(cmd);

    if (result == null) {
      if (m_Connections.length == 0)
	result = "No connections defined!";
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
    MessageCollection	result;
    int			i;
    String		msg;

    result = new MessageCollection();
    for (i = 0; i < m_Connections.length; i++) {
      msg = m_Connections[i].sendRequest(cmd);
      if (msg != null)
	result.add("#" + (i+1) + ": " + msg);
    }

    if (result.isEmpty())
      return null;
    else
      return result.toString();
  }

  /**
   * Hook method that checks the response command and setup for sending it.
   *
   * @param cmd		the command to check
   * @return		null if successful, otherwise error message
   */
  protected String checkResponse(RemoteCommand cmd) {
    String	result;

    result = super.checkResponse(cmd);

    if (result == null) {
      if (m_Connections.length == 0)
	result = "No connections defined for balancing!";
    }

    return result;
  }

  /**
   * Sends the response command.
   *
   * @param cmd		the command to send
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doSendResponse(RemoteCommand cmd) {
    MessageCollection	result;
    int			i;
    String		msg;

    result = new MessageCollection();
    for (i = 0; i < m_Connections.length; i++) {
      msg = m_Connections[i].sendResponse(cmd);
      if (msg != null)
	result.add("#" + (i+1) + ": " + msg);
    }

    if (result.isEmpty())
      return null;
    else
      return result.toString();
  }
}
