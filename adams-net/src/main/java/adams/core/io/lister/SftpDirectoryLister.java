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
 * SftpDirectoryLister.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core.io.lister;

import adams.core.Utils;
import adams.core.base.BasePassword;
import adams.core.io.FileObject;
import adams.core.io.PlaceholderFile;
import adams.core.io.SftpFileObject;
import adams.core.net.JSchUtils;
import adams.core.net.SSHAuthenticationType;
import adams.core.net.SSHSessionProvider;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

/**
 * Lists files/dirs on a remote server using SFTP.
 * The provided session provider takes precedence of the parameters.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SftpDirectoryLister
  extends AbstractRecursiveDirectoryLister {

  private static final long serialVersionUID = 2687222234652386893L;

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

  /** the session provider to use. */
  protected SSHSessionProvider m_SessionProvider;

  /** the SSH session. */
  protected transient Session m_Session;

  /**
   * Sets the host to connect to.
   *
   * @param value	the host name/ip
   */
  public void setHost(String value) {
    m_Host = value;
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
   * Sets the port to connect to.
   *
   * @param value	the port
   */
  public void setPort(int value) {
    if ((value > 0) && (value < 65535))
      m_Port = value;
  }

  /**
   * Returns the port to connect to.
   *
   * @return 		the port
   */
  public int getPort() {
    return m_Port;
  }

  /**
   * Sets the type of authentication to use.
   *
   * @param value	the type
   */
  public void setAuthenticationType(SSHAuthenticationType value) {
    m_AuthenticationType = value;
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
   * Sets the SSH user to use.
   *
   * @param value	the user name
   */
  public void setUser(String value) {
    m_User = value;
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
   * Sets the SSH password to use.
   *
   * @param value	the password
   */
  public void setPassword(BasePassword value) {
    m_Password = value;
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
   * Sets the location of the private key file.
   *
   * @param value	the key file
   */
  public void setPrivateKeyFile(PlaceholderFile value) {
    m_PrivateKeyFile = value;
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
   * Sets the passphrase for the private key file, ignored if empty.
   *
   * @param value	the passphrase
   */
  public void setPrivateKeyPassphrase(BasePassword value) {
    m_PrivateKeyPassphrase = value;
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
   * Sets the file with the known hosts.
   *
   * @param value	the file
   */
  public void setKnownHosts(PlaceholderFile value) {
    m_KnownHosts = value;
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
   * Sets whether to perform strict host key checking.
   * NB: only disabled for testing, as it is very insecure to disable it!
   *
   * @param value	if true then strict checking is on
   */
  public void setStrictHostKeyChecking(boolean value) {
    m_StrictHostKeyChecking = value;
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
   * Sets the session provider to use.
   *
   * @param value	the session provider
   */
  public void setSessionProvider(SSHSessionProvider value) {
    m_SessionProvider = value;
  }

  /**
   * Returns the current session provider.
   *
   * @return		the provider, null if none set
   */
  public SSHSessionProvider getSessionProvider() {
    return m_SessionProvider;
  }

  /**
   * Returns a new session for the host/port defined in the options.
   *
   * @return		the session
   */
  protected Session newSession() {
    return newSession(m_Host, m_Port);
  }

  /**
   * Returns a new session for the given host/port.
   *
   * @param host	the host to create the session for
   * @return		the session
   */
  protected Session newSession(String host, int port) {
    Session 	result;
    JSch jsch;
    String	password;

    password = m_Password.getValue();
    try {
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
      result.connect();
    }
    catch (Exception e) {
      Utils.handleException(this, "Failed to establish connection to '" + host + "' (using " + m_AuthenticationType + "): ", e);
      result = null;
    }

    return result;
  }

  /**
   * Disconnects the SSH session, if necessary.
   */
  protected void disconnect() {
    if (m_Session != null) {
      if (m_Session.isConnected()) {
	try {
	  m_Session.disconnect();
	}
	catch (Exception e) {
	  Utils.handleException(this, "Failed to disconnect from '" + m_Host + "':", e);
	}
      }
    }
    m_Session = null;
  }

  /**
   * Returns whether the watch directory has a parent directory.
   *
   * @return		true if parent directory available
   */
  public boolean hasParentDirectory() {
    return (m_WatchDir.getAbsoluteFile().getParentFile() != null);
  }

  /**
   * Returns a new directory relative to the watch directory.
   *
   * @param dir		the directory name
   * @return		the new wrapper
   */
  public SftpFileObject newDirectory(String dir) {
    return new SftpFileObject(new File(m_WatchDir.getAbsolutePath()), dir, true);
  }

  /**
   * Performs the recursive search. Search goes deeper if != 0 (use -1 to
   * start with for infinite search).
   *
   * @param channel	the SFTP channel to use
   * @param current	the current directory
   * @param files	the files collected so far
   * @param depth	the depth indicator (searched no deeper, if 0)
   * @throws Exception	if listing fails
   */
  protected void search(ChannelSftp channel, String current, List<SortContainer> files, int depth) throws Exception {
    Vector 	currFiles;
    int		i;
    LsEntry	entry;

    if (depth == 0)
      return;

    if (getDebug())
      getLogger().info("search: current=" + current + ", depth=" + depth);

    currFiles = channel.ls(current);
    if (currFiles == null) {
      getLogger().severe("No files listed!");
      return;
    }

    for (i = 0; i < currFiles.size(); i++) {
      // do we have to stop?
      if (m_Stopped)
	break;

      if (!(currFiles.get(i) instanceof LsEntry))
	continue;
      entry = (LsEntry) currFiles.get(i);

      // directory?
      if (entry.getAttrs().isDir()) {
	// ignore "." and ".."
	if (entry.getFilename().equals(".") || entry.getFilename().equals(".."))
	  continue;

	// search recursively?
	if (m_Recursive)
	  search(channel, current + "/" + entry.getFilename(), files, depth - 1);

	if (m_ListDirs) {
	  // does name match?
	  if (!m_RegExp.isEmpty() && !m_RegExp.isMatch(entry.getFilename()))
	    continue;

	  files.add(new SortContainer(new SftpFileObject(new File(current), entry), m_Sorting));
	}
      }
      else {
	if (m_ListFiles) {
	  // does name match?
	  if (!m_RegExp.isEmpty() && !m_RegExp.isMatch(entry.getFilename()))
	    continue;

	  files.add(new SortContainer(new SftpFileObject(new File(current), entry), m_Sorting));
	}
      }
    }
  }

  /**
   * Returns the list of files/directories in the watched directory. In case
   * the execution gets stopped, this method returns a 0-length array.
   *
   * @param channel	the SFTP channel to use
   * @return		the list of absolute file/directory names
   * @throws Exception	if listing fails
   */
  public List<SftpFileObject> search(ChannelSftp channel) throws Exception {
    List<SftpFileObject>	result;
    List<SortContainer>		list;
    SortContainer		cont;
    int				i;

    result    = new ArrayList<>();
    m_Stopped = false;

    if (m_ListFiles || m_ListDirs) {
      if (getDebug())
	getLogger().info("watching '" + m_WatchDir + "'");

      if (getDebug())
	getLogger().info("before search(...)");
      list = new ArrayList<>();
      search(channel, m_WatchDir.getAbsolutePath(), list, m_MaxDepth);

      // sort files ascendingly regarding lastModified
      if (getDebug())
	getLogger().info("before obtaining last modified timestamps");

      if (!m_Stopped && (m_Sorting != Sorting.NO_SORTING)) {
	if (getDebug())
	  getLogger().info("before sorting");
	Collections.sort(list);
	if (m_SortDescending) {
	  for (i = 0; i < list.size() / 2; i++) {
	    cont = list.get(i);
	    list.set(i, list.get(list.size() - 1 - i));
	    list.set(list.size() - 1 - i, cont);
	  }
	}
      }

      // match filenames and them to the result
      if (!m_Stopped) {
	if (getDebug())
	  getLogger().info("before matching");
	for (i = 0; i < list.size(); i++) {
	  result.add((SftpFileObject) list.get(i).getFile());

	  // maximum reached?
	  if (m_MaxItems > 0) {
	    if (result.size() == m_MaxItems) {
	      if (getDebug())
		getLogger().info("max size reached");
	      break;
	    }
	  }

	  // do we have to stop?
	  if (m_Stopped)
	    break;
	}
      }
    }

    // do we have to stop?
    if (m_Stopped)
      result.clear();

    return result;
  }

  /**
   * Returns the list of files/directories in the watched directory. In case
   * the execution gets stopped, this method returns a 0-length array.
   *
   * @return		 the list of absolute file/directory names
   */
  @Override
  public String[] list() {
    String[]		result;
    FileObject[]	wrappers;
    int			i;

    wrappers = listObjects();
    result   = new String[wrappers.length];
    for (i = 0; i < wrappers.length; i++)
      result[i] = wrappers[i].toString();

    return result;
  }

  /**
   * Returns the list of files/directories in the watched directory. In case
   * the execution gets stopped, this method returns a 0-length array.
   *
   * @return		 the list of file/directory wrappers
   */
  public SftpFileObject[] listObjects() {
    List<SftpFileObject> 	result;
    ChannelSftp 		channel;

    result    = new ArrayList<>();
    m_Stopped = false;
    if (m_SessionProvider != null)
      m_Session = m_SessionProvider.newSession();
    else
      m_Session = newSession();
    channel   = null;
    if (m_Session != null) {
      try {
	channel = (ChannelSftp) m_Session.openChannel("sftp");
	channel.connect();
	result = search(channel);
      }
      catch (Exception e) {
	Utils.handleException(this, "Failed to list remote directory!", e);
	result = new ArrayList<>();
      }
      finally {
	if (channel != null)
	  channel.disconnect();
      }
    }
    disconnect();

    return result.toArray(new SftpFileObject[result.size()]);
  }
}
