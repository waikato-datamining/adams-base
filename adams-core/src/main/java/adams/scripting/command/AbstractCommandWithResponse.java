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
 * AbstractCommandWithResponse.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.scripting.command;

import adams.core.Properties;

/**
 * Ancestor for commands that send a response.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractCommandWithResponse
  extends AbstractCommand
  implements RemoteCommandWithResponse {

  private static final long serialVersionUID = -2803551461382517312L;

  /** the response host. */
  protected String m_ResponseHost;

  /** the response host port. */
  protected int m_ResponsePort;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_ResponseHost = "127.0.0.1";
    m_ResponsePort = 12345;
  }

  /**
   * Sets the host to send the response to.
   *
   * @param value	the host
   */
  public void setResponseHost(String value) {
    m_ResponseHost = value;
  }

  /**
   * Returns the host to send the response to.
   *
   * @return		the host
   */
  public String getResponseHost() {
    return m_ResponseHost;
  }

  /**
   * Sets the port to send the response to.
   *
   * @param value	the port
   */
  public void setResponsePort(int value) {
    if ((value >= 1) && (value <= 65535))
      m_ResponsePort = value;
    else
      getLogger().warning("Response port must satisfy 0 < x < 65536, provided: " + value);
  }

  /**
   * Returns the port to send the response to.
   *
   * @return		the port
   */
  public int getResponsePort() {
    return m_ResponsePort;
  }

  /**
   * Parses the header information.
   *
   * @param header	the header
   * @return		null if successfully parsed, otherwise error message
   */
  public String parse(Properties header) {
    String	result;

    result = super.parse(header);
    if (result != null)
      return result;

    if (!header.hasKey(KEY_RESPONSEHOST))
      return "No '" + KEY_RESPONSEHOST + "' property found!";
    setResponseHost(header.getProperty(KEY_RESPONSEHOST, ""));

    if (!header.hasKey(KEY_RESPONSEPORT))
      return "No '" + KEY_RESPONSEPORT + "' property found!";
    setResponsePort(header.getInteger(KEY_RESPONSEPORT, 12345));

    return null;
  }

  /**
   * Assembles the request header.
   *
   * @return		the request header
   */
  @Override
  protected Properties assembleRequestHeader() {
    Properties		result;

    result = super.assembleRequestHeader();
    result.setProperty(KEY_RESPONSEHOST, m_ResponseHost);
    result.setInteger(KEY_RESPONSEPORT, m_ResponsePort);

    return result;
  }
}
