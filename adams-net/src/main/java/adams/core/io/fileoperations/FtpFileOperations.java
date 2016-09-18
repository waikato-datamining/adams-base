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
 * FtpFileOperations.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core.io.fileoperations;

import adams.core.Utils;
import adams.core.io.FileUtils;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * FTP file operations.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
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
	  result = Utils.handleException(this, "Failed to upload file '" + inFile + "' to '" + remoteFile + "': ", e);
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
	  result = Utils.handleException(this, "Failed to download file '" + remoteFile + "' to '" + outFile + "': ", e);
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
   * Renames a (remote) file.
   *
   * @param source	the source file (old)
   * @param target	the target file (new)
   * @return		null if successful, otherwise error message
   */
  public String rename(String source, String target) {
    try {
      m_Client.rename(source, target);
    }
    catch (Exception e) {
      return Utils.handleException(this, "Failed to rename file: " + source + " -> " + target, e);
    }
    return null;
  }

  /**
   * Deletes a (remote) file.
   *
   * @param file	the file to delete
   * @return		null if successful, otherwise error message
   */
  public String delete(String file) {
    try {
      m_Client.deleteFile(file);
    }
    catch (Exception e) {
      return Utils.handleException(this, "Failed to delete file: " + file, e);
    }
    return null;
  }
}
