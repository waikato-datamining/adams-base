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
 * FtpFileObject.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core.io;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.util.Date;

/**
 * Wrapper for remote FTP files.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FtpFileObject
  implements FileObject {

  private static final long serialVersionUID = -1391761454087211261L;

  /** the FTP client object. */
  protected FTPClient m_Client;

  /** the parent directory. */
  protected File m_ParentDir;

  /** the underlying file. */
  protected transient FTPFile m_File;

  /**
   * Initializes the wrapper.
   *
   * @param parentDir	the parent directory
   * @param file	the file/dir to wrap
   * @param client	the client
   */
  public FtpFileObject(File parentDir, FTPFile file, FTPClient client) {
    m_ParentDir = parentDir;
    m_File      = file;
    m_Client    = client;
  }

  /**
   * Returns the client.
   *
   * @return		the client
   */
  public FTPClient getClient() {
    return m_Client;
  }

  /**
   * Returns the parent directory.
   *
   * @return		the parent
   */
  public File getParentDir() {
    return m_ParentDir;
  }

  /**
   * Returns the wrapped file.
   *
   * @return		the file
   */
  @Override
  public File getFile() {
    return new File(m_ParentDir + "/" + m_File.getName());
  }

  /**
   * Returns the actual target (if possible) in case of a link.
   *
   * @return		the actual file
   */
  @Override
  public File getActualFile() {
    return getFile();
  }

  /**
   * Returns the file name.
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return m_File.getName();
  }

  /**
   * Returns the size of the file.
   *
   * @return		the size
   */
  @Override
  public long getLength() {
    return m_File.getSize();
  }

  /**
   * Returns whether the file represents a directory.
   *
   * @return		true if directory
   */
  @Override
  public boolean isDirectory() {
    return m_File.isDirectory();
  }

  /**
   * Returns the date when the file was last modified.
   *
   * @return		date when last modified
   */
  @Override
  public Date getLastModified() {
    return new Date(m_File.getTimestamp().getTimeInMillis());
  }

  /**
   * Returns whether the file is hidden.
   *
   * @return		true if hidden
   */
  @Override
  public boolean isHidden() {
    // bit hacky, assuming Linux FS
    return getName().startsWith(".") && !isDirectory();
  }

  /**
   * Returns whether the file represents a link.
   *
   * @return		true if link
   */
  @Override
  public boolean isLink() {
    return m_File.isSymbolicLink();
  }

  /**
   * Returns whether the file is a local file.
   *
   * @return		true if local
   */
  @Override
  public boolean isLocal() {
    return false;
  }

  /**
   * Returns the long name.
   *
   * @return		the long name
   */
  public String toString() {
    return getFile().toString();
  }
}
