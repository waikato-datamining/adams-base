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

/*
 * FTPConnection.java
 * Copyright (C) 2016-2019 University of Waikato, Hamilton, NZ
 */

package adams.scripting.connection;

import adams.core.MessageCollection;
import adams.core.PasswordSupporter;
import adams.core.QuickInfoHelper;
import adams.core.base.BasePassword;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.TempUtils;
import adams.core.logging.LoggingHelper;
import adams.scripting.command.RemoteCommand;
import adams.scripting.processor.RemoteCommandProcessor;
import org.apache.commons.net.ProtocolCommandEvent;
import org.apache.commons.net.ProtocolCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.logging.Level;

/**
 * Uses FTP to send commands.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class FTPConnection
  extends AbstractConnection
  implements ProtocolCommandListener, PasswordSupporter {

  private static final long serialVersionUID = 7719866884762680511L;

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

  /** the directory to upload the file to. */
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
    return "Uses an FTP to transfer commands.";
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
      "anonymous").dontOutputDefaultValue();

    m_OptionManager.add(
      "password", "password",
      new BasePassword("")).dontOutputDefaultValue();

    m_OptionManager.add(
      "passive", "usePassiveMode",
      false);

    m_OptionManager.add(
      "binary", "useBinaryMode",
      false);

    m_OptionManager.add(
      "remote-dir", "remoteDir",
      "/pub");
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
    return "The FTP directory to upload the command to.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    String  	result;

    result = QuickInfoHelper.toString(this, "user", (m_User.isEmpty() ? "-none-" : m_User));
    result += QuickInfoHelper.toString(this, "host", m_Host, "@");

    return result;
  }

  /**
   * Starts up a FTP session.
   *
   * @return		null if OK, otherwise error message
   */
  protected String connect() {
    String	result;
    int		reply;

    result = null;

    try {
      m_Client = new FTPClient();
      m_Client.addProtocolCommandListener(this);
      m_Client.connect(m_Host);
      reply = m_Client.getReplyCode();
      if (!FTPReply.isPositiveCompletion(reply)) {
	result = "FTP server refused connection: " + reply;
      }
      else {
	if (!m_Client.login(m_User, m_Password.getValue())) {
	  result = "Failed to connect to '" + m_Host + "' as user '" + m_User + "'";
	}
	else {
	  if (m_UsePassiveMode)
	    m_Client.enterLocalPassiveMode();
	  if (m_UseBinaryMode)
	    m_Client.setFileType(FTPClient.BINARY_FILE_TYPE);
	}
      }
    }
    catch (Exception e) {
      result   = LoggingHelper.handleException(this, "Failed to connect to '" + m_Host + "' as user '" + m_User + "': ", e);
      m_Client = null;
    }

    return result;
  }

  /**
   * Disconnects the FTP session, if necessary.
   */
  protected void disconnect() {
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

  /**
   * Returns the FTP client object.
   *
   * @return		the FTP client, null if failed to connect
   */
  protected FTPClient getFTPClient() {
    if (m_Client == null)
      connect();

    return m_Client;
  }

  /**
   * Sends the command to the specified sscripting engine.
   *
   * @param cmd		the command to send
   * @param processor 	for formatting/parsing
   * @return		null if successfully sent, otherwise error message
   */
  protected String doSend(RemoteCommand cmd, RemoteCommandProcessor processor) {
    String		result;
    FTPClient		client;
    File 		tmpfile;
    String		remotefile;
    FileInputStream 	fis;
    BufferedInputStream stream;
    MessageCollection 	errors;

    result = null;

    // save command to tmp file
    tmpfile = TempUtils.createTempFile("remote", ".rc");
    errors  = new MessageCollection();
    if (!processor.write(cmd, tmpfile, errors))
      result = "Failed to write command to: " + tmpfile + "\n" + errors;

    // send tmp file via FTP
    if (result == null) {
      remotefile = TempUtils.createTempFile(new PlaceholderDirectory(m_RemoteDir), "remote", ".rc").getAbsolutePath();
      client     = getFTPClient();
      stream     = null;
      fis        = null;
      try {
	if (isLoggingEnabled())
	  getLogger().info("Uploading " + tmpfile + " to " + remotefile);
	fis    = new FileInputStream(tmpfile.getAbsoluteFile());
	stream = new BufferedInputStream(fis);
	if (!client.storeFile(remotefile, stream))
	  result = "Failed to upload file, check console for error message!";
      }
      catch (Exception e) {
	result = LoggingHelper.handleException(this, "Failed to upload file '" + tmpfile + "' to '" + remotefile + "': ", e);
      }
      finally {
	FileUtils.closeQuietly(stream);
	FileUtils.closeQuietly(fis);
      }
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
  protected String doSendRequest(RemoteCommand cmd, RemoteCommandProcessor processor) {
    return doSend(cmd, processor);
  }

  /**
   * Sends the response command.
   *
   * @param cmd		the command to send
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doSendResponse(RemoteCommand cmd, RemoteCommandProcessor processor) {
    return doSend(cmd, processor);
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
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    disconnect();
    super.cleanUp();
  }
}
