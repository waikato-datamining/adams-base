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
 * SmbFileOperations.java
 * Copyright (C) 2016-2025 University of Waikato, Hamilton, NZ
 */

package adams.core.io.fileoperations;

import adams.core.io.PlaceholderFile;
import adams.core.io.lister.DirectoryLister;
import adams.core.io.lister.SmbDirectoryLister;
import adams.core.logging.LoggingHelper;
import adams.core.net.SMB;
import adams.core.net.SMBSessionProvider;
import com.hierynomus.smbj.share.DiskShare;

/**
 * SMB / Windows share file operations.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SmbFileOperations
  extends AbstractRemoteFileOperations {

  private static final long serialVersionUID = -4668267794023495691L;

  /** the authentication provider to use. */
  protected SMBSessionProvider m_Provider;

  /** the share to access. */
  protected String m_Share;

  /** the diskshare instance. */
  protected transient DiskShare m_DiskShare;

  /**
   * Cleans up SMB resources.
   */
  protected void cleanUpSmb() {
    if (m_DiskShare != null) {
      try {
	m_DiskShare.close();
      }
      catch (Exception e) {
	// ignored
      }
      m_DiskShare = null;
    }
  }

  /**
   * Returns the disk share to use.
   *
   * @return		the share
   */
  protected DiskShare getDiskShare() {
    if (m_DiskShare == null)
      m_DiskShare = (DiskShare) m_Provider.getSession().connectShare(m_Share);
    return m_DiskShare;
  }

  /**
   * Sets the authentication provider to use.
   *
   * @param value	the provider
   */
  public void setProvider(SMBSessionProvider value) {
    m_Provider = value;
    cleanUpSmb();
  }

  /**
   * Returns the authentication provider in use.
   *
   * @return		the provider, null if none set
   */
  public SMBSessionProvider getProvider() {
    return m_Provider;
  }

  /**
   * Sets the share to access.
   *
   * @param value	the share
   */
  public void setShare(String value) {
    m_Share = value;
    cleanUpSmb();
  }

  /**
   * Returns the share to access.
   *
   * @return		the share
   */
  public String getShare() {
    return m_Share;
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

    switch (m_Direction) {
      case LOCAL_TO_REMOTE:
        result = SMB.copyTo(this, m_Provider, new PlaceholderFile(source), getDiskShare(), target);
        break;

      case REMOTE_TO_LOCAL:
        result = SMB.copyFrom(this, m_Provider, getDiskShare(), source, new PlaceholderFile(target));
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
  protected DirectoryLister newRemoteDirectoryLister() {
    SmbDirectoryLister	result;

    result = new SmbDirectoryLister();
    result.setSessionProvider(m_Provider);
    result.setShare(m_Share);

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
    if (getDiskShare().folderExists(source)) {
      // TODO
      return "Not implemented!";
    }
    else if (getDiskShare().fileExists(source)) {
      // TODO
      return "Not implemented!";
    }
    else {
      return "File/dir does not exist: " + source;
    }
  }

  /**
   * Deletes a remote file/dir.
   *
   * @param path	the file/dir to delete
   * @return		null if successful, otherwise error message
   */
  protected String deleteRemote(String path) {
    try {
      if (getDiskShare().folderExists(path))
	getDiskShare().rmdir(path, true);
      else
	getDiskShare().rm(path);
    }
    catch (Exception e) {
      return LoggingHelper.handleException(this, "Failed to delete file/dir: " + path, e);
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
    try {
      getDiskShare().mkdir(dir);
    }
    catch (Exception e) {
      return LoggingHelper.handleException(this, "Failed to create directory: " + dir, e);
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
    try {
      return getDiskShare().folderExists(path);
    }
    catch (Exception e) {
      LoggingHelper.handleException(this, "Failed to check directory: " + path, e);
      return false;
    }
  }
}
