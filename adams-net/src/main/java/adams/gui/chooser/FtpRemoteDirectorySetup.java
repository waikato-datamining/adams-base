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
 * FtpRemoteDirectory.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.chooser;

import adams.core.Utils;
import adams.core.base.BasePassword;
import adams.core.option.AbstractOptionHandler;
import org.apache.commons.net.ProtocolCommandEvent;
import org.apache.commons.net.ProtocolCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.util.logging.Level;

/**
 * For configuring an SSH connection and remote directory.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 14538 $
 */
public class FtpRemoteDirectorySetup
  extends AbstractOptionHandler
  implements RemoteDirectorySetup, ProtocolCommandListener {

  private static final long serialVersionUID = -8429471751146663032L;

  /** the FTP host. */
  protected String m_Host;

  /** the FTP user to use. */
  protected String m_User;

  /** the FTP password to use. */
  protected BasePassword m_Password;

  /** whether to use passive mode. */
  protected boolean m_UsePassiveMode;

  /** whether to use binary file transfer mode. */
  protected boolean m_UseBinaryMode;

  /** the directory to list. */
  protected String m_RemoteDir;

  /** the FTP client object. */
  protected FTPClient m_Client;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "For configuring access to a remote directory via FTP.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "host", "host",
      "");

    m_OptionManager.add(
      "user", "user",
      "anonymous", false);

    m_OptionManager.add(
      "password", "password",
      new BasePassword(""), false);

    m_OptionManager.add(
      "passive", "usePassiveMode",
      false);

    m_OptionManager.add(
      "binary", "useBinaryMode",
      false);

    m_OptionManager.add(
      "remote-dir", "remoteDir",
      "");
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
    return "The host (name/IP address) to connect to.";
  }

  /**
   * Sets the FTP user to use.
   *
   * @param value	the user name
   */
  public void setUser(String value) {
    m_User = value;
    reset();
  }

  /**
   * Returns the FTP user name to use.
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
    return "The FTP user to use for connecting.";
  }

  /**
   * Sets the FTP password to use.
   *
   * @param value	the password
   */
  public void setPassword(BasePassword value) {
    m_Password = value;
    reset();
  }

  /**
   * Returns the FTP password to use.
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
    return "The password of the FTP user to use for connecting.";
  }

  /**
   * Sets whether to use passive mode.
   *
   * @param value	if true passive mode is used
   */
  public void setUsePassiveMode(boolean value) {
    m_UsePassiveMode = value;
    reset();
  }

  /**
   * Returns whether passive mode is used.
   *
   * @return		true if passive mode is used
   */
  public boolean getUsePassiveMode() {
    return m_UsePassiveMode;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String usePassiveModeTipText() {
    return "If enabled, passive mode is used instead.";
  }

  /**
   * Sets whether to use binary mode.
   *
   * @param value	if true binary mode is used
   */
  public void setUseBinaryMode(boolean value) {
    m_UseBinaryMode = value;
    reset();
  }

  /**
   * Returns whether binary mode is used.
   *
   * @return		true if binary mode is used
   */
  public boolean getUseBinaryMode() {
    return m_UseBinaryMode;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useBinaryModeTipText() {
    return "If enabled, binary mode is used instead of ASCII.";
  }

  /**
   * Sets the remote directory.
   *
   * @param value	the remote directory
   */
  public void setRemoteDir(String value) {
    m_RemoteDir = value;
    reset();
  }

  /**
   * Returns the remote directory.
   *
   * @return		the remote directory.
   */
  public String getRemoteDir() {
    return m_RemoteDir;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String remoteDirTipText() {
    return "The SFTP directory to download the file from.";
  }

  /**
   * Returns the FTP client.
   *
   * @return		the FTP client, null if not connected
   */
  public synchronized FTPClient getClient() {
    if (m_Client == null)
      m_Client = newClient();
    return m_Client;
  }

  /**
   * Returns a new client for the host/port defined in the options.
   *
   * @return		the client
   */
  public FTPClient newClient() {
    FTPClient   result;
    int		reply;

    try {
      result = new FTPClient();
      result.addProtocolCommandListener(this);
      result.connect(m_Host);
      reply = result.getReplyCode();
      if (!FTPReply.isPositiveCompletion(reply)) {
	getLogger().severe("FTP server refused connection: " + reply);
      }
      else {
	if (!result.login(m_User, m_Password.getValue())) {
	  getLogger().severe("Failed to connect to '" + m_Host + "' as user '" + m_User + "'");
	}
	else {
	  if (m_UsePassiveMode)
	    result.enterLocalPassiveMode();
	  if (m_UseBinaryMode)
	    result.setFileType(FTPClient.BINARY_FILE_TYPE);
	}
      }
    }
    catch (Exception e) {
      Utils.handleException(this, "Failed to connect to '" + m_Host + "' as user '" + m_User + "': ", e);
      result = null;
    }

    return result;
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    if (m_Client != null) {
      if (m_Client.isConnected()) {
	try {
	  m_Client.disconnect();
	}
	catch (Exception e) {
	  getLogger().log(Level.SEVERE, "Failed to disconnect from '" + m_Host + "':", e);
	}
	m_Client.removeProtocolCommandListener(this);
      }
    }
    m_Client = null;
  }

  /***
   * This method is invoked by a ProtocolCommandEvent source after
   * sending a protocol command to a server.
   *
   * @param event The ProtocolCommandEvent fired.
   */
  public void protocolCommandSent(ProtocolCommandEvent event) {
    if (isLoggingEnabled())
      getLogger().info("cmd sent: " + event.getCommand() + "/" + event.getReplyCode());
    else if (event.getReplyCode() >= 400)
      getLogger().severe("cmd sent: " + event.getCommand() + "/" + event.getReplyCode());
  }

  /***
   * This method is invoked by a ProtocolCommandEvent source after
   * receiving a reply from a server.
   *
   * @param event The ProtocolCommandEvent fired.
   */
  public void protocolReplyReceived(ProtocolCommandEvent event) {
    if (isLoggingEnabled())
      getLogger().info("reply received: " + event.getMessage() + "/" + event.getReplyCode());
    else if (event.getReplyCode() >= 400)
      getLogger().severe("reply received: " + event.getMessage() + "/" + event.getReplyCode());
  }

  /**
   * Returns whether the setup needs to be configured by the user or whether
   * it can be used straight away.
   *
   * @return		true if user needs to configure first
   */
  public boolean requiresInitialization() {
    return true;
  }
}
