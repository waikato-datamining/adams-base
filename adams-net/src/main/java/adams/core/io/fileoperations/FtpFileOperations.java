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
 * FtpFileOperations.java
 * Copyright (C) 2016-2025 University of Waikato, Hamilton, NZ
 */

package adams.core.io.fileoperations;

import adams.core.io.FileUtils;
import adams.core.io.lister.DirectoryLister;
import adams.core.io.lister.FtpDirectoryLister;
import adams.core.logging.LoggingHelper;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * FTP file operations.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class FtpFileOperations
  extends AbstractRemoteFileOperations {

  private static final long serialVersionUID = -4668267794023495691L;

  /** the FTP client to use. */
  protected FTPClient m_Client;

  /**
   * Sets the FTP client to use.
   *
   * @param value	the client
   */
  public void setClient(FTPClient value) {
    m_Client = value;
  }

  /**
   * Returns the FTP client in use.
   *
   * @return		the client, null if none set
   */
  public FTPClient getClient() {
    return m_Client;
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
  protected String copyFile(String source, String target) {
    String			result;
    String 			remoteFile;
    String			outFile;
    String 			inFile;
    BufferedOutputStream	ostream;
    FileOutputStream		fos;
    BufferedInputStream		istream;
    FileInputStream		fis;

    result = null;

    switch (m_Direction) {
      case LOCAL_TO_REMOTE:
	remoteFile = target;
	inFile     = source;
	istream    = null;
	fis        = null;
	try {
	  if (isLoggingEnabled())
	    getLogger().info("Uploading " + inFile + " to " + remoteFile);
	  fis     = new FileInputStream(inFile);
	  istream = new BufferedInputStream(fis);
	  m_Client.storeFile(remoteFile, istream);
	}
	catch (Exception e) {
	  result = LoggingHelper.handleException(this, "Failed to upload file '" + inFile + "' to '" + remoteFile + "': ", e);
	}
	finally {
	  FileUtils.closeQuietly(istream);
	  FileUtils.closeQuietly(fis);
	}
	break;

      case REMOTE_TO_LOCAL:
	remoteFile = source;
	outFile    = target;
	fos        = null;
	ostream    = null;
	try {
	  if (isLoggingEnabled())
	    getLogger().info("Downloading " + remoteFile);
	  fos     = new FileOutputStream(outFile);
	  ostream = new BufferedOutputStream(fos);
	  m_Client.retrieveFile(remoteFile, ostream);
	  ostream.flush();
	  ostream.close();
	}
	catch (Exception e) {
	  result = LoggingHelper.handleException(this, "Failed to download file '" + remoteFile + "' to '" + outFile + "': ", e);
	}
	finally {
	  FileUtils.closeQuietly(ostream);
	  FileUtils.closeQuietly(fos);
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
    FtpDirectoryLister	result;

    result = new FtpDirectoryLister();
    result.setClient(m_Client);

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
    try {
      m_Client.rename(source, target);
    }
    catch (Exception e) {
      return LoggingHelper.handleException(this, "Failed to rename file: " + source + " -> " + target, e);
    }
    return null;
  }

  /**
   * Deletes a remote file/dir.
   *
   * @param path	the file/dir to delete
   * @return		null if successful, otherwise error message
   */
  protected String deleteRemote(String path) {
    boolean		isDir;

    isDir = isDirRemote(path);
    try {
      if (isDir)
	m_Client.rmd(path);
      else
	m_Client.deleteFile(path);
    }
    catch (Exception e) {
      return LoggingHelper.handleException(this, "Failed to delete " + (isDir ? "directory" : "file") + ": " + path, e);
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
      m_Client.makeDirectory(dir);
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
    boolean	result;
    String	oldPath;

    result = false;

    try {
      oldPath = m_Client.printWorkingDirectory();
      result  = (m_Client.cwd(path) == 250);
      m_Client.cwd(oldPath);
    }
    catch (Exception e) {
      LoggingHelper.handleException(this, "Failed to check directory: " + path, e);
    }

    return result;
  }
}
