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
import adams.core.base.BasePassword;
import adams.core.io.PlaceholderFile;
import adams.core.net.JSchUtils;
import adams.core.net.SSHAuthenticationType;
import adams.core.net.SSHSessionProvider;
import adams.scripting.command.RemoteCommand;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.apache.commons.vfs2.provider.sftp.TrustEveryoneUserInfo;

import java.io.File;

/**
 * Ancestor of connection schemes that use an SSH tunnel to connect to the remote scripting engine.
 * <br>
 * Inspired by: <a href="http://www.beanizer.org/site/index.php/en/Articles/Java-ssh-tunneling-with-jsch.html">www.beanizer.org</a>
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractSSHConnection
  extends AbstractConnection
  implements SSHSessionProvider {

  private static final long serialVersionUID = 7719866884762680511L;

  /** the SSH host. */
  protected String m_Host;

  /** the SSH port. */
  protected int m_Port;

  /** the type of authentication to use. */
  protected SSHAuthenticationType m_AuthenticationType;

  /** the SSH user to use. */
  protected String m_User;

  /** the SSH password to use. */
  protected BasePassword m_Password;

  /** the location of the private key. */
  protected PlaceholderFile m_PrivateKeyFile;

  /** the passphrase for the private key. */
  protected BasePassword m_PrivateKeyPassphrase;

  /** the file with known hosts. */
  protected PlaceholderFile m_KnownHosts;

  /** whether to perform strict host key checking (only disable for testing!! insecure!!). */
  protected boolean m_StrictHostKeyChecking;

  /** the SSH session. */
  protected transient Session m_Session;

  /** the assigned port. */
  protected int m_AssignedPort;

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
      22, 1, 65535);

    m_OptionManager.add(
      "authentication-type", "authenticationType",
      SSHAuthenticationType.CREDENTIALS);

    m_OptionManager.add(
      "user", "user",
      System.getProperty("user.name"), false);

    m_OptionManager.add(
      "password", "password",
      new BasePassword(""), false);

    m_OptionManager.add(
      "private-key-file", "privateKeyFile",
      new PlaceholderFile(
        System.getProperty("user.home")
          + File.separator
          + ".ssh"
          + File.separator
          + "id_rsa"));

    m_OptionManager.add(
      "private-key-passphrase", "privateKeyPassphrase",
      new BasePassword(""), false);

    m_OptionManager.add(
      "known-hosts", "knownHosts",
      new PlaceholderFile(
        System.getProperty("user.home")
          + File.separator
          + ".ssh"
          + File.separator
          + "known_hosts"));

    m_OptionManager.add(
      "strict-host-key-checking", "strictHostKeyChecking",
      true);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    if (m_Session != null) {
      m_Session.disconnect();
      m_Session = null;
    }
  }

  /**
   * Sets the host to connect to.
   *
   * @param value	the host name/ip
   */
  public void setHost(String value) {
    m_Host = value;
    reset();
  }

  /**
   * Returns the host to connect to.
   *
   * @return		the host name/ip
   */
  public String getHost() {
    return m_Host;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String hostTipText() {
    return "The remote host (name/IP address) to connect to.";
  }

  /**
   * Sets the remote SSH port to connect to.
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
   * Returns the remote SSH port to connect to.
   *
   * @return 		the port
   */
  public int getPort() {
    return m_Port;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String portTipText() {
    return "The remote SSH port to connect to.";
  }

  /**
   * Sets the type of authentication to use.
   *
   * @param value	the type
   */
  public void setAuthenticationType(SSHAuthenticationType value) {
    m_AuthenticationType = value;
    reset();
  }

  /**
   * Returns the type of authentication to use.
   *
   * @return		the type
   */
  public SSHAuthenticationType getAuthenticationType() {
    return m_AuthenticationType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String authenticationTypeTipText() {
    return "The type of authentication to use.";
  }

  /**
   * Sets the SSH user to use.
   *
   * @param value	the user name
   */
  public void setUser(String value) {
    m_User = value;
    reset();
  }

  /**
   * Returns the SSH user name to use.
   *
   * @return		the user name
   */
  public String getUser() {
    return m_User;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String userTipText() {
    return "The SSH user to use for connecting.";
  }

  /**
   * Sets the SSH password to use.
   *
   * @param value	the password
   */
  public void setPassword(BasePassword value) {
    m_Password = value;
    reset();
  }

  /**
   * Returns the SSH password to use.
   *
   * @return		the password
   */
  public BasePassword getPassword() {
    return m_Password;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String passwordTipText() {
    return "The password of the SSH user to use for connecting.";
  }

  /**
   * Sets the location of the private key file.
   *
   * @param value	the key file
   */
  public void setPrivateKeyFile(PlaceholderFile value) {
    m_PrivateKeyFile = value;
    reset();
  }

  /**
   * Returns the location of the private key file.
   *
   * @return		the key file
   */
  public PlaceholderFile getPrivateKeyFile() {
    return m_PrivateKeyFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String privateKeyFileTipText() {
    return "The location of the private key.";
  }

  /**
   * Sets the passphrase for the private key file, ignored if empty.
   *
   * @param value	the passphrase
   */
  public void setPrivateKeyPassphrase(BasePassword value) {
    m_PrivateKeyPassphrase = value;
    reset();
  }

  /**
   * Returns the passphrase for the private key file, ignored if empty.
   *
   * @return		the passphrase
   */
  public BasePassword getPrivateKeyPassphrase() {
    return m_PrivateKeyPassphrase;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String privateKeyPassphraseTipText() {
    return "The passphrase for the private key file, ignored if empty.";
  }

  /**
   * Sets the file with the known hosts.
   *
   * @param value	the file
   */
  public void setKnownHosts(PlaceholderFile value) {
    m_KnownHosts = value;
    reset();
  }

  /**
   * Returns the file with the known hosts.
   *
   * @return		the file
   */
  public PlaceholderFile getKnownHosts() {
    return m_KnownHosts;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String knownHostsTipText() {
    return "The file storing the known hosts.";
  }

  /**
   * Sets whether to perform strict host key checking.
   * NB: only disabled for testing, as it is very insecure to disable it!
   *
   * @param value	if true then strict checking is on
   */
  public void setStrictHostKeyChecking(boolean value) {
    m_StrictHostKeyChecking = value;
    reset();
  }

  /**
   * Returns whether to perform strict host key checking.
   * NB: only disabled for testing, as it is very insecure to disable it!
   *
   * @return 		true if strict checking is on
   */
  public boolean getStrictHostKeyChecking() {
    return m_StrictHostKeyChecking;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String strictHostKeyCheckingTipText() {
    return
      "Enables/disables strict host key checking - strict checking is the "
	+ "recommended setting, as disabling it is very insecure!";
  }

  /**
   * Creates a new {@link Session} object, but does not connect or establish
   * the tunnel.
   *
   * @return		the Session object
   * @throws Exception
   */
  protected Session createSession(String host, int port) throws Exception {
    Session 	result;
    JSch 	jsch;
    String	password;

    password = m_Password.getValue();
    jsch = JSchUtils.newJsch(m_KnownHosts);
    switch (m_AuthenticationType) {
      case CREDENTIALS:
	result = JSchUtils.newSession(jsch, m_User, password, host, port);
	break;
      case PUBLIC_KEY:
	result = JSchUtils.newSession(jsch, m_User, m_PrivateKeyFile, password, host, port);
	break;
      default:
	throw new IllegalStateException("Unhandled authentication type: " + m_AuthenticationType);
    }
    JSchUtils.configureStrictHostKeyChecking(result, m_StrictHostKeyChecking);
    result.setUserInfo(new TrustEveryoneUserInfo());

    return result;
  }

  /**
   * Returns a new session, connects and configures the tunnel.
   *
   * @return		the session
   */
  public Session newSession() {
    return newSession(m_Host, m_Port);
  }

  /**
   * Returns a new session and connects.
   *
   * @param host	the host to use
   * @param port	the port to use
   * @return		the session
   */
  public Session newSession(String host, int port) {
    Session 	result;

    try {
      result = createSession(host, port);
      result.connect();
    }
    catch (Exception e) {
      Utils.handleException(this, "Failed to establish connection to '" + m_Host + "' (using " + m_AuthenticationType + "): ", e);
      result = null;
    }

    return result;
  }

  /**
   * Returns the SSH session.
   *
   * @return		the SSH session, null if not connected
   */
  @Override
  public Session getSession() {
    return m_Session;
  }

  /**
   * Sends the command to the specified sscripting engine.
   *
   * @param cmd		the command to send
   * @param request	whether Request or Response
   * @return		null if successfully sent, otherwise error message
   */
  protected abstract String doSend(RemoteCommand cmd, boolean request);

  /**
   * Sends the command to the specified sscripting engine.
   *
   * @param cmd		the command to send
   * @param request	whether Request or Response
   * @return		null if successfully sent, otherwise error message
   */
  protected synchronized String send(RemoteCommand cmd, boolean request) {
    String	result;

    result = null;

    if (m_Session == null) {
      m_Session = newSession();
      if (m_Session == null)
	result = "Failed to create new SSH session!";
    }

    if (result == null)
      result = doSend(cmd, request);

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
    return send(cmd, true);
  }

  /**
   * Sends the response command.
   *
   * @param cmd		the command to send
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doSendResponse(RemoteCommand cmd) {
    return send(cmd, false);
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    if (m_Session != null) {
      if (m_Session.isConnected())
	m_Session.disconnect();
      m_Session = null;
    }
    super.cleanUp();
  }
}
