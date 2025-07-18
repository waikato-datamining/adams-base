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
 * LocalFileObject.java
 * Copyright (C) 2016-2025 University of Waikato, Hamilton, NZ
 */

package adams.core.io;

import java.io.File;
import java.nio.file.Files;
import java.util.Date;

/**
 * Wraps a local file and avoids costly API calls by caching values.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class LocalFileObject
  implements FileObject {

  private static final long serialVersionUID = -9056432057204433829L;

  /** the wrapped file. */
  protected File m_File;

  /** the length. */
  protected Long m_Length;

  /** whether a directory. */
  protected Boolean m_Directory;

  /** the modified date. */
  protected Date m_LastModified;

  /** whether the file is hidden. */
  protected Boolean m_Hidden;

  /** whether the file is a link. */
  protected Boolean m_Link;

  /**
   * Initializes the wrapper.
   *
   * @param file	the file to wrap
   */
  public LocalFileObject(File file) {
    m_File = file.getAbsoluteFile();
  }

  /**
   * Returns the parent, if available.
   *
   * @return		the parent or null if not available
   */
  @Override
  public FileObject getParent() {
    File	file;
    try {
      if (m_File.getName().equals(".."))
	return new LocalFileObject(m_File.getCanonicalFile());
      file = m_File.getAbsoluteFile();
      if (file.getParentFile() != null)
	return new LocalFileObject(file.getParentFile());
    }
    catch (Exception e) {
      // ignored
    }
    return null;
  }

  /**
   * Returns the wrapped file.
   *
   * @return		the file
   */
  @Override
  public File getFile() {
    return m_File;
  }

  /**
   * Returns the actual target (if possible) in case of a link.
   *
   * @return		the actual file
   */
  @Override
  public File getActualFile() {
    if (isLink()) {
      try {
	return Files.readSymbolicLink(m_File.toPath()).toFile();
      }
      catch (Exception e) {
	return m_File;
      }
    }
    else {
      return m_File;
    }
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
    if (m_Length == null)
      m_Length = m_File.length();
    return m_Length;
  }

  /**
   * Returns whether the file represents a directory.
   *
   * @return		true if directory
   */
  @Override
  public boolean isDirectory() {
    if (m_Directory == null)
      m_Directory = m_File.isDirectory();
    return m_Directory;
  }

  /**
   * Returns the date when the file was last modified.
   *
   * @return		date when last modified
   */
  @Override
  public Date getLastModified() {
    if (m_LastModified == null)
      m_LastModified = new Date(m_File.lastModified());
    return m_LastModified;
  }

  /**
   * Returns whether the file is hidden.
   *
   * @return		true if hidden
   */
  @Override
  public boolean isHidden() {
    if (m_Hidden == null)
      m_Hidden = m_File.isHidden();
    return m_Hidden;
  }

  /**
   * Returns whether the file represents a link.
   *
   * @return		true if link
   */
  @Override
  public boolean isLink() {
    if (m_Link == null)
      m_Link = Files.isSymbolicLink(m_File.toPath());
    return m_Link;
  }

  /**
   * Returns whether the file is a local file.
   *
   * @return		true if local
   */
  @Override
  public boolean isLocal() {
    return true;
  }

  /**
   * Returns just the file's string representation.
   *
   * @return		the string representation
   */
  @Override
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
