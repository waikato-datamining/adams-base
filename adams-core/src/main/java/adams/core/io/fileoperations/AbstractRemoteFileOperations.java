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
 * AbstractRemoteFileOperations.java
 * Copyright (C) 2016-2025 University of Waikato, Hamilton, NZ
 */

package adams.core.io.fileoperations;

/**
 * Ancestor for remote file operation classes.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractRemoteFileOperations
  extends AbstractFileOperations
  implements RemoteFileOperations {

  private static final long serialVersionUID = -5717588876672274558L;

  /** the direction. */
  protected RemoteDirection m_Direction;

  /** for local file operations. */
  protected LocalFileOperations m_LocalOperations;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Direction       = RemoteDirection.LOCAL_TO_REMOTE;
    m_LocalOperations = new LocalFileOperations();
  }

  /**
   * Sets the direction for the operations.
   *
   * @param value	the direction
   */
  public void setDirection(RemoteDirection value) {
    m_Direction = value;
  }

  /**
   * Returns the direction of the operations.
   *
   * @return		the direction
   */
  public RemoteDirection getDirection() {
    return m_Direction;
  }

  /**
   * Renames a local file/dir.
   *
   * @param source	the source file/dir (old)
   * @param target	the target file/dir (new)
   * @return		null if successful, otherwise error message
   */
  protected String renameLocal(String source, String target) {
    return m_LocalOperations.rename(source, target);
  }

  /**
   * Renames a remote file/dir.
   *
   * @param source	the source file/dir (old)
   * @param target	the target file/dir (new)
   * @return		null if successful, otherwise error message
   */
  protected abstract String renameRemote(String source, String target);

  /**
   * Renames a file/dir.
   *
   * @param source	the source file/dir (old)
   * @param target	the target file/dir (new)
   * @return		null if successful, otherwise error message
   */
  public String rename(String source, String target) {
    switch (m_Direction) {
      case LOCAL_TO_REMOTE:
        return renameLocal(source, target);
      case REMOTE_TO_LOCAL:
        return renameRemote(source, target);
      default:
	throw new IllegalStateException("Unhandled direction!");
    }
  }

  /**
   * Deletes a local file/dir.
   *
   * @param path	the file/dir to delete
   * @return		null if successful, otherwise error message
   */
  protected String deleteLocal(String path) {
    return m_LocalOperations.delete(path);
  }

  /**
   * Deletes a remote file/dir.
   *
   * @param path	the file/dir to delete
   * @return		null if successful, otherwise error message
   */
  protected abstract String deleteRemote(String path);

  /**
   * Deletes a file/dir.
   *
   * @param path	the file/dir to delete
   * @return		null if successful, otherwise error message
   */
  public String delete(String path) {
    switch (m_Direction) {
      case LOCAL_TO_REMOTE:
        return deleteLocal(path);
      case REMOTE_TO_LOCAL:
        return deleteRemote(path);
      default:
	throw new IllegalStateException("Unhandled direction!");
    }
  }

  /**
   * Creates the local directory.
   *
   * @param dir		the directory to create
   * @return		null if successful, otherwise error message
   */
  protected String mkdirLocal(String dir) {
    return m_LocalOperations.mkdir(dir);
  }

  /**
   * Creates the remote directory.
   *
   * @param dir		the directory to create
   * @return		null if successful, otherwise error message
   */
  protected abstract String mkdirRemote(String dir);

  /**
   * Creates the directory.
   *
   * @param dir		the directory to create
   * @return		null if successful, otherwise error message
   */
  public String mkdir(String dir) {
    switch (m_Direction) {
      case LOCAL_TO_REMOTE:
        return mkdirLocal(dir);
      case REMOTE_TO_LOCAL:
        return mkdirRemote(dir);
      default:
	throw new IllegalStateException("Unhandled direction!");
    }
  }

  /**
   * Checks whether the local path is a directory.
   *
   * @param path	the path to check
   * @return		true if path exists and is a directory
   */
  protected boolean isDirLocal(String path) {
    return m_LocalOperations.isDir(path);
  }

  /**
   * Checks whether the remote path is a directory.
   *
   * @param path	the path to check
   * @return		true if path exists and is a directory
   */
  protected abstract boolean isDirRemote(String path);

  /**
   * Checks whether the path is a directory.
   *
   * @param path	the path to check
   * @return		true if path exists and is a directory
   */
  public boolean isDir(String path) {
    switch (m_Direction) {
      case LOCAL_TO_REMOTE:
	return isDirLocal(path);
      case REMOTE_TO_LOCAL:
	return isDirRemote(path);
      default:
	throw new IllegalStateException("Unhandled direction!");
    }
  }
}
