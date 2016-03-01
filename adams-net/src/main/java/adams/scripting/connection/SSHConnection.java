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
 * SSHConnection.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.scripting.connection;

import adams.core.Utils;
import adams.core.net.JSchUtils;
import adams.scripting.command.CommandUtils;
import adams.scripting.command.RemoteCommand;
import adams.scripting.command.RemoteCommandWithResponse;
import com.jcraft.jsch.Session;

import java.net.Socket;
import java.nio.charset.Charset;

/**
 * Uses an SSH tunnel to connect to the remote scripting engine.
 * <br>
 * Inspired by: <a href="http://www.beanizer.org/site/index.php/en/Articles/Java-ssh-tunneling-with-jsch.html">www.beanizer.org</a>
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SSHConnection
  extends AbstractSSHConnectionWithPortForwarding {

  private static final long serialVersionUID = 7719866884762680511L;

  /** whether to forward X11. */
  protected boolean m_ForwardX;

  /** the xhost to use. */
  protected String m_XHost;

  /** the xport to use. */
  protected int m_XPort;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses an SSH tunnel to reach the remote scripting engine.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "forward-x", "forwardX",
      false);

    m_OptionManager.add(
      "x-host", "XHost",
      "");

    m_OptionManager.add(
      "x-port", "XPort",
      6000, 1, 65535);
  }

  /**
   * Sets whether to forward X11.
   *
   * @param value	if true then X11 is forwarded
   */
  public void setForwardX(boolean value) {
    m_ForwardX = value;
    reset();
  }

  /**
   * Returns whether X11 is forwarded.
   *
   * @return 		true if X11 is forwarded
   */
  public boolean getForwardX() {
    return m_ForwardX;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String forwardXTipText() {
    return "If set to true, then X is forwarded.";
  }

  /**
   * Sets the xhost to connect to.
   *
   * @param value	the host name/ip
   */
  public void setXHost(String value) {
    m_XHost = value;
    reset();
  }

  /**
   * Returns the xhost to connect to.
   *
   * @return		the host name/ip
   */
  public String getXHost() {
    return m_XHost;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String XHostTipText() {
    return "The xhost (name/IP address) to connect to.";
  }

  /**
   * Sets the xport to connect to.
   *
   * @param value	the port
   */
  public void setXPort(int value) {
    if (getOptionManager().isValid("XPort", value)) {
      m_XPort = value;
      reset();
    }
  }

  /**
   * Returns the xport to connect to.
   *
   * @return 		the port
   */
  public int getXPort() {
    return m_XPort;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String XPortTipText() {
    return "The xport to connect to.";
  }

  /**
   * Creates a new {@link Session} object, but does not connect or establish
   * the tunnel.
   *
   * @return		the Session object
   * @throws Exception
   */
  protected Session createSession(String host, int port) throws Exception {
    Session	result;

    result = super.createSession(host, port);
    if (m_ForwardX)
      JSchUtils.configureX11(result, m_XHost, m_XPort);

    return result;
  }

  /**
   * Sends the command to the specified sscripting engine.
   *
   * @param cmd		the command to send
   * @param request	whether Request or Response
   * @return		null if successfully sent, otherwise error message
   */
  protected synchronized String doSend(RemoteCommand cmd, boolean request) {
    String	result;
    String	data;
    Socket 	socket;

    result = null;

    if (request)
      data = cmd.assembleRequest();
    else
      data = ((RemoteCommandWithResponse) cmd).assembleResponse();
    try {
      socket = new Socket("127.0.0.1", m_AssignedPort);
      socket.getOutputStream().write(data.getBytes(Charset.forName(CommandUtils.MESSAGE_CHARSET)));
      socket.getOutputStream().flush();
      socket.close();
    }
    catch (Exception e) {
      result = Utils.handleException(
	cmd, "Failed to send " + (request ? "request" : "response"), e);
    }

    return result;
  }
}
