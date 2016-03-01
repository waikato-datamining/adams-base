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
 * AbstractSSHConnection.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.scripting.connection;

import adams.core.Utils;
import com.jcraft.jsch.Session;

/**
 * Ancestor of connection schemes that use an SSH tunnel to connect to the remote scripting engine.
 * <br>
 * Inspired by: <a href="http://www.beanizer.org/site/index.php/en/Articles/Java-ssh-tunneling-with-jsch.html">www.beanizer.org</a>
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractSSHConnectionWithPortForwarding
  extends AbstractSSHConnection {

  private static final long serialVersionUID = 7719866884762680511L;

  /** the local port for the SSH tunnel. */
  protected int m_LocalPort;

  /** the remote port for the scripting engine. */
  protected int m_ScriptingPort;

  /** the assigned port. */
  protected int m_AssignedPort;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "local-port", "localPort",
      9000, 1, 65535);

    m_OptionManager.add(
      "scripting-port", "scriptingPort",
      12345, 1, 65535);
  }

  /**
   * Sets the local port to connect to (SSH tunnel).
   *
   * @param value	the port
   */
  public void setLocalPort(int value) {
    if (getOptionManager().isValid("localPort", value)) {
      m_LocalPort = value;
      reset();
    }
  }

  /**
   * Returns the local port to connect to (SSH tunnel).
   *
   * @return 		the port
   */
  public int getLocalPort() {
    return m_LocalPort;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String localPortTipText() {
    return "The local port to connect to (SSH tunnel).";
  }

  /**
   * Sets the port of the remote scripting engine to connect to.
   *
   * @param value	the port
   */
  public void setScriptingPort(int value) {
    if (getOptionManager().isValid("scriptingPort", value)) {
      m_ScriptingPort = value;
      reset();
    }
  }

  /**
   * Returns the port of the remote scripting engine to connect to.
   *
   * @return 		the port
   */
  public int getScriptingPort() {
    return m_ScriptingPort;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String scriptingPortTipText() {
    return "The port that the remote scripting engine is listening on.";
  }

  /**
   * Returns a new session, connects and configures the tunnel.
   *
   * @param host	the host to use
   * @param port	the port to use
   * @return		the session
   */
  public Session newSession(String host, int port) {
    Session 	result;

    result = super.newSession(host, port);
    if (result != null) {
      try {
        // set up tunnel
        m_AssignedPort = result.setPortForwardingL(m_LocalPort, host, m_ScriptingPort);
        if (isLoggingEnabled())
          getLogger().info("Assigned port: " + m_AssignedPort);
      }
      catch (Exception e) {
        Utils.handleException(this, "Failed to establish connection to '" + m_Host + "' (using " + m_AuthenticationType + "): ", e);
        result = null;
      }
    }

    return result;
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    if (m_Session != null) {
      if (m_Session.isConnected()) {
	try {
	  m_Session.delPortForwardingL(m_AssignedPort);
	}
	catch (Exception e) {
	  // ignored
	}
      }
    }
    super.cleanUp();
  }
}
