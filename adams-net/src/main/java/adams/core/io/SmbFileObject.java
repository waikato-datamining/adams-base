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
 * SmbFileObject.java
 * Copyright (C) 2016-2025 University of Waikato, Hamilton, NZ
 */

package adams.core.io;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;

import java.io.File;
import java.util.Date;

/**
 * Wrapper for remote SMB files.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
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
   * Returns the parent, if available.
   *
   * @return		the parent or null if not available
   */
  @Override
  public FileObject getParent() {
    // at the root "smb://"?
    if (m_File.getParent().equals(m_File.getCanonicalPath())) {
      return null;
    }
    else {
      try {
	return new SmbFileObject(new SmbFile(m_File.getParent(), (NtlmPasswordAuthentication) m_File.getPrincipal()));
      }
      catch (Exception e) {
	return null;
      }
    }
  }

  /**
   * Returns the wrapped file.
   *
   * @return		the file as UNC path
   */
  @Override
  public File getFile() {
    return new File(m_File.getUncPath());
  }

  /**
   * Returns the actual target (if possible) in case of a link.
   *
   * @return		the actual file as UNC path
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
   * @return		the long name, not UNC path!
   */
  public String toString() {
    return m_File.toString();
  }

  /**
   * Returns whether this file object is the same as the provided one.
   *
   * @param o		the file object to compare against
   * @return		less than zero, equal to zero, greater than zero if
   * 			this file object is less than, equal to, or greater
   * 			than the other file object
   */
  @Override
  public int compareTo(FileObject o) {
    return getFile().compareTo(o.getFile());
  }

  /**
   * Checks whether this object is the same as the provided one.
   *
   * @param obj		the object to compare against
   * @return		true if the same
   * @see		#compareTo(FileObject)
   */
  @Override
  public boolean equals(Object obj) {
    return (obj instanceof FileObject) && (compareTo((FileObject) obj) == 0);
  }
}
