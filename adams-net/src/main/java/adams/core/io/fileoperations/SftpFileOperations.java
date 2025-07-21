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
 * SftpFileOperations.java
 * Copyright (C) 2016-2025 University of Waikato, Hamilton, NZ
 */

package adams.core.io.fileoperations;

import adams.core.io.PlaceholderFile;
import adams.core.io.SftpFileObject;
import adams.core.io.lister.DirectoryLister;
import adams.core.io.lister.SftpDirectoryLister;
import adams.core.io.lister.Sorting;
import adams.core.logging.LoggingHelper;
import adams.core.net.SSHSessionProvider;
import com.jcraft.jsch.ChannelSftp;

/**
 * SFTP file operations.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SftpFileOperations
  extends AbstractRemoteFileOperations {

  private static final long serialVersionUID = -4668267794023495691L;

  /** the session provider to use. */
  protected SSHSessionProvider m_Provider;

  /**
   * Sets the SSH session provider to use.
   *
   * @param value	the provider
   */
  public void setProvider(SSHSessionProvider value) {
    m_Provider = value;
  }

  /**
   * Returns the SSH session provider in use.
   *
   * @return		the provider, null if none set
   */
  public SSHSessionProvider getProvider() {
    return m_Provider;
  }

  /**
   * Checks whether the given operation is supported.
   *
   * @param op		the operation to check
   * @return		true if supported
   */
  public boolean isSupported(Operation op) {
    switch (op) {
      case COPY:
      case MOVE:
      case RENAME:
      case DELETE:
      case MKDIR:
	return true;
      case DUPLICATE:
	return false;
      default:
	throw new IllegalStateException("Unhandled operation: " + op);
    }
  }

  /**
   * Copies a file.
   *
   * @param source	the source file
   * @param target	the target file
   * @return		null if successful, otherwise error message
   */
  protected String copyFile(String source, String target) {
    String		result;
    ChannelSftp 	channel;

    result  = null;
    channel = null;

    switch (m_Direction) {
      case LOCAL_TO_REMOTE:
	try {
	  if ((m_Provider.getSession() != null) && m_Provider.getSession().isConnected()) {
	    channel = (ChannelSftp) m_Provider.getSession().openChannel("sftp");
	    channel.connect();
	    if (isLoggingEnabled())
	      getLogger().info("Uploading " + source + " to " + target);
	    channel.put(new PlaceholderFile(source).getAbsolutePath(), target);
	  }
	  else {
	    getLogger().severe("No active connection (copy/" + m_Direction + ")!");
	  }
	}
	catch (Exception e) {
	  result = LoggingHelper.handleException(this, "Failed to upload file '" + source + "' to '" + target + "': ", e);
	}
	finally {
	  if (channel != null) {
	    channel.disconnect();
	  }
	}
	break;

      case REMOTE_TO_LOCAL:
	try {
	  if ((m_Provider.getSession() != null) && m_Provider.getSession().isConnected()) {
	    channel = (ChannelSftp) m_Provider.getSession().openChannel("sftp");
	    channel.connect();
	    if (isLoggingEnabled())
	      getLogger().info("Downloading " + source);
	    channel.get(source, new PlaceholderFile(target).getAbsolutePath());
	    channel.disconnect();
	  }
	  else {
	    getLogger().severe("No active connection (copy/" + m_Direction + ")!");
	  }
	}
	catch (Exception e) {
	  result = LoggingHelper.handleException(this, "Failed to download file '" + source + "' to '" + target + "': ", e);
	}
	finally {
	  if (channel != null) {
	    channel.disconnect();
	  }
	}
	break;

      default:
	throw new IllegalStateException("Unhandled direction: " + m_Direction);
    }

    return result;
  }

  /**
   * Returns an instance of the remote directory lister.
   *
   * @return		the directory lister
   */
  @Override
  protected DirectoryLister newRemoteDirectoryLister() {
    SftpDirectoryLister	result;

    result = new SftpDirectoryLister();
    result.setSessionProvider(m_Provider);

    return result;
  }

  /**
   * Renames a remote file/dir.
   *
   * @param source	the source file/dir (old)
   * @param target	the target file/dir (new)
   * @return		null if successful, otherwise error message
   */
  protected String renameRemote(String source, String target) {
    ChannelSftp 	channel;

    channel = null;
    try {
      if ((m_Provider.getSession() != null) && m_Provider.getSession().isConnected()) {
	channel = (ChannelSftp) m_Provider.getSession().openChannel("sftp");
	channel.connect();
	if (isLoggingEnabled())
	  getLogger().info("Renaming " + source + " to " + target);
	channel.rename(source, target);
	channel.disconnect();
      }
      else {
        getLogger().severe("No active connection (renameRemote)!");
      }
    }
    catch (Exception e) {
      return LoggingHelper.handleException(this, "Failed to rename file: " + source + " -> " + target, e);
    }
    finally {
      if (channel != null) {
	channel.disconnect();
      }
    }

    return null;
  }

  /**
   * Deletes a remote dir. Must be empty.
   *
   * @param channel 	the channel to use
   * @param path	the file/dir to delete
   * @return		null if successful, otherwise error message
   */
  protected String deleteRemoteDir(ChannelSftp channel, String path) {
    try {
      if (isLoggingEnabled())
	getLogger().info("Deleting " + path);
      channel.rmdir(path);
      return null;
    }
    catch (Exception e) {
      return LoggingHelper.handleException(this, "Failed to delete dir: " + path, e);
    }
  }

  /**
   * Deletes a remote file.
   *
   * @param channel 	the channel to use
   * @param path	the file/dir to delete
   * @return		null if successful, otherwise error message
   */
  protected String deleteRemoteFile(ChannelSftp channel, String path) {
    try {
      if (isLoggingEnabled())
	getLogger().info("Deleting " + path);
      channel.rm(path);
      return null;
    }
    catch (Exception e) {
      return LoggingHelper.handleException(this, "Failed to delete file: " + path, e);
    }
  }

  /**
   * Deletes a remote file/dir.
   *
   * @param path	the file/dir to delete
   * @return		null if successful, otherwise error message
   */
  protected String deleteRemote(String path) {
    ChannelSftp 	channel;
    boolean		isDir;
    SftpDirectoryLister	lister;
    SftpFileObject[]	files;
    String		msg;
    int			i;

    channel = null;
    isDir   = isDirRemote(path);
    try {
      if ((m_Provider.getSession() != null) && m_Provider.getSession().isConnected()) {
	channel = (ChannelSftp) m_Provider.getSession().openChannel("sftp");
	channel.connect();
	if (isDir) {
	  // list all files/dirs
	  lister = new SftpDirectoryLister();
	  lister.setWatchDir(path);
	  lister.setSessionProvider(m_Provider);
	  lister.setRecursive(true);
	  lister.setListFiles(true);
	  lister.setListDirs(true);
	  lister.setSorting(Sorting.SORT_BY_NAME);
	  files = lister.listObjects();
	  // delete files to empty the dirs
	  for (SftpFileObject file: files) {
	    if (file.isDirectory())
	      continue;
	    msg = deleteRemoteFile(channel, file.getFile().getAbsolutePath());
	    if (msg != null)
	      return msg;
	  }
	  // delete dirs in reverse order
	  for (i = files.length - 1; i >= 0; i--) {
	    if (files[i].isDirectory()) {
	      msg = deleteRemoteDir(channel, files[i].getFile().getAbsolutePath());
	      if (msg != null)
		return msg;
	    }
	  }
	  // delete uppermost dir
	  channel.rmdir(path);
	}
	else {
	  deleteRemoteFile(channel, path);
	}
	channel.disconnect();
      }
      else {
        getLogger().severe("No active connection (deleteRemote)!");
      }
    }
    catch (Exception e) {
      return LoggingHelper.handleException(this, "Failed to delete file: " + path, e);
    }
    finally {
      if (channel != null) {
	channel.disconnect();
      }
    }

    return null;
  }

  /**
   * Creates the remote directory.
   *
   * @param dir		the directory to create
   * @return		null if successful, otherwise error message
   */
  protected String mkdirRemote(String dir) {
    ChannelSftp 	channel;

    channel = null;
    try {
      if ((m_Provider.getSession() != null) && m_Provider.getSession().isConnected()) {
	channel = (ChannelSftp) m_Provider.getSession().openChannel("sftp");
	channel.connect();
	if (isLoggingEnabled())
	  getLogger().info("Creating directory " + dir);
	channel.mkdir(dir);
	channel.disconnect();
      }
      else {
        getLogger().severe("No active connection (mkdirRemote)!");
      }
    }
    catch (Exception e) {
      return LoggingHelper.handleException(this, "Failed to create directory: " + dir, e);
    }
    finally {
      if (channel != null) {
	channel.disconnect();
      }
    }

    return null;
  }

  /**
   * Checks whether the remote path is a directory.
   *
   * @param path	the path to check
   * @return		true if path exists and is a directory
   */
  protected boolean isDirRemote(String path) {
    boolean		result;
    ChannelSftp 	channel;

    result  = false;
    channel = null;
    try {
      if ((m_Provider.getSession() != null) && m_Provider.getSession().isConnected()) {
	channel = (ChannelSftp) m_Provider.getSession().openChannel("sftp");
	channel.connect();
	if (isLoggingEnabled())
	  getLogger().info("Checking directory " + path);
	result = channel.lstat(path).isDir();
	channel.disconnect();
      }
      else {
	getLogger().severe("No active connection (mkdirRemote)!");
      }
    }
    catch (Exception e) {
      LoggingHelper.handleException(this, "Failed to check directory: " + path, e);
    }
    finally {
      if (channel != null) {
	channel.disconnect();
      }
    }

    return result;
  }
}
