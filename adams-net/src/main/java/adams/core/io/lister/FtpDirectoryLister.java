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
 * FtpDirectoryLister.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core.io.lister;

import adams.core.Utils;
import adams.core.base.BasePassword;
import adams.core.io.FileObject;
import adams.core.io.FtpFileObject;
import org.apache.commons.net.ProtocolCommandEvent;
import org.apache.commons.net.ProtocolCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Lists files/dirs on a remote server using FTP.
 * The provided client provider takes precedence of the parameters.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FtpDirectoryLister
  extends AbstractRecursiveDirectoryLister
  implements ProtocolCommandListener {

  private static final long serialVersionUID = 2687222234652386893L;

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

  /** the FTP session. */
  protected transient FTPClient m_Client;

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
   * Sets the FTP user to use.
   *
   * @param value	the user name
   */
  public void setUser(String value) {
    m_User = value;
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
   * Sets the FTP password to use.
   *
   * @param value	the password
   */
  public void setPassword(BasePassword value) {
    m_Password = value;
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
   * Sets whether to use passive mode.
   *
   * @param value	if true passive mode is used
   */
  public void setUsePassiveMode(boolean value) {
    m_UsePassiveMode = value;
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
   * Sets whether to use binary mode.
   *
   * @param value	if true binary mode is used
   */
  public void setUseBinaryMode(boolean value) {
    m_UseBinaryMode = value;
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
   * Sets the client to use.
   *
   * @param value	the client
   */
  public void setClient(FTPClient value) {
    m_Client = value;
  }

  /**
   * Returns the current session provider.
   *
   * @return		the client, null if none set
   */
  public FTPClient getClient() {
    return m_Client;
  }

  /**
   * Returns a new client for the host defined in the options.
   *
   * @return		the client, null if failed to create
   */
  protected FTPClient newClient() {
    FTPClient 	result;
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
   * Disconnects the SSH session, if necessary.
   *
   * @param client	the client to disconnect
   */
  protected void disconnect(FTPClient client) {
    if (client != null) {
      if (client.isConnected()) {
	try {
	  client.disconnect();
	}
	catch (Exception e) {
	  Utils.handleException(this, "Failed to disconnect!", e);
	}
      }
    }
  }

  /**
   * Returns whether the directory lister operates locally or remotely.
   *
   * @return		true if local lister
   */
  public boolean isLocal() {
    return false;
  }

  /**
   * Returns whether the watch directory has a parent directory.
   *
   * @return		true if parent directory available
   */
  public boolean hasParentDirectory() {
    return (new File(m_WatchDir).getParentFile() != null);
  }

  /**
   * Returns a new directory relative to the watch directory.
   *
   * @param dir		the directory name
   * @return		the new wrapper
   */
  public FtpFileObject newDirectory(String dir) {
    return newDirectory(m_WatchDir, dir);
  }

  /**
   * Returns a new directory generated from parent and sub-directory.
   *
   * @param parent	the parent directory
   * @param dir		the directory name
   * @return		the new wrapper
   */
  public FtpFileObject newDirectory(String parent, String dir) {
    return new FtpFileObject(parent + "/" + dir, null, m_Client);
  }

  /**
   * Performs the recursive search. Search goes deeper if != 0 (use -1 to
   * start with for infinite search).
   *
   * @param client	the client to use
   * @param current	the current directory
   * @param files	the files collected so far
   * @param depth	the depth indicator (searched no deeper, if 0)
   * @throws Exception	if listing fails
   */
  protected void search(FTPClient client, String current, List<SortContainer> files, int depth) throws Exception {
    List<FTPFile> 	currFiles;
    int			i;
    FTPFile 		entry;
    SortContainer	cont;

    if (depth == 0)
      return;

    if (getDebug())
      getLogger().info("search: current=" + current + ", depth=" + depth);

    client.changeWorkingDirectory(current);
    currFiles = new ArrayList<>();
    currFiles.addAll(Arrays.asList(client.listFiles()));
    if (currFiles.size() == 0) {
      getLogger().severe("No files listed!");
      return;
    }

    for (i = 0; i < currFiles.size(); i++) {
      // do we have to stop?
      if (m_Stopped)
	break;

      entry = currFiles.get(i);

      // directory?
      if (entry.isDirectory()) {
	// ignore "." and ".."
	if (entry.getName().equals(".") || entry.getName().equals(".."))
	  continue;

	// search recursively?
	if (m_Recursive)
	  search(client, current + "/" + entry.getName(), files, depth - 1);

	if (m_ListDirs) {
	  // does name match?
	  if (!m_RegExp.isEmpty() && !m_RegExp.isMatch(entry.getName()))
	    continue;

	  files.add(new SortContainer(new FtpFileObject(current, entry, m_Client), m_Sorting));
	}
      }
      else {
	if (m_ListFiles) {
	  // does name match?
	  if (!m_RegExp.isEmpty() && !m_RegExp.isMatch(entry.getName()))
	    continue;

	  files.add(new SortContainer(new FtpFileObject(current, entry, m_Client), m_Sorting));
	}
      }
    }
  }

  /**
   * Returns the list of files/directories in the watched directory. In case
   * the execution gets stopped, this method returns a 0-length array.
   *
   * @param client	the FTP client to use
   * @return		the list of absolute file/directory names
   * @throws Exception	if listing fails
   */
  public List<FtpFileObject> search(FTPClient client) throws Exception {
    List<FtpFileObject>		result;
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
      search(client, m_WatchDir, list, m_MaxDepth);

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
	  result.add((FtpFileObject) list.get(i).getFile());

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
  public FtpFileObject[] listObjects() {
    List<FtpFileObject> 	result;
    FTPClient			client;

    m_Stopped = false;

    if (m_Client == null)
      client = newClient();
    else
      client = m_Client;

    try {
      result = search(client);
    }
    catch (Exception e) {
      Utils.handleException(this, "Failed to list remote directory!", e);
      result = new ArrayList<>();
    }
    finally {
      if (m_Client == null)
	disconnect(client);
    }

    return result.toArray(new FtpFileObject[result.size()]);
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
}
