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
 * SmbFileWrapper.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core.io;

import jcifs.smb.SmbFile;

import java.io.File;
import java.util.Date;

/**
 * Wrapper for remote SMB files.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SmbFileObject
  implements FileObject {

  private static final long serialVersionUID = -1391761454087211261L;

  /** the actual file. */
  protected SmbFile m_File;

  /** the length. */
  protected Long m_Length;

  /** whether a directory. */
  protected Boolean m_Directory;

  /** the modified date. */
  protected Date m_LastModified;

  /** whether the file is hidden. */
  protected Boolean m_Hidden;

  /**
   * Initializes the wrapper.
   *
   * @param file	the file
   */
  public SmbFileObject(SmbFile file) {
    m_File = file;
  }

  /**
   * Returns the wrapped file.
   *
   * @return		the file
   */
  @Override
  public File getFile() {
    return new File(m_File.getUncPath());
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
  public synchronized long getLength() {
    if (m_Length == null) {
      try {
	m_Length = m_File.length();
      }
      catch (Exception e) {
	m_Length = -1L;
      }
    }
    return m_Length;
  }

  /**
   * Returns whether the file represents a directory.
   *
   * @return		true if directory
   */
  @Override
  public synchronized boolean isDirectory() {
    if (m_Directory == null) {
      try {
	m_Directory = m_File.isDirectory();
      }
      catch (Exception e) {
	m_Directory = false;
      }
    }
    return m_Directory;
  }

  /**
   * Returns the date when the file was last modified.
   *
   * @return		date when last modified
   */
  @Override
  public synchronized Date getLastModified() {
    if (m_LastModified == null) {
      try {
	m_LastModified = new Date(m_File.lastModified());
      }
      catch (Exception e) {
	m_LastModified = new Date(0L);
      }
    }
    return m_LastModified;
  }

  /**
   * Returns whether the file is hidden.
   *
   * @return		true if hidden
   */
  @Override
  public synchronized boolean isHidden() {
    if (m_Hidden == null) {
      try {
	m_Hidden = m_File.isHidden();
      }
      catch (Exception e) {
	m_Hidden = false;
      }
    }
    return m_Hidden;
  }

  /**
   * Returns whether the file represents a link.
   *
   * @return		true if link
   */
  @Override
  public boolean isLink() {
    return false;
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
    return m_File.toString();
  }
}
