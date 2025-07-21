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

import adams.core.net.SMB;
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation;
import com.hierynomus.smbj.share.DiskShare;

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

  /** the disk share. */
  protected DiskShare m_Share;

  /** the parent dir. */
  protected String m_ParentDir;

  /** the actual file. */
  protected FileIdBothDirectoryInformation m_File;

  /** the name to use (overrides m_File). */
  protected String m_Name;

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
   * @param share 	the share to use
   * @param parentDir 	the parent dir
   * @param file	the file
   */
  public SmbFileObject(DiskShare share, String parentDir, FileIdBothDirectoryInformation file) {
    this(share, parentDir, file, null);
  }

  /**
   * Initializes the wrapper.
   *
   * @param share 	the share to use
   * @param parentDir 	the parent dir
   * @param file	the file
   * @param name 	the name to override the one from "file"
   */
  public SmbFileObject(DiskShare share, String parentDir, FileIdBothDirectoryInformation file, String name) {
    m_Share     = share;
    m_ParentDir = parentDir;
    m_File      = file;
    m_Name      = name;
  }

  /**
   * Returns the parent, if available.
   *
   * @return		the parent or null if not available
   */
  @Override
  public FileObject getParent() {
    SmbFileObject	result;
    String		parent;

    result = null;

    if (m_ParentDir.contains("/")) {
      parent = SMB.getParent(m_ParentDir);
      result = new SmbFileObject(m_Share, parent, null);
    }

    return result;
  }

  /**
   * Returns the current parent directory.
   *
   * @return		the dir
   */
  public String getParentDir() {
    return m_ParentDir;
  }

  /**
   * Returns the underlying disk share.
   *
   * @return		the share
   */
  public DiskShare getShare() {
    return m_Share;
  }

  /**
   * Returns the wrapped file.
   *
   * @return		the file as UNC path
   */
  @Override
  public File getFile() {
    if (m_File != null)
      return new File(m_File.getFileName());
    else
      return null;
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
    if (m_Name != null)
      return m_Name;
    else if (m_File != null)
      return m_File.getFileName();
    else
      return "";
  }

  /**
   * Returns the size of the file.
   *
   * @return		the size
   */
  @Override
  public synchronized long getLength() {
    if (m_Length == null) {
      if (m_File != null) {
	try {
	  m_Length = m_File.getEndOfFile();
	}
	catch (Exception e) {
	  m_Length = -1L;
	}
      }
      else {
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
      if (m_File != null) {
	try {
	  m_Directory = SMB.isDirectory(m_File);
	}
	catch (Exception e) {
	  m_Directory = false;
	}
      }
      else {
	m_Directory = m_ParentDir.endsWith("/");
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
      if (m_File != null) {
	try {
	  m_LastModified = m_File.getLastWriteTime().toDate();
	}
	catch (Exception e) {
	  m_LastModified = new Date(0L);
	}
      }
      else {
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
      if (m_File != null) {
	try {
	  m_Hidden = SMB.isHidden(m_File);
	}
	catch (Exception e) {
	  m_Hidden = false;
	}
      }
      else {
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
    if (m_Name != null)
      return m_ParentDir + m_Name;
    else if (m_File != null)
      return m_ParentDir + m_File.getFileName();
    else
      return m_ParentDir;
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
