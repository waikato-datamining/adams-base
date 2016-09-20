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
 * SftpFileOperations.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core.io.fileoperations;

import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.net.SSHSessionProvider;
import com.jcraft.jsch.ChannelSftp;

/**
 * SFTP file operations.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
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
  public String copy(String source, String target) {
    String		result;
    ChannelSftp 	channel;

    result  = null;
    channel = null;

    switch (m_Direction) {
      case LOCAL_TO_REMOTE:
	try {
	  channel = (ChannelSftp) m_Provider.getSession().openChannel("sftp");
	  channel.connect();
	  if (isLoggingEnabled())
	    getLogger().info("Uploading " + source + " to " + target);
	  channel.put(new PlaceholderFile(source).getAbsolutePath(), target);
	}
	catch (Exception e) {
	  result = Utils.handleException(this, "Failed to upload file '" + source + "' to '" + target + "': ", e);
	}
	finally {
	  if (channel != null) {
	    channel.disconnect();
	  }
	}
	break;

      case REMOTE_TO_LOCAL:
	try {
	  channel = (ChannelSftp) m_Provider.getSession().openChannel("sftp");
	  channel.connect();
	  if (isLoggingEnabled())
	    getLogger().info("Downloading " + source);
	  channel.get(source, new PlaceholderFile(target).getAbsolutePath());
	  channel.disconnect();
	}
	catch (Exception e) {
	  result = Utils.handleException(this, "Failed to download file '" + source + "' to '" + target + "': ", e);
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
   * Moves a file.
   *
   * @param source	the source file
   * @param target	the target file
   * @return		null if successful, otherwise error message
   */
  public String move(String source, String target) {
    String	result;

    result = copy(source, target);

    if (result == null) {
      switch (m_Direction) {
	case LOCAL_TO_REMOTE:
	  if (!FileUtils.delete(source))
	    result = "Failed to delete: " + source;
	  break;

	case REMOTE_TO_LOCAL:
	  result = delete(source);
	  break;

	default:
	  throw new IllegalStateException("Unhandled direction: " + m_Direction);
      }
    }

    return result;
  }

  /**
   * Renames a remote file.
   *
   * @param source	the source file (old)
   * @param target	the target file (new)
   * @return		null if successful, otherwise error message
   */
  protected String renameRemote(String source, String target) {
    ChannelSftp 	channel;

    channel = null;
    try {
      channel = (ChannelSftp) m_Provider.getSession().openChannel("sftp");
      channel.connect();
      if (isLoggingEnabled())
	getLogger().info("Renaming " + source + " to " + target);
      channel.rename(source, target);
      channel.disconnect();
    }
    catch (Exception e) {
      return Utils.handleException(this, "Failed to rename file: " + source + " -> " + target, e);
    }
    finally {
      if (channel != null) {
	channel.disconnect();
      }
    }

    return null;
  }

  /**
   * Deletes a remote file.
   *
   * @param file	the file to delete
   * @return		null if successful, otherwise error message
   */
  protected String deleteRemote(String file) {
    ChannelSftp 	channel;

    channel = null;
    try {
      channel = (ChannelSftp) m_Provider.getSession().openChannel("sftp");
      if (isLoggingEnabled())
	getLogger().info("Deleting " + file);
      channel.rm(file);
      channel.disconnect();
    }
    catch (Exception e) {
      return Utils.handleException(this, "Failed to delete file: " + file, e);
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
      channel = (ChannelSftp) m_Provider.getSession().openChannel("sftp");
      if (isLoggingEnabled())
	getLogger().info("Creating directory " + dir);
      channel.mkdir(dir);
      channel.disconnect();
    }
    catch (Exception e) {
      return Utils.handleException(this, "Failed to create directory: " + dir, e);
    }
    finally {
      if (channel != null) {
	channel.disconnect();
      }
    }

    return null;
  }
}
