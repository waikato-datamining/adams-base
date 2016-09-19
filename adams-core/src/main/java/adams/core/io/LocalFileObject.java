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
 * LocalFileWrapper.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core.io;

import java.io.File;
import java.nio.file.Files;
import java.util.Date;

/**
 * Wraps a local file and avoids costly API calls by caching values.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LocalFileObject
  implements FileObject {

  private static final long serialVersionUID = -9056432057204433829L;

  /** the wrapped file. */
  protected File m_File;

  /** the length. */
  protected long m_Length;

  /** whether a directory. */
  protected boolean m_Directory;

  /** the modified date. */
  protected Date m_LastModified;

  /** whether the file is hidden. */
  protected boolean m_Hidden;

  /** whether the file is a link. */
  protected boolean m_Link;

  /**
   * Initializes the wrapper.
   *
   * @param file	the file to wrap
   */
  public LocalFileObject(File file) {
    m_File         = file;
    m_Directory    = m_File.isDirectory();
    m_Length       = m_File.length();
    m_LastModified = new Date(m_File.lastModified());
    m_Hidden       = m_File.isHidden();
    m_Link         = Files.isSymbolicLink(m_File.toPath());
  }

  /**
   * Returns the wrapped file.
   *
   * @return		the file
   */
  public File getFile() {
    return m_File;
  }

  /**
   * Returns the actual target (if possible) in case of a link.
   *
   * @return		the actual file
   */
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
  public String getName() {
    return m_File.getName();
  }

  /**
   * Returns the size of the file.
   *
   * @return		the size
   */
  public long getLength() {
    return m_Length;
  }

  /**
   * Returns whether the file represents a directory.
   *
   * @return		true if directory
   */
  public boolean isDirectory() {
    return m_Directory;
  }

  /**
   * Returns the date when the file was last modified.
   *
   * @return		date when last modified
   */
  public Date getLastModified() {
    return m_LastModified;
  }

  /**
   * Returns whether the file is hidden.
   *
   * @return		true if hidden
   */
  public boolean isHidden() {
    return m_Hidden;
  }

  /**
   * Returns whether the file represents a link.
   *
   * @return		true if link
   */
  public boolean isLink() {
    return m_Link;
  }

  /**
   * Returns whether the file is a local file.
   *
   * @return		true if local
   */
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
