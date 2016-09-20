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
 * AbstractRemoteFileOperations.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core.io.fileoperations;

/**
 * Ancestor for remote file operation classes.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
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
   * Renames a local file.
   *
   * @param source	the source file (old)
   * @param target	the target file (new)
   * @return		null if successful, otherwise error message
   */
  protected String renameLocal(String source, String target) {
    return m_LocalOperations.rename(source, target);
  }

  /**
   * Renames a remote file.
   *
   * @param source	the source file (old)
   * @param target	the target file (new)
   * @return		null if successful, otherwise error message
   */
  protected abstract String renameRemote(String source, String target);

  /**
   * Renames a file.
   *
   * @param source	the source file (old)
   * @param target	the target file (new)
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
   * Deletes a local file.
   *
   * @param file	the file to delete
   * @return		null if successful, otherwise error message
   */
  protected String deleteLocal(String file) {
    return m_LocalOperations.delete(file);
  }

  /**
   * Deletes a remote file.
   *
   * @param file	the file to delete
   * @return		null if successful, otherwise error message
   */
  protected abstract String deleteRemote(String file);

  /**
   * Deletes a file.
   *
   * @param file	the file to delete
   * @return		null if successful, otherwise error message
   */
  public String delete(String file) {
    switch (m_Direction) {
      case LOCAL_TO_REMOTE:
        return deleteLocal(file);
      case REMOTE_TO_LOCAL:
        return deleteRemote(file);
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
}
