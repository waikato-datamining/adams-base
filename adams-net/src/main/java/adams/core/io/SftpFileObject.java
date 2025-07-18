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
 * SftpFileObject.java
 * Copyright (C) 2016-2025 University of Waikato, Hamilton, NZ
 */

package adams.core.io;

import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.Session;

import java.io.File;
import java.util.Date;

/**
 * Wrapper for remote SFTP files.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SftpFileObject
  implements FileObject {

  private static final long serialVersionUID = -1391761454087211261L;

  /** the session. */
  protected transient Session m_Session;

  /** the parent directory. */
  protected File m_ParentDir;

  /** the file/dir (if no entry). */
  protected String m_Name;

  /** whether it is a directory (if no entry). */
  protected Boolean m_Directory;

  /** the underlying file. */
  protected transient LsEntry m_Entry;

  /**
   * Initializes the wrapper.
   *
   * @param parentDir	the parent directory
   * @param name	the file/dir to wrap
   * @param dir		whether it is a directory
   * @param session	the session
   */
  public SftpFileObject(File parentDir, String name, boolean dir, Session session) {
    this(parentDir, null, name, dir, session);
  }

  /**
   * Initializes the wrapper.
   *
   * @param parentDir	the parent directory
   * @param entry	the file to wrap
   * @param session	the session
   */
  public SftpFileObject(File parentDir, LsEntry entry, Session session) {
    this(parentDir, entry, null, null, session);
  }

  /**
   * Initializes the wrapper.
   *
   * @param parentDir	the parent directory
   * @param entry	the file to wrap
   * @param name	the file/dir to wrap
   * @param dir		whether it is a directory
   * @param session	the session
   */
  protected SftpFileObject(File parentDir, LsEntry entry, String name, Boolean dir, Session session) {
    m_ParentDir = parentDir;
    m_Entry     = entry;
    m_Name      = name;
    m_Directory = dir;
    m_Session   = session;
  }

  /**
   * Returns the parent, if available.
   *
   * @return		the parent or null if not available
   */
  @Override
  public FileObject getParent() {
    File	parent;

    parent = m_ParentDir.getParentFile();
    if (parent != null)
      return new SftpFileObject(parent.getParentFile(), parent.getName(), true, m_Session);
    else
      return null;
  }

  /**
   * Returns the session.
   *
   * @return		the session
   */
  public Session getSession() {
    return m_Session;
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
    if (m_Entry != null)
      return new File(m_ParentDir + "/" + m_Entry.getFilename());
    else
      return new File(m_ParentDir + "/" + m_Name);
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
    if (m_Entry != null)
      return m_Entry.getFilename();
    else
      return m_Name;
  }

  /**
   * Returns the size of the file.
   *
   * @return		the size
   */
  @Override
  public long getLength() {
    if (m_Entry != null)
      return m_Entry.getAttrs().getSize();
    else
      return -1;
  }

  /**
   * Returns whether the file represents a directory.
   *
   * @return		true if directory
   */
  @Override
  public boolean isDirectory() {
    if (m_Entry != null)
      return m_Entry.getAttrs().isDir();
    else
      return m_Directory;
  }

  /**
   * Returns the date when the file was last modified.
   *
   * @return		date when last modified
   */
  @Override
  public Date getLastModified() {
    if (m_Entry != null)
      return new Date((long) m_Entry.getAttrs().getMTime() * 1000L);
    else
      return new Date(0L);
  }

  /**
   * Returns whether the file is hidden.
   *
   * @return		true if hidden
   */
  @Override
  public boolean isHidden() {
    // bit hacky, assuming Linux FS
    return getName().startsWith(".");
  }

  /**
   * Returns whether the file represents a link.
   *
   * @return		true if link
   */
  @Override
  public boolean isLink() {
    if (m_Entry != null)
      return m_Entry.getAttrs().isLink();
    else
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
    return FileUtils.useForwardSlashes(getFile().toString());
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
